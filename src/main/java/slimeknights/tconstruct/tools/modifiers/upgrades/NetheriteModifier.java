package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;
import slimeknights.tconstruct.tools.modifiers.free.FireproofModifier;

public class NetheriteModifier extends FireproofModifier {
  public NetheriteModifier() {
    super(0x8E7C7F);
  }

  @Override
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {
    builder.multiplyDurability(1.25f);
    builder.multiplyAttackDamage(1.25f);
    builder.multiplyMiningSpeed(1.25f);
    builder.setHarvestLevel(HarvestLevels.NETHERITE);
  }
}
