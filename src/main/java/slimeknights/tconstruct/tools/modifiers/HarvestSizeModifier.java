package slimeknights.tconstruct.tools.modifiers;

import slimeknights.tconstruct.library.modifiers.ModifiedToolStatsBuilder;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;

public class HarvestSizeModifier extends Modifier {

  public HarvestSizeModifier() {
    addAspects();
  }

  @Override
  public void applyStats(ModifiedToolStatsBuilder statsBuilder, ModifierNBT modifierNBT) {
    // no extra data needed
  }

  @Override
  public int getColorIndex() {
    return 0xcaf6a2;
  }
}
