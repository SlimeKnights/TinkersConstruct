package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class KnockbackModifier extends Modifier {
  public KnockbackModifier() {
    super(0xBC9862);
  }

  @Override
  public float beforeLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damage, float baseKnockback, float knockback, boolean isCritical, boolean fullyCharged) {
    return knockback + level * 0.5f;
  }
}
