package slimeknights.tconstruct.gadgets;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.gadgets.block.BlockBrownstone;
import slimeknights.tconstruct.gadgets.block.BlockBrownstoneSlab;
import slimeknights.tconstruct.gadgets.block.BlockBrownstoneSlab2;
import slimeknights.tconstruct.gadgets.block.BlockDriedClay;
import slimeknights.tconstruct.gadgets.block.BlockDriedClaySlab;
import slimeknights.tconstruct.gadgets.block.BlockPunji;
import slimeknights.tconstruct.gadgets.block.BlockRack;
import slimeknights.tconstruct.gadgets.block.BlockSlimeChannel;
import slimeknights.tconstruct.gadgets.block.BlockStoneLadder;
import slimeknights.tconstruct.gadgets.block.BlockStoneTorch;
import slimeknights.tconstruct.gadgets.block.BlockWoodRail;
import slimeknights.tconstruct.gadgets.block.BlockWoodRailDropper;
import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.gadgets.item.ItemBlockRack;
import slimeknights.tconstruct.gadgets.item.ItemFancyItemFrame;
import slimeknights.tconstruct.gadgets.item.ItemMomsSpaghetti;
import slimeknights.tconstruct.gadgets.item.ItemPiggybackPack;
import slimeknights.tconstruct.gadgets.item.ItemSlimeBoots;
import slimeknights.tconstruct.gadgets.item.ItemSlimeSling;
import slimeknights.tconstruct.gadgets.item.ItemSpaghetti;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.gadgets.modifiers.ModSpaghettiMeat;
import slimeknights.tconstruct.gadgets.modifiers.ModSpaghettiSauce;
import slimeknights.tconstruct.gadgets.tileentity.TileDryingRack;
import slimeknights.tconstruct.gadgets.tileentity.TileItemRack;
import slimeknights.tconstruct.gadgets.tileentity.TileSlimeChannel;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockFirewood;
import slimeknights.tconstruct.shared.block.BlockSlime.SlimeType;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.tools.common.TableRecipe;

@Pulse(id = TinkerGadgets.PulseId, description = "All the fun toys")
public class TinkerGadgets extends TinkerPulse {

  public static final String PulseId = "TinkerGadgets";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.gadgets.GadgetClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static Block stoneTorch;
  public static Block stoneLadder;
  public static Block punji;
  public static BlockRack rack;
  public static BlockDriedClay driedClay;
  public static BlockBrownstone brownstone;

  public static Block woodRail;
  public static Block woodRailTrapdoor;

  public static Block slimeChannel;

  public static Block driedClaySlab;
  public static Block brownstoneSlab;
  public static Block brownstoneSlab2;

  public static Block driedClayStairs;
  public static Block driedBrickStairs;
  public static Block brownstoneStairsSmooth;
  public static Block brownstoneStairsRough;
  public static Block brownstoneStairsPaver;
  public static Block brownstoneStairsBrick;
  public static Block brownstoneStairsBrickCracked;
  public static Block brownstoneStairsBrickFancy;
  public static Block brownstoneStairsBrickSquare;
  public static Block brownstoneStairsBrickTriangle;
  public static Block brownstoneStairsBrickSmall;
  public static Block brownstoneStairsRoad;
  public static Block brownstoneStairsTile;
  public static Block brownstoneStairsCreeper;

  public static ItemSlimeSling slimeSling;
  public static ItemSlimeBoots slimeBoots;
  public static ItemPiggybackPack piggybackPack;
  public static ItemThrowball throwball;
  public static Item stoneStick;

  public static ItemMetaDynamic spaghetti;
  public static ItemMomsSpaghetti momsSpaghetti;
  public static Modifier modSpaghettiSauce;
  public static Modifier modSpaghettiMeat;

  public static ItemHangingEntity fancyFrame;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    stoneTorch = registerBlock(new BlockStoneTorch(), "stone_torch");
    stoneLadder = registerBlock(new BlockStoneLadder(), "stone_ladder");
    punji = registerBlock(new BlockPunji(), "punji");
    rack = registerBlock(new ItemBlockRack(new BlockRack()), "rack");

