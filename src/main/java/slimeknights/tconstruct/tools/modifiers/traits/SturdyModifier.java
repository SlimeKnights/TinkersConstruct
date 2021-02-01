package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

public class SturdyModifier extends Modifier {
  public SturdyModifier() {
    super(0xa7a7a7);
  }

  @Override
  public void addToolStats(int level, ToolStatsModifierBuilder builder) {
    builder.multiplyDurability(1 + (level * 0.1f));
  }
}
