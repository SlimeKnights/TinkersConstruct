package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

/** Logic for entities bouncing */
public class SlimeBounceHandler implements Consumer<LivingUpdateEvent> {
  private static final IdentityHashMap<Entity, SlimeBounceHandler> bouncingEntities = new IdentityHashMap<>();

  public final LivingEntity entityLiving;
  private int timer;
  private boolean wasInAir;
  private double bounce;
  private int bounceTick;

  private double lastMovX;
  private double lastMovZ;

  public SlimeBounceHandler(LivingEntity entityLiving, double bounce) {
    this.entityLiving = entityLiving;
    this.timer = 0;
    this.wasInAir = false;
    this.bounce = bounce;

    if (bounce != 0) {
      // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
      this.bounceTick = entityLiving.ticksExisted + 1;
    } else {
      this.bounceTick = 0;
    }

    bouncingEntities.put(entityLiving, this);
    //entityLiving.addChatMessage(new ChatComponentText("added " + entityLiving.worldObj.isRemote));
  }

  @Override
  public void accept(LivingUpdateEvent event) {
    // this is only relevant for the local player
    if (event.getEntityLiving() == this.entityLiving && !this.entityLiving.isElytraFlying()) {
      // bounce up. This is to circumvent the logic that resets y motion after landing
      if (this.entityLiving.ticksExisted == this.bounceTick) {
        Vector3d vec3d = this.entityLiving.getMotion();
        this.entityLiving.setMotion(vec3d.x, this.bounce, vec3d.z);
        this.bounceTick = 0;
      }

      // preserve motion
      if (!this.entityLiving.isOnGround() && this.entityLiving.ticksExisted != this.bounceTick) {
        if (this.lastMovX != this.entityLiving.getMotion().x || this.lastMovZ != this.entityLiving.getMotion().z) {
          double f = 0.91d + 0.025d;
          //System.out.println((entityLiving.worldObj.isRemote ? "client: " : "server: ") + entityLiving.motionX);
          Vector3d vec3d = this.entityLiving.getMotion();
          this.entityLiving.setMotion(vec3d.x / f, vec3d.y, vec3d.z / f);
          this.entityLiving.isAirBorne = true;
          this.lastMovX = this.entityLiving.getMotion().x;
          this.lastMovZ = this.entityLiving.getMotion().z;
        }
      }

      // timing the effect out
      if (this.wasInAir && this.entityLiving.isOnGround()) {
        if (this.timer == 0) {
          this.timer = this.entityLiving.ticksExisted;
        } else if (this.entityLiving.ticksExisted - this.timer > 5) {
          MinecraftForge.EVENT_BUS.unregister(this);
          bouncingEntities.remove(this.entityLiving);
        }
      } else {
        this.timer = 0;
        this.wasInAir = true;
      }
    }
  }

  public static void addBounceHandler(LivingEntity entity) {
    addBounceHandler(entity, 0d);
  }

  /**
   * Causes the entity to bounce, needed because the fall event will reset motion afterwards
   * @param entity  Entity to bounce
   * @param bounce  Bounce amoint
   */
  public static void addBounceHandler(LivingEntity entity, double bounce) {
    // no fake players PlayerTick event
    if (entity instanceof FakePlayer) {
      return;
    }
    SlimeBounceHandler handler = bouncingEntities.get(entity);
    if (handler == null) {
      // wasn't bouncing yet, register it
      MinecraftForge.EVENT_BUS.addListener(new SlimeBounceHandler(entity, bounce));
    } else if (bounce != 0) {
      // updated bounce if needed
      handler.bounce = bounce;
      // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
      handler.bounceTick = entity.ticksExisted + 1;
    }
  }
}
