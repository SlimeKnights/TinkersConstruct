package slimeknights.tconstruct.smeltery;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.BucketCastingRecipe;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.smeltery.OreCastingRecipe;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController;
import slimeknights.tconstruct.smeltery.block.BlockSearedGlass;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab2;
import slimeknights.tconstruct.smeltery.block.BlockSearedStairs;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryIO;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.block.BlockTinkerTankController;
import slimeknights.tconstruct.smeltery.item.CastCustom;
import slimeknights.tconstruct.smeltery.item.ItemTank;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = TinkerSmeltery.PulseId, description = "The smeltery and items needed for it")
public class TinkerSmeltery extends TinkerPulse {

  public static final String PulseId = "TinkerSmeltery";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.smeltery.SmelteryClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static BlockSeared searedBlock;
  public static BlockSmelteryController smelteryController;
  public static BlockTank searedTank;
  public static BlockFaucet faucet;
  public static BlockCasting castingBlock;
  public static BlockSmelteryIO smelteryIO;
  public static Block searedGlass;

  public static Block searedFurnaceController;
  public static Block tinkerTankController;

  public static Block searedSlab;
  public static Block searedSlab2;

  // stairs
  public static Block searedStairsStone;
  public static Block searedStairsCobble;
  public static Block searedStairsPaver;
  public static Block searedStairsBrick;
  public static Block searedStairsBrickCracked;
  public static Block searedStairsBrickFancy;
  public static Block searedStairsBrickSquare;
  public static Block searedStairsBrickTriangle;
  public static Block searedStairsBrickSmall;
  public static Block searedStairsRoad;
  public static Block searedStairsTile;
  public static Block searedStairsCreeper;

  // Items
  public static Cast cast;
  public static CastCustom castCustom;
  public static Cast clayCast;

  // itemstacks!
  public static ItemStack castIngot;
  public static ItemStack castNugget;
  public static ItemStack castGem;
  public static ItemStack castShard;
  public static ItemStack castPlate;
  public static ItemStack castGear;

  private static Map<Fluid, Set<Pair<List<ItemStack>, Integer>>> knownOreFluids = Maps.newHashMap();
  public static List<FluidStack> castCreationFluids = Lists.newLinkedList();
  public static List<FluidStack> clayCreationFluids = Lists.newLinkedList();

  public static ImmutableSet<Block> validSmelteryBlocks;
  public static ImmutableSet<Block> validSearedFurnaceBlocks;
  public static ImmutableSet<Block> validTinkerTankBlocks;
  public static List<ItemStack> meltingBlacklist = Lists.newLinkedList();

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    searedBlock = registerEnumBlock(new BlockSeared(), "seared");
    smelteryController = registerBlock(new BlockSmelteryController(), "smeltery_controller");
    searedTank = registerBlock(new ItemTank(new BlockTank()), "seared_tank");
    faucet = registerBlock(new BlockFaucet(), "faucet");
    castingBlock = registerBlock(new ItemBlockMeta(new BlockCasting()), "casting");
    smelteryIO = registerEnumBlock(new BlockSmelteryIO(), "smeltery_io");
    searedGlass = registerEnumBlock(new BlockSearedGlass(), "seared_glass");

    searedFurnaceController = registerBlock(new BlockSearedFurnaceController(), "seared_furnace_controller");
    tinkerTankController = registerBlock(new BlockTinkerTankController(), "tinker_tank_controller");

    ItemBlockMeta.setMappingProperty(searedTank, BlockTank.TYPE);
    ItemBlockMeta.setMappingProperty(castingBlock, BlockCasting.TYPE);

    // slabs
    searedSlab = registerEnumBlockSlab(new BlockSearedSlab(), "seared_slab");
    searedSlab2 = registerEnumBlockSlab(new BlockSearedSlab2(), "seared_slab2");

