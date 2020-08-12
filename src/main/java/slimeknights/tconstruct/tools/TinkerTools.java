package slimeknights.tconstruct.tools;

import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.utils.SupplierItemGroup;
import slimeknights.tconstruct.tools.data.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all complete tool items
 */
@SuppressWarnings("unused")
public final class TinkerTools extends TinkerModule {
  /** Creative tab for all tool items */
  public static final ItemGroup TAB_TOOLS = new SupplierItemGroup(TConstruct.modID, "tools", () -> {
    List<IMaterial> materials = new ArrayList<>(MaterialRegistry.getInstance().getMaterials());
    if (materials.isEmpty()) {
      return new ItemStack(TinkerTools.pickaxe);
    }
    // TODO: use ToolCore::buildToolForRendering
    List<IMaterial> toolMats = new ArrayList<>(3);
    for (int i = 0; i < 3; i++) {
      toolMats.add(materials.get(TConstruct.random.nextInt(materials.size())));
    }
    return ToolBuildHandler.buildItemFromMaterials(TinkerTools.pickaxe.get(), toolMats);
  });

  /*
   * Items
   */
  public static final ItemObject<PickaxeTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.PICKAXE));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<IndestructibleEntityItem>> indestructibleItem = ENTITIES.register("indestructible_item", () -> {
    return EntityType.Builder.<IndestructibleEntityItem>create(IndestructibleEntityItem::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .immuneToFire();
  });

  /*
   * Events
   */

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new ToolsRecipeProvider(datagenerator));
      datagenerator.addProvider(new MaterialDataProvider(datagenerator));
      datagenerator.addProvider(new MaterialStatsDataProvider(datagenerator));
    }
  }
}
