package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule} */
@Deprecated
public class PaddedModifier extends Modifier {
  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // at level 1, leaves 0.2f by default, and 0.25f per level of knockback
    return (float)(knockback / (Math.pow(2, level)));
  }
}
