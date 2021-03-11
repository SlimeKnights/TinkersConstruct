package slimeknights.tconstruct.tools.melee;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.tools.item.SwordCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class BroadSword extends SwordCore {

  public BroadSword(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  // sword sweep attack
  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity living, Entity targetEntity, float damage, boolean isCritical, boolean fullyCharged) {
    // deal damage first
    boolean hit = super.dealDamage(tool, living, targetEntity, damage, isCritical, fullyCharged);

    // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
    // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
    if (hit && fullyCharged && !living.isSprinting() && !isCritical && living.isOnGround() && (living.distanceWalkedModified - living.prevDistanceWalkedModified) < living.getAIMoveSpeed()) {
      // loop through all nearby entities
      for (LivingEntity livingEntity : living.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
        if (livingEntity != living && livingEntity != targetEntity && !living.isOnSameTeam(livingEntity)
            && (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingEntity).hasMarker()) && living.getDistanceSq(livingEntity) < 9.0D) {
          livingEntity.applyKnockback(0.4F, MathHelper.sin(living.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(living.rotationYaw * ((float) Math.PI / 180F)));
          super.dealDamage(tool, living, targetEntity, damage, false, true);
        }
      }

      living.world.playSound(null, living.getPosX(), living.getPosY(), living.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, living.getSoundCategory(), 1.0F, 1.0F);
      if (living instanceof PlayerEntity) {
        ((PlayerEntity) living).spawnSweepParticles();
      }
    }

    return hit;
  }
}
