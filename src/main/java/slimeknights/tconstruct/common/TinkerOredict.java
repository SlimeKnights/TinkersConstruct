package slimeknights.tconstruct.common;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;

import static slimeknights.tconstruct.gadgets.TinkerGadgets.stoneStick;
import static slimeknights.tconstruct.gadgets.TinkerGadgets.stoneTorch;
import static slimeknights.tconstruct.shared.TinkerCommons.blockAlubrass;
import static slimeknights.tconstruct.shared.TinkerCommons.blockArdite;
import static slimeknights.tconstruct.shared.TinkerCommons.blockClearGlass;
import static slimeknights.tconstruct.shared.TinkerCommons.blockClearStainedGlass;
import static slimeknights.tconstruct.shared.TinkerCommons.blockCobalt;
import static slimeknights.tconstruct.shared.TinkerCommons.blockKnightSlime;
import static slimeknights.tconstruct.shared.TinkerCommons.blockManyullyn;
import static slimeknights.tconstruct.shared.TinkerCommons.blockPigIron;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSlime;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSlimeCongealed;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotAlubrass;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotArdite;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotCobalt;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotKnightSlime;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotManyullyn;
import static slimeknights.tconstruct.shared.TinkerCommons.ingotPigIron;
import static slimeknights.tconstruct.shared.TinkerCommons.matNecroticBone;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeBallBlood;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeBallBlue;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeBallMagma;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeBallPurple;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeCrystalBlue;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeCrystalGreen;
import static slimeknights.tconstruct.shared.TinkerCommons.matSlimeCrystalMagma;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetAlubrass;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetArdite;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetCobalt;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetKnightSlime;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetManyullyn;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggetPigIron;
import static slimeknights.tconstruct.shared.TinkerCommons.oreArdite;
import static slimeknights.tconstruct.shared.TinkerCommons.oreCobalt;
import static slimeknights.tconstruct.shared.TinkerCommons.searedBrick;
import static slimeknights.tconstruct.tools.TinkerTools.binding;
import static slimeknights.tconstruct.tools.TinkerTools.pattern;
import static slimeknights.tconstruct.tools.TinkerTools.pickHead;
import static slimeknights.tconstruct.tools.TinkerTools.toolRod;
import static slimeknights.tconstruct.tools.TinkerTools.toolTables;
import static slimeknights.tconstruct.world.TinkerWorld.slimeDirt;
import static slimeknights.tconstruct.world.TinkerWorld.slimeGrass;
import static slimeknights.tconstruct.world.TinkerWorld.slimeLeaves;
import static slimeknights.tconstruct.world.TinkerWorld.slimeSapling;
import static slimeknights.tconstruct.world.TinkerWorld.slimeVineBlue1;
import static slimeknights.tconstruct.world.TinkerWorld.slimeVineBlue2;
import static slimeknights.tconstruct.world.TinkerWorld.slimeVineBlue3;
import static slimeknights.tconstruct.world.TinkerWorld.slimeVinePurple1;
import static slimeknights.tconstruct.world.TinkerWorld.slimeVinePurple2;
import static slimeknights.tconstruct.world.TinkerWorld.slimeVinePurple3;

/**
 * oredicts ALL the things in TConstruct.
 * Conveniently gathered in one place!
 */
@Pulse(id = TinkerOredict.PulseId, forced = true)
public class TinkerOredict {

  public static final String PulseId = "TinkerOredict";
  public static final String[] dyes = { // makes oredict to int a bit easier in a couple other places
                                        "White",
                                        "Orange",
                                        "Magenta",
                                        "LightBlue",
                                        "Yellow",
                                        "Lime",
                                        "Pink",
                                        "Gray",
                                        "LightGray",
                                        "Cyan",
                                        "Purple",
                                        "Blue",
                                        "Brown",
                                        "Green",
                                        "Red",
                                        "Black"
  };
  public static final Set<Item> COOKED_MEAT = ImmutableSet.<Item>builder()
      .add(Items.COOKED_BEEF)
      .add(Items.COOKED_CHICKEN)
      .add(Items.COOKED_FISH)
      .add(Items.COOKED_MUTTON)
      .add(Items.COOKED_PORKCHOP)
      .add(Items.COOKED_RABBIT)
      .build();

  /**
   * Registers all the blocks and item oredicts.
   * Note that it's using the item registry event, since it's called after blocks.
   * This relies on the TinkerOredict pulse being called after the pulses registering the items
   */
  @SubscribeEvent
  public void registerItems(RegistryEvent.Register<Item> event) {
    ensureOredict();
    registerCommon();
    registerTools();
    registerSmeltery();
    registerWorld();
    registerGadgets();
  }


  // Things that are not from tinkers but should be oredicted
  private static void ensureOredict() {
    // crafting table
    oredict(Blocks.CRAFTING_TABLE, "workbench");
    // some vanilla blocks
    oredict(Blocks.CACTUS, "blockCactus");
    oredict(Blocks.SLIME_BLOCK, "blockSlime");
    oredict(Blocks.OBSIDIAN, "obsidian");
    oredict(Blocks.NETHERRACK, "netherrack");
    oredict(Blocks.PRISMARINE, "prismarine");
    oredict(Blocks.VINE, "vine");
    // glowstone block, redstone block

    oredict(Blocks.DIRT, "dirt");

    oredict(Blocks.MOSSY_COBBLESTONE, "blockMossy");
    oredict(new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.MOSSY_META), "blockMossy");

