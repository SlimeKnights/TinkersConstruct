package slimeknights.tconstruct.tables;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeSerializer;
import slimeknights.tconstruct.tables.block.TinkerChestBlock;
import slimeknights.tconstruct.tables.block.table.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.table.PartBuilderBlock;
import slimeknights.tconstruct.tables.block.table.TinkerStationBlock;
import slimeknights.tconstruct.tables.block.table.TinkersAnvilBlock;
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
  private static final AbstractBlock.Settings WOOD_TABLE = builder(Material.WOOD, FabricToolTags.AXES, BlockSoundGroup.WOOD).strength(1.0F, 5.0F).nonOpaque();
  /** Call with .apply to set the tag type for a block item provider */
  private static final Function<Tag<Item>, Function<Block,RetexturedBlockItem>> RETEXTURED_BLOCK_ITEM = (tag) -> (block) -> new RetexturedBlockItem(block, tag, GENERAL_PROPS);
  public static final ItemObject<Block> craftingStation = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(WOOD_TABLE), GENERAL_BLOCK_ITEM);
  public static final ItemObject<Block> tinkerStation = BLOCKS.register("tinker_station", () -> new TinkerStationBlock(WOOD_TABLE, 4), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS));
  public static final ItemObject<Block> partBuilder = BLOCKS.register("part_builder", () -> new PartBuilderBlock(WOOD_TABLE), RETEXTURED_BLOCK_ITEM.apply(ItemTags.PLANKS));
  public static final ItemObject<Block> modifierChest = BLOCKS.register("modifier_chest", () -> new TinkerChestBlock(WOOD_TABLE, ModifierChestTileEntity::new), GENERAL_BLOCK_ITEM);
  public static final ItemObject<Block> partChest = BLOCKS.register("part_chest", () -> new TinkerChestBlock(WOOD_TABLE, PartChestTileEntity::new), GENERAL_BLOCK_ITEM);

  private static final Block.Settings METAL_TABLE = builder(Material.REPAIR_STATION, FabricToolTags.PICKAXES, BlockSoundGroup.ANVIL).requiresTool().strength(5.0F, 1200.0F).nonOpaque();
  public static final ItemObject<Block> tinkersAnvil = BLOCKS.register("tinkers_anvil", () -> new TinkersAnvilBlock(METAL_TABLE, 6), RETEXTURED_BLOCK_ITEM.apply(TinkerTags.Items.ANVIL_METAL));
  private static final Block.Settings STONE_TABLE = builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(3.0F, 9.0F).nonOpaque();
  public static final ItemObject<Block> castChest = BLOCKS.register("cast_chest", () -> new TinkerChestBlock(STONE_TABLE, CastChestTileEntity::new), GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> pattern = ITEMS.register("pattern", GENERAL_PROPS);

  /*
   * Tile entites
   */
  public static final BlockEntityType<CraftingStationTileEntity> craftingStationTile = TILE_ENTITIES.register("crafting_station", CraftingStationTileEntity::new, craftingStation);
  public static final BlockEntityType<TinkerStationTileEntity> tinkerStationTile = TILE_ENTITIES.register("tinker_station", TinkerStationTileEntity::new, builder -> builder.add(tinkerStation.get(), tinkersAnvil.get()));
  public static final BlockEntityType<PartBuilderTileEntity> partBuilderTile = TILE_ENTITIES.register("part_builder", PartBuilderTileEntity::new, partBuilder);
  public static final BlockEntityType<ModifierChestTileEntity> modifierChestTile = TILE_ENTITIES.register("modifier_chest", ModifierChestTileEntity::new, modifierChest);
  public static final BlockEntityType<PartChestTileEntity> partChestTile = TILE_ENTITIES.register("part_chest", PartChestTileEntity::new, partChest);
  public static final BlockEntityType<CastChestTileEntity> castChestTile = TILE_ENTITIES.register("cast_chest", CastChestTileEntity::new, castChest);

  /*
   * Containers
   */
  public static final ScreenHandlerType<CraftingStationContainer> craftingStationContainer = CONTAINERS.register(id("crafting_station"), CraftingStationContainer::new);
  public static final ScreenHandlerType<TinkerStationContainer> tinkerStationContainer = CONTAINERS.register(id("tinker_station"), TinkerStationContainer::new);
  public static final ScreenHandlerType<PartBuilderContainer> partBuilderContainer = CONTAINERS.register(id("part_builder"), PartBuilderContainer::new);
  public static final ScreenHandlerType<TinkerChestContainer> tinkerChestContainer = CONTAINERS.register(id("tinker_chest"), TinkerChestContainer::new);

  /*
   * Recipes
   */
  public static final PartRecipeSerializer partRecipeSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("part_builder"), PartRecipeSerializer::new);
  public static final MaterialRecipeSerializer materialRecipeSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("material"), MaterialRecipeSerializer::new);
  public static final ToolBuildingRecipeSerializer toolBuildingRecipeSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("tool_building"), ToolBuildingRecipeSerializer::new);
  public static final SpecialRecipeSerializer<TinkerStationRepairRecipe> tinkerStationRepairSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("tinker_station_repair"), new SpecialRecipeSerializer<>(TinkerStationRepairRecipe::new));
  public static final SpecialRecipeSerializer<TinkerStationPartSwapping> tinkerStationPartSwappingSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("tinker_station_part_swapping"), new SpecialRecipeSerializer<>(TinkerStationPartSwapping::new));

  @Override
  public void onInitialize() {
  }
}
