package slimeknights.tconstruct.smeltery;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.mantle.util.SupplierCreativeTab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.item.EmptyPotionTransfer;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.PotionCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.RetexturedCastingRecipe;
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
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.shared.block.PlaceBlockDispenserBehavior;
import slimeknights.tconstruct.shared.block.SoulGlassPaneBlock;
import slimeknights.tconstruct.smeltery.block.CastingBasinBlock;
import slimeknights.tconstruct.smeltery.block.CastingTableBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.SearedLanternBlock;
import slimeknights.tconstruct.smeltery.block.component.OrientableSmelteryBlock;
import slimeknights.tconstruct.smeltery.block.component.RetexturedOrientableSmelteryBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedDrainBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedDuctBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedGlassBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedLadderBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedPillarBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedSoulGlassBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.block.component.SearedTintedGlassBlock;
import slimeknights.tconstruct.smeltery.block.controller.AlloyerBlock;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.FoundryControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.HeaterBlock;
import slimeknights.tconstruct.smeltery.block.controller.MelterBlock;
import slimeknights.tconstruct.smeltery.block.controller.SmelteryControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.HeaterBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.LanternBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.DrainBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.DuctBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity.ChuteBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.MelterBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.SmelteryBlockEntity;
import slimeknights.tconstruct.smeltery.data.FluidContainerTransferProvider;
import slimeknights.tconstruct.smeltery.data.SmelteryRecipeProvider;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.menu.AlloyerContainerMenu;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;
import slimeknights.tconstruct.smeltery.menu.MelterContainerMenu;
import slimeknights.tconstruct.smeltery.menu.SingleItemContainerMenu;
import slimeknights.tconstruct.tables.item.TableBlockItem;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Contains logic for the multiblocks in the mod
 */
@SuppressWarnings("unused")
public final class TinkerSmeltery extends TinkerModule {
  /** Tab for all blocks related to the smeltery */
  public static final CreativeModeTab TAB_SMELTERY = new SupplierCreativeTab(TConstruct.MOD_ID, "smeltery", () -> new ItemStack(TinkerSmeltery.smelteryController));
  public static final Logger log = Util.getLogger("tinker_smeltery");

  /* Bricks */
  /* Crafting related items */

