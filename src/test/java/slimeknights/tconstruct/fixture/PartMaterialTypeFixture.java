package slimeknights.tconstruct.fixture;

import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class PartMaterialTypeFixture {

  public static final PartMaterialRequirement PART_MATERIAL_TYPE = new PartMaterialRequirement(
    () -> MaterialItemFixture.MATERIAL_ITEM,
    MaterialStatsFixture.STATS_TYPE
  );

  public static final PartMaterialRequirement PART_MATERIAL_TYPE_2 = new PartMaterialRequirement(
    () -> MaterialItemFixture.MATERIAL_ITEM_2,
    MaterialStatsFixture.STATS_TYPE_2
  );

  public static final PartMaterialRequirement PART_MATERIAL_HEAD = new PartMaterialRequirement(
    () -> MaterialItemFixture.MATERIAL_ITEM_HEAD,
    HeadMaterialStats.ID
  );


  private PartMaterialTypeFixture() {
  }
}
