package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

public class DiamondModifier extends SingleUseModifier {
  public DiamondModifier() {
    super(0x8cf4e2);
  }

  @Override
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {
    builder.addDurability(500);
    builder.addAttackDamage(1f);
    builder.addMiningSpeed(2f);
    builder.setHarvestLevel(HarvestLevels.DIAMOND);
  }
}
