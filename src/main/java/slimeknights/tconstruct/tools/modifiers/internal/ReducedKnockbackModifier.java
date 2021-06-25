package slimeknights.tconstruct.tools.modifiers.internal;

import lombok.Getter;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/** Modifier that cancels the base 0.4 modifier tools get */
public class ReducedKnockbackModifier extends SingleUseModifier {

  @Getter
  private final int priority;
  public ReducedKnockbackModifier(int color, int priority) {
    super(color);
    this.priority = priority;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public float beforeLivingHit(IModifierToolStack tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // leaves 0.2f by default, and 0.25f per level of knockback
    return (knockback) / 2f;
  }
}
