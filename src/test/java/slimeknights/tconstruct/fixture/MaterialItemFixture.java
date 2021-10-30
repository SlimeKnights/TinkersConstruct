package slimeknights.tconstruct.fixture;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
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

  private static boolean init = false;
  public static void init() {
    if (init) {
      return;
    }
    init = true;
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM.setRegistryName(new ResourceLocation("test", "test_material")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_2.setRegistryName(new ResourceLocation("test", "test_material_2")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_HEAD.setRegistryName(new ResourceLocation("test", "test_head")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_HANDLE.setRegistryName(new ResourceLocation("test", "test_handle")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_EXTRA.setRegistryName(new ResourceLocation("test", "test_extra")));
  }
}
