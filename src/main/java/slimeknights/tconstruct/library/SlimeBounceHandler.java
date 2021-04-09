package slimeknights.tconstruct.library;

import java.util.IdentityHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class SlimeBounceHandler {

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

  public SlimeBounceHandler(LivingEntity entityLiving, double bounce) {
    this.entityLiving = entityLiving;
    this.timer = 0;
    this.wasInAir = false;
    this.bounce = bounce;

    if (bounce != 0) {
      this.bounceTick = entityLiving.age;
    } else {
      this.bounceTick = 0;
    }

    bouncingEntities.put(entityLiving, this);
    //entityLiving.addChatMessage(new ChatComponentText("added " + entityLiving.worldObj.isRemote));
  }

  public void playerTickPost(PlayerEntity player) {
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
          //MinecraftForge.EVENT_BUS.unregister(this);
          bouncingEntities.remove(this.entityLiving);
          //entityLiving.addChatMessage(new ChatComponentText("removed " + entityLiving.worldObj.isRemote));
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

  public static void addBounceHandler(LivingEntity entity, double bounce) {
    // only supports actual players as it uses the PlayerTick event
    if (!(entity instanceof PlayerEntity)) {
      return;
    }
    SlimeBounceHandler handler = bouncingEntities.get(entity);
    if (handler == null) {
      // wasn't bouncing yet, register it
      //MinecraftForge.EVENT_BUS.register(new SlimeBounceHandler(entity, bounce));
    } else if (bounce != 0) {
      // updated bounce if needed
      handler.bounce = bounce;
      handler.bounceTick = entity.age;
    }
  }
}
