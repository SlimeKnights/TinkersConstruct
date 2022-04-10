package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

public class OverforcedModifier extends IncrementalModifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    overslime.addCapacity(volatileData, (int)(getScaledLevel(context, level) * 75));
    overslime.setFriend(volatileData);
  }
}
