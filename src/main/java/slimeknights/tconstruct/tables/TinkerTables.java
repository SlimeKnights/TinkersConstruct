package slimeknights.tconstruct.tables;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeSerializer;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.tables.block.TableBlock;
import slimeknights.tconstruct.tables.block.chest.PartChestBlock;
import slimeknights.tconstruct.tables.block.chest.PatternChestBlock;
import slimeknights.tconstruct.tables.block.table.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.table.PartBuilderBlock;
import slimeknights.tconstruct.tables.block.table.ToolStationBlock;
import slimeknights.tconstruct.tables.inventory.chest.PartChestContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;
import slimeknights.tconstruct.tables.inventory.table.partbuilder.PartBuilderContainer;
import slimeknights.tconstruct.tables.inventory.table.toolstation.ToolStationContainer;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.ToolStationTileEntity;

/**
 * Handles all the table for tool creation
 */
public final class TinkerTables extends TinkerModule {
  /*
   * Blocks
   */
  private static final Block.Properties TOOL_TABLE = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).hardnessAndResistance(1.0F, 5.0F).notSolid();
  public static final BlockItemObject<TableBlock> craftingStation = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);
  public static final BlockItemObject<TableBlock> partBuilder = BLOCKS.register("part_builder", () -> new PartBuilderBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);
  public static final BlockItemObject<TableBlock> patternChest = BLOCKS.register("pattern_chest", () -> new PatternChestBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);
  public static final BlockItemObject<TableBlock> partChest = BLOCKS.register("part_chest", () -> new PartChestBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);
  public static final BlockItemObject<TableBlock> toolStation = BLOCKS.register("tool_station", () -> new ToolStationBlock(TOOL_TABLE), GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> pattern = ITEMS.register("pattern", GENERAL_PROPS);

  /*
   * Tile entites
   */
  public static final RegistryObject<TileEntityType<CraftingStationTileEntity>> craftingStationTile = TILE_ENTITIES.register("crafting_station", CraftingStationTileEntity::new, craftingStation);
  public static final RegistryObject<TileEntityType<PartBuilderTileEntity>> partBuilderTile = TILE_ENTITIES.register("part_builder", PartBuilderTileEntity::new, partBuilder);
  public static final RegistryObject<TileEntityType<PatternChestTileEntity>> patternChestTile = TILE_ENTITIES.register("pattern_chest", PatternChestTileEntity::new, patternChest);
  public static final RegistryObject<TileEntityType<PartChestTileEntity>> partChestTile = TILE_ENTITIES.register("part_chest", PartChestTileEntity::new, partChest);
  public static final RegistryObject<TileEntityType<ToolStationTileEntity>> toolStationTile = TILE_ENTITIES.register("tool_station", ToolStationTileEntity::new, toolStation);

  /*
   * Containers
   */
  public static final RegistryObject<ContainerType<CraftingStationContainer>> craftingStationContainer = CONTAINERS.register("crafting_station", CraftingStationContainer::new);
  public static final RegistryObject<ContainerType<PartBuilderContainer>> partBuilderContainer = CONTAINERS.register("part_builder", PartBuilderContainer::new);
  public static final RegistryObject<ContainerType<PatternChestContainer>> patternChestContainer = CONTAINERS.register("pattern_chest", PatternChestContainer::new);
  public static final RegistryObject<ContainerType<PartChestContainer>> partChestContainer = CONTAINERS.register("part_chest", PartChestContainer::new);
  public static final RegistryObject<ContainerType<ToolStationContainer>> toolStationContainer = CONTAINERS.register("tool_station", ToolStationContainer::new);

  /*
   * Recipes
   */
  public static final RegistryObject<PartRecipeSerializer<PartRecipe>> partRecipeSerializer = RECIPE_SERIALIZERS.register("part_builder", () -> new PartRecipeSerializer<>(PartRecipe::new));
  public static final RegistryObject<MaterialRecipeSerializer<MaterialRecipe>> materialRecipeSerializer = RECIPE_SERIALIZERS.register("material", () -> new MaterialRecipeSerializer<>(MaterialRecipe::new));
}
