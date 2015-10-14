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

  // Material Itemstacks
  public static ItemStack matSlimeBallBlue;
  public static ItemStack matSlimeBallPurple;
  public static ItemStack matSlimeCrystal;
  public static ItemStack matSlimeCrystalBlue;
  public static ItemStack matKnightSlime;

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    // Ingots and nuggets
    if(isToolsLoaded() || isSmelteryLoaded()) {
      nuggets = registerItem(new ItemMetaDynamic(), "Nuggets");
      ingots = registerItem(new ItemMetaDynamic(), "Ingots");

      nuggetCobalt = nuggets.addMeta(0, "NuggetCobalt");
      ingotCobalt = ingots.addMeta(0, "IngotCobalt");

      nuggetArdite = nuggets.addMeta(1, "NuggetArdite");
      ingotArdite = ingots.addMeta(1, "IngotArdite");

      if(isSmelteryLoaded()) {
        nuggetManyullyn = nuggets.addMeta(2, "NuggetManyullyn");
        ingotManyullyn = ingots.addMeta(2, "IngotManyullyn");
      }
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
      matKnightSlime = materials.addMeta(11, "KnightSlime");
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
