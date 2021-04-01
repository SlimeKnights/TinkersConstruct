package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.modifiers.free.WorldboundModifier;

public class NetheriteModifier extends WorldboundModifier {
  public NetheriteModifier() {
    super(0x8E7C7F);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    builder.multiplyDurability(1.25f);
    builder.multiplyAttackDamage(1.25f);
    builder.multiplyMiningSpeed(1.25f);
    builder.setHarvestLevel(HarvestLevels.NETHERITE);
  }
}
