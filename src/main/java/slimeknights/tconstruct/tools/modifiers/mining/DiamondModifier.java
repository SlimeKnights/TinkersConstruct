package slimeknights.tconstruct.tools.modifiers.mining;

import slimeknights.tconstruct.library.modifiers.ModifiedToolStatsBuilder;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class DiamondModifier extends Modifier {

  @Override
  public void applyStats(ModifiedToolStatsBuilder statsBuilder, ModifierNBT modifierNBT) {
    statsBuilder.setDurability(statsBuilder.getDurability() + 500);

    if (statsBuilder.getHarvestLevel() < HarvestLevels.OBSIDIAN) {
      statsBuilder.setHarvestLevel(statsBuilder.getHarvestLevel() + 1);
    }

    statsBuilder.setAttack(statsBuilder.getAttack() + 1f);
    statsBuilder.setMiningSpeed(statsBuilder.getMiningSpeed() + 0.5f);
  }

  @Override
  public int getColorIndex() {
    return 0x8cf4e2;
  }
}
