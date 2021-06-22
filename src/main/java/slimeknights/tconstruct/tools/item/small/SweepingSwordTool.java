package slimeknights.tconstruct.tools.item.small;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Sword that also has a sweep attack */
public class SweepingSwordTool extends SwordTool {
  public SweepingSwordTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  /** Gets the bonus area of the sweep attack */
  protected double getSweepRange(IModifierToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.expanded.get()) + 1;
  }

  // sword sweep attack
  @Override
  public boolean dealDamage(IModifierToolStack tool, LivingEntity living, Hand hand, Entity targetEntity, float damage, boolean isCritical, boolean fullyCharged) {
    // deal damage first
    boolean hit = super.dealDamage(tool, living, hand, targetEntity, damage, isCritical, fullyCharged);

    // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
    // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
    if (hit && fullyCharged && !living.isSprinting() && !isCritical && living.isOnGround() && (living.distanceWalkedModified - living.prevDistanceWalkedModified) < living.getAIMoveSpeed()) {
      // loop through all nearby entities
      double range = getSweepRange(tool);
      // if the modifier is missing, sweeping damage will be 0, so easiest to let it fully control this
      float sweepDamage = TinkerModifiers.sweeping.get().getSweepingDamage(tool, damage);
      for (LivingEntity livingEntity : living.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(range, 0.25D, range))) {
        if (livingEntity != living && livingEntity != targetEntity && !living.isOnSameTeam(livingEntity)
            && (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingEntity).hasMarker()) && living.getDistanceSq(livingEntity) < 10.0D + range) {
          livingEntity.applyKnockback(0.4F, MathHelper.sin(living.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(living.rotationYaw * ((float) Math.PI / 180F)));
          super.dealDamage(tool, living, hand, livingEntity, sweepDamage, false, true);
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
