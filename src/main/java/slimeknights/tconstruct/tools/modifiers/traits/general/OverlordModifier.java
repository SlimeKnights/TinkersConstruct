package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

public class OverlordModifier extends Modifier {
  public OverlordModifier() {
    super(0x236c45);
  }
  @Override
  public int getPriority() {
    return 80; // after overcast
  }

  /** Gets the durability boost per level */
  private int getBoost(StatsNBT baseStats, int level, float perLevel) {
    return (int)(baseStats.getFloat(ToolStats.DURABILITY) * perLevel * level);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    overslime.setFriend(volatileData);
    // gains +15% of the durability per level, note that base stats does not consider the durability modifier
    overslime.addCapacity(volatileData, getBoost(baseStats, level, 0.10f * toolDefinition.getData().getMultiplier(ToolStats.DURABILITY)));
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    // at most subtract 90% durability, note this runs before the tool durability modifier
    ToolStats.DURABILITY.add(builder, -getBoost(baseStats, Math.min(level, 6), 0.15f));
  }
}
