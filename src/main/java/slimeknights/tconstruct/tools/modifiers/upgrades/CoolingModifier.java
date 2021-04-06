package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class CoolingModifier extends IncrementalModifier {
  public CoolingModifier() {
    super(0x91C5B7);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    if (target.isImmuneToFire()) {
      damage += getScaledLevel(tool, level) * 1.5f;
    }
    return damage;
  }
}
