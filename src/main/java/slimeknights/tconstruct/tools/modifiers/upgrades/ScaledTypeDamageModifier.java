package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/** Shared logic for all modifiers that boost damage against a creature type */
public class ScaledTypeDamageModifier extends IncrementalModifier {
  private final CreatureAttribute type;
  public ScaledTypeDamageModifier(int color, CreatureAttribute type) {
    super(color);
    this.type = type;
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attackerLiving, LivingEntity targetLiving, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    if (targetLiving.getCreatureAttribute() == type) {
      damage += getScaledLevel(tool, level) * 2.5f;
    }
    return damage;
  }
}
