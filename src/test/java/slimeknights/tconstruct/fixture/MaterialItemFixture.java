package slimeknights.tconstruct.fixture;

import net.minecraft.item.Item;
import slimeknights.tconstruct.library.tools.item.ToolPartItem;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class MaterialItemFixture {

  public static final ToolPartItem MATERIAL_ITEM = new ToolPartItem(new Item.Properties(), MaterialStatsFixture.STATS_TYPE);
  public static final ToolPartItem MATERIAL_ITEM_2 = new ToolPartItem(new Item.Properties(), MaterialStatsFixture.STATS_TYPE_2);

  public static final ToolPartItem MATERIAL_ITEM_HEAD = new ToolPartItem(new Item.Properties(), HeadMaterialStats.ID);
  public static final ToolPartItem MATERIAL_ITEM_HANDLE = new ToolPartItem(new Item.Properties(), HandleMaterialStats.ID);
  public static final ToolPartItem MATERIAL_ITEM_EXTRA = new ToolPartItem(new Item.Properties(), ExtraMaterialStats.ID);

  private MaterialItemFixture() {
  }
}
