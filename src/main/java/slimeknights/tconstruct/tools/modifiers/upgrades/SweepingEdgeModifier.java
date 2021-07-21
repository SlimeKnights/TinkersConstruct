package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

public class SweepingEdgeModifier extends IncrementalModifier {
  private static final ITextComponent SWEEPING_BONUS = TConstruct.makeTranslation("modifier", "sweeping_edge.attack_damage");

  public SweepingEdgeModifier() {
    super(0x888888);
  }

  /** Gets the damage dealt by this tool, boosted properly by sweeping */
  public float getSweepingDamage(IModifierToolStack toolStack, float baseDamage) {
    int level = toolStack.getModifierLevel(this);
    float sweepingDamage = 1;
    if (level > 4) {
      sweepingDamage += baseDamage;
    } else if (level > 0) {
      // gives 25% per level
      sweepingDamage += getScaledLevel(toolStack, level) * 0.25f * baseDamage;
    }
    return sweepingDamage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    float amount = getScaledLevel(tool, level) * 0.25f;
    tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_FORMAT.format(amount)).appendString(" ").appendSibling(SWEEPING_BONUS)));
  }
}