  /*
   * Block base properties
   */
  private static final Item.Properties SMELTERY_PROPS = new Item.Properties().tab(TAB_SMELTERY);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, SMELTERY_PROPS);

  /*
   * Blocks
   */
  public static final ItemObject<Block> grout = BLOCKS.register("grout", builder(Material.SAND, MaterialColor.COLOR_LIGHT_GRAY, SoundType.SAND).strength(3.0f).friction(0.8F), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<Block> netherGrout = BLOCKS.register("nether_grout", builder(Material.SAND, SoundType.SOUL_SOIL).strength(3.0f).friction(0.8F), TOOLTIP_BLOCK_ITEM);

  // seared blocks
  private static final Properties SEARED, TOUGH_SEARED, SEARED_GLASS, SEARED_SOUL_GLASS, SEARED_NON_SOLID, SEARED_LANTERN;
  static {
    // solid
    IntFunction<Properties> solidProps = factor ->
      builder(Material.STONE, MaterialColor.COLOR_GRAY, SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F * factor, 9.0F * factor)
                                                                                   .isValidSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE));
    SEARED = solidProps.apply(1);
    TOUGH_SEARED = solidProps.apply(2);
    // non-solid
    Function<SoundType,Properties> nonSolidProps = sound -> builder(Material.STONE, MaterialColor.COLOR_GRAY, sound)
      .requiresCorrectToolForDrops().strength(3.0F, 9.0F).noOcclusion()
      .isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never);
    SEARED_GLASS = nonSolidProps.apply(SoundType.GLASS);
    SEARED_SOUL_GLASS = nonSolidProps.apply(SoundType.GLASS).noCollission().speedFactor(0.1f).isViewBlocking((state, getter, pos) -> true);
    SEARED_NON_SOLID = nonSolidProps.apply(SoundType.METAL);
    SEARED_LANTERN = nonSolidProps.apply(SoundType.LANTERN);
  }
  // blocks
  public static final BuildingBlockObject searedStone, searedPaver;
  public static final WallBuildingBlockObject searedCobble, searedBricks;
  public static final ItemObject<Block> searedCrackedBricks, searedFancyBricks, searedTriangleBricks;
  static {
    Supplier<SearedBlock> searedBlock = () -> new SearedBlock(SEARED);
    searedStone = BLOCKS.registerBuilding("seared_stone", searedBlock, TOOLTIP_BLOCK_ITEM);
    searedCobble = BLOCKS.registerWallBuilding("seared_cobble", searedBlock, TOOLTIP_BLOCK_ITEM);
    searedPaver = BLOCKS.registerBuilding("seared_paver", searedBlock, TOOLTIP_BLOCK_ITEM);
    searedBricks = BLOCKS.registerWallBuilding("seared_bricks", searedBlock, TOOLTIP_BLOCK_ITEM);
    searedCrackedBricks = BLOCKS.register("seared_cracked_bricks", searedBlock, TOOLTIP_BLOCK_ITEM);
    searedFancyBricks = BLOCKS.register("seared_fancy_bricks", searedBlock, TOOLTIP_BLOCK_ITEM);
    searedTriangleBricks = BLOCKS.register("seared_triangle_bricks", searedBlock, TOOLTIP_BLOCK_ITEM);
  }
  public static final ItemObject<SearedLadderBlock> searedLadder = BLOCKS.register("seared_ladder", () -> new SearedLadderBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedGlassBlock> searedGlass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(SEARED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedSoulGlassBlock> searedSoulGlass = BLOCKS.register("seared_soul_glass", () -> new SearedSoulGlassBlock(SEARED_SOUL_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedTintedGlassBlock> searedTintedGlass = BLOCKS.register("seared_tinted_glass", () -> new SearedTintedGlassBlock(SEARED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> searedGlassPane = BLOCKS.register("seared_glass_pane", () -> new ClearGlassPaneBlock(SEARED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SoulGlassPaneBlock> searedSoulGlassPane = BLOCKS.register("seared_soul_glass_pane", () -> new SoulGlassPaneBlock(SEARED_SOUL_GLASS), TOOLTIP_BLOCK_ITEM);
  // peripherals
  private static final Function<Block, ? extends BlockItem> SEARED_IO_BLOCK_ITEM = block -> new TableBlockItem(block, TinkerTags.Items.SMELTERY_BRICKS, SMELTERY_PROPS, Config.COMMON.showAllSmelteryVariants::get);
  public static final ItemObject<Block> searedDrain = BLOCKS.register("seared_drain", () -> new SearedDrainBlock(TOUGH_SEARED), SEARED_IO_BLOCK_ITEM);
  public static final ItemObject<Block> searedDuct = BLOCKS.register("seared_duct", () -> new SearedDuctBlock(TOUGH_SEARED), SEARED_IO_BLOCK_ITEM);
  public static final ItemObject<Block> searedChute = BLOCKS.register("seared_chute", () -> new RetexturedOrientableSmelteryBlock(TOUGH_SEARED, ChuteBlockEntity::new), SEARED_IO_BLOCK_ITEM);

  // scorched blocks
  private static final Properties SCORCHED, TOUGH_SCORCHED, SCORCHED_GLASS, SCORCHED_SOUL_GLASS, SCORCHED_NON_SOLID, SCORCHED_LANTERN;
  static {
    IntFunction<Properties> solidProps = factor -> builder(Material.STONE, MaterialColor.TERRACOTTA_BROWN, SoundType.BASALT)
      .requiresCorrectToolForDrops().strength(2.5F * factor, 8.0F * factor).isValidSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE));
    SCORCHED = solidProps.apply(1);
    TOUGH_SCORCHED = solidProps.apply(3);
    Function<SoundType,Properties> nonSolidProps = sound -> builder(Material.STONE, MaterialColor.TERRACOTTA_BROWN, sound)
      .requiresCorrectToolForDrops().strength(2.5F, 8.0F).noOcclusion()
      .isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never);
    SCORCHED_GLASS = nonSolidProps.apply(SoundType.GLASS);
    SCORCHED_SOUL_GLASS = nonSolidProps.apply(SoundType.GLASS).noCollission().speedFactor(0.1f).isViewBlocking((state, getter, pos) -> true);
    SCORCHED_NON_SOLID = nonSolidProps.apply(SoundType.BASALT);
    SCORCHED_LANTERN = nonSolidProps.apply(SoundType.LANTERN);
  }

  // blocks
  public static final ItemObject<Block> scorchedStone, polishedScorchedStone, chiseledScorchedBricks;
  public static final FenceBuildingBlockObject scorchedBricks;
  public static final BuildingBlockObject scorchedRoad;
  static {
    Supplier<SearedPillarBlock> pillar = () -> new SearedPillarBlock(SCORCHED);
    scorchedStone = BLOCKS.register("scorched_stone", pillar, TOOLTIP_BLOCK_ITEM);
    polishedScorchedStone = BLOCKS.register("polished_scorched_stone", pillar, TOOLTIP_BLOCK_ITEM);
    Supplier<SearedBlock> block = () -> new SearedBlock(SCORCHED);
    scorchedBricks = BLOCKS.registerFenceBuilding("scorched_bricks", block, TOOLTIP_BLOCK_ITEM);
    scorchedRoad = BLOCKS.registerBuilding("scorched_road", block, TOOLTIP_BLOCK_ITEM);
    chiseledScorchedBricks = BLOCKS.register("chiseled_scorched_bricks", block, TOOLTIP_BLOCK_ITEM);
  }
  public static final ItemObject<SearedLadderBlock> scorchedLadder = BLOCKS.register("scorched_ladder", () -> new SearedLadderBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedGlassBlock> scorchedGlass = BLOCKS.register("scorched_glass", () -> new SearedGlassBlock(SCORCHED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedSoulGlassBlock> scorchedSoulGlass = BLOCKS.register("scorched_soul_glass", () -> new SearedSoulGlassBlock(SCORCHED_SOUL_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SearedTintedGlassBlock> scorchedTintedGlass = BLOCKS.register("scorched_tinted_glass", () -> new SearedTintedGlassBlock(SCORCHED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> scorchedGlassPane = BLOCKS.register("scorched_glass_pane", () -> new ClearGlassPaneBlock(SCORCHED_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<SoulGlassPaneBlock> scorchedSoulGlassPane = BLOCKS.register("scorched_soul_glass_pane", () -> new SoulGlassPaneBlock(SCORCHED_SOUL_GLASS), TOOLTIP_BLOCK_ITEM);
  // peripherals
  private static final Function<Block, ? extends BlockItem> SCORCHED_IO_BLOCK_ITEM = block -> new TableBlockItem(block, TinkerTags.Items.FOUNDRY_BRICKS, SMELTERY_PROPS, Config.COMMON.showAllSmelteryVariants::get);
  public static final ItemObject<Block> scorchedDrain = BLOCKS.register("scorched_drain", () -> new SearedDrainBlock(TOUGH_SCORCHED), SCORCHED_IO_BLOCK_ITEM);
  public static final ItemObject<Block> scorchedDuct = BLOCKS.register("scorched_duct", () -> new SearedDuctBlock(TOUGH_SCORCHED), SCORCHED_IO_BLOCK_ITEM);
  public static final ItemObject<Block> scorchedChute = BLOCKS.register("scorched_chute", () -> new OrientableSmelteryBlock(TOUGH_SCORCHED, ChuteBlockEntity::new), SCORCHED_IO_BLOCK_ITEM);

  // seared
  public static final EnumObject<TankType,SearedTankBlock> searedTank = BLOCKS.registerEnum("seared", SearedTankBlock.TankType.values(), type -> new SearedTankBlock(SEARED_NON_SOLID, type.getCapacity(), PushReaction.DESTROY), b -> new TankItem(b, SMELTERY_PROPS, true));
  public static final ItemObject<SearedLanternBlock> searedLantern = BLOCKS.register("seared_lantern", () -> new SearedLanternBlock(SEARED_LANTERN, FluidValues.LANTERN_CAPACITY), b -> new TankItem(b, SMELTERY_PROPS, false));
  public static final ItemObject<FaucetBlock> searedFaucet = BLOCKS.register("seared_faucet", () -> new FaucetBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ChannelBlock> searedChannel = BLOCKS.register("seared_channel", () -> new ChannelBlock(SEARED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingBasinBlock> searedBasin = BLOCKS.register("seared_basin", () -> new CastingBasinBlock(SEARED_NON_SOLID, false), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingTableBlock> searedTable = BLOCKS.register("seared_table", () -> new CastingTableBlock(SEARED_NON_SOLID, false), TOOLTIP_BLOCK_ITEM);
  // scorched
  public static final EnumObject<TankType,SearedTankBlock> scorchedTank = BLOCKS.registerEnum("scorched", SearedTankBlock.TankType.values(), type -> new SearedTankBlock(SCORCHED_NON_SOLID, type.getCapacity(), PushReaction.DESTROY), b -> new TankItem(b, SMELTERY_PROPS, true));
  public static final ItemObject<SearedLanternBlock> scorchedLantern = BLOCKS.register("scorched_lantern", () -> new SearedLanternBlock(SCORCHED_LANTERN, FluidValues.LANTERN_CAPACITY), b -> new TankItem(b, SMELTERY_PROPS, false));
  public static final ItemObject<FaucetBlock> scorchedFaucet = BLOCKS.register("scorched_faucet", () -> new FaucetBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ChannelBlock> scorchedChannel = BLOCKS.register("scorched_channel", () -> new ChannelBlock(SCORCHED_NON_SOLID), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingBasinBlock> scorchedBasin = BLOCKS.register("scorched_basin", () -> new CastingBasinBlock(SCORCHED_NON_SOLID, true), TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<CastingTableBlock> scorchedTable = BLOCKS.register("scorched_table", () -> new CastingTableBlock(SCORCHED_NON_SOLID, true), TOOLTIP_BLOCK_ITEM);

  // controllers
  public static final ItemObject<SmelteryControllerBlock> smelteryController;
  public static final ItemObject<FoundryControllerBlock> foundryController;
  // tiny
  public static final ItemObject<MelterBlock> searedMelter;
  public static final ItemObject<HeaterBlock> searedHeater;
  public static final ItemObject<AlloyerBlock> scorchedAlloyer;
  static {
    Supplier<Properties> seared = () -> builder(Material.STONE, MaterialColor.COLOR_GRAY, SoundType.METAL).requiresCorrectToolForDrops().strength(8.0F, 28F).lightLevel(s -> s.getValue(ControllerBlock.ACTIVE) ? 13 : 0);
    Supplier<Properties> scorched = () -> builder(Material.STONE, MaterialColor.TERRACOTTA_BROWN, SoundType.BASALT).requiresCorrectToolForDrops().strength(9.0F, 35f).lightLevel(s -> s.getValue(ControllerBlock.ACTIVE) ? 13 : 0);
    smelteryController = BLOCKS.register("smeltery_controller", () -> new SmelteryControllerBlock(seared.get()),  SEARED_IO_BLOCK_ITEM);
    foundryController = BLOCKS.register("foundry_controller", () -> new FoundryControllerBlock(scorched.get()),  SCORCHED_IO_BLOCK_ITEM);
    // tiny
    searedMelter = BLOCKS.register("seared_melter", () -> new MelterBlock(seared.get().noOcclusion()), TOOLTIP_BLOCK_ITEM);
    searedHeater = BLOCKS.register("seared_heater", () -> new HeaterBlock(seared.get()), TOOLTIP_BLOCK_ITEM);
    scorchedAlloyer = BLOCKS.register("scorched_alloyer", () -> new AlloyerBlock(scorched.get().noOcclusion()), TOOLTIP_BLOCK_ITEM);
  }

  /*
   * Tile entities
   */
  // smeltery
  public static final RegistryObject<BlockEntityType<SmelteryComponentBlockEntity>> smelteryComponent = BLOCK_ENTITIES.register("smeltery_component", SmelteryComponentBlockEntity::new, set -> {
    // seared
    set.addAll(searedStone.values());
    set.addAll(searedCobble.values());
    set.addAll(searedBricks.values());
    set.addAll(searedPaver.values());
    set.add(searedCrackedBricks.get(), searedFancyBricks.get(), searedTriangleBricks.get(), searedLadder.get(), searedGlass.get(), searedSoulGlass.get(), searedTintedGlass.get());
    // scorched
    set.add(scorchedStone.get(), polishedScorchedStone.get(), chiseledScorchedBricks.get(), scorchedLadder.get(), scorchedGlass.get(), scorchedSoulGlass.get(), scorchedTintedGlass.get());
    set.addAll(scorchedBricks.values());
    set.addAll(scorchedRoad.values());
  });
  public static final RegistryObject<BlockEntityType<SmelteryFluidIO>> drain = BLOCK_ENTITIES.register("drain", DrainBlockEntity::new, set -> set.add(searedDrain.get(), scorchedDrain.get()));
  public static final RegistryObject<BlockEntityType<ChuteBlockEntity>> chute = BLOCK_ENTITIES.register("chute", ChuteBlockEntity::new, set -> set.add(searedChute.get(), scorchedChute.get()));
  public static final RegistryObject<BlockEntityType<DuctBlockEntity>> duct = BLOCK_ENTITIES.register("duct", DuctBlockEntity::new, set -> set.add(searedDuct.get(), scorchedDuct.get()));
  public static final RegistryObject<BlockEntityType<TankBlockEntity>> tank = BLOCK_ENTITIES.register("tank", TankBlockEntity::new, set -> {
    set.addAll(searedTank.values());
    set.addAll(scorchedTank.values());
  });
  public static final RegistryObject<BlockEntityType<LanternBlockEntity>> lantern = BLOCK_ENTITIES.register("lantern", LanternBlockEntity::new, set -> set.add(searedLantern.get(), scorchedLantern.get()));
  // controller
  public static final RegistryObject<BlockEntityType<MelterBlockEntity>> melter = BLOCK_ENTITIES.register("melter", MelterBlockEntity::new, searedMelter);
  public static final RegistryObject<BlockEntityType<SmelteryBlockEntity>> smeltery = BLOCK_ENTITIES.register("smeltery", SmelteryBlockEntity::new, smelteryController);
  public static final RegistryObject<BlockEntityType<FoundryBlockEntity>> foundry = BLOCK_ENTITIES.register("foundry", FoundryBlockEntity::new, foundryController);
  public static final RegistryObject<BlockEntityType<HeaterBlockEntity>> heater = BLOCK_ENTITIES.register("heater", HeaterBlockEntity::new, searedHeater);
  public static final RegistryObject<BlockEntityType<AlloyerBlockEntity>> alloyer = BLOCK_ENTITIES.register("alloyer", AlloyerBlockEntity::new, scorchedAlloyer);
  // fluid transfer
  public static final RegistryObject<BlockEntityType<FaucetBlockEntity>> faucet = BLOCK_ENTITIES.register("faucet", FaucetBlockEntity::new, set -> set.add(searedFaucet.get(), scorchedFaucet.get()));
  public static final RegistryObject<BlockEntityType<ChannelBlockEntity>> channel = BLOCK_ENTITIES.register("channel", ChannelBlockEntity::new, set -> set.add(searedChannel.get(), scorchedChannel.get()));
  // casting
  public static final RegistryObject<BlockEntityType<CastingBlockEntity>> basin = BLOCK_ENTITIES.register("basin", CastingBlockEntity.Basin::new, set -> set.add(searedBasin.get(), scorchedBasin.get()));
  public static final RegistryObject<BlockEntityType<CastingBlockEntity>> table = BLOCK_ENTITIES.register("table", CastingBlockEntity.Table::new, set -> set.add(searedTable.get(), scorchedTable.get()));

  /*
   * Items
   */
  public static final ItemObject<Item> searedBrick = ITEMS.register("seared_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> scorchedBrick = ITEMS.register("scorched_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> copperCan = ITEMS.register("copper_can", () -> new CopperCanItem(new Item.Properties().stacksTo(16).tab(TAB_SMELTERY)));

  // casts
  // basic
  public static final ItemObject<Item> blankSandCast  = ITEMS.register("blank_sand_cast",  SMELTERY_PROPS);
  public static final ItemObject<Item> blankRedSandCast  = ITEMS.register("blank_red_sand_cast",  SMELTERY_PROPS);
  public static final CastItemObject ingotCast  = ITEMS.registerCast("ingot",  SMELTERY_PROPS);
  public static final CastItemObject nuggetCast = ITEMS.registerCast("nugget", SMELTERY_PROPS);
  public static final CastItemObject gemCast    = ITEMS.registerCast("gem",    SMELTERY_PROPS);
  public static final CastItemObject rodCast    = ITEMS.registerCast("rod",   SMELTERY_PROPS);
  public static final CastItemObject repairKitCast = ITEMS.registerCast("repair_kit", SMELTERY_PROPS);
  // compatability
  public static final CastItemObject plateCast  = ITEMS.registerCast("plate", SMELTERY_PROPS);
  public static final CastItemObject gearCast   = ITEMS.registerCast("gear",  SMELTERY_PROPS);
  public static final CastItemObject coinCast   = ITEMS.registerCast("coin",  SMELTERY_PROPS);
  public static final CastItemObject wireCast   = ITEMS.registerCast("wire",  SMELTERY_PROPS);
  // small tool heads
  public static final CastItemObject pickHeadCast = ITEMS.registerCast("pick_head", SMELTERY_PROPS);
  public static final CastItemObject smallAxeHeadCast = ITEMS.registerCast("small_axe_head", SMELTERY_PROPS);
  public static final CastItemObject smallBladeCast = ITEMS.registerCast("small_blade", SMELTERY_PROPS);
  // large tool heads
  public static final CastItemObject hammerHeadCast   = ITEMS.registerCast("hammer_head", SMELTERY_PROPS);
  public static final CastItemObject broadBladeCast   = ITEMS.registerCast("broad_blade", SMELTERY_PROPS);
  public static final CastItemObject broadAxeHeadCast = ITEMS.registerCast("broad_axe_head", SMELTERY_PROPS);
  // bindings
  public static final CastItemObject toolBindingCast = ITEMS.registerCast("tool_binding", SMELTERY_PROPS);
  public static final CastItemObject roundPlateCast  = ITEMS.registerCast("round_plate", SMELTERY_PROPS);
  public static final CastItemObject largePlateCast  = ITEMS.registerCast("large_plate", SMELTERY_PROPS);
  // tool rods
  public static final CastItemObject toolHandleCast  = ITEMS.registerCast("tool_handle", SMELTERY_PROPS);
  public static final CastItemObject toughHandleCast = ITEMS.registerCast("tough_handle", SMELTERY_PROPS);
  // bow
  public static final CastItemObject bowLimbCast = ITEMS.registerCast("bow_limb", SMELTERY_PROPS);
  public static final CastItemObject bowGripCast = ITEMS.registerCast("bow_grip", SMELTERY_PROPS);

  /*
   * Recipe
   */
  // casting
  public static final RegistryObject<ItemCastingRecipe.Serializer<ItemCastingRecipe.Basin>> basinRecipeSerializer = RECIPE_SERIALIZERS.register("casting_basin", () -> new ItemCastingRecipe.Serializer<>(ItemCastingRecipe.Basin::new));
  public static final RegistryObject<ItemCastingRecipe.Serializer<ItemCastingRecipe.Table>> tableRecipeSerializer = RECIPE_SERIALIZERS.register("casting_table", () -> new ItemCastingRecipe.Serializer<>(ItemCastingRecipe.Table::new));
  public static final RegistryObject<ContainerFillingRecipeSerializer<ContainerFillingRecipe.Basin>> basinFillingRecipeSerializer = RECIPE_SERIALIZERS.register("basin_filling", () -> new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Basin::new));
  public static final RegistryObject<ContainerFillingRecipeSerializer<ContainerFillingRecipe.Table>> tableFillingRecipeSerializer = RECIPE_SERIALIZERS.register("table_filling", () -> new ContainerFillingRecipeSerializer<>(ContainerFillingRecipe.Table::new));
  public static final RegistryObject<PotionCastingRecipe.Serializer> basinPotionRecipeSerializer = RECIPE_SERIALIZERS.register("casting_basin_potion", () -> new PotionCastingRecipe.Serializer(TinkerRecipeTypes.CASTING_BASIN));
  public static final RegistryObject<PotionCastingRecipe.Serializer> tablePotionRecipeSerializer = RECIPE_SERIALIZERS.register("casting_table_potion", () -> new PotionCastingRecipe.Serializer(TinkerRecipeTypes.CASTING_TABLE));
  public static final RegistryObject<ItemCastingRecipe.Serializer<RetexturedCastingRecipe.Basin>> retexturedBasinRecipeSerializer = RECIPE_SERIALIZERS.register("retextured_casting_basin", () -> new ItemCastingRecipe.Serializer<>(RetexturedCastingRecipe.Basin::new));
  public static final RegistryObject<ItemCastingRecipe.Serializer<RetexturedCastingRecipe.Table>> retexturedTableRecipeSerializer = RECIPE_SERIALIZERS.register("retextured_casting_table", () -> new ItemCastingRecipe.Serializer<>(RetexturedCastingRecipe.Table::new));
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
  public static final RegistryObject<RecipeSerializer<MeltingRecipe>> meltingSerializer = RECIPE_SERIALIZERS.register("melting", () -> new MeltingRecipe.Serializer<>(MeltingRecipe::new));
  public static final RegistryObject<RecipeSerializer<OreMeltingRecipe>> oreMeltingSerializer = RECIPE_SERIALIZERS.register("ore_melting", OreMeltingRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<DamageableMeltingRecipe>> damagableMeltingSerializer = RECIPE_SERIALIZERS.register("damagable_melting", DamageableMeltingRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<MaterialMeltingRecipe>> materialMeltingSerializer = RECIPE_SERIALIZERS.register("material_melting", MaterialMeltingRecipe.Serializer::new);
  public static final RegistryObject<RecipeSerializer<MeltingFuel>> fuelSerializer = RECIPE_SERIALIZERS.register("melting_fuel", MeltingFuel.Serializer::new);
  public static final RegistryObject<RecipeSerializer<EntityMeltingRecipe>> entityMeltingSerializer = RECIPE_SERIALIZERS.register("entity_melting", EntityMeltingRecipe.Serializer::new);
  // alloying
  public static final RegistryObject<RecipeSerializer<AlloyRecipe>> alloyingSerializer = RECIPE_SERIALIZERS.register("alloy", AlloyRecipe.Serializer::new);

  /*
   * Inventory
   */
  public static final RegistryObject<MenuType<MelterContainerMenu>> melterContainer = MENUS.register("melter", MelterContainerMenu::new);
  public static final RegistryObject<MenuType<HeatingStructureContainerMenu>> smelteryContainer = MENUS.register("smeltery", HeatingStructureContainerMenu::new);
  public static final RegistryObject<MenuType<SingleItemContainerMenu>> singleItemContainer = MENUS.register("single_item", SingleItemContainerMenu::new);
  public static final RegistryObject<MenuType<AlloyerContainerMenu>> alloyerContainer = MENUS.register("alloyer", AlloyerContainerMenu::new);

  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      Consumer<Block> dispenserBehavior = block -> DispenserBlock.registerBehavior(block.asItem(), PlaceBlockDispenserBehavior.INSTANCE);
      searedTank.forEach(dispenserBehavior);
      scorchedTank.forEach(dispenserBehavior);
    });
  }

  @SubscribeEvent
  void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(EmptyPotionTransfer.ID, EmptyPotionTransfer.DESERIALIZER);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new SmelteryRecipeProvider(datagenerator));
      datagenerator.addProvider(new FluidContainerTransferProvider(datagenerator));
    }
  }
}
