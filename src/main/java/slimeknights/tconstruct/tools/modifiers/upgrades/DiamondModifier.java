package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class DiamondModifier extends SingleUseModifier {
  public DiamondModifier() {
    super(0x8cf4e2);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    builder.addDurability(500);
    builder.addAttackDamage(0.5f);
    builder.addMiningSpeed(1f);
    builder.setHarvestLevel(HarvestLevels.DIAMOND);
  }
}
