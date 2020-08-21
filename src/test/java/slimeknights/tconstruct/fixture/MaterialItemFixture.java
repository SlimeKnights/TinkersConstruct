package slimeknights.tconstruct.fixture;

import net.minecraft.item.Item;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.ToolPartItem;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class MaterialItemFixture {

  public static final MaterialItem MATERIAL_ITEM = new MaterialItem(new Item.Properties());
  public static final MaterialItem MATERIAL_ITEM_2 = new MaterialItem(new Item.Properties());

  public static final ToolPartItem MATERIAL_ITEM_HEAD = new ToolPartItem(new Item.Properties(), HeadMaterialStats.ID);
  public static final ToolPartItem MATERIAL_ITEM_HANDLE = new ToolPartItem(new Item.Properties(), HandleMaterialStats.ID);
  public static final ToolPartItem MATERIAL_ITEM_EXTRA = new ToolPartItem(new Item.Properties(), ExtraMaterialStats.ID);

  private MaterialItemFixture() {
  }
}
