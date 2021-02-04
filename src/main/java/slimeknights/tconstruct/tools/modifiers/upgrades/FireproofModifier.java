package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class FireproofModifier extends SingleUseModifier {
  public FireproofModifier(int color) {
    super(color);
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(ToolCore.INDESTRUCTIBLE_ENTITY, true);
  }
}
