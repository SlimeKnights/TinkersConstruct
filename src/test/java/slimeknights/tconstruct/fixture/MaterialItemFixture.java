package slimeknights.tconstruct.fixture;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class MaterialItemFixture {

  public static ToolPartItem MATERIAL_ITEM, MATERIAL_ITEM_2, MATERIAL_ITEM_HEAD, MATERIAL_ITEM_HANDLE, MATERIAL_ITEM_EXTRA;

  private MaterialItemFixture() {
  }

  private static boolean init = false;
  public static void init() {
    if (init) {
      return;
    }
    init = true;
    Registry.ITEM.unfreeze(); // yes, I know this is bad, but this is testing so we do bad things sometimes
    MATERIAL_ITEM = new ToolPartItem(new Item.Properties(), MaterialStatsFixture.STATS_TYPE);
    MATERIAL_ITEM_2 = new ToolPartItem(new Item.Properties(), MaterialStatsFixture.STATS_TYPE_2);
    MATERIAL_ITEM_HEAD = new ToolPartItem(new Item.Properties(), HeadMaterialStats.ID);
    MATERIAL_ITEM_HANDLE = new ToolPartItem(new Item.Properties(), HandleMaterialStats.ID);
    MATERIAL_ITEM_EXTRA = new ToolPartItem(new Item.Properties(), ExtraMaterialStats.ID);
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM.setRegistryName(new ResourceLocation("test", "test_material")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_2.setRegistryName(new ResourceLocation("test", "test_material_2")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_HEAD.setRegistryName(new ResourceLocation("test", "test_head")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_HANDLE.setRegistryName(new ResourceLocation("test", "test_handle")));
    ForgeRegistries.ITEMS.register(MATERIAL_ITEM_EXTRA.setRegistryName(new ResourceLocation("test", "test_extra")));
  }
}
