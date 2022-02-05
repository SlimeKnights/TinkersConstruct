package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class SturdyModifier extends Modifier {
  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.15f));
  }
}
