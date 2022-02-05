package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class LightweightModifier extends Modifier {
  public LightweightModifier() {
    super(0x2882d4);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_SPEED.multiply(builder, 1 + (level * 0.07f));
    ToolStats.MINING_SPEED.multiply(builder, 1 + (level * 0.07f));
  }
}
