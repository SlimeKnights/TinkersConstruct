package slimeknights.tconstruct.fixture;

import net.minecraft.item.Item;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public final class PartMaterialTypeFixture {

  public static PartMaterialType getTestPartMaterialType() {
    return new PartMaterialType(
      new MaterialItem(new Item.Properties()),
      new MaterialStatsId("test", "mat_stat")
    );
  }

  private PartMaterialTypeFixture() {
  }
}
