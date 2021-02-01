package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class EnhancedModifier extends Modifier {
  public EnhancedModifier() {
    super(0xffdbcc);
  }

  @Override
  public void addVolatileData(int level, ModDataNBT data) {
    data.addUpgrades(level);
  }
}
