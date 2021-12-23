package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ReinforcedModifier;

import java.util.List;

public class DenseModifier extends ReinforcedModifier {
  public DenseModifier() {
    super(0xBED3CD);
  }

  @Override
  public float getRepairFactor(IModifierToolStack toolStack, int level, float factor) {
    // the scale used by reinforced was quite nice to use for reduction here, so 25% loss at level 1, etc.
    // by level 5, you will be repairing at 25% efficiency, at level 10 its 0%
    return factor * (1 - super.getPercentage(level));
  }

  @Override
  protected float getPercentage(float level) {
    // formula gives 33%, 55%, 70%, 80% for first 4 levels
    return 1f - (float)(1f / (Math.pow(1.5f, level)));
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_FORMAT.format(getPercentage(getScaledLevel(tool, level))) + " ")
                             .appendSibling(makeDisplayName())));
  }
}
