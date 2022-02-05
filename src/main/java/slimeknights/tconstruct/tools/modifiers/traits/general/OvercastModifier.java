package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

public class OvercastModifier extends Modifier {
  @Override
  public int getPriority() {
    return 90; // after overslime and overforced
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    overslime.setFriend(volatileData);
    overslime.addCapacity(volatileData, level * 25);
    overslime.multiplyCapacity(volatileData, 1f + (level * 0.5f));
  }
}
