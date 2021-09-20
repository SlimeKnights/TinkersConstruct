package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.IdentityHashMap;

/** Logic for entities bouncing */
public class SlimeBounceHandler {
  private SlimeBounceHandler() {}
  private static final IdentityHashMap<Entity, BounceInfo> BOUNCING_ENTITIES = new IdentityHashMap<>();

  /** Registers event handlers */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(SlimeBounceHandler::onLivingTick);
    MinecraftForge.EVENT_BUS.addListener(SlimeBounceHandler::serverStopping);
  }

  /**
   * Preserves entity air momentum
   * @param entity  Entity to bounce
   */
  public static void addBounceHandler(LivingEntity entity) {
    addBounceHandler(entity, 0d);
  }

  /**
   * Causes the entity to bounce, needed because the fall event will reset motion afterwards
   * @param entity  Entity to bounce
   * @param bounce  Bounce amount
   */
  public static void addBounceHandler(LivingEntity entity, double bounce) {
    // no fake players PlayerTick event
    if (entity instanceof FakePlayer) {
      return;
    }
    // update bounce info
    BounceInfo info = BOUNCING_ENTITIES.get(entity);
    if (info == null) {
      BOUNCING_ENTITIES.put(entity, new BounceInfo(entity, bounce));
    } else if (bounce != 0) {
      // updated bounce if needed
      info.bounce = bounce;
      // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
      info.bounceTick = entity.ticksExisted + 1;
    }
  }

  /** Called on living tick to preserve momentum and bounce */
  private static void onLivingTick(LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity.getEntityWorld().isRemote) {
      return;
    }
    BounceInfo info = BOUNCING_ENTITIES.get(entity);

    // if we have info for this entity, time to work
    if (info != null) {
      // if flying, nothing to do
      if (entity.isElytraFlying()) {
        BOUNCING_ENTITIES.remove(entity);
        return;
      }

      // if its the bounce tick, time to bounce. This is to circumvent the logic that resets y motion after landing
      if (entity.ticksExisted == info.bounceTick) {
        Vector3d motion = entity.getMotion();
        entity.setMotion(motion.x, info.bounce, motion.z);
        info.bounceTick = 0;
      }
      // preserve motion
      // TODO: needs cleanup, this basically gives you superspeed, we really need to check that the speed was reduced
      if (!entity.isOnGround() && (info.lastMovX != entity.getMotion().x || info.lastMovZ != entity.getMotion().z)) {
        Vector3d motion = entity.getMotion();
        entity.setMotion(motion.x / 0.935d , motion.y, motion.z / 0.935d);
        entity.isAirBorne = true;
        info.lastMovX = entity.getMotion().x;
        info.lastMovZ = entity.getMotion().z;
      }

      // timing the effect out
      if (info.wasInAir && entity.isOnGround()) {
        if (info.endHandler == 0) {
          info.endHandler = entity.ticksExisted + 5;
        } else if (entity.ticksExisted > info.endHandler) {
          BOUNCING_ENTITIES.remove(entity);
        }
      } else {
        info.endHandler = 0;
        info.wasInAir = true;
      }
    }
  }

  /** Called on server shutdown to prevent memory leaks */
  private static void serverStopping(FMLServerStoppingEvent event) {
    BOUNCING_ENTITIES.clear();
  }

  /** Data class to keep track of bouncing info for an entity */
  private static class BounceInfo {
    /** Velocity the entity should have, unused if 0 */
    private double bounce;
    /** Time to update the entities velocity */
    private int bounceTick;
    /** Last motion of the entity */
    private double lastMovX, lastMovZ;
    /** If true, the entity was in air last tick */
    private boolean wasInAir = false;
    /** Time when motion should stop */
    private int endHandler = 0;

    public BounceInfo(LivingEntity entity, double bounce) {
      this.bounce = bounce;
      if (bounce != 0) {
        // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
        this.bounceTick = entity.ticksExisted + 1;
      } else {
        this.bounceTick = 0;
      }
      //this.lastMovX = entity.getMotion().x;
      //this.lastMovZ = entity.getMotion().z;
    }
  }
}
