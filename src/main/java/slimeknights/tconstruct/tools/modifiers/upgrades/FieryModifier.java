package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class FieryModifier extends IncrementalModifier {
  public FieryModifier() {
    super(0x953300);
  }

  @Override
  public float beforeLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damage, float baseKnockback, float knockback, boolean isCritical, boolean fullyCharged) {
    // vanilla hack: apply fire so the entity drops the proper items on instant kill
    if (!target.isBurning()) {
      target.setFire(1);
    }
    return knockback;
  }

  @Override
  public void failedLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, boolean isCritical, boolean fullyCharged) {
    // conclusion of vanilla hack: we don't want the target on fire if we did not hit them
    if (target.isBurning()) {
      target.extinguish();
    }
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    if (!target.isBurning()) {
      target.setFire(Math.round(getScaledLevel(tool, level) * 5));
    }
    return 0;
  }
}
