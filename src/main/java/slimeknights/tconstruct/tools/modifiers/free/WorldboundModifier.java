package slimeknights.tconstruct.tools.modifiers.free;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class WorldboundModifier extends SingleUseModifier {
  public WorldboundModifier(int color) {
    super(color);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(ToolCore.INDESTRUCTIBLE_ENTITY, true);
  }
}
