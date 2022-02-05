package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class OverforcedModifier extends IncrementalModifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.overslime.get().addCapacity(volatileData, (int)(getScaledLevel(context, level) * 75));
  }
}
