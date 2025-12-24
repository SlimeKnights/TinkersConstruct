package slimeknights.tconstruct.tables;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.SimpleRecipeSerializer;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.ShapedMaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.ShapedMaterialsRecipe;
import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderRecycle;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.tables.block.ChestBlock;
import slimeknights.tconstruct.tables.block.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.GenericTableBlock;
import slimeknights.tconstruct.tables.block.ScorchedAnvilBlock;
import slimeknights.tconstruct.tables.block.TinkerStationBlock;
import slimeknights.tconstruct.tables.block.TinkersAnvilBlock;
import slimeknights.tconstruct.tables.block.TinkersChestBlock;
import slimeknights.tconstruct.tables.block.entity.chest.CastChestBlockEntity;
import slimeknights.tconstruct.tables.block.entity.chest.PartChestBlockEntity;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.ModifierWorktableBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;
import slimeknights.tconstruct.tables.data.TableRecipeProvider;
import slimeknights.tconstruct.tables.item.AnvilBlockItem;
import slimeknights.tconstruct.tables.item.TinkersChestBlockItem;
import slimeknights.tconstruct.tables.menu.CraftingStationContainerMenu;
import slimeknights.tconstruct.tables.menu.ModifierWorktableContainerMenu;
import slimeknights.tconstruct.tables.menu.PartBuilderContainerMenu;
import slimeknights.tconstruct.tables.menu.TinkerChestContainerMenu;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;
import slimeknights.tconstruct.tables.recipe.CraftingTableRepairKitRecipe;
import slimeknights.tconstruct.tables.recipe.PartBuilderToolRecycle;
import slimeknights.tconstruct.tables.recipe.TinkerStationDamagingRecipe;
import slimeknights.tconstruct.tables.recipe.TinkerStationPartSwapping;
import slimeknights.tconstruct.tables.recipe.TinkerStationRepairRecipe;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Handles all the table for tool creation
 */
