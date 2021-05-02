package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.modifiers.ability.MeltingModifier;

public class TankModifier extends Modifier {
  private final int capacityBoost;
  public TankModifier(int color, int capacityBoost) {
    super(color);
    this.capacityBoost = capacityBoost;
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    MeltingModifier.addCapacity(volatileData, capacityBoost);
  }
}
