package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.modifiers.internal.OffhandAttackModifier;

import java.util.List;

public class DuelWieldingModifier extends OffhandAttackModifier {
  private final ITextComponent debuffTooltip;
  public DuelWieldingModifier() {
    super(0xA6846A, 25);
    this.debuffTooltip = applyStyle(TConstruct.makeTranslation("modifier", "dual_wielding.debuff"));
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    if (context.getHand() == Hand.OFF_HAND) {
      return damage * 0.8f;
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    tooltip.add(debuffTooltip);
  }
}
