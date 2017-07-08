package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class EntityUtil {

  private EntityUtil() {
  }

  public static RayTraceResult raytraceEntityPlayerLook(EntityPlayer player, float range) {
    Vec3d eye = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ); // Entity.getPositionEyes
    Vec3d look = player.getLook(1.0f);

    return raytraceEntity(player, eye, look, range, true);
  }

  // based on EntityRenderer.getMouseOver
  public static RayTraceResult raytraceEntity(Entity entity, Vec3d start, Vec3d look, double range, boolean ignoreCanBeCollidedWith) {
    //Vec3 look = entity.getLook(partialTicks);
    Vec3d direction = start.addVector(look.x * range, look.y * range, look.z * range);

    //Vec3 direction = vec3.addVector(vec31.x * d0, vec31.y * d0, vec31.z * d0);
    Entity pointedEntity = null;
    Vec3d hit = null;
    AxisAlignedBB bb = entity.getEntityBoundingBox().expand(look.x * range, look.y * range, look.z * range).expand(1, 1, 1);
    List<Entity> entitiesInArea = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity, bb);
    double range2 = range; // range to the current candidate. Used to find the closest entity.

    for(Entity candidate : entitiesInArea) {
      if(ignoreCanBeCollidedWith || candidate.canBeCollidedWith()) {
        // does our vector go through the entity?
        double colBorder = candidate.getCollisionBorderSize();
        AxisAlignedBB entityBB = candidate.getEntityBoundingBox().expand(colBorder, colBorder, colBorder);
        RayTraceResult movingobjectposition = entityBB.calculateIntercept(start, direction);

        // needs special casing: vector starts inside the entity
        if(entityBB.contains(start)) {
          if(0.0D < range2 || range2 == 0.0D) {
            pointedEntity = candidate;
            hit = movingobjectposition == null ? start : movingobjectposition.hitVec;
            range2 = 0.0D;
          }
        }
        else if(movingobjectposition != null) {
          double dist = start.distanceTo(movingobjectposition.hitVec);

          if(dist < range2 || range2 == 0.0D) {
            if(candidate == entity.getRidingEntity() && !entity.canRiderInteract()) {
              if(range2 == 0.0D) {
                pointedEntity = candidate;
                hit = movingobjectposition.hitVec;
              }
            }
            else {
              pointedEntity = candidate;
              hit = movingobjectposition.hitVec;
              range2 = dist;
            }
          }
        }
      }
    }

    if(pointedEntity != null && range2 < range) {
      return new RayTraceResult(pointedEntity, hit);
    }
    return null;
  }
}
