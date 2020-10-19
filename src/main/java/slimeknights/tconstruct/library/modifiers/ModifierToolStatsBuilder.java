package slimeknights.tconstruct.library.modifiers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@ToString
public final class ModifierToolStatsBuilder {

  public int durability;
  public int harvestLevel;
  public float attack;
  public float miningSpeed;
  public float attackSpeedMultiplier;
  public int freeModifiers;

  public final boolean broken;

  public static ModifierToolStatsBuilder from(ToolData toolData) {
    StatsNBT stats = toolData.getStats();

    return new ModifierToolStatsBuilder(stats.durability, stats.harvestLevel, stats.attack, stats.miningSpeed, stats.attackSpeedMultiplier, stats.freeModifiers, stats.broken);
  }

  public StatsNBT buildNewStats() {
    return new StatsNBT(
      durability,
      harvestLevel,
      attack,
      miningSpeed,
      attackSpeedMultiplier,
      freeModifiers,
      broken
    );
  }
}
