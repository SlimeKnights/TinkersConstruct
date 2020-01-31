package slimeknights.tconstruct.fixture;

import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.ComplexTestStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public final class MaterialStatsFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "mat_stat_1");
  public static final MaterialStatsId STATS_TYPE_2 = new MaterialStatsId("test", "mat_stat_2");
  public static final BaseMaterialStats MATERIAL_STATS = new ComplexTestStats(STATS_TYPE, 1, 2, "3");
  public static final BaseMaterialStats MATERIAL_STATS_2 = new ComplexTestStats(STATS_TYPE_2, 4, 5, "6");

  private MaterialStatsFixture() {
  }
}
