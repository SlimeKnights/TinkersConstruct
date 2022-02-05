package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.ComplexTestStats;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

public final class MaterialStatsFixture {

  public static final MaterialStatsId STATS_TYPE = new MaterialStatsId("test", "mat_stat_1");
  public static final MaterialStatsId STATS_TYPE_2 = new MaterialStatsId("test", "mat_stat_2");
  public static final MaterialStatsId STATS_TYPE_3 = new MaterialStatsId("test", "mat_stat_3");
  public static final MaterialStatsId STATS_TYPE_4 = new MaterialStatsId("test", "mat_stat_4");

  public static final BaseMaterialStats MATERIAL_STATS = new ComplexTestStats(STATS_TYPE, 1, 2, "3");
  public static final BaseMaterialStats MATERIAL_STATS_2 = new ComplexTestStats(STATS_TYPE_2, 4, 5, "6");

  public static final HeadMaterialStats MATERIAL_STATS_HEAD = new HeadMaterialStats(100, 1f, Tiers.GOLD, 1f);
  public static final HandleMaterialStats MATERIAL_STATS_HANDLE = new HandleMaterialStats(1.5f, 1.0f, 1.0f, 1.0f);
  public static final ExtraMaterialStats MATERIAL_STATS_EXTRA = ExtraMaterialStats.DEFAULT;

  public static final List<IMaterialStats> TIC_DEFAULT_STATS = ImmutableList.of(
    HeadMaterialStats.DEFAULT,
    HandleMaterialStats.DEFAULT,
    ExtraMaterialStats.DEFAULT
  );

  private MaterialStatsFixture() {
  }
}
