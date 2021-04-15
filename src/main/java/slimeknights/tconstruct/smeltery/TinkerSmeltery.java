package slimeknights.tconstruct.smeltery;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipe;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.DamagableMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.OreMeltingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.smeltery.block.CastingBasinBlock;
import slimeknights.tconstruct.smeltery.block.CastingTableBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.HeaterBlock;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.block.SmelteryControllerBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedDrainBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedDuctBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedGlassBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedLadderBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.block.component.SmelteryIOBlock;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.ChannelTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.DuctTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.HeaterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains logic for the multiblocks in the mod
 */
@SuppressWarnings("unused")
public final class TinkerSmeltery extends TinkerModule {
  /** Tab for all blocks related to the smeltery */
  public static final ItemGroup TAB_SMELTERY = FabricItemGroupBuilder.build(new Identifier(TConstruct.modID, "smeltery"), () -> new ItemStack(TinkerSmeltery.searedTank.get(TankType.TANK)));
  public static final Logger log = Util.getLogger("tinker_smeltery");

  /* Bricks */
  /* Crafting related items */

  /*
   * Block base properties
   */
  private static final Item.Settings SMELTERY_PROPS = new Item.Settings().group(TAB_SMELTERY);
  private static final Function<Block, BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, SMELTERY_PROPS);

  /*
   * Blocks
   */
  private static final FabricBlockSettings SMELTERY_GLASS = builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(3.0F, 9.0F).nonOpaque().suffocates((s, w, p) -> false);
  public static final ItemObject<Block> grout = BLOCKS.register("grout", getGenericSandBlock(), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedGlass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedGlassPane = BLOCKS.register("seared_glass_pane", () -> new ClearGlassPaneBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);

  // seared
  /** Properties for all smeltery blocks */
  private static final Block.Settings SMELTERY = builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(3.0F, 9.0F);
  /** Constructor to create a seared block */
  private static final Supplier<Block> SEARED_BLOCK = () -> new SearedBlock(SMELTERY);
  public static final BuildingBlockObject searedStone = BLOCKS.registerBuilding("seared_stone", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedCobble = BLOCKS.registerWallBuilding("seared_cobble", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedPaver = BLOCKS.registerBuilding("seared_paver", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedBricks = BLOCKS.registerWallBuilding("seared_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedCrackedBricks = BLOCKS.register("seared_cracked_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedFancyBricks = BLOCKS.register("seared_fancy_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedTriangleBricks = BLOCKS.register("seared_triangle_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedLadder = BLOCKS.register("seared_ladder", () -> new SearedLadderBlock(builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(3.0F, 9.0F).nonOpaque()), TOOLTIP_BLOCK_ITEM);

  // peripherals
  public static final ItemObject<Block> searedDrain = BLOCKS.register("seared_drain", () -> new SearedDrainBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedDuct = BLOCKS.register("seared_duct", () -> new SearedDuctBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedChute = BLOCKS.register("seared_chute", () -> new SmelteryIOBlock(SMELTERY, SmelteryFluidIO.ChuteTileEntity::new), TOOLTIP_BLOCK_ITEM);

  /** Properties for a faucet block */
  private static final Block.Settings FAUCET = builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(3.0F, 9.0F).nonOpaque();
  public static final EnumObject<TankType,SearedTankBlock> searedTank = BLOCKS.registerEnum("seared", TankType.values(), type -> new SearedTankBlock(SMELTERY_GLASS, type.getCapacity()), b -> new TankItem(b, SMELTERY_PROPS));
  public static final ItemObject<Block> searedFaucet = BLOCKS.register("faucet", () -> new FaucetBlock(FAUCET), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedChannel = BLOCKS.register("channel", () -> new ChannelBlock(FAUCET), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> castingBasin = BLOCKS.register("casting_basin", () -> new CastingBasinBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> castingTable = BLOCKS.register("casting_table", () -> new CastingTableBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);

  // controllers
  private static final Supplier<AbstractBlock.Settings> CONTROLLER = () -> builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(3.0F, 9.0F).luminance(s -> s.get(ControllerBlock.ACTIVE) ? 13 : 0);
  public static final ItemObject<Block> searedMelter = BLOCKS.register("melter", () -> new MelterBlock(CONTROLLER.get().nonOpaque()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> smelteryController = BLOCKS.register("smeltery_controller", () -> new SmelteryControllerBlock(CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedHeater = BLOCKS.register("seared_heater", () -> new HeaterBlock(CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);

  /*
   * Tile entities
   */
  // smeltery
  public static final BlockEntityType<SmelteryComponentTileEntity> smelteryComponent = TILE_ENTITIES.register("smeltery_component", SmelteryComponentTileEntity::new, (set) -> {
    set.addAll(searedStone.values());
    set.addAll(searedCobble.values());
    set.addAll(searedBricks.values());
    set.addAll(searedPaver.values());
    set.add(searedCrackedBricks.get(), searedFancyBricks.get(), searedTriangleBricks.get(), searedLadder.get(), searedGlass.get());
  });
  
  public static final BlockEntityType<SmelteryFluidIO> drain = TILE_ENTITIES.register("drain", DrainTileEntity::new, searedDrain);
  public static final BlockEntityType<SmelteryFluidIO.ChuteTileEntity> chute = TILE_ENTITIES.register("chute", SmelteryFluidIO.ChuteTileEntity::new, searedChute);
  public static final BlockEntityType<DuctTileEntity> duct = TILE_ENTITIES.register("duct", DuctTileEntity::new, searedDuct);
  public static final BlockEntityType<TankTileEntity> tank = TILE_ENTITIES.register("tank", TankTileEntity::new, (set) -> set.addAll(searedTank.values()));
  // controller
  public static final BlockEntityType<MelterTileEntity> melter = TILE_ENTITIES.register("melter", MelterTileEntity::new, searedMelter);
  public static final BlockEntityType<SmelteryTileEntity> smeltery = TILE_ENTITIES.register("smeltery", SmelteryTileEntity::new, smelteryController);
  public static final BlockEntityType<HeaterTileEntity> heater = TILE_ENTITIES.register("heater", HeaterTileEntity::new, searedHeater);
  // fluid transfer
  public static final BlockEntityType<FaucetTileEntity> faucet = TILE_ENTITIES.register("faucet", FaucetTileEntity::new, searedFaucet);
  public static final BlockEntityType<ChannelTileEntity> channel = TILE_ENTITIES.register("channel", ChannelTileEntity::new, searedChannel);
  // casting
  public static final BlockEntityType<CastingTileEntity> basin = TILE_ENTITIES.register("basin", CastingTileEntity.Basin::new, castingBasin);
  public static final BlockEntityType<CastingTileEntity> table = TILE_ENTITIES.register("table", CastingTileEntity.Table::new, castingTable);

  /*
   * Items
   */
  public static final ItemObject<Item> searedBrick = ITEMS.register("seared_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> copperCan = ITEMS.register("copper_can", () -> new CopperCanItem(new Item.Settings().maxCount(16).group(TAB_SMELTERY)));

  // casts
  // basic
  public static final CastItemObject blankCast  = ITEMS.registerCast("blank", SMELTERY_PROPS);
  public static final CastItemObject ingotCast  = ITEMS.registerCast("ingot", SMELTERY_PROPS);
  public static final CastItemObject nuggetCast = ITEMS.registerCast("nugget", SMELTERY_PROPS);
  public static final CastItemObject gemCast    = ITEMS.registerCast("gem", SMELTERY_PROPS);
  //  public static final ItemObject<Item> sharpeningKitCast = ITEMS.register("sharpening_kit_cast", SMELTERY_PROPS);
  //  public static final ItemObject<Item> gearCast = ITEMS.register("gear_cast", SMELTERY_PROPS);
  // small tool heads
  public static final CastItemObject pickaxeHeadCast  = ITEMS.registerCast("pickaxe_head", SMELTERY_PROPS);
  public static final CastItemObject axeHeadCast      = ITEMS.registerCast("axe_head", SMELTERY_PROPS);
  public static final CastItemObject kamaHeadCast     = ITEMS.registerCast("kama_head", SMELTERY_PROPS);
  public static final CastItemObject swordBladeCast   = ITEMS.registerCast("sword_blade", SMELTERY_PROPS);
  //  public static final ItemObject<Item> signHeadCast = ITEMS.register("sign_head_cast", SMELTERY_PROPS);
  //  public static final ItemObject<Item> bowLimbCast = ITEMS.register("bow_limb_cast", SMELTERY_PROPS);
  // large tool heads
  public static final CastItemObject hammerHeadCast    = ITEMS.registerCast("hammer_head", SMELTERY_PROPS);
  //  public static final ItemObject<Item> broadAxeHeadCast = ITEMS.register("broad_axe_head_cast", SMELTERY_PROPS);
  //  public static final ItemObject<Item> scytheHeadCast = ITEMS.register("scythe_head_cast", SMELTERY_PROPS);
  //  public static final ItemObject<Item> largeSwordBladeCast = ITEMS.register("large_sword_blade_cast", SMELTERY_PROPS);
  // bindings
  public static final CastItemObject toolBindingCast = ITEMS.registerCast("tool_binding", SMELTERY_PROPS);
  public static final CastItemObject largePlateCast  = ITEMS.registerCast("large_plate", SMELTERY_PROPS);
  // tool rods
  public static final CastItemObject toolRodCast      = ITEMS.registerCast("tool_rod", SMELTERY_PROPS);
  public static final CastItemObject toughToolRodCast = ITEMS.registerCast("tough_tool_rod", SMELTERY_PROPS);

  /*
   * Recipe
   */
  // casting
  public static final ItemCastingRecipe.Serializer<ItemCastingRecipe.Basin> basinRecipeSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("casting_basin"), new ItemCastingRecipe.Serializer<>(ItemCastingRecipe.Basin::new));
  public static final ItemCastingRecipe.Serializer<ItemCastingRecipe.Table> tableRecipeSerializer = null; //  Registry.register(Registry.RECIPE_SERIALIZER, id("casting_table"), new ItemCastingRecipe.Serializer<>(ItemCastingRecipe.Table::new));
  public static final ContainerFillingRecipeSerializer<ContainerFillingRecipe.Basin> basinFillingRecipeSerializer = null; //  Registry.register(Registry.RECIPE_SERIALIZER, id("basin_filling"), new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Basin::new));
  public static final ContainerFillingRecipeSerializer<ContainerFillingRecipe.Table> tableFillingRecipeSerializer = null; //  Registry.register(Registry.RECIPE_SERIALIZER, id("table_filling"), new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Table::new));
  // material casting
  public static final MaterialCastingRecipe.Serializer<MaterialCastingRecipe.Basin> basinMaterialSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("basin_casting_material"), new MaterialCastingRecipe.Serializer<>(MaterialCastingRecipe.Basin::new));
  public static final MaterialCastingRecipe.Serializer<MaterialCastingRecipe.Table> tableMaterialSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("table_casting_material"), new MaterialCastingRecipe.Serializer<>(MaterialCastingRecipe.Table::new));
  public static final CompositeCastingRecipe.Serializer<CompositeCastingRecipe.Basin> basinCompositeSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("basin_casting_composite"), new CompositeCastingRecipe.Serializer<>(CompositeCastingRecipe.Basin::new));
  public static final CompositeCastingRecipe.Serializer<CompositeCastingRecipe.Table> tableCompositeSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("table_casting_composite"), new CompositeCastingRecipe.Serializer<>(CompositeCastingRecipe.Table::new));
  // molding
  public static final MoldingRecipe.Serializer<MoldingRecipe.Table> moldingTableSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("molding_table"), new MoldingRecipe.Serializer<>(MoldingRecipe.Table::new));
  public static final MoldingRecipe.Serializer<MoldingRecipe.Basin> moldingBasinSerializer = null; // Registry.register(Registry.RECIPE_SERIALIZER, id("molding_basin"), new MoldingRecipe.Serializer<>(MoldingRecipe.Basin::new));
  // melting
  public static final RecipeSerializer<MeltingRecipe> meltingSerializer =  Registry.register(Registry.RECIPE_SERIALIZER, id("melting"), new MeltingRecipe.Serializer<>(MeltingRecipe::new));
  public static final RecipeSerializer<MeltingRecipe> oreMeltingSerializer =  Registry.register(Registry.RECIPE_SERIALIZER, id("ore_melting"), new MeltingRecipe.Serializer<>(OreMeltingRecipe::new));
  public static final RecipeSerializer<MeltingRecipe> damagableMeltingSerializer =  Registry.register(Registry.RECIPE_SERIALIZER, id("damagable_melting"), new MeltingRecipe.Serializer<>(DamagableMeltingRecipe::new));
  public static final RecipeSerializer<MaterialMeltingRecipe> materialMeltingSerializer =  Registry.register(Registry.RECIPE_SERIALIZER, id("material_melting"), new MaterialMeltingRecipe.Serializer());
  public static final RecipeSerializer<MeltingFuel> fuelSerializer =  Registry.register(Registry.RECIPE_SERIALIZER, id("melting_fuel"), new MeltingFuel.Serializer());
  public static final RecipeSerializer<EntityMeltingRecipe> entityMeltingSerializer =  Registry.register(Registry.RECIPE_SERIALIZER, id("entity_melting"), new EntityMeltingRecipe.Serializer());
  // alloying
  public static final RecipeSerializer<AlloyRecipe> alloyingSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("alloy"), new AlloyRecipe.Serializer());

  /*
   * Inventory
   */
  public static final ScreenHandlerType<MelterContainer> melterContainer = ScreenHandlerRegistry.registerExtended(id("melter"), MelterContainer::new);
  public static final ScreenHandlerType<SmelteryContainer> smelteryContainer = ScreenHandlerRegistry.registerExtended(id("smeltery"), SmelteryContainer::new);
  public static final ScreenHandlerType<SingleItemContainer> singleItemContainer = ScreenHandlerRegistry.registerExtended(id("single_item"), SingleItemContainer::new);

  @Override
  public void onInitialize() {

  }
}
