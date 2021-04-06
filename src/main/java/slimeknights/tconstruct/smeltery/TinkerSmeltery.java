package slimeknights.tconstruct.smeltery;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.mantle.util.SupplierItemGroup;
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
import slimeknights.tconstruct.smeltery.data.SmelteryRecipeProvider;
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
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.ChuteTileEntity;
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
  public static final ItemGroup TAB_SMELTERY = new SupplierItemGroup(TConstruct.modID, "smeltery", () -> new ItemStack(TinkerSmeltery.searedTank.get(TankType.TANK)));
  public static final Logger log = Util.getLogger("tinker_smeltery");

  /* Bricks */
  /* Crafting related items */

  /*
   * Block base properties
   */
  private static final Item.Properties SMELTERY_PROPS = new Item.Properties().group(TAB_SMELTERY);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, SMELTERY_PROPS);

  /*
   * Blocks
   */
  private static final Block.Properties SMELTERY_GLASS = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(3.0F, 9.0F).notSolid().setSuffocates((s,w,p) -> false);
  public static final ItemObject<Block> grout = BLOCKS.register("grout", GENERIC_SAND_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedGlassBlock> searedGlass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> searedGlassPane = BLOCKS.register("seared_glass_pane", () -> new ClearGlassPaneBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);

  // seared
  /** Properties for all smeltery blocks */
  private static final Block.Properties SMELTERY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(3.0F, 9.0F);
  /** Constructor to create a seared block */
  private static final Supplier<SearedBlock> SEARED_BLOCK = () -> new SearedBlock(SMELTERY);
  public static final BuildingBlockObject searedStone = BLOCKS.registerBuilding("seared_stone", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedCobble = BLOCKS.registerWallBuilding("seared_cobble", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedPaver = BLOCKS.registerBuilding("seared_paver", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedBricks = BLOCKS.registerWallBuilding("seared_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedCrackedBricks = BLOCKS.register("seared_cracked_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedFancyBricks = BLOCKS.register("seared_fancy_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedTriangleBricks = BLOCKS.register("seared_triangle_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedLadder = BLOCKS.register("seared_ladder", () -> new SearedLadderBlock(builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(3.0F, 9.0F).notSolid()), TOOLTIP_BLOCK_ITEM);

  // peripherals
  public static final ItemObject<Block> searedDrain = BLOCKS.register("seared_drain", () -> new SearedDrainBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedDuct = BLOCKS.register("seared_duct", () -> new SearedDuctBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedChute = BLOCKS.register("seared_chute", () -> new SmelteryIOBlock(SMELTERY, ChuteTileEntity::new), TOOLTIP_BLOCK_ITEM);

  /** Properties for a faucet block */
  private static final Block.Properties FAUCET = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(3.0F, 9.0F).notSolid();
  public static final EnumObject<TankType,SearedTankBlock> searedTank = BLOCKS.registerEnum("seared", SearedTankBlock.TankType.values(), type -> new SearedTankBlock(SMELTERY_GLASS, type.getCapacity()), b -> new TankItem(b, SMELTERY_PROPS));
  public static final ItemObject<FaucetBlock> searedFaucet = BLOCKS.register("faucet", () -> new FaucetBlock(FAUCET), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ChannelBlock> searedChannel = BLOCKS.register("channel", () -> new ChannelBlock(FAUCET), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingBasinBlock> castingBasin = BLOCKS.register("casting_basin", () -> new CastingBasinBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingTableBlock> castingTable = BLOCKS.register("casting_table", () -> new CastingTableBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);

  // controllers
  private static final Supplier<Block.Properties> CONTROLLER = () -> builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(3.0F, 9.0F).setLightLevel(s -> s.get(ControllerBlock.ACTIVE) ? 13 : 0);
  public static final ItemObject<MelterBlock> searedMelter = BLOCKS.register("melter", () -> new MelterBlock(CONTROLLER.get().notSolid()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SmelteryControllerBlock> smelteryController = BLOCKS.register("smeltery_controller", () -> new SmelteryControllerBlock(CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<HeaterBlock> searedHeater = BLOCKS.register("seared_heater", () -> new HeaterBlock(CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);

  /*
   * Tile entities
   */
  // smeltery
  public static final RegistryObject<TileEntityType<SmelteryComponentTileEntity>> smelteryComponent = TILE_ENTITIES.register("smeltery_component", SmelteryComponentTileEntity::new, (set) -> {
    set.addAll(searedStone.values());
    set.addAll(searedCobble.values());
    set.addAll(searedBricks.values());
    set.addAll(searedPaver.values());
    set.add(searedCrackedBricks.get(), searedFancyBricks.get(), searedTriangleBricks.get(), searedLadder.get(), searedGlass.get());
  });
  public static final RegistryObject<TileEntityType<SmelteryFluidIO>> drain = TILE_ENTITIES.register("drain", DrainTileEntity::new, searedDrain);
  public static final RegistryObject<TileEntityType<ChuteTileEntity>> chute = TILE_ENTITIES.register("chute", ChuteTileEntity::new, searedChute);
  public static final RegistryObject<TileEntityType<DuctTileEntity>> duct = TILE_ENTITIES.register("duct", DuctTileEntity::new, searedDuct);
  public static final RegistryObject<TileEntityType<TankTileEntity>> tank = TILE_ENTITIES.register("tank", TankTileEntity::new, (set) -> set.addAll(searedTank.values()));
  // controller
  public static final RegistryObject<TileEntityType<MelterTileEntity>> melter = TILE_ENTITIES.register("melter", MelterTileEntity::new, searedMelter);
  public static final RegistryObject<TileEntityType<SmelteryTileEntity>> smeltery = TILE_ENTITIES.register("smeltery", SmelteryTileEntity::new, smelteryController);
  public static final RegistryObject<TileEntityType<HeaterTileEntity>> heater = TILE_ENTITIES.register("heater", HeaterTileEntity::new, searedHeater);
  // fluid transfer
  public static final RegistryObject<TileEntityType<FaucetTileEntity>> faucet = TILE_ENTITIES.register("faucet", FaucetTileEntity::new, searedFaucet);
  public static final RegistryObject<TileEntityType<ChannelTileEntity>> channel = TILE_ENTITIES.register("channel", ChannelTileEntity::new, searedChannel);
  // casting
  public static final RegistryObject<TileEntityType<CastingTileEntity>> basin = TILE_ENTITIES.register("basin", CastingTileEntity.Basin::new, castingBasin);
  public static final RegistryObject<TileEntityType<CastingTileEntity>> table = TILE_ENTITIES.register("table", CastingTileEntity.Table::new, castingTable);

  /*
   * Items
   */
  public static final ItemObject<Item> searedBrick = ITEMS.register("seared_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> copperCan = ITEMS.register("copper_can", () -> new CopperCanItem(new Item.Properties().maxStackSize(16).group(TAB_SMELTERY)));

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
  public static final RegistryObject<ItemCastingRecipe.Serializer<ItemCastingRecipe.Basin>> basinRecipeSerializer = RECIPE_SERIALIZERS.register("casting_basin", () -> new ItemCastingRecipe.Serializer<>(ItemCastingRecipe.Basin::new));
  public static final RegistryObject<ItemCastingRecipe.Serializer<ItemCastingRecipe.Table>> tableRecipeSerializer = RECIPE_SERIALIZERS.register("casting_table", () -> new ItemCastingRecipe.Serializer<>(ItemCastingRecipe.Table::new));
  public static final RegistryObject<ContainerFillingRecipeSerializer<ContainerFillingRecipe.Basin>> basinFillingRecipeSerializer = RECIPE_SERIALIZERS.register("basin_filling", () -> new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Basin::new));
  public static final RegistryObject<ContainerFillingRecipeSerializer<ContainerFillingRecipe.Table>> tableFillingRecipeSerializer = RECIPE_SERIALIZERS.register("table_filling", () -> new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Table::new));
  // material casting
  public static final RegistryObject<MaterialCastingRecipe.Serializer<MaterialCastingRecipe.Basin>> basinMaterialSerializer = RECIPE_SERIALIZERS.register("basin_casting_material", () -> new MaterialCastingRecipe.Serializer<>(MaterialCastingRecipe.Basin::new));
  public static final RegistryObject<MaterialCastingRecipe.Serializer<MaterialCastingRecipe.Table>> tableMaterialSerializer = RECIPE_SERIALIZERS.register("table_casting_material", () -> new MaterialCastingRecipe.Serializer<>(MaterialCastingRecipe.Table::new));
  public static final RegistryObject<CompositeCastingRecipe.Serializer<CompositeCastingRecipe.Basin>> basinCompositeSerializer = RECIPE_SERIALIZERS.register("basin_casting_composite", () -> new CompositeCastingRecipe.Serializer<>(CompositeCastingRecipe.Basin::new));
  public static final RegistryObject<CompositeCastingRecipe.Serializer<CompositeCastingRecipe.Table>> tableCompositeSerializer = RECIPE_SERIALIZERS.register("table_casting_composite", () -> new CompositeCastingRecipe.Serializer<>(CompositeCastingRecipe.Table::new));
  // molding
  public static final RegistryObject<MoldingRecipe.Serializer<MoldingRecipe.Table>> moldingTableSerializer = RECIPE_SERIALIZERS.register("molding_table", () -> new MoldingRecipe.Serializer<>(MoldingRecipe.Table::new));
  public static final RegistryObject<MoldingRecipe.Serializer<MoldingRecipe.Basin>> moldingBasinSerializer = RECIPE_SERIALIZERS.register("molding_basin", () -> new MoldingRecipe.Serializer<>(MoldingRecipe.Basin::new));
  // melting
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> meltingSerializer = RECIPE_SERIALIZERS.register("melting", () -> new MeltingRecipe.Serializer<>(MeltingRecipe::new));
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> oreMeltingSerializer = RECIPE_SERIALIZERS.register("ore_melting", () -> new MeltingRecipe.Serializer<>(OreMeltingRecipe::new));
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> damagableMeltingSerializer = RECIPE_SERIALIZERS.register("damagable_melting", () -> new MeltingRecipe.Serializer<>(DamagableMeltingRecipe::new));
  public static final RegistryObject<IRecipeSerializer<MaterialMeltingRecipe>> materialMeltingSerializer = RECIPE_SERIALIZERS.register("material_melting", MaterialMeltingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<MeltingFuel>> fuelSerializer = RECIPE_SERIALIZERS.register("melting_fuel", MeltingFuel.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<EntityMeltingRecipe>> entityMeltingSerializer = RECIPE_SERIALIZERS.register("entity_melting", EntityMeltingRecipe.Serializer::new);
  // alloying
  public static final RegistryObject<IRecipeSerializer<AlloyRecipe>> alloyingSerializer = RECIPE_SERIALIZERS.register("alloy", AlloyRecipe.Serializer::new);

  /*
   * Inventory
   */
  public static final RegistryObject<ContainerType<MelterContainer>> melterContainer = CONTAINERS.register("melter", MelterContainer::new);
  public static final RegistryObject<ContainerType<SmelteryContainer>> smelteryContainer = CONTAINERS.register("smeltery", SmelteryContainer::new);
  public static final RegistryObject<ContainerType<SingleItemContainer>> singleItemContainer = CONTAINERS.register("single_item", SingleItemContainer::new);

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new SmelteryRecipeProvider(datagenerator));
    }
  }
}
