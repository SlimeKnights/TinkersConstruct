package slimeknights.tconstruct.tables;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairKitRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeSerializer;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.tables.block.ChestBlock;
import slimeknights.tconstruct.tables.block.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.PartBuilderBlock;
import slimeknights.tconstruct.tables.block.ScorchedAnvilBlock;
import slimeknights.tconstruct.tables.block.TinkerStationBlock;
import slimeknights.tconstruct.tables.block.TinkersAnvilBlock;
import slimeknights.tconstruct.tables.block.TinkersChestBlock;
import slimeknights.tconstruct.tables.block.entity.chest.CastChestBlockEntity;
import slimeknights.tconstruct.tables.block.entity.chest.PartChestBlockEntity;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;
import slimeknights.tconstruct.tables.data.TableRecipeProvider;
import slimeknights.tconstruct.tables.item.TableBlockItem;
import slimeknights.tconstruct.tables.item.TinkersChestBlockItem;
import slimeknights.tconstruct.tables.menu.CraftingStationContainerMenu;
import slimeknights.tconstruct.tables.menu.PartBuilderContainerMenu;
import slimeknights.tconstruct.tables.menu.TinkerChestContainerMenu;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;
import slimeknights.tconstruct.tables.recipe.CraftingTableRepairKitRecipe;
import slimeknights.tconstruct.tables.recipe.TinkerStationDamagingRecipe;
import slimeknights.tconstruct.tables.recipe.TinkerStationPartSwapping;
import slimeknights.tconstruct.tables.recipe.TinkerStationRepairRecipe;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Handles all the table for tool creation
 */
@SuppressWarnings("unused")
public final class TinkerTables extends TinkerModule {

