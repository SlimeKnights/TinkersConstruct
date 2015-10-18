package slimeknights.tconstruct.shared;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.Util;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerCommons.PulseId, forced = true)
public class TinkerCommons extends TinkerPulse {

  public static final String PulseId = "TinkerTools";
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
      nuggets = registerItem(new ItemMetaDynamic(), "Nuggets");
      ingots = registerItem(new ItemMetaDynamic(), "Ingots");

      nuggetCobalt = nuggets.addMeta(0, "Cobalt");
      ingotCobalt = ingots.addMeta(0, "Cobalt");

      nuggetArdite = nuggets.addMeta(1, "Ardite");
      ingotArdite = ingots.addMeta(1, "Ardite");

      nuggetManyullyn = nuggets.addMeta(2, "Manyullyn");
      ingotManyullyn = ingots.addMeta(2, "Manyullyn");
    }

    // Materials
    if(isToolsLoaded() || isWorldLoaded()) {
      materials = registerItem(new ItemMetaDynamic(), "Materials");
      matSlimeBallBlue = materials.addMeta(0, "SlimeBallBlue");
      matSlimeBallPurple = materials.addMeta(1, "SlimeBallPurple");
    }

    if(isToolsLoaded()) {
      matSlimeCrystal = materials.addMeta(9, "SlimeCrystalGreen");
      matSlimeCrystalBlue = materials.addMeta(10, "SlimeCrystalBlue");
      matExpanderW = materials.addMeta(12, "ExpanderW");
      matExpanderH = materials.addMeta(13, "ExpanderH");

      ingotKnightSlime = ingots.addMeta(3, "KnightSlime");
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
