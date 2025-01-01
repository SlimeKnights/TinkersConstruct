package slimeknights.tconstruct.fixture;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

public class MaterialItemFixture {

  public static ToolPartItem MATERIAL_ITEM, MATERIAL_ITEM_2, MATERIAL_ITEM_HEAD, MATERIAL_ITEM_HANDLE, MATERIAL_ITEM_EXTRA;

  private MaterialItemFixture() {
  }

  private static boolean init = false;
  @SuppressWarnings("unchecked")  // its correct, and if it were to fail this is tests
  public static void init() {
    if (init) {
      return;
    }
    init = true;
    ((MappedRegistry<Item>)BuiltInRegistries.ITEM).unfreeze(); // yes, I know this is bad, but this is testing so we do bad things sometimes
    MATERIAL_ITEM = new ToolPartItem(new Item.Properties(), MaterialStatsFixture.STATS_TYPE);
    MATERIAL_ITEM_2 = new ToolPartItem(new Item.Properties(), MaterialStatsFixture.STATS_TYPE_2);
    MATERIAL_ITEM_HEAD = new ToolPartItem(new Item.Properties(), HeadMaterialStats.ID);
    MATERIAL_ITEM_HANDLE = new ToolPartItem(new Item.Properties(), HandleMaterialStats.ID);
    MATERIAL_ITEM_EXTRA = new ToolPartItem(new Item.Properties(), StatlessMaterialStats.BINDING.getIdentifier());
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "test_material"), MATERIAL_ITEM);
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "test_material_2"), MATERIAL_ITEM_2);
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "test_head"), MATERIAL_ITEM_HEAD);
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "test_handle"), MATERIAL_ITEM_HANDLE);
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "test_extra"), MATERIAL_ITEM_EXTRA);
  }
}
