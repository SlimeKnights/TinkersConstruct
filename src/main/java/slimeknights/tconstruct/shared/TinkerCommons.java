package slimeknights.tconstruct.shared;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.shared.block.BlockMetal;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.BlockOre;
import slimeknights.tconstruct.shared.worldgen.NetherOreGenerator;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerCommons.PulseId, forced = true)
public class TinkerCommons extends TinkerPulse {

  public static final String PulseId = "TinkerCommons";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.shared.CommonsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static BlockSoil blockSoil;
  public static BlockOre blockOre;
  public static BlockMetal blockMetal;

  // block itemstacks
  public static ItemStack grout;
  public static ItemStack slimyMudGreen;
  public static ItemStack slimyMudBlue;
  public static ItemStack graveyardSoil;
  public static ItemStack consecratedSoil;

  public static ItemStack oreCobalt;
  public static ItemStack oreArdite;

  public static ItemStack blockCobalt;
  public static ItemStack blockArdite;
  public static ItemStack blockManyullyn;
  public static ItemStack blockKnightSlime;

  public static ItemMetaDynamic nuggets;
  public static ItemMetaDynamic ingots;
  public static ItemMetaDynamic materials;
  //public static ItemMetaDynamic slimeballs;
  public static ItemEdible edibles;

  // Nugget Itemstacks
  public static ItemStack nuggetCobalt;
  public static ItemStack nuggetArdite;
  public static ItemStack nuggetManyullyn;
  public static ItemStack nuggetKnightSlime;

  // Ingot Itemstacks
  public static ItemStack ingotCobalt;
  public static ItemStack ingotArdite;
  public static ItemStack ingotManyullyn;
  public static ItemStack ingotKnightSlime;

  // Material Itemstacks
  public static ItemStack searedBrick;
  public static ItemStack matSlimeBallBlue;
  public static ItemStack matSlimeBallPurple;
  public static ItemStack matSlimeBallBlood;

  public static ItemStack matSlimeCrystal;
  public static ItemStack matSlimeCrystalBlue;

  public static ItemStack matExpanderW;
  public static ItemStack matExpanderH;
  public static ItemStack matReinforcement;
  public static ItemStack matCreativeModifier;

  // Misc.
  public static ItemStack bacon;

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    // Soils
    blockSoil = registerEnumBlock(new BlockSoil(), "soil");

    grout = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GROUT.getMeta());
    slimyMudGreen = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_GREEN.getMeta());
    slimyMudBlue = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_BLUE.getMeta());
    graveyardSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GRAVEYARD.getMeta());
    consecratedSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.CONSECRATED.getMeta());

    // Ores
    blockOre = registerEnumBlock(new BlockOre(), "ore");

    oreCobalt = new ItemStack(blockOre, 1, BlockOre.OreTypes.COBALT.getMeta());
    oreArdite = new ItemStack(blockOre, 1, BlockOre.OreTypes.ARDITE.getMeta());

    // create the items. We can probably always create them since they handle themselves dynamically
    nuggets = registerItem(new ItemMetaDynamic(), "nuggets");
    ingots = registerItem(new ItemMetaDynamic(), "ingots");
    materials = registerItem(new ItemMetaDynamic(), "materials");
    edibles = registerItem(new ItemEdible(), "edible");

    nuggets.setCreativeTab(TinkerRegistry.tabGeneral);
    ingots.setCreativeTab(TinkerRegistry.tabGeneral);
    materials.setCreativeTab(TinkerRegistry.tabGeneral);
    edibles.setCreativeTab(TinkerRegistry.tabGeneral);

    // Items that can always be present.. slimeballs
    matSlimeBallBlue = edibles.addFood(1, 1, 1f, "slimeball_blue");
    matSlimeBallPurple = edibles.addFood(2, 1, 2f, "slimeball_purple");
    matSlimeBallBlood = edibles.addFood(3, 1, 1.5f, "slimeball_blood");

    // All other items are either ingots or items for modifiers

    if(isSmelteryLoaded() || forced) {
      searedBrick = materials.addMeta(0, "seared_brick");
    }

    // Ingots and nuggets
    if(isToolsLoaded() || isSmelteryLoaded() || forced) {
      nuggetCobalt = nuggets.addMeta(0, "cobalt");
      ingotCobalt = ingots.addMeta(0, "cobalt");

      nuggetArdite = nuggets.addMeta(1, "ardite");
      ingotArdite = ingots.addMeta(1, "ardite");

      nuggetManyullyn = nuggets.addMeta(2, "manyullyn");
      ingotManyullyn = ingots.addMeta(2, "manyullyn");

      blockMetal = registerEnumBlock(new BlockMetal(), "metal");

      blockCobalt = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.COBALT.getMeta());
      blockArdite = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.ARDITE.getMeta());
      blockManyullyn = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.MANYULLYN.getMeta());
      blockKnightSlime = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.KNIGHTSLIME.getMeta());
    }

    // Materials
    if(isToolsLoaded() || forced) {
      bacon = edibles.addFood(0, 4, 0.6f, "bacon");

      matSlimeCrystal = materials.addMeta(9, "slimecrystal_green");
      matSlimeCrystalBlue = materials.addMeta(10, "slimecrystal_blue");
      matExpanderW = materials.addMeta(12, "expander_w");
      matExpanderH = materials.addMeta(13, "expander_h");
      matReinforcement = materials.addMeta(14, "reinforcement");

      matCreativeModifier = materials.addMeta(50, "creative_modifier");

      ingotKnightSlime = ingots.addMeta(3, "knightslime");
      nuggetKnightSlime = nuggets.addMeta(3, "knightslime");
    }

    // oredicting time
    registerRecipies();

    proxy.preInit();

    TinkerRegistry.tabGeneral.setDisplayIcon(matSlimeBallBlue);
  }

  private void registerRecipies() {
    registerMetalRecipes("Cobalt", ingotCobalt, nuggetCobalt, blockCobalt);
    registerMetalRecipes("Ardite", ingotArdite, nuggetArdite, blockArdite);
    registerMetalRecipes("Manyullyn", ingotManyullyn, nuggetManyullyn, blockManyullyn);
    registerMetalRecipes("Knightslime", ingotKnightSlime, nuggetKnightSlime, blockKnightSlime);
  }

  private void registerMetalRecipes(String oreString, ItemStack ingot, ItemStack nugget, ItemStack block) {
    if(ingot == null) {
      return;
    }

    // nugget recipies
    if(nugget != null) {
      registerFullrecipe(nugget, ingot, "nugget" + oreString, "ingot" + oreString);
    }
    // block recipies
    if(block != null) {
      registerFullrecipe(ingot, block, "ingot" + oreString, "block" + oreString);
    }
  }

  private void registerFullrecipe(ItemStack small, ItemStack big, String oreSmall, String oreBig) {
    // ingot -> block
    //GameRegistry.addShapedRecipe(big, "###", "###", "###", '#', small);
    GameRegistry.addRecipe(new ShapedOreRecipe(big, "###", "###", "###", '#', oreSmall));
    // block -> 9 ingot
    small = small.copy();
    small.stackSize = 9;
    //GameRegistry.addShapelessRecipe(small, big);
    GameRegistry.addRecipe(new ShapelessOreRecipe(small, oreBig));
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    GameRegistry.registerWorldGenerator(NetherOreGenerator.INSTANCE, 0);
  }
}
