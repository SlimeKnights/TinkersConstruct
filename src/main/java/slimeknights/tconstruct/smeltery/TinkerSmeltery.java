package slimeknights.tconstruct.smeltery;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
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
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.DamageableMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.OreMeltingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.smeltery.block.AlloyerBlock;
import slimeknights.tconstruct.smeltery.block.CastingBasinBlock;
import slimeknights.tconstruct.smeltery.block.CastingTableBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.FoundryControllerBlock;
import slimeknights.tconstruct.smeltery.block.HeaterBlock;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.block.SearedLanternBlock;
import slimeknights.tconstruct.smeltery.block.SmelteryControllerBlock;
import slimeknights.tconstruct.smeltery.block.component.OrientableSmelteryBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedDrainBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedDuctBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedGlassBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedLadderBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedPillarBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.data.SmelteryRecipeProvider;
import slimeknights.tconstruct.smeltery.inventory.AlloyerContainer;
import slimeknights.tconstruct.smeltery.inventory.HeatingStructureContainer;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.AlloyerTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.ChannelTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.DuctTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FoundryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.HeaterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.LanternTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.ChuteTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Contains logic for the multiblocks in the mod
 */
@SuppressWarnings("unused")
public final class TinkerSmeltery extends TinkerModule {
  /** Tab for all blocks related to the smeltery */
  public static final ItemGroup TAB_SMELTERY = new SupplierItemGroup(TConstruct.modID, "smeltery", () -> new ItemStack(TinkerSmeltery.smelteryController));
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
  public static final ItemObject<Block> grout = BLOCKS.register("grout", GENERIC_SAND_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> netherGrout = BLOCKS.register("nether_grout", builder(Material.SAND, ToolType.SHOVEL, SoundType.SOUL_SOIL).hardnessAndResistance(3.0f).slipperiness(0.8F), TOOLTIP_BLOCK_ITEM);

  // seared blocks
  private static final IntFunction<Properties> SEARED_PROPS = factor -> builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL)
    .setRequiresTool().hardnessAndResistance(3.0F * factor, 9.0F * factor).setAllowsSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.get(SearedBlock.IN_STRUCTURE));
  private static final Properties SEARED = SEARED_PROPS.apply(1);
  private static final Properties TOUGH_SEARED = SEARED_PROPS.apply(2);
  private static final Function<SoundType,Properties> SEARED_NON_SOLID_TEMPLATE = sound -> builder(Material.ROCK, ToolType.PICKAXE, sound)
    .setRequiresTool().hardnessAndResistance(3.0F, 9.0F).notSolid()
    .setAllowsSpawn(Blocks::neverAllowSpawn).setOpaque(Blocks::isntSolid).setSuffocates(Blocks::isntSolid).setBlocksVision(Blocks::isntSolid);
  private static final Properties SEARED_GLASS = SEARED_NON_SOLID_TEMPLATE.apply(SoundType.GLASS);
  private static final Properties SEARED_NON_SOLID = SEARED_NON_SOLID_TEMPLATE.apply(SoundType.METAL);
  private static final Supplier<SearedBlock> SEARED_BLOCK = () -> new SearedBlock(SEARED);
  // blocks
  public static final BuildingBlockObject searedStone = BLOCKS.registerBuilding("seared_stone", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedCobble = BLOCKS.registerWallBuilding("seared_cobble", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedPaver = BLOCKS.registerBuilding("seared_paver", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final WallBuildingBlockObject searedBricks = BLOCKS.registerWallBuilding("seared_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedCrackedBricks = BLOCKS.register("seared_cracked_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedFancyBricks = BLOCKS.register("seared_fancy_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedTriangleBricks = BLOCKS.register("seared_triangle_bricks", SEARED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedLadder = BLOCKS.register("seared_ladder", () -> new SearedLadderBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedGlassBlock> searedGlass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(SEARED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> searedGlassPane = BLOCKS.register("seared_glass_pane", () -> new ClearGlassPaneBlock(SEARED_GLASS), TOOLTIP_BLOCK_ITEM);
  // peripherals
  public static final ItemObject<Block> searedDrain = BLOCKS.register("seared_drain", () -> new SearedDrainBlock(TOUGH_SEARED), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedDuct = BLOCKS.register("seared_duct", () -> new SearedDuctBlock(TOUGH_SEARED), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> searedChute = BLOCKS.register("seared_chute", () -> new OrientableSmelteryBlock(TOUGH_SEARED, ChuteTileEntity::new), TOOLTIP_BLOCK_ITEM);

  // scorched blocks
  private static final IntFunction<Properties> SCORCHED_PROPS = factor -> builder(Material.ROCK, ToolType.PICKAXE, SoundType.BASALT)
    .setRequiresTool().hardnessAndResistance(2.5F * factor, 8.0F * factor).setAllowsSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.get(SearedBlock.IN_STRUCTURE));
  private static final Properties SCORCHED = SCORCHED_PROPS.apply(1);
  private static final Properties TOUGH_SCORCHED = SCORCHED_PROPS.apply(3);
  private static final Function<SoundType,Properties> SCORCHED_NON_SOLID_TEMPLATE = sound -> builder(Material.ROCK, ToolType.PICKAXE, sound)
    .setRequiresTool().hardnessAndResistance(2.5F, 8.0F).notSolid()
    .setAllowsSpawn(Blocks::neverAllowSpawn).setOpaque(Blocks::isntSolid).setSuffocates(Blocks::isntSolid).setBlocksVision(Blocks::isntSolid);
  private static final Properties SCORCHED_GLASS = SEARED_NON_SOLID_TEMPLATE.apply(SoundType.GLASS);
  private static final Properties SCORCHED_NON_SOLID = SEARED_NON_SOLID_TEMPLATE.apply(SoundType.BASALT);
  private static final Supplier<SearedBlock> SCORCHED_BLOCK = () -> new SearedBlock(SCORCHED);
  private static final Supplier<SearedPillarBlock> SCORCHED_PILLAR = () -> new SearedPillarBlock(SCORCHED);
  // blocks
  public static final ItemObject<Block> scorchedStone = BLOCKS.register("scorched_stone", SCORCHED_PILLAR, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> polishedScorchedStone = BLOCKS.register("polished_scorched_stone", SCORCHED_PILLAR, TOOLTIP_BLOCK_ITEM);
  public static final FenceBuildingBlockObject scorchedBricks = BLOCKS.registerFenceBuilding("scorched_bricks", SCORCHED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject scorchedRoad = BLOCKS.registerBuilding("scorched_road", SCORCHED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> chiseledScorchedBricks = BLOCKS.register("chiseled_scorched_bricks", SCORCHED_BLOCK, TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> scorchedLadder = BLOCKS.register("scorched_ladder", () -> new SearedLadderBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedGlassBlock> scorchedGlass = BLOCKS.register("scorched_glass", () -> new SearedGlassBlock(SCORCHED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> scorchedGlassPane = BLOCKS.register("scorched_glass_pane", () -> new ClearGlassPaneBlock(SCORCHED_GLASS), TOOLTIP_BLOCK_ITEM);
  // peripherals
  public static final ItemObject<Block> scorchedDrain = BLOCKS.register("scorched_drain", () -> new SearedDrainBlock(TOUGH_SCORCHED), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> scorchedDuct = BLOCKS.register("scorched_duct", () -> new SearedDuctBlock(TOUGH_SCORCHED), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> scorchedChute = BLOCKS.register("scorched_chute", () -> new OrientableSmelteryBlock(TOUGH_SCORCHED, ChuteTileEntity::new), TOOLTIP_BLOCK_ITEM);

  // seared
  public static final EnumObject<TankType,SearedTankBlock> searedTank = BLOCKS.registerEnum("seared", SearedTankBlock.TankType.values(), type -> new SearedTankBlock(SEARED_NON_SOLID, type.getCapacity()), b -> new TankItem(b, SMELTERY_PROPS, true));
  public static final ItemObject<SearedLanternBlock> searedLantern = BLOCKS.register("seared_lantern", () -> new SearedLanternBlock(SEARED_NON_SOLID_TEMPLATE.apply(SoundType.LANTERN), FluidAttributes.BUCKET_VOLUME / 10), b -> new TankItem(b, SMELTERY_PROPS, false));
  public static final ItemObject<FaucetBlock> searedFaucet = BLOCKS.register("seared_faucet", () -> new FaucetBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ChannelBlock> searedChannel = BLOCKS.register("seared_channel", () -> new ChannelBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingBasinBlock> searedBasin = BLOCKS.register("seared_basin", () -> new CastingBasinBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingTableBlock> searedTable = BLOCKS.register("seared_table", () -> new CastingTableBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  // scorched
  public static final EnumObject<TankType,SearedTankBlock> scorchedTank = BLOCKS.registerEnum("scorched", SearedTankBlock.TankType.values(), type -> new SearedTankBlock(SCORCHED_NON_SOLID, type.getCapacity()), b -> new TankItem(b, SMELTERY_PROPS, true));
  public static final ItemObject<SearedLanternBlock> scorchedLantern = BLOCKS.register("scorched_lantern", () -> new SearedLanternBlock(SCORCHED_NON_SOLID_TEMPLATE.apply(SoundType.LANTERN), FluidAttributes.BUCKET_VOLUME / 10), b -> new TankItem(b, SMELTERY_PROPS, false));
  public static final ItemObject<FaucetBlock> scorchedFaucet = BLOCKS.register("scorched_faucet", () -> new FaucetBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ChannelBlock> scorchedChannel = BLOCKS.register("scorched_channel", () -> new ChannelBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingBasinBlock> scorchedBasin = BLOCKS.register("scorched_basin", () -> new CastingBasinBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingTableBlock> scorchedTable = BLOCKS.register("scorched_table", () -> new CastingTableBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);

  // controllers
  private static final Supplier<Properties> SEARED_CONTROLLER = () -> builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(8.0F, 28F).setLightLevel(s -> s.get(ControllerBlock.ACTIVE) ? 13 : 0);
  private static final Supplier<Properties> SCORCHED_CONTROLLER = () -> builder(Material.ROCK, ToolType.PICKAXE, SoundType.BASALT).setRequiresTool().hardnessAndResistance(9.0F, 35f).setLightLevel(s -> s.get(ControllerBlock.ACTIVE) ? 13 : 0);
  public static final ItemObject<SmelteryControllerBlock> smelteryController = BLOCKS.register("smeltery_controller", () -> new SmelteryControllerBlock(SEARED_CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<FoundryControllerBlock> foundryController = BLOCKS.register("foundry_controller", () -> new FoundryControllerBlock(SCORCHED_CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);
  // tiny
  public static final ItemObject<MelterBlock> searedMelter = BLOCKS.register("seared_melter", () -> new MelterBlock(SEARED_CONTROLLER.get().notSolid()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<HeaterBlock> searedHeater = BLOCKS.register("seared_heater", () -> new HeaterBlock(SEARED_CONTROLLER.get()), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<AlloyerBlock> scorchedAlloyer = BLOCKS.register("scorched_alloyer", () -> new AlloyerBlock(SCORCHED_CONTROLLER.get().notSolid()), TOOLTIP_BLOCK_ITEM);

  /*
   * Tile entities
   */
  // smeltery
  public static final RegistryObject<TileEntityType<SmelteryComponentTileEntity>> smelteryComponent = TILE_ENTITIES.register("smeltery_component", SmelteryComponentTileEntity::new, set -> {
    // seared
    set.addAll(searedStone.values());
    set.addAll(searedCobble.values());
    set.addAll(searedBricks.values());
    set.addAll(searedPaver.values());
    set.add(searedCrackedBricks.get(), searedFancyBricks.get(), searedTriangleBricks.get(), searedLadder.get(), searedGlass.get());
    // scorched
    set.add(scorchedStone.get(), polishedScorchedStone.get(), chiseledScorchedBricks.get(), scorchedLadder.get());
    set.addAll(scorchedBricks.values());
    set.addAll(scorchedRoad.values());
  });
  public static final RegistryObject<TileEntityType<SmelteryFluidIO>> drain = TILE_ENTITIES.register("drain", DrainTileEntity::new, set -> set.add(searedDrain.get(), scorchedDrain.get()));
  public static final RegistryObject<TileEntityType<ChuteTileEntity>> chute = TILE_ENTITIES.register("chute", ChuteTileEntity::new, set -> set.add(searedChute.get(), scorchedChute.get()));
  public static final RegistryObject<TileEntityType<DuctTileEntity>> duct = TILE_ENTITIES.register("duct", DuctTileEntity::new, set -> set.add(searedDuct.get(), scorchedDuct.get()));
  public static final RegistryObject<TileEntityType<TankTileEntity>> tank = TILE_ENTITIES.register("tank", TankTileEntity::new, set -> {
    set.addAll(searedTank.values());
    set.addAll(scorchedTank.values());
  });
  public static final RegistryObject<TileEntityType<LanternTileEntity>> lantern = TILE_ENTITIES.register("lantern", LanternTileEntity::new, set -> set.add(searedLantern.get(), scorchedLantern.get()));
  // controller
  public static final RegistryObject<TileEntityType<MelterTileEntity>> melter = TILE_ENTITIES.register("melter", MelterTileEntity::new, searedMelter);
  public static final RegistryObject<TileEntityType<SmelteryTileEntity>> smeltery = TILE_ENTITIES.register("smeltery", SmelteryTileEntity::new, smelteryController);
  public static final RegistryObject<TileEntityType<FoundryTileEntity>> foundry = TILE_ENTITIES.register("foundry", FoundryTileEntity::new, foundryController);
  public static final RegistryObject<TileEntityType<HeaterTileEntity>> heater = TILE_ENTITIES.register("heater", HeaterTileEntity::new, searedHeater);
  public static final RegistryObject<TileEntityType<AlloyerTileEntity>> alloyer = TILE_ENTITIES.register("alloyer", AlloyerTileEntity::new, scorchedAlloyer);
  // fluid transfer
  public static final RegistryObject<TileEntityType<FaucetTileEntity>> faucet = TILE_ENTITIES.register("faucet", FaucetTileEntity::new, set -> set.add(searedFaucet.get(), scorchedFaucet.get()));
  public static final RegistryObject<TileEntityType<ChannelTileEntity>> channel = TILE_ENTITIES.register("channel", ChannelTileEntity::new, set -> set.add(searedChannel.get(), scorchedChannel.get()));
  // casting
  public static final RegistryObject<TileEntityType<CastingTileEntity>> basin = TILE_ENTITIES.register("basin", CastingTileEntity.Basin::new, set -> set.add(searedBasin.get(), scorchedBasin.get()));
  public static final RegistryObject<TileEntityType<CastingTileEntity>> table = TILE_ENTITIES.register("table", CastingTileEntity.Table::new, set -> set.add(searedTable.get(), scorchedTable.get()));

  /*
   * Items
   */
  public static final ItemObject<Item> searedBrick = ITEMS.register("seared_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> scorchedBrick = ITEMS.register("scorched_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> copperCan = ITEMS.register("copper_can", () -> new CopperCanItem(new Item.Properties().maxStackSize(16).group(TAB_SMELTERY)));

  // casts
  // basic
  public static final CastItemObject blankCast  = ITEMS.registerCast("blank",  SMELTERY_PROPS);
  public static final CastItemObject ingotCast  = ITEMS.registerCast("ingot",  SMELTERY_PROPS);
  public static final CastItemObject nuggetCast = ITEMS.registerCast("nugget", SMELTERY_PROPS);
  public static final CastItemObject gemCast    = ITEMS.registerCast("gem",    SMELTERY_PROPS);
  public static final CastItemObject rodCast    = ITEMS.registerCast("rod",   SMELTERY_PROPS);
  public static final CastItemObject repairKitCast = ITEMS.registerCast("repair_kit", SMELTERY_PROPS);
  // compatability
  public static final CastItemObject plateCast  = ITEMS.registerCast("plate", SMELTERY_PROPS);
  public static final CastItemObject gearCast   = ITEMS.registerCast("gear",  SMELTERY_PROPS);
  public static final CastItemObject coinCast   = ITEMS.registerCast("coin",  SMELTERY_PROPS);
  // small tool heads
  public static final CastItemObject pickaxeHeadCast  = ITEMS.registerCast("pickaxe_head", SMELTERY_PROPS);
  public static final CastItemObject smallAxeHeadCast = ITEMS.registerCast("small_axe_head", SMELTERY_PROPS);
  public static final CastItemObject smallBladeCast = ITEMS.registerCast("small_blade", SMELTERY_PROPS);
  //  public static final ItemObject<Item> signHeadCast = ITEMS.register("sign_head_cast", SMELTERY_PROPS);
  //  public static final ItemObject<Item> bowLimbCast = ITEMS.register("bow_limb_cast", SMELTERY_PROPS);
  // large tool heads
  public static final CastItemObject hammerHeadCast   = ITEMS.registerCast("hammer_head", SMELTERY_PROPS);
  public static final CastItemObject broadBladeCast   = ITEMS.registerCast("broad_blade", SMELTERY_PROPS);
  public static final CastItemObject broadAxeHeadCast = ITEMS.registerCast("broad_axe_head", SMELTERY_PROPS);
  //  public static final ItemObject<Item> scytheHeadCast = ITEMS.register("scythe_head_cast", SMELTERY_PROPS);
  // bindings
  public static final CastItemObject toolBindingCast = ITEMS.registerCast("tool_binding", SMELTERY_PROPS);
  public static final CastItemObject largePlateCast  = ITEMS.registerCast("large_plate", SMELTERY_PROPS);
  // tool rods
  public static final CastItemObject toolHandleCast  = ITEMS.registerCast("tool_handle", SMELTERY_PROPS);
  public static final CastItemObject toughHandleCast = ITEMS.registerCast("tough_handle", SMELTERY_PROPS);

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
  public static final RegistryObject<MaterialFluidRecipe.Serializer> materialFluidRecipe = RECIPE_SERIALIZERS.register("material_fluid", MaterialFluidRecipe.Serializer::new);
  // molding
  public static final RegistryObject<MoldingRecipe.Serializer<MoldingRecipe.Table>> moldingTableSerializer = RECIPE_SERIALIZERS.register("molding_table", () -> new MoldingRecipe.Serializer<>(MoldingRecipe.Table::new));
  public static final RegistryObject<MoldingRecipe.Serializer<MoldingRecipe.Basin>> moldingBasinSerializer = RECIPE_SERIALIZERS.register("molding_basin", () -> new MoldingRecipe.Serializer<>(MoldingRecipe.Basin::new));
  // melting
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> meltingSerializer = RECIPE_SERIALIZERS.register("melting", () -> new MeltingRecipe.Serializer<>(MeltingRecipe::new));
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> oreMeltingSerializer = RECIPE_SERIALIZERS.register("ore_melting", () -> new MeltingRecipe.Serializer<>(OreMeltingRecipe::new));
  public static final RegistryObject<IRecipeSerializer<MeltingRecipe>> damagableMeltingSerializer = RECIPE_SERIALIZERS.register("damagable_melting", () -> new MeltingRecipe.Serializer<>(DamageableMeltingRecipe::new));
  public static final RegistryObject<IRecipeSerializer<MaterialMeltingRecipe>> materialMeltingSerializer = RECIPE_SERIALIZERS.register("material_melting", MaterialMeltingRecipe.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<MeltingFuel>> fuelSerializer = RECIPE_SERIALIZERS.register("melting_fuel", MeltingFuel.Serializer::new);
  public static final RegistryObject<IRecipeSerializer<EntityMeltingRecipe>> entityMeltingSerializer = RECIPE_SERIALIZERS.register("entity_melting", EntityMeltingRecipe.Serializer::new);
  // alloying
  public static final RegistryObject<IRecipeSerializer<AlloyRecipe>> alloyingSerializer = RECIPE_SERIALIZERS.register("alloy", AlloyRecipe.Serializer::new);

  /*
   * Inventory
   */
  public static final RegistryObject<ContainerType<MelterContainer>> melterContainer = CONTAINERS.register("melter", MelterContainer::new);
  public static final RegistryObject<ContainerType<HeatingStructureContainer>> smelteryContainer = CONTAINERS.register("smeltery", HeatingStructureContainer::new);
  public static final RegistryObject<ContainerType<SingleItemContainer>> singleItemContainer = CONTAINERS.register("single_item", SingleItemContainer::new);
  public static final RegistryObject<ContainerType<AlloyerContainer>> alloyerContainer = CONTAINERS.register("alloyer", AlloyerContainer::new);

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new SmelteryRecipeProvider(datagenerator));
    }
  }
}
