package slimeknights.tconstruct.tools.modifiers.free;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class WorldboundModifier extends SingleUseModifier {
  public WorldboundModifier(int color) {
    super(color);
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(ToolCore.INDESTRUCTIBLE_ENTITY, true);
  }
}
