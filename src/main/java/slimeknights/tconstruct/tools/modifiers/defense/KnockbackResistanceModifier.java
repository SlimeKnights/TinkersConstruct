package slimeknights.tconstruct.tools.modifiers.defense;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class KnockbackResistanceModifier extends Modifier {
  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.KNOCKBACK_RESISTANCE.add(builder, 0.1f * level);
  }
}
