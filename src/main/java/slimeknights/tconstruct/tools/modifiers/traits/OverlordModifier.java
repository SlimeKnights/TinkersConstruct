package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

public class OverlordModifier extends Modifier {
  public OverlordModifier() {
    super(0x236c45);
  }

  /** Gets the durability boost per level */
  private int getBoost(StatsNBT baseStats, int level, float perLevel) {
    return (int)(baseStats.getDurability() * perLevel * level);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(OverslimeModifier.KEY_OVERSLIME_FRIEND, true);
    // gains +15% of the durability per level, note that base stats does not consider the durability modifier
    OverslimeModifier.addCap(toolDefinition, volatileData, getBoost(baseStats, level, 0.10f * toolDefinition.getBaseStatDefinition().getDurabilityModifier()));
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    // at most subtract 90% durability, note this runs before the tool durability modifier
    builder.addDurability(-getBoost(baseStats, Math.min(level, 6), 0.15f));
  }
}
