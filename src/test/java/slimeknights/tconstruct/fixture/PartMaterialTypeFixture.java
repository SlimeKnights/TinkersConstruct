package slimeknights.tconstruct.fixture;

import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class PartMaterialTypeFixture {

  public static final PartMaterialType PART_MATERIAL_TYPE = new PartMaterialType(
    MaterialItemFixture.MATERIAL_ITEM,
    MaterialStatsFixture.STATS_TYPE
  );

  public static final PartMaterialType PART_MATERIAL_TYPE_2 = new PartMaterialType(
    MaterialItemFixture.MATERIAL_ITEM_2,
    MaterialStatsFixture.STATS_TYPE_2
  );

  public static final PartMaterialType PART_MATERIAL_HEAD = new PartMaterialType(
    MaterialItemFixture.MATERIAL_ITEM_HEAD,
    HeadMaterialStats.ID
  );


  private PartMaterialTypeFixture() {
  }
}
