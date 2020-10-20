package slimeknights.tconstruct.tools.modifiers;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifiedToolStatsBuilder;

public class TestModifier extends Modifier {

  @Override
  public void applyStats(ModifiedToolStatsBuilder statsBuilder, ModifierNBT modifierNBT) {
    System.out.println("Test modifier");

    statsBuilder.setDurability(999999);
    statsBuilder.setAttack(999999f);
    statsBuilder.setMiningSpeed(999999f);
  }
}
