package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierToolStatsBuilder;

public class TestModifier extends Modifier {

  @Override
  public void applyEffectToStats(ModifierToolStatsBuilder statsBuilder, CompoundNBT modifierTag) {
    System.out.println("Test modifier");

    statsBuilder.setDurability(999999);
    statsBuilder.setAttack(999999f);
    statsBuilder.setMiningSpeed(999999f);
    statsBuilder.setFreeModifiers(0);
  }

  @Override
  public void applyEffectToModifiersList() {

  }
}
