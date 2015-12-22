package slimeknights.tconstruct.common;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.block.BlockToolTable;

import static slimeknights.tconstruct.shared.TinkerCommons.*;
import static slimeknights.tconstruct.tools.TinkerTools.*;
import static slimeknights.tconstruct.world.TinkerWorld.*;

/**
 * oredicts ALL the things in TConstruct.
 * Conveniently gathered in one place!
 */
@Pulse(id=TinkerOredict.PulseId, forced = true)
public class TinkerOredict {

  public static final String PulseId = "TinkerOredict";

  @Subscribe
  public static void doTheOredict(FMLPreInitializationEvent event) {
    ensureOredict();
    registerCommon();
    registerTools();
    registerSmeltery();
    registerWorld();
  }


  // Things that are not from tinkers but should be oredicted
  private static void ensureOredict() {
    // crafting table
    oredict(Blocks.crafting_table, "workbench");
    // some vanilla blocks
    oredict(Blocks.cactus, "blockCactus");
    oredict(Blocks.pumpkin, "blockPumpkin");
    oredict(Blocks.lit_pumpkin, "blockPumpkin");
    oredict(Blocks.obsidian, "blockObsidian");
    oredict(Blocks.netherrack, "blockNetherrack");
    oredict(Blocks.prismarine, "blockPrismarine");
    oredict(Blocks.obsidian, "blockObsidian");
    oredict(Blocks.slime_block, "blockSlime");
    oredict(Blocks.piston, "blockPiston");
    oredict(Blocks.sticky_piston, "blockPiston");
    oredict(Blocks.vine, "vine");
    // glowstone block, redstone block

    oredict(Blocks.dirt, "dirt");

    oredict(Items.flint, "flint");
    oredict(Items.prismarine_shard, "shardPrismarine");
    oredict(Items.blaze_powder, "dustBlaze");
  }

  // common items and blocks
  private static void registerCommon() {
    String dict = "slimeball";
    oredict(Items.slime_ball,   dict + "Green"); // oredict vanilla as green slime ball
    oredict(matSlimeBallBlue,   dict, dict + "Blue");
    oredict(matSlimeBallPurple, dict, dict + "Purple");
    oredict(matSlimeBallBlood,  dict, dict + "Blood");

    dict = "ingot";
    oredict(ingotCobalt,      dict + "Cobalt");
    oredict(ingotArdite,      dict + "Ardite");
    oredict(ingotManyullyn,   dict + "Manyullyn");
    oredict(ingotKnightSlime, dict + "Knightslime");

    dict = "nugget";
    oredict(nuggetCobalt,      dict + "Cobalt");
    oredict(nuggetArdite,      dict + "Ardite");
    oredict(nuggetManyullyn,   dict + "Manyullyn");
    oredict(nuggetKnightSlime, dict + "Knightslime");

    dict = "block";
    oredict(blockCobalt,      dict + "Cobalt");
    oredict(blockArdite,      dict + "Ardite");
    oredict(blockManyullyn,   dict + "Manyullyn");
    oredict(blockKnightSlime, dict + "Knightslime");

    // other materials
    oredict(searedBrick, "ingotBrickSeared");
    dict = "slimecrystal";
    oredict(matSlimeCrystal,     dict, dict + "Green");
    oredict(matSlimeCrystalBlue, dict, dict + "Blue");

    // Ores
    oredict(oreCobalt, "oreCobalt");
    oredict(oreArdite, "oreArdite");

  }

  private static void registerTools() {
    // TinkerTools Pulse
    oredict(toolTables, BlockToolTable.TableTypes.CraftingStation.meta, "workbench");

    oredict(pickHead, "partPickHead");
    oredict(binding,  "partBinding");
    oredict(toolRod,  "partToolRod");

    oredict(pattern, "pattern");
  }

  private static void registerSmeltery() {
    oredict(TinkerSmeltery.cast, "cast");
  }

  private static void registerWorld() {
    oredict(slimeSapling, "treeSapling");
    oredict(slimeBlock, "blockSlime");
    oredict(slimeBlockCongealed, "blockSlimeCongealed");
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
    if(stack != null && stack.getItem() != null) {
      for(String name : names) {
        OreDictionary.registerOre(name, stack);
      }
    }
  }
}
