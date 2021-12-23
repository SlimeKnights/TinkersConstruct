package slimeknights.tconstruct.gadgets;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

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
import slimeknights.tconstruct.gadgets.block.BlockWoodenHopper;
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
import slimeknights.tconstruct.gadgets.tileentity.TileWoodenHopper;
import slimeknights.tconstruct.gadgets.tileentity.TileItemRack;
import slimeknights.tconstruct.gadgets.tileentity.TileSlimeChannel;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSlime.SlimeType;

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

  public static BlockWoodenHopper woodenHopper;

  public static BlockSlimeChannel slimeChannel;

  public static BlockDriedClaySlab driedClaySlab;
  public static BlockBrownstoneSlab brownstoneSlab;
  public static BlockBrownstoneSlab2 brownstoneSlab2;

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

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    stoneTorch = registerBlock(registry, new BlockStoneTorch(), "stone_torch");
    stoneLadder = registerBlock(registry, new BlockStoneLadder(), "stone_ladder");
    punji = registerBlock(registry, new BlockPunji(), "punji");
    rack = registerBlock(registry, new BlockRack(), "rack");

    woodRail = registerBlock(registry, new BlockWoodRail(), "wood_rail");
    woodRailTrapdoor = registerBlock(registry, new BlockWoodRailDropper(), "wood_rail_trapdoor");

    woodenHopper = registerBlock(registry, new BlockWoodenHopper(), "wooden_hopper");

    // slime channels
    slimeChannel = registerBlock(registry, new BlockSlimeChannel(), "slime_channel");

    // dried clay
    driedClay = registerBlock(registry, new BlockDriedClay(), "dried_clay");
    driedClaySlab = registerBlock(registry, new BlockDriedClaySlab(), "dried_clay_slab");
    driedClayStairs = registerBlockStairsFrom(registry, driedClay, BlockDriedClay.DriedClayType.CLAY, "dried_clay_stairs");
    driedBrickStairs = registerBlockStairsFrom(registry, driedClay, BlockDriedClay.DriedClayType.BRICK, "dried_brick_stairs");

    // brownstone
    brownstone = registerBlock(registry, new BlockBrownstone(), "brownstone");
    brownstoneSlab = registerBlock(registry, new BlockBrownstoneSlab(), "brownstone_slab");
    brownstoneSlab2 = registerBlock(registry, new BlockBrownstoneSlab2(), "brownstone_slab2");

    // stairs
    brownstoneStairsSmooth = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.SMOOTH, "brownstone_stairs_smooth");
    brownstoneStairsRough = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.ROUGH, "brownstone_stairs_rough");
    brownstoneStairsPaver = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.PAVER, "brownstone_stairs_paver");
    brownstoneStairsBrick = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.BRICK, "brownstone_stairs_brick");
    brownstoneStairsBrickCracked = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.BRICK_CRACKED, "brownstone_stairs_brick_cracked");
    brownstoneStairsBrickFancy = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.BRICK_FANCY, "brownstone_stairs_brick_fancy");
    brownstoneStairsBrickSquare = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.BRICK_SQUARE, "brownstone_stairs_brick_square");
    brownstoneStairsBrickTriangle = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.BRICK_TRIANGLE, "brownstone_stairs_brick_triangle");
    brownstoneStairsBrickSmall = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.BRICK_SMALL, "brownstone_stairs_brick_small");
    brownstoneStairsRoad = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.ROAD, "brownstone_stairs_road");
    brownstoneStairsTile = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.TILE, "brownstone_stairs_tile");
    brownstoneStairsCreeper = registerBlockStairsFrom(registry, brownstone, BlockBrownstone.BrownstoneType.CREEPER, "brownstone_stairs_creeper");

    registerTE(TileItemRack.class, "item_rack");
    registerTE(TileDryingRack.class, "drying_rack");
    registerTE(TileWoodenHopper.class, "wooden_hopper");
    registerTE(TileSlimeChannel.class, "slime_channel");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    stoneTorch = registerItemBlock(registry, stoneTorch);
    stoneLadder = registerItemBlock(registry, stoneLadder);
    punji = registerItemBlock(registry, punji);
    rack = registerItemBlock(registry, new ItemBlockRack(rack));

    woodRail = registerItemBlock(registry, woodRail);
    woodRailTrapdoor = registerItemBlock(registry, woodRailTrapdoor);

    woodenHopper = registerItemBlock(registry, woodenHopper);

    // slime channels
    slimeChannel = registerEnumItemBlock(registry, slimeChannel);

    // dried clay
    driedClay = registerEnumItemBlock(registry, driedClay);
    driedClaySlab = registerEnumItemBlockSlab(registry, driedClaySlab);
    driedClayStairs = registerItemBlock(registry, driedClayStairs);
    driedBrickStairs = registerItemBlock(registry, driedBrickStairs);

    // brownstone
    brownstone = registerEnumItemBlock(registry, brownstone);
    brownstoneSlab = registerEnumItemBlockSlab(registry, brownstoneSlab);
    brownstoneSlab2 = registerEnumItemBlockSlab(registry, brownstoneSlab2);

    // stairs
    brownstoneStairsSmooth = registerItemBlock(registry, brownstoneStairsSmooth);
    brownstoneStairsRough = registerItemBlock(registry, brownstoneStairsRough);
    brownstoneStairsPaver = registerItemBlock(registry, brownstoneStairsPaver);
    brownstoneStairsBrick = registerItemBlock(registry, brownstoneStairsBrick);
    brownstoneStairsBrickCracked = registerItemBlock(registry, brownstoneStairsBrickCracked);
    brownstoneStairsBrickFancy = registerItemBlock(registry, brownstoneStairsBrickFancy);
    brownstoneStairsBrickSquare = registerItemBlock(registry, brownstoneStairsBrickSquare);
    brownstoneStairsBrickTriangle = registerItemBlock(registry, brownstoneStairsBrickTriangle);
    brownstoneStairsBrickSmall = registerItemBlock(registry, brownstoneStairsBrickSmall);
    brownstoneStairsRoad = registerItemBlock(registry, brownstoneStairsRoad);
    brownstoneStairsTile = registerItemBlock(registry, brownstoneStairsTile);
    brownstoneStairsCreeper = registerItemBlock(registry, brownstoneStairsCreeper);

    slimeSling = registerItem(registry, new ItemSlimeSling(), "slimesling");
    slimeBoots = registerItem(registry, new ItemSlimeBoots(), "slime_boots");
    piggybackPack = registerItem(registry, new ItemPiggybackPack(), "piggybackpack");
    throwball = registerItem(registry, new ItemThrowball(), "throwball");
    stoneStick = registerItem(registry, new Item(), "stone_stick");
    stoneStick.setFull3D().setCreativeTab(TinkerRegistry.tabGadgets);

    fancyFrame = registerItem(registry, new ItemFancyItemFrame(), "fancy_frame");

    spaghetti = registerItem(registry, new ItemSpaghetti(), "spaghetti");
    momsSpaghetti = registerItem(registry, new ItemMomsSpaghetti(), "moms_spaghetti");

    ItemStack hardSpaghetti = spaghetti.addMeta(0, "hard");
    ItemStack wetSpaghetti = spaghetti.addMeta(1, "soggy");
    ItemStack coldSpaghetti = spaghetti.addMeta(2, "cold");

    modSpaghettiSauce = new ModSpaghettiSauce();

    modSpaghettiMeat = new ModSpaghettiMeat();
    modSpaghettiMeat.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(Items.COOKED_BEEF), new ItemStack(Items.COOKED_CHICKEN), new ItemStack(Items.COOKED_MUTTON), new ItemStack(Items.COOKED_PORKCHOP)));

    // Recipe for mom's spaghetti: soak em, dry em, cook em, eat em
    TinkerRegistry.registerTableCasting(new CastingRecipe(wetSpaghetti, RecipeMatch.of(hardSpaghetti), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME * 3), 15 * 60 * 20, true, false));
    TinkerRegistry.registerDryingRecipe(wetSpaghetti, coldSpaghetti, 15 * 60 * 20);
    GameRegistry.addSmelting(coldSpaghetti, new ItemStack(momsSpaghetti), 2.0f);

    MinecraftForge.EVENT_BUS.register(slimeBoots);
  }

  @SubscribeEvent
  public void registerEntities(Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(Util.getResource("fancy_frame"), EntityFancyItemFrame.class, "Fancy Item Frame", EntityIDs.FANCY_FRAME, TConstruct.instance, 160, Integer.MAX_VALUE, false);
    EntityRegistry.registerModEntity(Util.getResource("throwball"), EntityThrowball.class, "Throwball", EntityIDs.THROWBALL, TConstruct.instance, 64, 10, true);
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerSmelting();

    proxy.init();
  }

  private void registerSmelting() {
    // slime channels
    for(SlimeType type : SlimeType.values()) {
      GameRegistry.addSmelting(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, type.getMeta()),
                               new ItemStack(slimeChannel, 3, type.getMeta()), 0.15f);
    }

    ItemStack stackBrownstoneSmooth = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.SMOOTH.getMeta());
    ItemStack stackBrownstoneRough = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.ROUGH.getMeta());
    ItemStack stackBrownstoneBrick = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK.getMeta());
    ItemStack stackBrownstoneBrickCracked = new ItemStack(brownstone, 1, BlockBrownstone.BrownstoneType.BRICK_CRACKED.getMeta());

    // smelting to get smooth and cracked
    GameRegistry.addSmelting(stackBrownstoneRough.copy(), stackBrownstoneSmooth.copy(), 0.1f);
    GameRegistry.addSmelting(stackBrownstoneBrick.copy(), stackBrownstoneBrickCracked.copy(), 0.1f);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerDrying();

    // prevents items from despawning in slime channels
    MinecraftForge.EVENT_BUS.register(BlockSlimeChannel.EventHandler.instance);
    MinecraftForge.EVENT_BUS.register(new GadgetEvents());

    proxy.postInit();

    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(slimeSling));
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
