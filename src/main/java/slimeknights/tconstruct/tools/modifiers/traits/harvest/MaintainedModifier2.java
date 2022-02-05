package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** Well maintained for classic Bronze */
public class MaintainedModifier2 extends MaintainedModifier {
  @Override
  protected float getTotalBoost(IToolStackView tool, int level) {
    int durability = tool.getCurrentDurability();
    int fullMax = tool.getStats().getInt(ToolStats.DURABILITY);
    // from 25% to 100%: 12.5% boost per level
    return boost(durability, 0.125f, fullMax / 4, fullMax) * level;
  }
}
