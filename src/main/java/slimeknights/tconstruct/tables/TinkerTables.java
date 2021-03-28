package slimeknights.tconstruct.tables;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeSerializer;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.tables.block.TinkerChestBlock;
import slimeknights.tconstruct.tables.block.table.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.table.PartBuilderBlock;
import slimeknights.tconstruct.tables.block.table.TinkerStationBlock;
import slimeknights.tconstruct.tables.block.table.TinkersAnvilBlock;
import slimeknights.tconstruct.tables.data.TableRecipeProvider;
import slimeknights.tconstruct.tables.inventory.TinkerChestContainer;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.inventory.table.partbuilder.PartBuilderContainer;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;
import slimeknights.tconstruct.tables.recipe.TinkerStationPartSwapping;
import slimeknights.tconstruct.tables.recipe.TinkerStationRepairRecipe;
import slimeknights.tconstruct.tables.tileentity.chest.CastChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.ModifierChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import java.util.function.Function;

/**
 * Handles all the table for tool creation
 */
@SuppressWarnings("unused")
public final class TinkerTables extends TinkerModule {

  /*
   * Blocks
   */
  private static final Block.Properties WOOD_TABLE = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).hardnessAndResistance(1.0F, 5.0F).notSolid();
  /** Call with .apply to set the tag type for a block item provider */
  private static final Function<ITag<Item>, Function<Block,RetexturedBlockItem>> RETEXTURED_BLOCK_ITEM = (tag) -> (block) -> new RetexturedBlockItem(block, tag, GENERAL_PROPS);
  public static final ItemObject<TableBlock> craftingStation = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(WOOD_TABLE), GENERAL_BLOCK_ITEM);
  public static final ItemObject<TableBlock> tinkerStation = BLOCKS.register("tinker_station", () -> new TinkerStationBlock(WOOD_TABLE, 4), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS));
  public static final ItemObject<TableBlock> partBuilder = BLOCKS.register("part_builder", () -> new PartBuilderBlock(WOOD_TABLE), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS));
  public static final ItemObject<TableBlock> modifierChest = BLOCKS.register("modifier_chest", () -> new TinkerChestBlock(WOOD_TABLE, ModifierChestTileEntity::new), GENERAL_BLOCK_ITEM);
  public static final ItemObject<TableBlock> partChest = BLOCKS.register("part_chest", () -> new TinkerChestBlock(WOOD_TABLE, PartChestTileEntity::new), GENERAL_BLOCK_ITEM);

  private static final Block.Properties METAL_TABLE = builder(Material.ANVIL, ToolType.PICKAXE, SoundType.ANVIL).setRequiresTool().hardnessAndResistance(5.0F, 1200.0F).notSolid();
  public static final ItemObject<TableBlock> tinkersAnvil = BLOCKS.register("tinkers_anvil", () -> new TinkersAnvilBlock(METAL_TABLE, 6), RETEXTURED_BLOCK_ITEM.apply(TinkerTags.Items.ANVIL_METAL));
  private static final Block.Properties STONE_TABLE = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(3.0F, 9.0F).notSolid();
  public static final ItemObject<TableBlock> castChest = BLOCKS.register("cast_chest", () -> new TinkerChestBlock(STONE_TABLE, CastChestTileEntity::new), GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> pattern = ITEMS.register("pattern", GENERAL_PROPS);

  /*
   * Tile entites
   */
  public static final RegistryObject<TileEntityType<CraftingStationTileEntity>> craftingStationTile = TILE_ENTITIES.register("crafting_station", CraftingStationTileEntity::new, craftingStation);
  public static final RegistryObject<TileEntityType<TinkerStationTileEntity>> tinkerStationTile = TILE_ENTITIES.register("tinker_station", TinkerStationTileEntity::new, builder -> {
    builder.add(tinkerStation.get(), tinkersAnvil.get());
  });
  public static final RegistryObject<TileEntityType<PartBuilderTileEntity>> partBuilderTile = TILE_ENTITIES.register("part_builder", PartBuilderTileEntity::new, partBuilder);
  public static final RegistryObject<TileEntityType<ModifierChestTileEntity>> modifierChestTile = TILE_ENTITIES.register("modifier_chest", ModifierChestTileEntity::new, modifierChest);
  public static final RegistryObject<TileEntityType<PartChestTileEntity>> partChestTile = TILE_ENTITIES.register("part_chest", PartChestTileEntity::new, partChest);
  public static final RegistryObject<TileEntityType<CastChestTileEntity>> castChestTile = TILE_ENTITIES.register("cast_chest", CastChestTileEntity::new, castChest);

  /*
   * Containers
   */
  public static final RegistryObject<ContainerType<CraftingStationContainer>> craftingStationContainer = CONTAINERS.register("crafting_station", CraftingStationContainer::new);
  public static final RegistryObject<ContainerType<TinkerStationContainer>> tinkerStationContainer = CONTAINERS.register("tinker_station", TinkerStationContainer::new);
  public static final RegistryObject<ContainerType<PartBuilderContainer>> partBuilderContainer = CONTAINERS.register("part_builder", PartBuilderContainer::new);
  public static final RegistryObject<ContainerType<TinkerChestContainer>> tinkerChestContainer = CONTAINERS.register("tinker_chest", TinkerChestContainer::new);

  /*
   * Recipes
   */
  public static final RegistryObject<PartRecipeSerializer> partRecipeSerializer = RECIPE_SERIALIZERS.register("part_builder", PartRecipeSerializer::new);
  public static final RegistryObject<MaterialRecipeSerializer> materialRecipeSerializer = RECIPE_SERIALIZERS.register("material", MaterialRecipeSerializer::new);
  public static final RegistryObject<ToolBuildingRecipeSerializer> toolBuildingRecipeSerializer = RECIPE_SERIALIZERS.register("tool_building", ToolBuildingRecipeSerializer::new);
  public static final RegistryObject<SpecialRecipeSerializer<TinkerStationRepairRecipe>> tinkerStationRepairSerializer = RECIPE_SERIALIZERS.register("tinker_station_repair", () -> new SpecialRecipeSerializer<>(TinkerStationRepairRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<TinkerStationPartSwapping>> tinkerStationPartSwappingSerializer = RECIPE_SERIALIZERS.register("tinker_station_part_swapping", () -> new SpecialRecipeSerializer<>(TinkerStationPartSwapping::new));

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new TableRecipeProvider(datagenerator));
    }
  }
}
