package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

public class NetheriteModifier extends SingleUseModifier {
  public NetheriteModifier() {
    super(0x3C0249);
  }

  @Override
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {
    builder.multiplyDurability(1.25f);
    builder.multiplyAttackDamage(1.25f);
    builder.multiplyMiningSpeed(1.25f);
    builder.setHarvestLevel(HarvestLevels.NETHERITE);
  }
}
