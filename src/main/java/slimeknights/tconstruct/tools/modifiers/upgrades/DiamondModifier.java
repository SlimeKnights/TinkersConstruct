package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class DiamondModifier extends SingleLevelModifier {
  public DiamondModifier() {
    super(0x8cf4e2);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    ToolStats.DURABILITY.add(builder, 500);
    ToolStats.ATTACK_DAMAGE.add(builder, 0.5f);
    ToolStats.MINING_SPEED.add(builder, 1f);
    ToolStats.HARVEST_LEVEL.set(builder, HarvestLevels.DIAMOND);
  }
}
