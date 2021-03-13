package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public final class EntityUtil {

  private EntityUtil() {
  }

  public static RayTraceResult raytraceEntityPlayerLook(PlayerEntity player, float range) {
    Vector3d eye = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()); // Entity.getPositionEyes
    Vector3d look = player.getLook(1.0f);

    return raytraceEntity(player, eye, look, range, true);
  }

  // based on EntityRenderer.getMouseOver
  public static RayTraceResult raytraceEntity(Entity entity, Vector3d start, Vector3d look, double range, boolean ignoreCanBeCollidedWith) {
    Vector3d direction = start.add(look.x * range, look.y * range, look.z * range);

    Entity pointedEntity = null;
    Vector3d hit = null;
    AxisAlignedBB bb = entity.getBoundingBox().expand(look.x * range, look.y * range, look.z * range).expand(1, 1, 1);
    List<Entity> entitiesInArea = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity, bb);
    double range2 = range; // range to the current candidate. Used to find the closest entity.

    for (Entity candidate : entitiesInArea) {
      if (ignoreCanBeCollidedWith || candidate.canBeCollidedWith()) {
        // does our vector go through the entity?
        double colBorder = candidate.getCollisionBorderSize();
        AxisAlignedBB entityBB = candidate.getBoundingBox().expand(colBorder, colBorder, colBorder);

        RayTraceResult movingobjectposition = entityBB.intersects(start, direction) ? new EntityRayTraceResult(entity, direction) : null;

        // needs special casing: vector starts inside the entity
        if (entityBB.contains(start)) {
          if (0.0D < range2 || range2 == 0.0D) {
            pointedEntity = candidate;
            hit = movingobjectposition == null ? start : movingobjectposition.getHitVec();
            range2 = 0.0D;
          }
        } else if (movingobjectposition != null) {
          double dist = start.distanceTo(movingobjectposition.getHitVec());

          if (dist < range2 || range2 == 0.0D) {
            if (candidate == entity.getRidingEntity() && !entity.canRiderInteract()) {
              if (range2 == 0.0D) {
                pointedEntity = candidate;
                hit = movingobjectposition.getHitVec();
              }
            } else {
              pointedEntity = candidate;
              hit = movingobjectposition.getHitVec();
              range2 = dist;
            }
          }
        }
      }
    }

    if (pointedEntity != null && range2 < range) {
      return new EntityRayTraceResult(pointedEntity, hit);
    }
    // TODO: Make better return
    return new EntityRayTraceResult(null, null);
  }
}
