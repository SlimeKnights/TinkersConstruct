package slimeknights.tconstruct.shared;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.Config;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerCommons.PulseId, forced = true)
public class TinkerCommons extends TinkerPulse {

  public static final String PulseId = "TinkerCommons";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.shared.CommonsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

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
    registerIngotNuggetRecipe(ingotCobalt, nuggetCobalt);
    registerIngotNuggetRecipe(ingotArdite, nuggetArdite);
    registerIngotNuggetRecipe(ingotManyullyn, nuggetManyullyn);
    registerIngotNuggetRecipe(ingotKnightSlime, nuggetKnightSlime);
  }

  private void registerIngotNuggetRecipe(ItemStack ingot, ItemStack nugget) {
    if(ingot == null || nugget == null) {
      return;
    }
    nugget = nugget.copy();
    nugget.stackSize = 9;
    GameRegistry.addShapelessRecipe(nugget, ingot);
    GameRegistry.addShapedRecipe(ingot, "###","###","###", '#', nugget);
  }

}
