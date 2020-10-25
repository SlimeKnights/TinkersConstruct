package slimeknights.tconstruct.tools.modifiers.mining;

import slimeknights.tconstruct.library.modifiers.ModifiedToolStatsBuilder;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class EmeraldModifier extends Modifier {

  @Override
  public void applyStats(ModifiedToolStatsBuilder statsBuilder, ModifierNBT modifierNBT) {
    statsBuilder.setDurability(statsBuilder.getDurability() + this.getOriginalStats().durability / 2);

    if (statsBuilder.getHarvestLevel() < HarvestLevels.DIAMOND) {
      statsBuilder.setHarvestLevel(statsBuilder.getHarvestLevel() + 1);
    }
  }

  @Override
  public int getColorIndex() {
    return 0x41f384;
  }
}