@SuppressWarnings("unused")
public final class TinkerTables extends TinkerModule {
  /** Creative tab for general items, or those that lack another tab */
  public static final RegistryObject<CreativeModeTab> tabTables = CREATIVE_TABS.register(
    "tables", () -> CreativeModeTab.builder().title(TConstruct.makeTranslation("itemGroup", "tables"))
                                   .icon(() -> new ItemStack(TinkerTables.craftingStation))
                                   .displayItems(TinkerTables::addTabItems)
                                   .withTabsBefore(TinkerCommons.tabGeneral.getId())
                                   .build());
  /*
   * Blocks
   */
  public static final ItemObject<TableBlock> craftingStation, tinkerStation, partBuilder, tinkersChest, partChest;
  static {
    Block.Properties WOOD_TABLE = builder(MapColor.WOOD, SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.0F, 5.0F).noOcclusion();
    craftingStation = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(WOOD_TABLE), BLOCK_ITEM);
    tinkerStation = BLOCKS.register("tinker_station", () -> new TinkerStationBlock(WOOD_TABLE, 4), BLOCK_ITEM);
    partBuilder = BLOCKS.register("part_builder", () -> new GenericTableBlock(WOOD_TABLE, PartBuilderBlockEntity::new), BLOCK_ITEM);
    tinkersChest = BLOCKS.register("tinkers_chest", () -> new TinkersChestBlock(WOOD_TABLE, TinkersChestBlockEntity::new, true), block -> new TinkersChestBlockItem(block, ITEM_PROPS));
    partChest = BLOCKS.register("part_chest", () -> new ChestBlock(WOOD_TABLE, PartChestBlockEntity::new, true), BLOCK_ITEM);
  }

  public static final ItemObject<TableBlock> castChest, modifierWorktable;
  static {
    Block.Properties STONE_TABLE = builder(MapColor.COLOR_GRAY, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 9.0F).noOcclusion();
    castChest = BLOCKS.register("cast_chest", () -> new ChestBlock(STONE_TABLE, CastChestBlockEntity::new, false), BLOCK_ITEM);
    modifierWorktable = BLOCKS.register("modifier_worktable", () -> new GenericTableBlock(STONE_TABLE, ModifierWorktableBlockEntity::new), BLOCK_ITEM);
  }

  public static final ItemObject<TableBlock> tinkersAnvil, scorchedAnvil;
  static {
    Block.Properties METAL_TABLE = builder(MapColor.COLOR_GRAY, SoundType.ANVIL).pushReaction(PushReaction.BLOCK).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).noOcclusion();
    Function<Block, BlockItem> blockItem = block -> new AnvilBlockItem(block, ITEM_PROPS, TinkerToolParts.fakeStorageBlockItem);
    tinkersAnvil = BLOCKS.register("tinkers_anvil", () -> new TinkersAnvilBlock(METAL_TABLE, 6), blockItem);
    scorchedAnvil = BLOCKS.register("scorched_anvil", () -> new ScorchedAnvilBlock(METAL_TABLE, 6), blockItem);
  }
  /*
   * Items
   */
  public static final ItemObject<Item> pattern = ITEMS.register("pattern", ITEM_PROPS);

  /*
   * Tile entites
   */
  public static final RegistryObject<BlockEntityType<CraftingStationBlockEntity>> craftingStationTile = BLOCK_ENTITIES.register("crafting_station", CraftingStationBlockEntity::new, craftingStation);
  public static final RegistryObject<BlockEntityType<TinkerStationBlockEntity>> tinkerStationTile = BLOCK_ENTITIES.register("tinker_station", TinkerStationBlockEntity::new, builder ->
    builder.add(tinkerStation.get(), tinkersAnvil.get(), scorchedAnvil.get()));
  public static final RegistryObject<BlockEntityType<PartBuilderBlockEntity>> partBuilderTile = BLOCK_ENTITIES.register("part_builder", PartBuilderBlockEntity::new, partBuilder);
  public static final RegistryObject<BlockEntityType<ModifierWorktableBlockEntity>> modifierWorktableTile = BLOCK_ENTITIES.register("modifier_worktable", ModifierWorktableBlockEntity::new, modifierWorktable);
  // legacy name as tile entities cannot be remapped
  public static final RegistryObject<BlockEntityType<TinkersChestBlockEntity>> tinkersChestTile = BLOCK_ENTITIES.register("modifier_chest", TinkersChestBlockEntity::new, tinkersChest);
  public static final RegistryObject<BlockEntityType<PartChestBlockEntity>> partChestTile = BLOCK_ENTITIES.register("part_chest", PartChestBlockEntity::new, partChest);
  public static final RegistryObject<BlockEntityType<CastChestBlockEntity>> castChestTile = BLOCK_ENTITIES.register("cast_chest", CastChestBlockEntity::new, castChest);

  /*
   * Containers
   */
  public static final RegistryObject<MenuType<CraftingStationContainerMenu>> craftingStationContainer = MENUS.register("crafting_station", CraftingStationContainerMenu::new);
  public static final RegistryObject<MenuType<TinkerStationContainerMenu>> tinkerStationContainer = MENUS.register("tinker_station", TinkerStationContainerMenu::new);
  public static final RegistryObject<MenuType<PartBuilderContainerMenu>> partBuilderContainer = MENUS.register("part_builder", PartBuilderContainerMenu::new);
  public static final RegistryObject<MenuType<ModifierWorktableContainerMenu>> modifierWorktableContainer = MENUS.register("modifier_worktable", ModifierWorktableContainerMenu::new);
  public static final RegistryObject<MenuType<TinkerChestContainerMenu>> tinkerChestContainer = MENUS.register("tinker_chest", TinkerChestContainerMenu::new);

  /*
   * Recipes
   */
  public static final RegistryObject<RecipeSerializer<MaterialRecipe>> materialRecipeSerializer = RECIPE_SERIALIZERS.register("material", () -> LoadableRecipeSerializer.of(MaterialRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ToolBuildingRecipe>> toolBuildingRecipeSerializer = RECIPE_SERIALIZERS.register("tool_building", () -> LoadableRecipeSerializer.of(ToolBuildingRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<TinkerStationPartSwapping>> tinkerStationPartSwappingSerializer = RECIPE_SERIALIZERS.register("tinker_station_part_swapping", () -> LoadableRecipeSerializer.of(TinkerStationPartSwapping.LOADER));
  public static final RegistryObject<RecipeSerializer<TinkerStationDamagingRecipe>> tinkerStationDamagingSerializer = RECIPE_SERIALIZERS.register("tinker_station_damaging", () -> LoadableRecipeSerializer.of(TinkerStationDamagingRecipe.LOADER));
  @Deprecated
  public static final RegistryObject<RecipeSerializer<ShapedMaterialRecipe>> shapedMaterialRecipeSerializer = RECIPE_SERIALIZERS.register("crafting_shaped_material", ShapedMaterialRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<ShapedMaterialsRecipe>> shapedMaterialsRecipeSerializer = RECIPE_SERIALIZERS.register("crafting_shaped_materials", ShapedMaterialsRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<ShapelessMaterialsRecipe>> shapelessMaterialsRecipeSerializer = RECIPE_SERIALIZERS.register("crafting_shapeless_materials", ShapelessMaterialsRecipe.Serializer::new);
  // part builder
  public static final RegistryObject<RecipeSerializer<PartRecipe>> partRecipeSerializer = RECIPE_SERIALIZERS.register("part_builder", () -> LoadableRecipeSerializer.of(PartRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ItemPartRecipe>> itemPartBuilderSerializer = RECIPE_SERIALIZERS.register("item_part_builder", () -> LoadableRecipeSerializer.of(ItemPartRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<PartBuilderToolRecycle>> partBuilderToolRecycling = RECIPE_SERIALIZERS.register("part_builder_tool_recycling", () -> LoadableRecipeSerializer.of(PartBuilderToolRecycle.LOADER));
  public static final RegistryObject<RecipeSerializer<PartBuilderRecycle>> partBuilderDamageableRecycling = RECIPE_SERIALIZERS.register("part_builder_recycling", () -> LoadableRecipeSerializer.of(PartBuilderRecycle.LOADER));
  // repair - standard
  public static final RegistryObject<SimpleRecipeSerializer<TinkerStationRepairRecipe>> tinkerStationRepairSerializer = RECIPE_SERIALIZERS.register("tinker_station_repair", () -> new SimpleRecipeSerializer<>(TinkerStationRepairRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<CraftingTableRepairKitRecipe>> craftingTableRepairSerializer = RECIPE_SERIALIZERS.register("crafting_table_repair", () -> new SimpleRecipeSerializer<>(CraftingTableRepairKitRecipe::new));

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      StationSlotLayoutLoader loader = StationSlotLayoutLoader.getInstance();
      loader.registerRequiredLayout(tinkerStation.getId());
      loader.registerRequiredLayout(tinkersAnvil.getId());
      loader.registerRequiredLayout(scorchedAnvil.getId());
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    generator.addProvider(event.includeServer(), new TableRecipeProvider(generator.getPackOutput()));
  }

  /** Adds all relevant items to the creative tab, called in the general tab */
  private static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
    output.accept(pattern);

    // add one of each standard table
    output.accept(craftingStation);
    output.accept(partBuilder);
    output.accept(tinkerStation);
    // if showing all anvil variants, skip them in search at this first stage
    output.accept(tinkersAnvil);
    output.accept(scorchedAnvil);
    output.accept(modifierWorktable);

    // chests, have less variants so go first
    output.accept(tinkersChest);
    output.accept(partChest);
    output.accept(castChest);

    // table variants at the end as there may be a lot
    Predicate<ItemStack> variants = stack -> {
      output.accept(stack);
      return false;
    };
    // crafting tables
    // add crafting station with the default variant, its nice
    RetexturedHelper.addTagVariants(variants, craftingStation, ItemTags.LOGS);
    // rest the default variant is the same as oak
    RetexturedHelper.addTagVariants(variants, partBuilder, ItemTags.PLANKS);
    RetexturedHelper.addTagVariants(variants, tinkerStation, ItemTags.PLANKS);
    // anvil variants use their own config prop as the variants are less obvious
    Consumer<ItemStack> consumer = output::accept;
    ((IMaterialItem) tinkersAnvil.asItem()).addVariants(consumer, "");
    ((IMaterialItem) scorchedAnvil.asItem()).addVariants(consumer, "");
    RetexturedHelper.addTagVariants(variants, modifierWorktable, TinkerTags.Items.WORKSTATION_ROCK);
  }
}
