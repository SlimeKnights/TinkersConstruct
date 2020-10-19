package slimeknights.tconstruct.library.modifiers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

@With
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public final class ModifierToolStatsBuilder {

  public final int durability;
  public final int harvestLevel;
  public final float attack;
  public final float miningSpeed;
  public final float attackSpeedMultiplier;
  public final int freeModifiers;

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
