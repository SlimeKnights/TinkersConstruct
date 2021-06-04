package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** Well maintained for classic Bronze */
public class MaintainedModifier2 extends MaintainedModifier {
  public MaintainedModifier2() {
    super(0xD58F36);
  }

  @Override
  protected float getTotalBoost(IModifierToolStack tool, int level) {
    int durability = tool.getCurrentDurability();
    int fullMax = tool.getStats().getInt(ToolStats.DURABILITY);
    // from 25% to 100%: 12.5% boost per level
    return boost(durability, 0.125f, fullMax / 4, fullMax) * level;
  }
}
