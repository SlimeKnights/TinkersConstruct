package slimeknights.tconstruct.fixture;

import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public final class MaterialStatsFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "mat_stat_1");
  public static final MaterialStatsId STATS_TYPE_2 = new MaterialStatsId("test", "mat_stat_2");
  public static final IMaterialStats MATERIAL_STATS = new BaseMaterialStats();
  public static final IMaterialStats MATERIAL_STATS_2 = new BaseMaterialStats();

  private MaterialStatsFixture() {
  }
}
