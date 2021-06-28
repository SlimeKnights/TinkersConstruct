package slimeknights.tconstruct.tools.item.small;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
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
  public boolean dealDamage(IModifierToolStack tool, ToolAttackContext context, float damage) {
    // deal damage first
    boolean hit = super.dealDamage(tool, context, damage);

    // sweep code from EntityPlayer#attackTargetEntityWithCurrentItem()
    // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
    LivingEntity attacker = context.getAttacker();
    if (hit && context.isFullyCharged() && !attacker.isSprinting() && !context.isCritical() && attacker.isOnGround() && (attacker.distanceWalkedModified - attacker.prevDistanceWalkedModified) < attacker.getAIMoveSpeed()) {
      // loop through all nearby entities
      double range = getSweepRange(tool);
      // if the modifier is missing, sweeping damage will be 0, so easiest to let it fully control this
      float sweepDamage = TinkerModifiers.sweeping.get().getSweepingDamage(tool, damage);
      Entity target = context.getTarget();
      for (LivingEntity aoeTarget : attacker.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(range, 0.25D, range))) {
        if (aoeTarget != attacker && aoeTarget != target && !attacker.isOnSameTeam(aoeTarget)
            && (!(aoeTarget instanceof ArmorStandEntity) || !((ArmorStandEntity) aoeTarget).hasMarker()) && attacker.getDistanceSq(aoeTarget) < 10.0D + range) {
          aoeTarget.applyKnockback(0.4F, MathHelper.sin(attacker.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(attacker.rotationYaw * ((float) Math.PI / 180F)));
          ToolAttackUtil.dealDefaultDamage(attacker, aoeTarget, sweepDamage);
        }
      }

      attacker.world.playSound(null, attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
      if (attacker instanceof PlayerEntity) {
        ((PlayerEntity) attacker).spawnSweepParticles();
      }
    }

    return hit;
  }
}