    // stairs
    searedStairsStone = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.STONE, "seared_stairs_stone");
    searedStairsCobble = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.COBBLE, "seared_stairs_cobble");
    searedStairsPaver = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.PAVER, "seared_stairs_paver");
    searedStairsBrick = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.BRICK, "seared_stairs_brick");
    searedStairsBrickCracked = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.BRICK_CRACKED, "seared_stairs_brick_cracked");
    searedStairsBrickFancy = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.BRICK_FANCY, "seared_stairs_brick_fancy");
    searedStairsBrickSquare = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.BRICK_SQUARE, "seared_stairs_brick_square");
    searedStairsBrickTriangle = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.BRICK_TRIANGLE, "seared_stairs_brick_triangle");
    searedStairsBrickSmall = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.BRICK_SMALL, "seared_stairs_brick_small");
    searedStairsRoad = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.ROAD, "seared_stairs_road");
    searedStairsTile = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.TILE, "seared_stairs_tile");
    searedStairsCreeper = registerBlockSearedStairsFrom(searedBlock, BlockSeared.SearedType.CREEPER, "seared_stairs_creeper");

    registerTE(TileSmeltery.class, "smeltery_controller");
    registerTE(TileSmelteryComponent.class, "smeltery_component");
    registerTE(TileTank.class, "tank");
    registerTE(TileFaucet.class, "faucet");
    registerTE(TileCastingTable.class, "casting_table");
    registerTE(TileCastingBasin.class, "casting_basin");
    registerTE(TileDrain.class, "smeltery_drain");
    registerTE(TileSearedFurnace.class, "seared_furnace");
    registerTE(TileTinkerTank.class, "tinker_tank");

    cast = registerItem(new Cast(), "cast");
    castCustom = registerItem(new CastCustom(), "cast_custom");
    castIngot = castCustom.addMeta(0, "ingot", Material.VALUE_Ingot);
    castNugget = castCustom.addMeta(1, "nugget", Material.VALUE_Nugget);
    castGem = castCustom.addMeta(2, "gem", Material.VALUE_Gem);
    castPlate = castCustom.addMeta(3, "plate", Material.VALUE_Ingot);
    castGear = castCustom.addMeta(4, "gear", Material.VALUE_Ingot * 4);

    clayCast = registerItem(new Cast(), "clay_cast");

    if(TinkerRegistry.getShard() != null) {
      TinkerRegistry.addCastForItem(TinkerRegistry.getShard());
      castShard = new ItemStack(cast);
      Cast.setTagForPart(castShard, TinkerRegistry.getShard());
    }

    proxy.preInit();

    TinkerRegistry.tabSmeltery.setDisplayIcon(new ItemStack(searedTank));

    // smeltery blocks
    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(searedBlock);
    builder.add(searedTank);
    builder.add(smelteryIO);
    builder.add(searedGlass);

    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build(); // same blocks right now

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.add(searedBlock);

    builder.add(searedSlab);
    builder.add(searedSlab2);
    builder.add(searedStairsStone);
    builder.add(searedStairsCobble);
    builder.add(searedStairsPaver);
    builder.add(searedStairsBrick);
    builder.add(searedStairsBrickCracked);
    builder.add(searedStairsBrickFancy);
    builder.add(searedStairsBrickSquare);
    builder.add(searedStairsBrickTriangle);
    builder.add(searedStairsBrickSmall);
    builder.add(searedStairsRoad);
    builder.add(searedStairsTile);
    builder.add(searedStairsCreeper);

    validSearedFurnaceBlocks = builder.build();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    // done here so they're present for integration in MaterialIntegration and fluids in TinkerFluids are also initialized
    castCreationFluids.add(new FluidStack(TinkerFluids.gold, Material.VALUE_Ingot * 2));
    if(TinkerIntegration.isIntegrated(TinkerFluids.brass)) {
      castCreationFluids.add(new FluidStack(TinkerFluids.brass, Material.VALUE_Ingot));
    }
    if(TinkerIntegration.isIntegrated(TinkerFluids.alubrass)) {
      castCreationFluids.add(new FluidStack(TinkerFluids.alubrass, Material.VALUE_Ingot));
    }
    if(FluidRegistry.isFluidRegistered(TinkerFluids.clay)) {
      clayCreationFluids.add(new FluidStack(TinkerFluids.clay, Material.VALUE_Ingot * 2));
    }

    registerRecipes();

    proxy.init();
  }

  private void registerRecipes() {
    // storing stacks for following recipes, mainly slabs and stairs
    ItemStack stackSearedStone = new ItemStack(searedBlock, 1, BlockSeared.SearedType.STONE.getMeta());
    ItemStack stackSearedCobble = new ItemStack(searedBlock, 1, BlockSeared.SearedType.COBBLE.getMeta());
    ItemStack stackSearedPaver = new ItemStack(searedBlock, 1, BlockSeared.SearedType.PAVER.getMeta());
    ItemStack stackSearedBrick = new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK.getMeta());
    ItemStack stackSearedBrickCracked = new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_CRACKED.getMeta());
    ItemStack stackSearedBrickFancy = new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_FANCY.getMeta());
    ItemStack stackSearedBrickSquare = new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_SQUARE.getMeta());
    ItemStack stackSearedBrickTriangle = new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_TRIANGLE.getMeta());
    ItemStack stackSearedBrickSmall = new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_SMALL.getMeta());
    ItemStack stackSearedRoad = new ItemStack(searedBlock, 1, BlockSeared.SearedType.ROAD.getMeta());
    ItemStack stackSearedTile = new ItemStack(searedBlock, 1, BlockSeared.SearedType.TILE.getMeta());
    ItemStack stackSearedCreeper = new ItemStack(searedBlock, 1, BlockSeared.SearedType.CREEPER.getMeta());

    // I AM GROUT
    ItemStack grout = TinkerCommons.grout.copy();
    grout.stackSize = 2;
    GameRegistry.addRecipe(new ShapelessOreRecipe(grout, Items.CLAY_BALL, Blocks.GRAVEL, "sand"));
    grout = grout.copy();
    grout.stackSize = 8;
    GameRegistry.addRecipe(new ShapelessOreRecipe(grout, Blocks.GRAVEL, "sand", Blocks.GRAVEL, "sand", Blocks.CLAY, "sand", Blocks.GRAVEL, "sand", Blocks.GRAVEL));

    // seared bricks
    ItemStack searedBrick = TinkerCommons.searedBrick;
    GameRegistry.addSmelting(TinkerCommons.grout, searedBrick, 0);
    GameRegistry.addShapedRecipe(stackSearedBrick, "bb", "bb", 'b', searedBrick);
    // you always seem to have a few leftover bricks
    GameRegistry.addShapedRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.BRICK.getMeta()), "bb", 'b', searedBrick);

    // remaining smeltery component recipes
    GameRegistry.addRecipe(new ItemStack(smelteryController),
                           "bbb", "b b", "bbb", 'b', searedBrick); // Controller
    GameRegistry.addRecipe(new ItemStack(smelteryIO, 1, BlockSmelteryIO.IOType.DRAIN.getMeta()),
                           "b b", "b b", "b b", 'b', searedBrick); // Drain
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(searedTank, 1, BlockTank.TankType.TANK.getMeta()),
                                               "bbb", "bgb", "bbb", 'b', searedBrick, 'g', "blockGlass")); // Tank
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(searedTank, 1, BlockTank.TankType.GAUGE.getMeta()),
                                               "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "blockGlass")); // Glass
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(searedTank, 1, BlockTank.TankType.WINDOW.getMeta()),
                                               "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "blockGlass")); // Window
    // glass, requires clear glass rather than regular like the stained variants
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(searedGlass, 1, BlockSearedGlass.GlassType.GLASS.getMeta()),
                                               " b ", "bgb", " b ", 'b', searedBrick, 'g', "blockGlass"));

    GameRegistry.addRecipe(new ItemStack(castingBlock, 1, BlockCasting.CastingType.TABLE.getMeta()),
                           "bbb", "b b", "b b", 'b', searedBrick); // Table
    GameRegistry.addRecipe(new ItemStack(castingBlock, 1, BlockCasting.CastingType.BASIN.getMeta()),
                           "b b", "b b", "bbb", 'b', searedBrick); // Basin
    GameRegistry.addRecipe(new ItemStack(faucet),
                           "b b", " b ", 'b', searedBrick); // Faucet
    //GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); // Channel

    // seared furnace, uses a furnace in the center as just bricks gets the smeltery controller
    // there is an alternative recipe below using the casting basin
    GameRegistry.addRecipe(new ItemStack(searedFurnaceController), "bbb", "bfb", "bbb", 'b', searedBrick, 'f', Blocks.FURNACE);

    // Tinker Tank, same as above but with a bucket
    GameRegistry.addRecipe(new ItemStack(tinkerTankController), "bbb", "bub", "bbb", 'b', searedBrick, 'u', Items.BUCKET);

    // polish stone into the paver
    addSearedBrickRecipe(BlockSeared.SearedType.PAVER, BlockSeared.SearedType.STONE);
    addSearedBrickRecipe(BlockSeared.SearedType.BRICK, BlockSeared.SearedType.PAVER);

    // remaining seared bricks
    addSearedBrickRecipe(BlockSeared.SearedType.BRICK_FANCY, BlockSeared.SearedType.BRICK);
    addSearedBrickRecipe(BlockSeared.SearedType.BRICK_SQUARE, BlockSeared.SearedType.BRICK_FANCY);
    addSearedBrickRecipe(BlockSeared.SearedType.BRICK_TRIANGLE, BlockSeared.SearedType.BRICK_SQUARE);
    addSearedBrickRecipe(BlockSeared.SearedType.CREEPER, BlockSeared.SearedType.BRICK_TRIANGLE);
    addSearedBrickRecipe(BlockSeared.SearedType.BRICK_SMALL, BlockSeared.SearedType.CREEPER);
    addSearedBrickRecipe(BlockSeared.SearedType.TILE, BlockSeared.SearedType.BRICK_SMALL);
    addSearedBrickRecipe(BlockSeared.SearedType.ROAD, BlockSeared.SearedType.TILE);
    addSearedBrickRecipe(BlockSeared.SearedType.PAVER, BlockSeared.SearedType.ROAD);

    GameRegistry.addSmelting(stackSearedBrick.copy(), stackSearedBrickCracked.copy(), 0.1f);

    // slabs
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.STONE.getMeta()), stackSearedStone.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.COBBLE.getMeta()), stackSearedCobble.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.PAVER.getMeta()), stackSearedPaver.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.BRICK.getMeta()), stackSearedBrick.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.BRICK_CRACKED.getMeta()), stackSearedBrickCracked.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.BRICK_FANCY.getMeta()), stackSearedBrickFancy.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.BRICK_SQUARE.getMeta()), stackSearedBrickSquare.copy());
    addSlabRecipe(new ItemStack(searedSlab, 1, BlockSearedSlab.SearedType.ROAD.getMeta()), stackSearedRoad.copy());
    addSlabRecipe(new ItemStack(searedSlab2, 1, BlockSearedSlab2.SearedType.CREEPER.getMeta()), stackSearedCreeper.copy());
    addSlabRecipe(new ItemStack(searedSlab2, 1, BlockSearedSlab2.SearedType.BRICK_TRIANGLE.getMeta()), stackSearedBrickTriangle.copy());
    addSlabRecipe(new ItemStack(searedSlab2, 1, BlockSearedSlab2.SearedType.BRICK_SMALL.getMeta()), stackSearedBrickSmall.copy());
    addSlabRecipe(new ItemStack(searedSlab2, 1, BlockSearedSlab2.SearedType.TILE.getMeta()), stackSearedTile.copy());

    // stairs
    addStairRecipe(searedStairsStone, stackSearedStone);
    addStairRecipe(searedStairsCobble, stackSearedCobble);
    addStairRecipe(searedStairsPaver, stackSearedPaver);
    addStairRecipe(searedStairsBrick, stackSearedBrick);
    addStairRecipe(searedStairsBrickCracked, stackSearedBrickCracked);
    addStairRecipe(searedStairsBrickFancy, stackSearedBrickFancy);
    addStairRecipe(searedStairsBrickSquare, stackSearedBrickSquare);
    addStairRecipe(searedStairsRoad, stackSearedRoad);
    addStairRecipe(searedStairsCreeper, stackSearedCreeper);
    addStairRecipe(searedStairsBrickTriangle, stackSearedBrickTriangle);
    addStairRecipe(searedStairsBrickSmall, stackSearedBrickSmall);
    addStairRecipe(searedStairsTile, stackSearedTile);

  }

  private void addSearedBrickRecipe(BlockSeared.SearedType out, BlockSeared.SearedType in) {
    addBrickRecipe(searedBlock, out, in);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerSmelteryFuel();
    registerMeltingCasting();
    registerAlloys();

    registerRecipeOredictMelting();

    // register remaining cast creation
    for(FluidStack fs : castCreationFluids) {
      TinkerRegistry.registerTableCasting(new ItemStack(cast), null, fs.getFluid(), fs.amount);
      TinkerRegistry.registerTableCasting(new CastingRecipe(castGem, RecipeMatch.of("gemEmerald"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of("ingotBrick"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of("ingotBrickNether"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, new RecipeMatch.Item(TinkerCommons.searedBrick, 1), fs, true, true));
    }

    proxy.postInit();
  }

  private void registerSmelteryFuel() {
    TinkerRegistry.registerSmelteryFuel(new FluidStack(FluidRegistry.LAVA, 50), 100);
  }

  private void registerMeltingCasting() {
    int bucket = Fluid.BUCKET_VOLUME;

    // bucket casting
    TinkerRegistry.registerTableCasting(new BucketCastingRecipe());

    // Water
    Fluid water = FluidRegistry.WATER;
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.ICE, bucket), water, 305));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.PACKED_ICE, bucket * 2), water, 310));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.SNOW, bucket), water, 305));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Items.SNOWBALL, bucket / 8), water, 301));

    // bloooooood
    TinkerRegistry.registerMelting(Items.ROTTEN_FLESH, TinkerFluids.blood, 5);
    if(TinkerCommons.matSlimeBallBlood != null) {
      TinkerRegistry.registerTableCasting(TinkerCommons.matSlimeBallBlood.copy(), null, TinkerFluids.blood, 160);
    }

    // purple slime
    TinkerRegistry.registerMelting(TinkerCommons.matSlimeBallPurple, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall);
    ItemStack slimeblock = new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.PURPLE.meta);
    TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 4);
    slimeblock = new ItemStack(TinkerCommons.blockSlime, 1, BlockSlime.SlimeType.PURPLE.meta);
    TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 9);

    // seared stone, takes as long as a full block to melt, but gives less
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("stone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore()));
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("cobblestone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore()));

    // obsidian
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("obsidian", Material.VALUE_Ore()),
                                                           TinkerFluids.obsidian, Material.VALUE_Ore()));
    // note that obsidian casting gives you 2 ingot value per obsidian, while part crafting only gives 1 per obsidian
    registerToolpartMeltingCasting(TinkerMaterials.obsidian);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.OBSIDIAN), null, TinkerFluids.obsidian, Material.VALUE_Ore());


    // gold is melt and castable too, but no tools. Remaining materials are done directly in the MaterialIntegration
    // gold is integrated via MaterialIntegration in TinkerIntegration now

    // special melting
    TinkerRegistry.registerMelting(Items.IRON_HORSE_ARMOR, TinkerFluids.iron, Material.VALUE_Ingot * 8);
    TinkerRegistry.registerMelting(Items.GOLDEN_HORSE_ARMOR, TinkerFluids.gold, Material.VALUE_Ingot * 8);

    // register stone toolpart melting
    for(IToolPart toolPart : TinkerRegistry.getToolParts()) {
      if(toolPart instanceof MaterialItem) {
        ItemStack stack = toolPart.getItemstackWithMaterial(TinkerMaterials.stone);
        TinkerRegistry.registerMelting(stack, TinkerFluids.searedStone, toolPart.getCost());
      }
    }

    // seared block casting and melting
    ItemStack blockSeared = new ItemStack(searedBlock);
    blockSeared.setItemDamage(BlockSeared.SearedType.STONE.getMeta());
    TinkerRegistry.registerTableCasting(TinkerCommons.searedBrick, castIngot, TinkerFluids.searedStone, Material.VALUE_SearedMaterial);
    TinkerRegistry.registerBasinCasting(blockSeared, null, TinkerFluids.searedStone, Material.VALUE_SearedBlock);

    ItemStack searedCobble = new ItemStack(searedBlock, 1, BlockSeared.SearedType.COBBLE.getMeta());
    TinkerRegistry.registerBasinCasting(new CastingRecipe(searedCobble, RecipeMatch.of("cobblestone"), TinkerFluids.searedStone, Material.VALUE_SearedBlock - Material.VALUE_SearedMaterial));

    // seared furnaces have an additional recipe above using a crafting table, to allow creation without a smeltery
    // this one is convenience for those with one
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(searedFurnaceController),
                                                          RecipeMatch.of(Blocks.FURNACE),
                                                          new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial * 8),
                                                          true, true));

    // basically a pseudo-oredict of the seared blocks to support wildcard value
    TinkerRegistry.registerMelting(searedBlock, TinkerFluids.searedStone, Material.VALUE_SearedBlock);
    TinkerRegistry.registerMelting(TinkerCommons.searedBrick, TinkerFluids.searedStone, Material.VALUE_SearedMaterial);
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of(TinkerCommons.grout, Material.VALUE_SearedMaterial), TinkerFluids.searedStone, Material.VALUE_SearedMaterial / 3));

    // melt all the dirt into mud
    ItemStack stack = new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE);
    RecipeMatch rm = new RecipeMatch.Item(stack, 1, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(rm, TinkerFluids.dirt, Material.VALUE_BrickBlock));
    TinkerRegistry.registerTableCasting(TinkerCommons.mudBrick, castIngot, TinkerFluids.dirt, Material.VALUE_Ingot);

    // hardened clay
    TinkerRegistry.registerMelting(Items.CLAY_BALL, TinkerFluids.clay, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(Blocks.CLAY, TinkerFluids.clay, Material.VALUE_BrickBlock);
    // decided against support for melting hardened clay. Once it's hardened, it stays hard. Same for bricks.
    //TinkerRegistry.registerMelting(Blocks.hardened_clay, TinkerFluids.clay, Material.VALUE_BrickBlock);
    //TinkerRegistry.registerMelting(Blocks.stained_hardened_clay, TinkerFluids.clay, Material.VALUE_BrickBlock);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.HARDENED_CLAY), null, TinkerFluids.clay, Material.VALUE_BrickBlock);
    // funny thing about hardened clay. If it's stained and you wash it with water, it turns back into regular hardened clay!
    TinkerRegistry.registerBasinCasting(new CastingRecipe(
        new ItemStack(Blocks.HARDENED_CLAY),
        RecipeMatch.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE)),
        new FluidStack(FluidRegistry.WATER, 250),
        150,
        true,
        false));
    // let's allow bricks because we're nice
    if(Config.castableBricks) {
      TinkerRegistry.registerTableCasting(new ItemStack(Items.BRICK), castIngot, TinkerFluids.clay, Material.VALUE_Ingot);
    }

    // emerald melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("gemEmerald", Material.VALUE_Gem), TinkerFluids.emerald));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("oreEmerald", (int) (Material.VALUE_Gem * Config.oreToIngotRatio)), TinkerFluids.emerald));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("blockEmerald", Material.VALUE_Gem * 9), TinkerFluids.emerald));
    TinkerRegistry.registerTableCasting(new ItemStack(Items.EMERALD), castGem, TinkerFluids.emerald, Material.VALUE_Gem);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.EMERALD_BLOCK), null, TinkerFluids.emerald, Material.VALUE_Gem * 9);

    // glass melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("sand", Material.VALUE_Glass), TinkerFluids.glass));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("blockGlass", Material.VALUE_Glass), TinkerFluids.glass));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("paneGlass", Material.VALUE_Glass * 6 / 16), TinkerFluids.glass));
    TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Blocks.GLASS_PANE), null, TinkerFluids.glass, Material.VALUE_Glass * 6 / 16, 50));
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockClearGlass), null, TinkerFluids.glass, Material.VALUE_Glass, 120));

    // lavawood
    TinkerRegistry.registerBasinCasting(new CastingRecipe(TinkerCommons.lavawood, RecipeMatch.of("plankWood"),
                                                          new FluidStack(FluidRegistry.LAVA, 250),
                                                          100, true, false));


    // red sand
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(Blocks.SAND, 1, 1),
                                                          RecipeMatch.of(new ItemStack(Blocks.SAND, 1, 0)),
                                                          new FluidStack(TinkerFluids.blood, 10),
                                                          true, false));

    // melt entities into a pulp
    TinkerRegistry.registerEntityMelting(EntityIronGolem.class, new FluidStack(TinkerFluids.iron, 18));
    TinkerRegistry.registerEntityMelting(EntitySnowman.class, new FluidStack(FluidRegistry.WATER, 100));
    TinkerRegistry.registerEntityMelting(EntityVillager.class, new FluidStack(TinkerFluids.emerald, 6));
  }

  private void registerAlloys() {
    // 1 bucket lava + 1 bucket water = 2 ingots = 1 block obsidian
    // 1000 + 1000 = 288
    // 125 + 125 = 36
    if(Config.obsidianAlloy) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.obsidian, 36),
                                   new FluidStack(FluidRegistry.WATER, 125),
                                   new FluidStack(FluidRegistry.LAVA, 125));
    }

    // 1 bucket water + 4 seared ingot + 4 mud bricks = 1 block hardened clay
    // 1000 + 288 + 576 = 576
    // 250 + 72 + 144 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.clay, 144),
                                 new FluidStack(FluidRegistry.WATER, 250),
                                 new FluidStack(TinkerFluids.searedStone, 72),
                                 new FluidStack(TinkerFluids.dirt, 144));

    // 1 iron ingot + 1 purple slime ball + seared stone in molten form = 1 knightslime ingot
    // 144 + 250 + 288 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.knightslime, 72),
                                 new FluidStack(TinkerFluids.iron, 72),
                                 new FluidStack(TinkerFluids.purpleSlime, 125),
                                 new FluidStack(TinkerFluids.searedStone, 144));

    // i iron ingot + 1 blood... unit thingie + 1/3 gem = 1 pigiron
    // 144 + 99 + 222 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.pigIron, 144),
                                 new FluidStack(TinkerFluids.iron, 48),
                                 new FluidStack(TinkerFluids.blood, 33),
                                 new FluidStack(TinkerFluids.emerald, 74));

    // 1 ingot cobalt + 1 ingot ardite = 1 ingot manyullyn!
    // 144 + 144 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.manyullyn, 2),
                                 new FluidStack(TinkerFluids.cobalt, 2),
                                 new FluidStack(TinkerFluids.ardite, 2));

    // 3 ingots copper + 1 ingot tin = 4 ingots bronze
    if(TinkerIntegration.isIntegrated(TinkerFluids.bronze) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.tin)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.bronze, 4),
                                   new FluidStack(TinkerFluids.copper, 3),
                                   new FluidStack(TinkerFluids.tin, 1));
    }

    // 1 ingot gold + 1 ingot silver = 2 ingots electrum
    if(TinkerIntegration.isIntegrated(TinkerFluids.electrum) &&
       TinkerIntegration.isIntegrated(TinkerFluids.gold) &&
       TinkerIntegration.isIntegrated(TinkerFluids.silver)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.electrum, 2),
                                   new FluidStack(TinkerFluids.gold, 1),
                                   new FluidStack(TinkerFluids.silver, 1));
    }

    // 1 ingot copper + 3 ingots aluminium = 4 ingots alubrass
    if(TinkerIntegration.isIntegrated(TinkerFluids.alubrass) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.aluminum)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.alubrass, 4),
                                   new FluidStack(TinkerFluids.copper, 1),
                                   new FluidStack(TinkerFluids.aluminum, 3));
    }

    // 2 ingots copper + 1 ingot zinc = 3 ingots brass
    if(TinkerIntegration.isIntegrated(TinkerFluids.brass) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.zinc)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.brass, 3),
                                   new FluidStack(TinkerFluids.copper, 2),
                                   new FluidStack(TinkerFluids.zinc, 1));
    }
  }

  public static void registerToolpartMeltingCasting(Material material) {
    // melt ALL the toolparts n stuff. Also cast them.
    Fluid fluid = material.getFluid();
    for(IToolPart toolPart : TinkerRegistry.getToolParts()) {
      if(toolPart instanceof MaterialItem) {
        ItemStack stack = toolPart.getItemstackWithMaterial(material);
        ItemStack cast = new ItemStack(TinkerSmeltery.cast);
        Cast.setTagForPart(cast, stack.getItem());

        if(fluid != null) {
          // melting
          TinkerRegistry.registerMelting(stack, fluid, toolPart.getCost());
          // casting
          TinkerRegistry.registerTableCasting(stack, cast, fluid, toolPart.getCost());
        }
        // register cast creation from the toolparts
        for(FluidStack fs : castCreationFluids) {
          TinkerRegistry.registerTableCasting(new CastingRecipe(cast,
                                                                RecipeMatch.ofNBT(stack),
                                                                fs,
                                                                true, true));
        }

        // clay casts
        if(Config.claycasts) {
          ItemStack clayCast = new ItemStack(TinkerSmeltery.clayCast);
          Cast.setTagForPart(clayCast, stack.getItem());

          if(fluid != null) {
            RecipeMatch rm = RecipeMatch.ofNBT(clayCast);
            FluidStack fs = new FluidStack(fluid, toolPart.getCost());
            TinkerRegistry.registerTableCasting(new CastingRecipe(stack, rm, fs, true, false));
          }
          for(FluidStack fs : clayCreationFluids) {
            TinkerRegistry.registerTableCasting(new CastingRecipe(clayCast,
                                                                  RecipeMatch.ofNBT(stack),
                                                                  fs,
                                                                  true, true));
          }
        }
      }
    }

    // same for shard
    if(castShard != null) {
      ItemStack stack = TinkerRegistry.getShard(material);
      int cost = TinkerRegistry.getShard().getCost();

      if(fluid != null) {
        // melting
        TinkerRegistry.registerMelting(stack, fluid, cost);
        // casting
        TinkerRegistry.registerTableCasting(stack, castShard, fluid, cost);
      }
      // register cast creation from the toolparts
      for(FluidStack fs : castCreationFluids) {
        TinkerRegistry.registerTableCasting(new CastingRecipe(castShard,
                                                              RecipeMatch.ofNBT(stack),
                                                              fs,
                                                              true, true));
      }
    }
  }

  /**
   * Registers melting for all directly supported pre- and suffixes of the ore.
   * E.g. "Iron" -> "ingotIron", "blockIron", "oreIron",
   */
  public static void registerOredictMeltingCasting(Fluid fluid, String ore) {
    ImmutableSet.Builder<Pair<List<ItemStack>, Integer>> builder = ImmutableSet.builder();
    Pair<List<ItemStack>, Integer> nuggetOre = Pair.of(OreDictionary.getOres("nugget" + ore), Material.VALUE_Nugget);
    Pair<List<ItemStack>, Integer> ingotOre = Pair.of(OreDictionary.getOres("ingot" + ore), Material.VALUE_Ingot);
    Pair<List<ItemStack>, Integer> blockOre = Pair.of(OreDictionary.getOres("block" + ore), Material.VALUE_Block);
    Pair<List<ItemStack>, Integer> oreOre = Pair.of(OreDictionary.getOres("ore" + ore), Material.VALUE_Ore());
    Pair<List<ItemStack>, Integer> oreNetherOre = Pair.of(OreDictionary.getOres("oreNether" + ore), (int) (2 * Material.VALUE_Ingot * Config.oreToIngotRatio));
    Pair<List<ItemStack>, Integer> oreDenseOre = Pair.of(OreDictionary.getOres("denseore" + ore), (int) (3 * Material.VALUE_Ingot * Config.oreToIngotRatio));
    Pair<List<ItemStack>, Integer> orePoorOre = Pair.of(OreDictionary.getOres("orePoor" + ore), (int) (Material.VALUE_Nugget * Config.oreToIngotRatio));
    Pair<List<ItemStack>, Integer> plateOre = Pair.of(OreDictionary.getOres("plate" + ore), Material.VALUE_Ingot);
    Pair<List<ItemStack>, Integer> gearOre = Pair.of(OreDictionary.getOres("gear" + ore), Material.VALUE_Ingot * 4);
    Pair<List<ItemStack>, Integer> dustOre = Pair.of(OreDictionary.getOres("dust" + ore), Material.VALUE_Ingot);

    builder.add(nuggetOre, ingotOre, blockOre, oreOre, oreNetherOre, oreDenseOre, orePoorOre, plateOre, gearOre, dustOre);
    Set<Pair<List<ItemStack>, Integer>> knownOres = builder.build();


    // register oredicts
    for(Pair<List<ItemStack>, Integer> pair : knownOres) {
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
    }

    // register oredict castings!
    // ingot casting
    TinkerRegistry.registerTableCasting(new OreCastingRecipe(ingotOre.getLeft(),
                                                             RecipeMatch.ofNBT(castIngot),
                                                             fluid,
                                                             ingotOre.getRight()));
    // nugget casting
    TinkerRegistry.registerTableCasting(new OreCastingRecipe(nuggetOre.getLeft(),
                                                             RecipeMatch.ofNBT(castNugget),
                                                             fluid,
                                                             nuggetOre.getRight()));
    // block casting
    TinkerRegistry.registerBasinCasting(new OreCastingRecipe(blockOre.getLeft(),
                                                             null, // no cast
                                                             fluid,
                                                             blockOre.getRight()));
    // plate casting
    TinkerRegistry.registerTableCasting(new OreCastingRecipe(plateOre.getLeft(),
                                                             RecipeMatch.ofNBT(castPlate),
                                                             fluid,
                                                             plateOre.getRight()));
    // gear casting
    TinkerRegistry.registerTableCasting(new OreCastingRecipe(gearOre.getLeft(),
                                                             RecipeMatch.ofNBT(castGear),
                                                             fluid,
                                                             gearOre.getRight()));

    // and also cast creation!
    for(FluidStack fs : castCreationFluids) {
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of(ingotOre.getLeft()), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castNugget, RecipeMatch.of(nuggetOre.getLeft()), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castPlate, RecipeMatch.of(plateOre.getLeft()), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castGear, RecipeMatch.of(gearOre.getLeft()), fs, true, true));
    }

    // used for recipe detection
    knownOreFluids.put(fluid, knownOres);
  }

  // take all fluids we registered oredicts for and scan all recipies for oredict-recipies that we can apply this to
  private static void registerRecipeOredictMelting() {
    // we go through all recipies, and if it's an ore recipe we go through its contents and check if it
    // only consists of one of our known oredict entries
    for(IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
      // blacklisted?
      boolean blacklisted = false;
      for(ItemStack blacklistItem : meltingBlacklist) {
        if(OreDictionary.itemMatches(blacklistItem, irecipe.getRecipeOutput(), false)) {
          blacklisted = true;
          break;
        }
      }

      // recipe already has a melting recipe?
      if(blacklisted || TinkerRegistry.getMelting(irecipe.getRecipeOutput()) != null) {
        continue;
      }

      List<Object> inputs;
      if(irecipe instanceof ShapelessOreRecipe) {
        inputs = ((ShapelessOreRecipe) irecipe).getInput();
      }
      else if(irecipe instanceof ShapedOreRecipe) {
        inputs = Arrays.asList(((ShapedOreRecipe) irecipe).getInput());
      }
      else if(irecipe instanceof ShapelessRecipes) {
        inputs = Lists.<Object>newLinkedList(((ShapelessRecipes) irecipe).recipeItems);
      }
      else if(irecipe instanceof ShapedRecipes) {
        inputs = Arrays.asList((Object[]) ((ShapedRecipes) irecipe).recipeItems);
      }
      else {
        // not an ore recipe, stop here because we can't handle it
        continue;
      }

      // this map holds how much of which fluid is known of the recipe
      // if an recipe contains an itemstack that can't be mapped to a fluid calculation is aborted
      Map<Fluid, Integer> known = Maps.newHashMap();
      for(Object o : inputs) {
        // can contain nulls because of shapedrecipe
        if(o == null) {
          continue;
        }
        boolean found = false;
        for(Map.Entry<Fluid, Set<Pair<List<ItemStack>, Integer>>> entry : knownOreFluids.entrySet()) {
          // check if it's a known oredict (all oredict lists are equal if they match the same oredict)
          // OR if it's an itemstack contained in one of our oredicts
          for(Pair<List<ItemStack>, Integer> pair : entry.getValue()) {
            if(o == pair.getLeft() || (o instanceof ItemStack && pair.getLeft().contains(o))) {
              // matches! Update fluid amount known
              Integer amount = known.get(entry.getKey()); // what we found for the liquid so far
              if(amount == null) {
                // nothing is what we found so far.
                amount = 0;
              }
              amount += pair.getRight();
              known.put(entry.getKey(), amount);
              found = true;
              break;
            }
          }
          if(found) {
            break;
          }
        }
        // not a recipe we can process, contains an item that can't melt
        if(!found) {
          known.clear();
          break;
        }
      }

      // add a melting recipe for it
      // we only support single-liquid recipies currently :I
      if(known.keySet().size() == 1) {
        Fluid fluid = known.keySet().iterator().next();
        ItemStack output = irecipe.getRecipeOutput().copy();
        int amount = known.get(fluid) / output.stackSize;
        output.stackSize = 1;
        TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(output, amount), fluid));
        log.trace("Added automatic melting recipe for {} ({} {})", irecipe.getRecipeOutput().toString(), amount, fluid
            .getName());
      }
    }
  }

  protected static <E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> BlockSearedStairs registerBlockSearedStairsFrom(EnumBlock<E> block, E value, String name) {
    return registerBlock(new BlockSearedStairs(block.getDefaultState().withProperty(block.prop, value)), name);
  }
}
