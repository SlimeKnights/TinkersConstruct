package slimeknights.tconstruct.library;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import slimeknights.tconstruct.event.LivingEntityTickCallback;

/** Logic for entities bouncing */
public class SlimeBounceHandler implements Consumer<PlayerEntity>, LivingEntityTickCallback {
  private static final IdentityHashMap<Entity, SlimeBounceHandler> bouncingEntities = new IdentityHashMap<>();

  public static IdentityHashMap<Entity, SlimeBounceHandler> getBouncingEntities() {
    return bouncingEntities;
  }

  public final LivingEntity entityLiving;
  private int timer;
  private boolean wasInAir;
  private double bounce;
  private int bounceTick;

  private double lastMovX;
  private double lastMovZ;

  private boolean active = true;

  public SlimeBounceHandler(LivingEntity entityLiving, double bounce) {
    this.entityLiving = entityLiving;
    this.timer = 0;
    this.wasInAir = false;
    this.bounce = bounce;

    if (bounce != 0) {
      // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
      this.bounceTick = entityLiving.age + 1;
    } else {
      this.bounceTick = 0;
    }

    bouncingEntities.put(entityLiving, this);
    //entityLiving.addChatMessage(new ChatComponentText("added " + entityLiving.worldObj.isRemote));
  }

  @Override
  public void accept(PlayerEntity player) {
    if(!active) {
      return; // FIXME: PORT (the fix for not being able to unregister is this)
    }
    // this is only relevant for the local player
    if (player == this.entityLiving && !player.isFallFlying()) {
      // bounce up. This is to pcircumvent the logic that resets y motion after landing
      if (player.age == this.bounceTick) {
        Vec3d vec3d = player.getVelocity();
        player.setVelocity(vec3d.x, this.bounce, vec3d.z);
        this.bounceTick = 0;
      }

      // preserve motion
      if (!this.entityLiving.isOnGround() && this.entityLiving.age != this.bounceTick) {
        if (this.lastMovX != this.entityLiving.getVelocity().x || this.lastMovZ != this.entityLiving.getVelocity().z) {
          double f = 0.91d + 0.025d;
          //System.out.println((entityLiving.worldObj.isRemote ? "client: " : "server: ") + entityLiving.motionX);
          Vec3d vec3d = this.entityLiving.getVelocity();
          player.setVelocity(vec3d.x / f, vec3d.y, vec3d.z / f);
          this.entityLiving.velocityDirty = true;
          this.lastMovX = this.entityLiving.getVelocity().x;
          this.lastMovZ = this.entityLiving.getVelocity().z;
        }
      }

      // timing the effect out
      if (this.wasInAir && this.entityLiving.isOnGround()) {
        if (this.timer == 0) {
          this.timer = this.entityLiving.age;
        } else if (this.entityLiving.age - this.timer > 5) {
          active = false;
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

  @Override
  public void onEntityTick(LivingEntity entity) {
    accept((PlayerEntity) entity);
  }

  /**
   * Causes the entity to bounce, needed because the fall event will reset motion afterwards
   * @param entity  Entity to bounce
   * @param bounce  Bounce amoint
   */
  public static void addBounceHandler(LivingEntity entity, double bounce) {
    // no fake players PlayerTick event
    if (!(entity instanceof PlayerEntity)) {
      return;
    }
    SlimeBounceHandler handler = bouncingEntities.get(entity);
    if (handler == null) {
      // wasn't bouncing yet, register it
      LivingEntityTickCallback.EVENT.register(new SlimeBounceHandler(entity, bounce));
    } else if (bounce != 0) {
      // updated bounce if needed
      handler.bounce = bounce;
      // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
      handler.bounceTick = entity.age + 1;
    }
  }
}