    oredict(Blocks.TRAPDOOR, "trapdoorWood");

    // vanilla cooked meat
    // compatibility with pams harvestcraft
    for(Item meat : COOKED_MEAT) {
      oredict(meat, "listAllmeatcooked");
    }

    oredict(new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE), "fish");
  }

  // common items and blocks
  private static void registerCommon() {
    String dict = "slimeball";
    oredict(Items.SLIME_BALL, dict + "Green"); // oredict vanilla as green slime ball
    oredict(matSlimeBallBlue, dict, dict + "Blue");
    oredict(matSlimeBallPurple, dict, dict + "Purple");
    oredict(matSlimeBallBlood, dict, dict + "Blood");
    oredict(matSlimeBallMagma, dict, dict + "Magma");

    oredictNIB(nuggetCobalt, ingotCobalt, blockCobalt, "Cobalt");
    oredictNIB(nuggetArdite, ingotArdite, blockArdite, "Ardite");
    oredictNIB(nuggetManyullyn, ingotManyullyn, blockManyullyn, "Manyullyn");
    oredictNIB(nuggetKnightSlime, ingotKnightSlime, blockKnightSlime, "Knightslime");
    oredictNIB(nuggetPigIron, ingotPigIron, blockPigIron, "Pigiron");
    oredictNIB(nuggetAlubrass, ingotAlubrass, blockAlubrass, "Alubrass");

    String metal = "blockMetal";
    oredict(new ItemStack(Blocks.IRON_BLOCK), metal);
    oredict(new ItemStack(Blocks.GOLD_BLOCK), metal);

    // other materials
    oredict(searedBrick, "ingotBrickSeared");
    dict = "slimecrystal";
    oredict(matSlimeCrystalGreen, dict, dict + "Green");
    oredict(matSlimeCrystalBlue, dict, dict + "Blue");
    oredict(matSlimeCrystalMagma, dict, dict + "Magma");

    // Ores
    oredict(oreCobalt, "oreCobalt");
    oredict(oreArdite, "oreArdite");

    // glass
    oredict(blockClearGlass, "blockGlass"); // no blockGlassColorless as then it is assumed as available for staining
    // which blocks our own staining recipes
    oredict(blockClearStainedGlass, "blockGlass");
    for(int i = 0; i < 16; i++) {
      oredict(blockClearStainedGlass, i, "blockGlass" + dyes[i]);
    }
  }

  private static void oredictNIB(ItemStack nugget, ItemStack ingot, ItemStack block, String oreSuffix) {
    oredict(nugget, "nugget" + oreSuffix);
    oredict(ingot, "ingot" + oreSuffix);
    oredict(block, "block" + oreSuffix);
  }


  private static void registerTools() {
    // TinkerTools Pulse
    oredict(toolTables, BlockToolTable.TableTypes.CraftingStation.meta, "workbench");
    oredict(matNecroticBone, "boneWithered");

    oredict(pickHead, "partPickHead");
    oredict(binding, "partBinding");
    oredict(toolRod, "partToolRod");

    oredict(pattern, "pattern");
  }

  private static void registerSmeltery() {
    oredict(TinkerSmeltery.cast, "cast");
    oredict(TinkerSmeltery.castCustom, "cast");
    oredict(TinkerSmeltery.searedBlock, OreDictionary.WILDCARD_VALUE, "blockSeared");
  }

  private static void registerWorld() {
    oredict(slimeSapling, "treeSapling");
    oredict(blockSlime, "blockSlime");
    oredict(blockSlimeCongealed, "blockSlimeCongealed");
    oredict(slimeDirt, "blockSlimeDirt");
    oredict(slimeGrass, "blockSlimeGrass");
    oredict(slimeLeaves, "treeLeaves");
    oredict(slimeVineBlue1, "vine");
    oredict(slimeVineBlue2, "vine");
    oredict(slimeVineBlue3, "vine");
    oredict(slimeVinePurple1, "vine");
    oredict(slimeVinePurple2, "vine");
    oredict(slimeVinePurple3, "vine");
  }

  private static void registerGadgets() {
    oredict(stoneStick, "rodStone");
    oredict(stoneTorch, "torch");
  }

  /* Helper functions */

  public static void oredict(Item item, String... name) {
    oredict(item, OreDictionary.WILDCARD_VALUE, name);
  }

  public static void oredict(Block block, String... name) {
    oredict(block, OreDictionary.WILDCARD_VALUE, name);
  }

  public static void oredict(Item item, int meta, String... name) {
    oredict(new ItemStack(item, 1, meta), name);
  }

  public static void oredict(Block block, int meta, String... name) {
    oredict(new ItemStack(block, 1, meta), name);
  }

  public static void oredict(ItemStack stack, String... names) {
    if(stack != null && !stack.isEmpty()) {
      for(String name : names) {
        OreDictionary.registerOre(name, stack);
      }
    }
  }
}
