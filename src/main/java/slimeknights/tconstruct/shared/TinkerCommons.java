package slimeknights.tconstruct.shared;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
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

  // Nugget Itemstacks
  public static ItemStack nuggetCobalt;
  public static ItemStack nuggetArdite;
  public static ItemStack nuggetManyullyn;

  // Ingot Itemstacks
  public static ItemStack searedBrick;
  public static ItemStack ingotCobalt;
  public static ItemStack ingotArdite;
  public static ItemStack ingotManyullyn;
  public static ItemStack ingotKnightSlime;

  // Material Itemstacks
  public static ItemStack matSlimeBallBlue;
  public static ItemStack matSlimeBallPurple;
  public static ItemStack matSlimeCrystal;
  public static ItemStack matSlimeCrystalBlue;
  public static ItemStack matExpanderW;
  public static ItemStack matExpanderH;

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    // Ingots and nuggets
    if(isToolsLoaded() || isSmelteryLoaded()) {
      nuggets = registerItem(new ItemMetaDynamic(), "nuggets");
      ingots = registerItem(new ItemMetaDynamic(), "ingots");

      nuggetCobalt = nuggets.addMeta(0, "cobalt");
      ingotCobalt = ingots.addMeta(0, "cobalt");

      nuggetArdite = nuggets.addMeta(1, "ardite");
      ingotArdite = ingots.addMeta(1, "ardite");

      nuggetManyullyn = nuggets.addMeta(2, "manyullyn");
      ingotManyullyn = ingots.addMeta(2, "manyullyn");

      ItemStack nugget = nuggetCobalt.copy();
      nugget.stackSize = 9;
      GameRegistry.addShapelessRecipe(nugget, ingotCobalt);
      GameRegistry.addShapedRecipe(ingotCobalt, "###","###","###", '#', nuggetCobalt);

      nugget = nuggetArdite.copy();
      nugget.stackSize = 9;
      GameRegistry.addShapelessRecipe(nugget, ingotArdite);
      GameRegistry.addShapedRecipe(ingotArdite, "###","###","###", '#', nuggetArdite);

      nugget = nuggetManyullyn.copy();
      nugget.stackSize = 9;
      GameRegistry.addShapelessRecipe(nugget, ingotManyullyn);
      GameRegistry.addShapedRecipe(ingotManyullyn, "###","###","###", '#', nuggetManyullyn);
    }

    // Materials
    if(isToolsLoaded() || isWorldLoaded()) {
      materials = registerItem(new ItemMetaDynamic(), "materials");
      matSlimeBallBlue = materials.addMeta(0, "slimeball_blue");
      matSlimeBallPurple = materials.addMeta(1, "slimeball_purple");
    }

    if(isToolsLoaded()) {
      log.info("adding expander");
      matSlimeCrystal = materials.addMeta(9, "slimecrystal_green");
      matSlimeCrystalBlue = materials.addMeta(10, "slimecrystal_blue");
      matExpanderW = materials.addMeta(12, "expander_w");
      matExpanderH = materials.addMeta(13, "expander_h");

      ingotKnightSlime = ingots.addMeta(3, "knightslime");
    }

    // oredicting time
    registerOredicts();
    registerRecipies();

    proxy.preInit();
  }

  private void registerOredicts() {
    oredict(matSlimeBallBlue, "slimeball");
  }

  private void registerRecipies() {}

  private void oredict(ItemStack stack, String name) {
    if(stack != null) {
      OreDictionary.registerOre("slimeball", matSlimeBallBlue);
    }
  }
}