    woodRail = registerBlock(new BlockWoodRail(), "wood_rail");
    woodRailTrapdoor = registerBlock(new BlockWoodRailDropper(), "wood_rail_trapdoor");

    // slime channels
    slimeChannel = registerEnumBlock(new BlockSlimeChannel(), "slime_channel");

    // dried clay
    driedClay = registerEnumBlock(new BlockDriedClay(), "dried_clay");
    driedClaySlab = registerEnumBlockSlab(new BlockDriedClaySlab(), "dried_clay_slab");
    driedClayStairs = registerBlockStairsFrom(driedClay, BlockDriedClay.DriedClayType.CLAY, "dried_clay_stairs");
    driedBrickStairs = registerBlockStairsFrom(driedClay, BlockDriedClay.DriedClayType.BRICK, "dried_brick_stairs");

    // brownstone
    brownstone = registerEnumBlock(new BlockBrownstone(), "brownstone");
    brownstoneSlab = registerEnumBlockSlab(new BlockBrownstoneSlab(), "brownstone_slab");
    brownstoneSlab2 = registerEnumBlockSlab(new BlockBrownstoneSlab2(), "brownstone_slab2");

    // stairs
    brownstoneStairsSmooth = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.SMOOTH, "brownstone_stairs_smooth");
    brownstoneStairsRough = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.ROUGH, "brownstone_stairs_rough");
    brownstoneStairsPaver = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.PAVER, "brownstone_stairs_paver");
    brownstoneStairsBrick = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.BRICK, "brownstone_stairs_brick");
    brownstoneStairsBrickCracked = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.BRICK_CRACKED, "brownstone_stairs_brick_cracked");
    brownstoneStairsBrickFancy = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.BRICK_FANCY, "brownstone_stairs_brick_fancy");
    brownstoneStairsBrickSquare = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.BRICK_SQUARE, "brownstone_stairs_brick_square");
    brownstoneStairsBrickTriangle = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.BRICK_TRIANGLE, "brownstone_stairs_brick_triangle");
    brownstoneStairsBrickSmall = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.BRICK_SMALL, "brownstone_stairs_brick_small");
    brownstoneStairsRoad = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.ROAD, "brownstone_stairs_road");
    brownstoneStairsTile = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.TILE, "brownstone_stairs_tile");
    brownstoneStairsCreeper = registerBlockStairsFrom(brownstone, BlockBrownstone.BrownstoneType.CREEPER, "brownstone_stairs_creeper");

    registerTE(TileItemRack.class, "item_rack");
    registerTE(TileDryingRack.class, "drying_rack");
    registerTE(TileSlimeChannel.class, "slime_channel");

    slimeSling = registerItem(new ItemSlimeSling(), "slimesling");
    slimeBoots = registerItem(new ItemSlimeBoots(), "slime_boots");
    piggybackPack = registerItem(new ItemPiggybackPack(), "piggybackpack");
    throwball = registerItem(new ItemThrowball(), "throwball");
    stoneStick = registerItem(new Item(), "stone_stick");
    stoneStick.setFull3D().setCreativeTab(TinkerRegistry.tabGadgets);

    fancyFrame = registerItem(new ItemFancyItemFrame(), "fancy_frame");

    registerMomsSpaghetti();

    EntityRegistry.registerModEntity(EntityFancyItemFrame.class, "Fancy Item Frame", EntityIDs.FANCY_FRAME, TConstruct.instance, 160, Integer.MAX_VALUE, false);
    EntityRegistry.registerModEntity(EntityThrowball.class, "Throwball", EntityIDs.THROWBALL, TConstruct.instance, 64, 10, true);
    //EntityRegistry.instance().lookupModSpawn(EntityFancyItemFrame.class, false).setCustomSpawning(null, true);

    MinecraftForge.EVENT_BUS.register(slimeBoots);

    proxy.preInit();

    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(slimeSling));
  }

  private void registerMomsSpaghetti() {
    spaghetti = registerItem(new ItemSpaghetti(), "spaghetti");
    momsSpaghetti = registerItem(new ItemMomsSpaghetti(), "moms_spaghetti");

    ItemStack hardSpaghetti = spaghetti.addMeta(0, "hard");
    ItemStack wetSpaghetti = spaghetti.addMeta(1, "soggy");
    ItemStack coldSpaghetti = spaghetti.addMeta(2, "cold");

    modSpaghettiSauce = new ModSpaghettiSauce();

    modSpaghettiMeat = new ModSpaghettiMeat();
    modSpaghettiMeat.addRecipeMatch(new RecipeMatch.ItemCombination(1,
                                                                    new ItemStack(Items.COOKED_BEEF),
                                                                    new ItemStack(Items.COOKED_CHICKEN),
                                                                    new ItemStack(Items.COOKED_MUTTON),
                                                                    new ItemStack(Items.COOKED_PORKCHOP)
    ));

    // Recipe for mom's spaghetti: soak em, dry em, cook em, eat em
    TinkerRegistry.registerTableCasting(new CastingRecipe(wetSpaghetti, RecipeMatch.of(hardSpaghetti), FluidRegistry.WATER, Fluid.BUCKET_VOLUME * 3, 15*60*20));
    TinkerRegistry.registerDryingRecipe(wetSpaghetti, coldSpaghetti, 15*60*20);
    GameRegistry.addSmelting(coldSpaghetti, new ItemStack(momsSpaghetti), 0f);
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerRecipes();

    proxy.init();
  }

  private void registerRecipes() {
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeSling), "fbf", "s s", " s ", 'f', Items.STRING, 's', "slimeball", 'b', "blockSlimeCongealed"));

    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(piggybackPack), " s ", "l l", " s ", 's', "stickWood", 'l', "leather"));


    // slimeboots
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots, 1, 0), "s s", "b b", 's', "slimeballGreen", 'b', new ItemStack(TinkerCommons.blockSlimeCongealed, 1, 0)));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots, 1, 1), "s s", "b b", 's', "slimeballBlue", 'b', new ItemStack(TinkerCommons.blockSlimeCongealed, 1, 1)));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots, 1, 2), "s s", "b b", 's', "slimeballPurple", 'b', new ItemStack(TinkerCommons.blockSlimeCongealed, 1, 2)));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots, 1, 3), "s s", "b b", 's', "slimeballBlood", 'b', new ItemStack(TinkerCommons.blockSlimeCongealed, 1, 3)));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots, 1, 4), "s s", "b b", 's', "slimeballMagma", 'b', new ItemStack(TinkerCommons.blockSlimeCongealed, 1, 4)));

    // fallback for other slime types
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots, 1, 0), "s s", "b b", 's', "slimeball", 'b', "blockSlimeCongealed"));

    // Stonesticks Recipes
    ItemStack rod = new ItemStack(stoneStick);
    GameRegistry.addRecipe(new ShapedOreRecipe(rod, "c", "c", 'c', "cobblestone"));
    GameRegistry.addRecipe(new ShapedOreRecipe(rod, "c", "c", 'c', "stone"));

    // Stone Torch Recipe
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneTorch, 4), "p", "w", 'p', new ItemStack(Items.COAL, 1, Short.MAX_VALUE), 'w', "rodStone"));
    // Jack o'Latern Recipe - Stone Torch
    GameRegistry.addRecipe(new ItemStack(Blocks.LIT_PUMPKIN, 1, 0), "p", "s", 'p', new ItemStack(Blocks.PUMPKIN), 's', new ItemStack(stoneTorch));
    // Stone Ladder Recipe
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneLadder, 3), "w w", "www", "w w", 'w', "rodStone"));
    // Wooden Rail Recipe
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(woodRail, 4, 0), "b b", "bxb", "b b", 'b', "plankWood", 'x', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(woodRailTrapdoor, 4, 0), "b b", "bxb", "b b", 'b', "plankWood", 'x', "trapdoorWood"));

    // Punji Sticks
    GameRegistry.addRecipe(new ItemStack(punji, 3, 0), "b b", " b ", "b b", 'b', new ItemStack(Items.REEDS));

    // Item Rack, a bit cheaper to encourage it for decoration
    GameRegistry.addRecipe(new TableRecipe(OreDictionary.getOres("slabWood"), rack, 0, "ww", 'w', "slabWood"));
    GameRegistry.addRecipe(BlockTable.createItemstack(rack, 0, TinkerCommons.slabFirewood, BlockFirewood.FirewoodType.LAVAWOOD.getMeta()),
                           "ww", 'w', new ItemStack(TinkerCommons.slabFirewood, 1, BlockFirewood.FirewoodType.LAVAWOOD.getMeta()));

    // Drying Rack
    GameRegistry.addRecipe(new TableRecipe(OreDictionary.getOres("slabWood"), rack, 1, "www", 'w', "slabWood"));
    GameRegistry.addRecipe(BlockTable.createItemstack(rack, 1, TinkerCommons.slabFirewood, BlockFirewood.FirewoodType.LAVAWOOD.getMeta()),
                           "www", 'w', new ItemStack(TinkerCommons.slabFirewood, 1, BlockFirewood.FirewoodType.LAVAWOOD.getMeta()));

    // Dried Bricks
    ItemStack stackDriedClay = new ItemStack(driedClay, 1, BlockDriedClay.DriedClayType.CLAY.getMeta());
    ItemStack stackDriedBrick = new ItemStack(driedClay, 1, BlockDriedClay.DriedClayType.BRICK.getMeta());

    GameRegistry.addRecipe(stackDriedBrick, "bb", "bb", 'b', TinkerCommons.driedBrick);
    GameRegistry.addShapedRecipe(new ItemStack(driedClaySlab, 1, BlockDriedClay.DriedClayType.BRICK.getMeta()), "bb", 'b', TinkerCommons.driedBrick);

    addSlabRecipe(new ItemStack(driedClaySlab, 1, BlockDriedClay.DriedClayType.CLAY.getMeta()), stackDriedClay);
    addSlabRecipe(new ItemStack(driedClaySlab, 1, BlockDriedClay.DriedClayType.BRICK.getMeta()), stackDriedBrick);
    addStairRecipe(driedClayStairs, stackDriedClay);
    addStairRecipe(driedBrickStairs, stackDriedBrick);

    // fancy item frames
    ItemStack frame = new ItemStack(TinkerGadgets.fancyFrame, 1, EntityFancyItemFrame.FrameType.GOLD.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, "nnn", "nOn", "nnn", 'O', Blocks.OBSIDIAN, 'n', "nuggetGold"));
    frame = new ItemStack(TinkerGadgets.fancyFrame, 1, EntityFancyItemFrame.FrameType.CLEAR.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, " n ", "nOn", " n ", 'O', "blockGlass", 'n', "paneGlass"));

    // slime channels
    for(SlimeType type : SlimeType.values()) {
      GameRegistry.addSmelting(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, type.getMeta()),
                               new ItemStack(slimeChannel, 3, type.getMeta()), 0.15f);
    }

    addFrameRecipe("nuggetGold", EntityFancyItemFrame.FrameType.JEWEL);

    if(TinkerCommons.nuggetAlubrass != null) {
      addFrameRecipe("nuggetAlubrass", EntityFancyItemFrame.FrameType.ALUBRASS);
    }
    if(TinkerCommons.nuggetCobalt != null) {
      addFrameRecipe("nuggetCobalt", EntityFancyItemFrame.FrameType.COBALT);
    }
    if(TinkerCommons.nuggetArdite != null) {
      addFrameRecipe("nuggetArdite", EntityFancyItemFrame.FrameType.ARDITE);
    }
    if(TinkerCommons.nuggetManyullyn != null) {
      addFrameRecipe("nuggetManyullyn", EntityFancyItemFrame.FrameType.MANYULLYN);
    }

    // throwballs
    ItemStack glowball = new ItemStack(throwball, 8, ItemThrowball.ThrowballType.GLOW.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(glowball, "SSS", "SGS", "SSS", 'S', Items.SNOWBALL, 'G', "dustGlowstone"));

    ItemStack efln = new ItemStack(throwball, 1, ItemThrowball.ThrowballType.EFLN.ordinal());
    GameRegistry.addShapelessRecipe(efln, Items.FLINT, Items.GUNPOWDER);
    GameRegistry.addRecipe(new ShapelessOreRecipe(efln, Items.FLINT, "dustSulfur"));

    // brownstone
    ItemStack stackBrownstoneSmooth = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.SMOOTH.getMeta());
    ItemStack stackBrownstoneRough = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.ROUGH.getMeta());
    ItemStack stackBrownstonePaver = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.PAVER.getMeta());
    ItemStack stackBrownstoneBrick = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK.getMeta());
    ItemStack stackBrownstoneBrickCracked = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK_CRACKED.getMeta());
    ItemStack stackBrownstoneBrickFancy = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK_FANCY.getMeta());
    ItemStack stackBrownstoneBrickSquare = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK_SQUARE.getMeta());
    ItemStack stackBrownstoneBrickTriangle = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK_TRIANGLE.getMeta());
    ItemStack stackBrownstoneBrickSmall = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK_SMALL.getMeta());
    ItemStack stackBrownstoneRoad = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.ROAD.getMeta());
    ItemStack stackBrownstoneTile = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.TILE.getMeta());
    ItemStack stackBrownstoneCreeper = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.CREEPER.getMeta());

    // normal recipe
    // 2 redstone + sandstone =  brownstone
    ItemStack regularBrownstoneRecipeOut = stackBrownstoneRough.copy();
    regularBrownstoneRecipeOut.stackSize = 3;
    GameRegistry.addRecipe(new ShapedOreRecipe(regularBrownstoneRecipeOut, " s ", "rsr", " s ", 'r', "dustRedstone", 's', "sandstone"));

    // smelting to get smooth and cracked
    GameRegistry.addSmelting(stackBrownstoneRough.copy(), stackBrownstoneSmooth.copy(), 0.1f);
    GameRegistry.addSmelting(stackBrownstoneBrick.copy(), stackBrownstoneBrickCracked.copy(), 0.1f);

    // remaining brownstone types
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.PAVER, BlockBrownstone.BrownstoneType.SMOOTH);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.BRICK, BlockBrownstone.BrownstoneType.PAVER);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.BRICK_FANCY, BlockBrownstone.BrownstoneType.BRICK);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.BRICK_SQUARE, BlockBrownstone.BrownstoneType.BRICK_FANCY);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.BRICK_TRIANGLE, BlockBrownstone.BrownstoneType.BRICK_SQUARE);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.CREEPER, BlockBrownstone.BrownstoneType.BRICK_TRIANGLE);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.BRICK_SMALL, BlockBrownstone.BrownstoneType.CREEPER);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.TILE, BlockBrownstone.BrownstoneType.BRICK_SMALL);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.ROAD, BlockBrownstone.BrownstoneType.TILE);
    addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType.PAVER, BlockBrownstone.BrownstoneType.ROAD);

    // slabs
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.SMOOTH.getMeta()), stackBrownstoneSmooth.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.ROUGH.getMeta()), stackBrownstoneRough.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.PAVER.getMeta()), stackBrownstonePaver.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.BRICK.getMeta()), stackBrownstoneBrick.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.BRICK_CRACKED.getMeta()), stackBrownstoneBrickCracked.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.BRICK_FANCY.getMeta()), stackBrownstoneBrickFancy.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.BRICK_SQUARE.getMeta()), stackBrownstoneBrickSquare.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab, 1, BlockBrownstoneSlab.BrownstoneType.ROAD.getMeta()), stackBrownstoneRoad.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab2, 1, BlockBrownstoneSlab2.BrownstoneType.CREEPER.getMeta()), stackBrownstoneCreeper.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab2, 1, BlockBrownstoneSlab2.BrownstoneType.BRICK_TRIANGLE.getMeta()), stackBrownstoneBrickTriangle.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab2, 1, BlockBrownstoneSlab2.BrownstoneType.BRICK_SMALL.getMeta()), stackBrownstoneBrickSmall.copy());
    addSlabRecipe(new ItemStack(brownstoneSlab2, 1, BlockBrownstoneSlab2.BrownstoneType.TILE.getMeta()), stackBrownstoneTile.copy());

    // stairs
    addStairRecipe(brownstoneStairsSmooth, stackBrownstoneSmooth);
    addStairRecipe(brownstoneStairsRough, stackBrownstoneRough);
    addStairRecipe(brownstoneStairsPaver, stackBrownstonePaver);
    addStairRecipe(brownstoneStairsBrick, stackBrownstoneBrick);
    addStairRecipe(brownstoneStairsBrickCracked, stackBrownstoneBrickCracked);
    addStairRecipe(brownstoneStairsBrickFancy, stackBrownstoneBrickFancy);
    addStairRecipe(brownstoneStairsBrickSquare, stackBrownstoneBrickSquare);
    addStairRecipe(brownstoneStairsRoad, stackBrownstoneRoad);
    addStairRecipe(brownstoneStairsCreeper, stackBrownstoneCreeper);
    addStairRecipe(brownstoneStairsBrickTriangle, stackBrownstoneBrickTriangle);
    addStairRecipe(brownstoneStairsBrickSmall, stackBrownstoneBrickSmall);
    addStairRecipe(brownstoneStairsTile, stackBrownstoneTile);
  }

  private void addFrameRecipe(String nugget, EntityFancyItemFrame.FrameType type) {
    Object obsidian = Blocks.OBSIDIAN;

    ItemStack frame = new ItemStack(TinkerGadgets.fancyFrame, 1, type.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, " n ", "nOn", " n ", 'O', obsidian, 'n', nugget));
  }

  private void addBrownstoneBrickRecipe(BlockBrownstone.BrownstoneType out, BlockBrownstone.BrownstoneType in) {
    addBrickRecipe(brownstone, out, in);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerDrying();

    // prevents items from despawning in slime channels
    MinecraftForge.EVENT_BUS.register(BlockSlimeChannel.EventHandler.instance);
    MinecraftForge.EVENT_BUS.register(new GadgetEvents());

    proxy.postInit();
  }

  private void registerDrying() {
    // Jerky
    int time = 20 * 60 * 5;
    TinkerRegistry.registerDryingRecipe(Items.ROTTEN_FLESH, TinkerCommons.jerkyMonster, time);
    TinkerRegistry.registerDryingRecipe(Items.BEEF, TinkerCommons.jerkyBeef, time);
    TinkerRegistry.registerDryingRecipe(Items.CHICKEN, TinkerCommons.jerkyChicken, time);
    TinkerRegistry.registerDryingRecipe(Items.PORKCHOP, TinkerCommons.jerkyPork, time);
    TinkerRegistry.registerDryingRecipe(Items.MUTTON, TinkerCommons.jerkyMutton, time);
    TinkerRegistry.registerDryingRecipe(Items.RABBIT, TinkerCommons.jerkyRabbit, time);

    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 0), TinkerCommons.jerkyFish, time);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 1), TinkerCommons.jerkySalmon, time);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 2), TinkerCommons.jerkyClownfish, time);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 3), TinkerCommons.jerkyPufferfish, time);

    TinkerRegistry.registerDryingRecipe(Items.SLIME_BALL, TinkerCommons.slimedropGreen, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallBlue, TinkerCommons.slimedropBlue, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallPurple, TinkerCommons.slimedropPurple, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallBlood, TinkerCommons.slimedropBlood, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallMagma, TinkerCommons.slimedropMagma, time);

    // leather
    if(Config.leatherDryingRecipe) {
      ItemStack leather = new ItemStack(Items.LEATHER);
      time = (int) (20 * 60 * 8.5);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_BEEF, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_CHICKEN, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_FISH, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_MUTTON, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_PORKCHOP, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_RABBIT, leather, time);
    }

    // Dried Clay
    TinkerRegistry.registerDryingRecipe(Items.CLAY_BALL, TinkerCommons.driedBrick, 20 * 60 * 2);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Blocks.CLAY), new ItemStack(driedClay, 1, BlockDriedClay.DriedClayType.CLAY.getMeta()), 20 * 60 * 6);

    // Wet sponge to dry sponge
    TinkerRegistry.registerDryingRecipe(new ItemStack(Blocks.SPONGE, 1, 1), new ItemStack(Blocks.SPONGE, 1, 0), 20 * 60 * 2);

    // Sapling to dead bush
    TinkerRegistry.registerDryingRecipe("treeSapling", new ItemStack(Blocks.DEADBUSH), 20 * 60 * 6);
  }
}
