package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class OverforcedModifier extends IncrementalModifier {
  public OverforcedModifier() {
    super(0x2759C4);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    TinkerModifiers.overslime.get().addCapacity(volatileData, (int)(getScaledLevel(persistentData, level) * 75));
  }
}
