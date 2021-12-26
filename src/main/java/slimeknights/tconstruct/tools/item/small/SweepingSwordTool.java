package slimeknights.tconstruct.tools.item.small;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
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
    if (hit && context.isFullyCharged() && !attacker.isSprinting() && !context.isCritical() && attacker.isOnGround() && (attacker.walkDist - attacker.walkDistO) < attacker.getSpeed()) {
      // loop through all nearby entities
      double range = getSweepRange(tool);
      // if the modifier is missing, sweeping damage will be 0, so easiest to let it fully control this
      float sweepDamage = TinkerModifiers.sweeping.get().getSweepingDamage(tool, damage);
      Entity target = context.getTarget();
      for (LivingEntity aoeTarget : attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25D, range))) {
        if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)
            && (!(aoeTarget instanceof ArmorStandEntity) || !((ArmorStandEntity) aoeTarget).isMarker()) && attacker.distanceToSqr(aoeTarget) < 10.0D + range) {
          aoeTarget.knockback(0.4F, MathHelper.sin(attacker.yRot * ((float) Math.PI / 180F)), -MathHelper.cos(attacker.yRot * ((float) Math.PI / 180F)));
          ToolAttackUtil.dealDefaultDamage(attacker, aoeTarget, sweepDamage);
        }
      }

      attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
      if (attacker instanceof PlayerEntity) {
        ((PlayerEntity) attacker).sweepAttack();
      }
    }

    return hit;
  }
}
