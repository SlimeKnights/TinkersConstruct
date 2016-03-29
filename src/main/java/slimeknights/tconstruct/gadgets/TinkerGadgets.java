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
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.gadgets.block.BlockPunji;
import slimeknights.tconstruct.gadgets.block.BlockStoneLadder;
import slimeknights.tconstruct.gadgets.block.BlockStoneTorch;
import slimeknights.tconstruct.gadgets.block.BlockWoodRail;
import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.gadgets.item.ItemFancyItemFrame;
import slimeknights.tconstruct.gadgets.item.ItemSlimeBoots;
import slimeknights.tconstruct.gadgets.item.ItemSlimeSling;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

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

  public static ItemSlimeSling slimeSling;
  public static ItemSlimeBoots slimeBoots;
  public static Item stoneStick;

  public static ItemHangingEntity fancyFrame;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    stoneTorch = registerBlock(new BlockStoneTorch(), "stone_torch");
    stoneLadder = registerBlock(new BlockStoneLadder(), "stone_ladder");
    woodRail = registerBlock(new BlockWoodRail(), "wood_rail");
    punji = registerBlock(new BlockPunji(), "punji");

    slimeSling = registerItem(new ItemSlimeSling(), "slimesling");
    slimeBoots = registerItem(new ItemSlimeBoots(), "slime_boots");
    stoneStick = registerItem(new Item(), "stone_stick");
    stoneStick.setFull3D().setCreativeTab(TinkerRegistry.tabGadgets);

    fancyFrame = registerItem(new ItemFancyItemFrame(), "fancy_frame");

    EntityRegistry.registerModEntity(EntityFancyItemFrame.class, "Fancy Item Frame", EntityIDs.FANCY_FRAME, TConstruct.instance, 160, Integer.MAX_VALUE, false);
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
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(slimeSling), "fbf", "s s", " s ", 'f', Items.string, 's', "slimeball", 'b', ore));

    // Stonesticks Recipes
    ItemStack rod = new ItemStack(stoneStick);
    GameRegistry.addRecipe(new ShapedOreRecipe(rod, "c", "c", 'c', "cobblestone"));
    GameRegistry.addRecipe(new ShapedOreRecipe(rod, "c", "c", 'c', "stone"));

    // Stone Torch Recipe
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneTorch, 4), "p", "w", 'p', new ItemStack(Items.coal, 1, Short.MAX_VALUE), 'w', "rodStone"));
    // Jack o'Latern Recipe - Stone Torch
    GameRegistry.addRecipe(new ItemStack(Blocks.lit_pumpkin, 1, 0), "p", "s", 'p', new ItemStack(Blocks.pumpkin), 's', new ItemStack(stoneTorch));
    // Stone Ladder Recipe
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneLadder, 3), "w w", "www", "w w", 'w', "rodStone"));
    // Wooden Rail Recipe
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(woodRail, 4, 0), "b b", "bxb", "b b", 'b', "plankWood", 'x', "stickWood"));

    // Punji Sticks
    GameRegistry.addRecipe(new ItemStack(punji, 3, 0), "b b", " b ", "b b", 'b', new ItemStack(Items.reeds));


    // fancy item frames
    ItemStack frame = new ItemStack(TinkerGadgets.fancyFrame, 1, EntityFancyItemFrame.FrameType.GOLD.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, "nnn", "nOn", "nnn", 'O', Blocks.obsidian, 'n', "nuggetGold"));

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
  }

  private void addFrameRecipe(String nugget, EntityFancyItemFrame.FrameType type) {
    Object obsidian = Blocks.obsidian;

    ItemStack frame = new ItemStack(TinkerGadgets.fancyFrame, 1, type.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(frame, " n ", "nOn", " n ", 'O', obsidian, 'n', nugget));
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit();
  }
}
