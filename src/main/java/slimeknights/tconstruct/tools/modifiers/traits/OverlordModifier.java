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
  private int getBoost(ToolDefinition definition, StatsNBT stats, int level, float perLevel) {
    return (int)(stats.getDurability() * definition.getBaseStatDefinition().getDurabilityModifier() * perLevel * level);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(OverslimeModifier.KEY_OVERSLIME_FRIEND, true);
    // gains +15% of the durability per level
    OverslimeModifier.addCap(toolDefinition, volatileData, getBoost(toolDefinition, baseStats, level, 0.10f));
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    // at most subtract 90% durability
    builder.addDurability(-getBoost(toolDefinition, baseStats, Math.min(level, 6), 0.15f));
  }
}
