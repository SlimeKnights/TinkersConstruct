package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

public class OverlordModifier extends Modifier {
  public OverlordModifier() {
    super(0x236c45);
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(OverslimeModifier.KEY_OVERSLIME_FRIEND, true);
    OverslimeModifier.addCap(volatileData, level * 75);
  }
}
