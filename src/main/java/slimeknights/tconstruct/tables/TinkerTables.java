package slimeknights.tconstruct.tables;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeSerializer;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.tables.block.chest.PartChestBlock;
import slimeknights.tconstruct.tables.block.chest.PatternChestBlock;
import slimeknights.tconstruct.tables.block.table.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.table.PartBuilderBlock;
import slimeknights.tconstruct.tables.block.table.TinkerStationBlock;
import slimeknights.tconstruct.tables.data.TableRecipeProvider;
import slimeknights.tconstruct.tables.inventory.chest.PartChestContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.inventory.table.partbuilder.PartBuilderContainer;
import slimeknights.tconstruct.tables.inventory.table.tinkerstation.TinkerStationContainer;
import slimeknights.tconstruct.tables.item.RetexturedTableBlockItem;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import java.util.function.Function;

/**
 * Handles all the table for tool creation
 */
public final class TinkerTables extends TinkerModule {
  /*
   * Blocks
   */
  private static final Block.Properties TOOL_TABLE = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).hardnessAndResistance(1.0F, 5.0F).notSolid();
  /** Call with .apply to set the tag type for a block item provider */
  private static final Function<ITag<Item>, Function<Block, RetexturedTableBlockItem>> RETEXTURED_BLOCK_ITEM = (tag) -> (block) -> new RetexturedTableBlockItem(block, tag, GENERAL_PROPS);
  public static final ItemObject<TableBlock> craftingStation = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);
  public static final ItemObject<TableBlock> tinkerStation = BLOCKS.register("tinker_station", () -> new TinkerStationBlock(TOOL_TABLE), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS));
  public static final ItemObject<TableBlock> partBuilder = BLOCKS.register("part_builder", () -> new PartBuilderBlock(TOOL_TABLE), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS));
  public static final ItemObject<TableBlock> patternChest = BLOCKS.register("pattern_chest", () -> new PatternChestBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);
  public static final ItemObject<TableBlock> partChest = BLOCKS.register("part_chest", () -> new PartChestBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> pattern = ITEMS.register("pattern", GENERAL_PROPS);

  /*
   * Tile entites
   */
  public static final RegistryObject<TileEntityType<CraftingStationTileEntity>> craftingStationTile = TILE_ENTITIES.register("crafting_station", CraftingStationTileEntity::new, craftingStation);
  public static final RegistryObject<TileEntityType<TinkerStationTileEntity>> tinkerStationTile = TILE_ENTITIES.register("tinker_station", TinkerStationTileEntity::new, tinkerStation);
  public static final RegistryObject<TileEntityType<PartBuilderTileEntity>> partBuilderTile = TILE_ENTITIES.register("part_builder", PartBuilderTileEntity::new, partBuilder);
  public static final RegistryObject<TileEntityType<PatternChestTileEntity>> patternChestTile = TILE_ENTITIES.register("pattern_chest", PatternChestTileEntity::new, patternChest);
  public static final RegistryObject<TileEntityType<PartChestTileEntity>> partChestTile = TILE_ENTITIES.register("part_chest", PartChestTileEntity::new, partChest);

  /*
   * Containers
   */
  public static final RegistryObject<ContainerType<CraftingStationContainer>> craftingStationContainer = CONTAINERS.register("crafting_station", CraftingStationContainer::new);
  public static final RegistryObject<ContainerType<TinkerStationContainer>> tinkerStationContainer = CONTAINERS.register("tinker_station", TinkerStationContainer::new);
  public static final RegistryObject<ContainerType<PartBuilderContainer>> partBuilderContainer = CONTAINERS.register("part_builder", PartBuilderContainer::new);
  public static final RegistryObject<ContainerType<PatternChestContainer>> patternChestContainer = CONTAINERS.register("pattern_chest", PatternChestContainer::new);
  public static final RegistryObject<ContainerType<PartChestContainer>> partChestContainer = CONTAINERS.register("part_chest", PartChestContainer::new);

  /*
   * Recipes
   */
  public static final RegistryObject<PartRecipeSerializer> partRecipeSerializer = RECIPE_SERIALIZERS.register("part_builder", PartRecipeSerializer::new);
  public static final RegistryObject<MaterialRecipeSerializer> materialRecipeSerializer = RECIPE_SERIALIZERS.register("material", MaterialRecipeSerializer::new);
  public static final RegistryObject<ToolBuildingRecipeSerializer> tinkerableBuildingRecipeSerializer = RECIPE_SERIALIZERS.register("tinkerable_building", ToolBuildingRecipeSerializer::new);

  @SuppressWarnings("unused")
  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new TableRecipeProvider(datagenerator));
    }
  }
}
