package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class OverworkedModifier extends Modifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.overslime.get().setFriend(volatileData);
  }
}
