package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class KnockbackModifier extends Modifier {
  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    return knockback + level * 0.5f;
  }
}