  /*
   * Blocks
   */
  private static final Block.Properties WOOD_TABLE = builder(Material.WOOD, SoundType.WOOD).strength(1.0F, 5.0F).noOcclusion();
  /** Call with .apply to set the tag type for a block item provider */
  private static final BiFunction<Tag<Item>,BooleanSupplier,Function<Block,RetexturedBlockItem>> RETEXTURED_BLOCK_ITEM = (tag, cond) -> block -> new TableBlockItem(block, tag, GENERAL_PROPS, cond);
  public static final ItemObject<TableBlock> craftingStation = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(WOOD_TABLE), RETEXTURED_BLOCK_ITEM.apply(ItemTags.LOGS, Config.COMMON.showAllTableVariants::get));
  public static final ItemObject<TableBlock> tinkerStation = BLOCKS.register("tinker_station", () -> new TinkerStationBlock(WOOD_TABLE, 4), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS, Config.COMMON.showAllTableVariants::get));
  public static final ItemObject<TableBlock> partBuilder = BLOCKS.register("part_builder", () -> new PartBuilderBlock(WOOD_TABLE), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS, Config.COMMON.showAllTableVariants::get));
  public static final ItemObject<TableBlock> tinkersChest = BLOCKS.register("tinkers_chest", () -> new TinkersChestBlock(WOOD_TABLE, TinkersChestBlockEntity::new, true), block -> new TinkersChestBlockItem(block, GENERAL_PROPS));
  public static final ItemObject<TableBlock> partChest = BLOCKS.register("part_chest", () -> new ChestBlock(WOOD_TABLE, PartChestBlockEntity::new, true), GENERAL_BLOCK_ITEM);

  private static final Block.Properties METAL_TABLE = builder(Material.HEAVY_METAL, SoundType.ANVIL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).noOcclusion();
  public static final ItemObject<TableBlock> tinkersAnvil = BLOCKS.register("tinkers_anvil", () -> new TinkersAnvilBlock(METAL_TABLE, 6), RETEXTURED_BLOCK_ITEM.apply(TinkerTags.Items.ANVIL_METAL, Config.COMMON.showAllAnvilVariants::get));
  public static final ItemObject<TableBlock> scorchedAnvil = BLOCKS.register("scorched_anvil", () -> new ScorchedAnvilBlock(METAL_TABLE, 6), RETEXTURED_BLOCK_ITEM.apply(TinkerTags.Items.ANVIL_METAL, Config.COMMON.showAllAnvilVariants::get));
  private static final Block.Properties STONE_TABLE = builder(Material.STONE, SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 9.0F).noOcclusion();
  public static final ItemObject<TableBlock> castChest = BLOCKS.register("cast_chest", () -> new ChestBlock(STONE_TABLE, CastChestBlockEntity::new, false), GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> pattern = ITEMS.register("pattern", GENERAL_PROPS);

  /*
   * Tile entites
   */
  public static final RegistryObject<BlockEntityType<CraftingStationBlockEntity>> craftingStationTile = BLOCK_ENTITIES.register("crafting_station", CraftingStationBlockEntity::new, craftingStation);
  public static final RegistryObject<BlockEntityType<TinkerStationBlockEntity>> tinkerStationTile = BLOCK_ENTITIES.register("tinker_station", TinkerStationBlockEntity::new, builder ->
    builder.add(tinkerStation.get(), tinkersAnvil.get(), scorchedAnvil.get()));
  public static final RegistryObject<BlockEntityType<PartBuilderBlockEntity>> partBuilderTile = BLOCK_ENTITIES.register("part_builder", PartBuilderBlockEntity::new, partBuilder);
  // legacy name as tile entities cannot be remapped
  public static final RegistryObject<BlockEntityType<TinkersChestBlockEntity>> tinkersChestTile = BLOCK_ENTITIES.register("modifier_chest", TinkersChestBlockEntity::new, tinkersChest);
  public static final RegistryObject<BlockEntityType<PartChestBlockEntity>> partChestTile = BLOCK_ENTITIES.register("part_chest", PartChestBlockEntity::new, partChest);
  public static final RegistryObject<BlockEntityType<CastChestBlockEntity>> castChestTile = BLOCK_ENTITIES.register("cast_chest", CastChestBlockEntity::new, castChest);

  /*
   * Containers
   */
  public static final RegistryObject<MenuType<CraftingStationContainerMenu>> craftingStationContainer = CONTAINERS.register("crafting_station", CraftingStationContainerMenu::new);
  public static final RegistryObject<MenuType<TinkerStationContainerMenu>> tinkerStationContainer = CONTAINERS.register("tinker_station", TinkerStationContainerMenu::new);
  public static final RegistryObject<MenuType<PartBuilderContainerMenu>> partBuilderContainer = CONTAINERS.register("part_builder", PartBuilderContainerMenu::new);
  public static final RegistryObject<MenuType<TinkerChestContainerMenu>> tinkerChestContainer = CONTAINERS.register("tinker_chest", TinkerChestContainerMenu::new);

  /*
   * Recipes
   */
  public static final RegistryObject<PartRecipe.Serializer> partRecipeSerializer = RECIPE_SERIALIZERS.register("part_builder", PartRecipe.Serializer::new);
  public static final RegistryObject<ItemPartRecipe.Serializer> itemPartBuilderSerializer = RECIPE_SERIALIZERS.register("item_part_builder", ItemPartRecipe.Serializer::new);
  public static final RegistryObject<MaterialRecipeSerializer> materialRecipeSerializer = RECIPE_SERIALIZERS.register("material", MaterialRecipeSerializer::new);
  public static final RegistryObject<ToolBuildingRecipeSerializer> toolBuildingRecipeSerializer = RECIPE_SERIALIZERS.register("tool_building", ToolBuildingRecipeSerializer::new);
  public static final RegistryObject<SimpleRecipeSerializer<TinkerStationRepairRecipe>> tinkerStationRepairSerializer = RECIPE_SERIALIZERS.register("tinker_station_repair", () -> new SimpleRecipeSerializer<>(TinkerStationRepairRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<CraftingTableRepairKitRecipe>> craftingTableRepairSerializer = RECIPE_SERIALIZERS.register("crafting_table_repair", () -> new SimpleRecipeSerializer<>(CraftingTableRepairKitRecipe::new));
  public static final RegistryObject<SpecializedRepairRecipeSerializer<?>> specializedRepairSerializer = RECIPE_SERIALIZERS.register("specialized_station_repair", () -> new SpecializedRepairRecipeSerializer<>(SpecializedRepairRecipe::new));
  public static final RegistryObject<SpecializedRepairRecipeSerializer<?>> specializedRepairKitSerializer = RECIPE_SERIALIZERS.register("specialized_repair_kit", () -> new SpecializedRepairRecipeSerializer<>(SpecializedRepairKitRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<TinkerStationPartSwapping>> tinkerStationPartSwappingSerializer = RECIPE_SERIALIZERS.register("tinker_station_part_swapping", () -> new SimpleRecipeSerializer<>(TinkerStationPartSwapping::new));
  public static final RegistryObject<TinkerStationDamagingRecipe.Serializer> tinkerStationDamagingSerializer = RECIPE_SERIALIZERS.register("tinker_station_damaging", TinkerStationDamagingRecipe.Serializer::new);

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      StationSlotLayoutLoader loader = StationSlotLayoutLoader.getInstance();
      loader.registerRequiredLayout(tinkerStation.getRegistryName());
      loader.registerRequiredLayout(tinkersAnvil.getRegistryName());
      loader.registerRequiredLayout(scorchedAnvil.getRegistryName());
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new TableRecipeProvider(datagenerator));
    }
  }
}
