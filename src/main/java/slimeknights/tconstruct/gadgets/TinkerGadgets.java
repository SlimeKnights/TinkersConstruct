package slimeknights.tconstruct.gadgets;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
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

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.gadgets.block.BlockDriedClay;
import slimeknights.tconstruct.gadgets.block.BlockPunji;
import slimeknights.tconstruct.gadgets.block.BlockRack;
import slimeknights.tconstruct.gadgets.block.BlockStoneLadder;
import slimeknights.tconstruct.gadgets.block.BlockStoneTorch;
import slimeknights.tconstruct.gadgets.block.BlockWoodRail;
import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.gadgets.item.ItemBlockRack;
import slimeknights.tconstruct.gadgets.item.ItemFancyItemFrame;
import slimeknights.tconstruct.gadgets.item.ItemSlimeBoots;
import slimeknights.tconstruct.gadgets.item.ItemSlimeSling;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.gadgets.tileentity.TileDryingRack;
import slimeknights.tconstruct.gadgets.tileentity.TileItemRack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.TableRecipe;

@Pulse(id = TinkerGadgets.PulseId, description = "All the fun toys")
public class TinkerGadgets extends TinkerPulse {

  public static final String PulseId = "TinkerGadgets";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.gadgets.GadgetClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static Block stoneTorch;
  public static Block stoneLadder;
  public static Block woodRail;
  public static Block punji;
  public static BlockRack rack;
  public static Block driedClay;

  public static ItemSlimeSling slimeSling;
  public static ItemSlimeBoots slimeBoots;
  public static ItemThrowball throwball;
  public static Item stoneStick;

  public static ItemHangingEntity fancyFrame;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    stoneTorch = registerBlock(new BlockStoneTorch(), "stone_torch");
    stoneLadder = registerBlock(new BlockStoneLadder(), "stone_ladder");
    woodRail = registerBlock(new BlockWoodRail(), "wood_rail");
    punji = registerBlock(new BlockPunji(), "punji");
    rack = registerBlock(new ItemBlockRack(new BlockRack()), "rack");
    driedClay = registerEnumBlock(new BlockDriedClay(), "dried_clay");

    registerTE(TileItemRack.class, "item_rack");
    registerTE(TileDryingRack.class, "drying_rack");

    slimeSling = registerItem(new ItemSlimeSling(), "slimesling");
    slimeBoots = registerItem(new ItemSlimeBoots(), "slime_boots");
    throwball = registerItem(new ItemThrowball(), "throwball");
    stoneStick = registerItem(new Item(), "stone_stick");
    stoneStick.setFull3D().setCreativeTab(TinkerRegistry.tabGadgets);

    fancyFrame = registerItem(new ItemFancyItemFrame(), "fancy_frame");

    EntityRegistry.registerModEntity(EntityFancyItemFrame.class, "Fancy Item Frame", EntityIDs.FANCY_FRAME, TConstruct.instance, 160, Integer.MAX_VALUE, false);
    EntityRegistry.registerModEntity(EntityThrowball.class, "Throwball", EntityIDs.THROWBALL, TConstruct.instance, 64, 10, true);
    //EntityRegistry.instance().lookupModSpawn(EntityFancyItemFrame.class, false).setCustomSpawning(null, true);

    MinecraftForge.EVENT_BUS.register(slimeBoots);

    proxy.preInit();

    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(slimeSling));
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerRecipes();

    proxy.init();
  }

  private void registerRecipes() {
    String ore = "blockSlime";
    if(isWorldLoaded()) {
      ore = "blockSlimeCongealed";
    }

    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeBoots), "   ", "s s", "b b", 's', "slimeball", 'b', ore));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeSling), "fbf", "s s", " s ", 'f', Items.STRING, 's', "slimeball", 'b', ore));

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

    // Punji Sticks
    GameRegistry.addRecipe(new ItemStack(punji, 3, 0), "b b", " b ", "b b", 'b', new ItemStack(Items.REEDS));

    // Item Rack, a bit cheaper to encourage it for decoration
    GameRegistry.addRecipe(new TableRecipe(OreDictionary.getOres("slabWood"), rack, 0, "ww", 'w', "slabWood"));

    // Drying Rack
    GameRegistry.addRecipe(new TableRecipe(OreDictionary.getOres("slabWood"), rack, 1, "www", 'w', "slabWood"));

    // Dried Bricks
    GameRegistry.addRecipe(new ItemStack(driedClay, 1, 1), "bb", "bb", 'b', TinkerCommons.driedBrick);

    // fancy item frames
    ItemStack frame = new ItemStack(TinkerGadgets.fancyFrame, 1, EntityFancyItemFrame.FrameType.GOLD.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, "nnn", "nOn", "nnn", 'O', Blocks.OBSIDIAN, 'n', "nuggetGold"));

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
    GameRegistry.addRecipe(new ShapedOreRecipe(glowball, "SSS","SGS", "SSS", 'S', Items.SNOWBALL, 'G', "dustGlowstone"));

    ItemStack efln = new ItemStack(throwball, 1, ItemThrowball.ThrowballType.EFLN.ordinal());
    GameRegistry.addShapelessRecipe(efln, Items.FLINT, Items.GUNPOWDER);
    GameRegistry.addRecipe(new ShapelessOreRecipe(efln, Items.FLINT, "dustSulfur"));
  }

  private void addFrameRecipe(String nugget, EntityFancyItemFrame.FrameType type) {
    Object obsidian = Blocks.OBSIDIAN;

    ItemStack frame = new ItemStack(TinkerGadgets.fancyFrame, 1, type.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, " n ", "nOn", " n ", 'O', obsidian, 'n', nugget));
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerDrying();

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

    // Dried Clay
    TinkerRegistry.registerDryingRecipe(Items.CLAY_BALL, TinkerCommons.driedBrick, 20 * 60 * 2);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Blocks.CLAY), new ItemStack(driedClay, 1, 0), 20 * 60 * 6);

    // Wet sponge to dry sponge
    TinkerRegistry.registerDryingRecipe(new ItemStack(Blocks.SPONGE, 1, 1), new ItemStack(Blocks.SPONGE, 1, 0), 20 * 60 * 2);

    // Sapling to dead bush
    TinkerRegistry.registerDryingRecipe("treeSapling", new ItemStack(Blocks.DEADBUSH), 20 * 60 * 6);
  }
}
