package slimeknights.tconstruct.fixture;

import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.CommonMaterialStats;

public final class MaterialStatsFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "mat_stat_1");
  public static final MaterialStatsId STATS_TYPE_2 = new MaterialStatsId("test", "mat_stat_2");
  public static final IMaterialStats MATERIAL_STATS = new CommonMaterialStats();
  public static final IMaterialStats MATERIAL_STATS_2 = new CommonMaterialStats();

  private MaterialStatsFixture() {
  }
}
