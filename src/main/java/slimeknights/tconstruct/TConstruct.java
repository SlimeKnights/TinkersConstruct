package slimeknights.tconstruct;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import mantle.pulsar.control.PulseManager;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.TinkerOredict;
import slimeknights.tconstruct.debug.DumpMaterialTest;
import slimeknights.tconstruct.debug.LocalizationCheckCommand;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.mantle.GuiHandler;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */


@Mod(modid = TConstruct.modID, name = "Tinkers' Construct", version = TConstruct.modVersion,
    dependencies = "required-after:Forge@[11.14.,);required-after:Mantle@[1.8-0.4,)")
//dependencies = "required-after:Forge@[10.13.1.1217,);required-after:Mantle@[1.7.10-0.3.2,);after:MineFactoryReloaded;after:NotEnoughItems;after:Waila;after:ThermalExpansion;after:ThermalFoundation")
public class TConstruct {

  public static final String modID = Util.MODID;
  public static final String modVersion = "${version}";
  /*
   * The value of one ingot in millibuckets
  public static final int ingotLiquidValue = 144;
  public static final int oreLiquidValue = ingotLiquidValue * 2;
  public static final int blockLiquidValue = ingotLiquidValue * 9;
  public static final int chunkLiquidValue = ingotLiquidValue / 2;
  public static final int nuggetLiquidValue = ingotLiquidValue / 9;
  public static final int stoneLiquidValue = ingotLiquidValue / 8;

  public static final int liquidUpdateAmount = 6;
*/
  public static final Logger log = LogManager.getLogger(modID);

  /* Instance of this mod, used for grabbing prototype fields */
  @Mod.Instance(modID)
  public static TConstruct instance;
    /* Proxies for sides, used for graphics processing and client controls */
  //@SidedProxy(clientSide = "tconstruct.client.TProxyClient", serverSide = "tconstruct.common.TProxyCommon")
  //public static TProxyCommon proxy;

    /* Loads modules in a way that doesn't clutter the @Mod list */
  //public static PulseManager pulsar = new PulseManager(modID, new ForgeCFG("TinkersModules", "Modules: Disabling these will disable a chunk of the mod"));

  public static PulseManager pulseManager = new PulseManager(modID, "TinkerModules");
  public static GuiHandler guiHandler = new GuiHandler();

  public TConstruct() {
    if(Loader.isModLoaded("Natura")) {
      log.info("Natura, what are we going to do tomorrow night?");
      LogManager.getLogger("Natura").info("TConstruct, we're going to take over the world!");
    }
    else {
      log.info("Preparing to take over the world");
    }
  }

  //Force the client and server to have or not have this mod
  @NetworkCheckHandler()
  public boolean matchModVersions(Map<String, String> remoteVersions, Side side) {
    return remoteVersions.containsKey(modID) && modVersion.equals(remoteVersions.get(modID));
  }

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    // Tinker pulses
    pulseManager.registerPulse(new TinkerTools());
    pulseManager.registerPulse(new TinkerSmeltery());
    pulseManager.registerPulse(new TinkerMaterials());
    // Plugins/Integration

    pulseManager.preInit(event);

    // the basic tinker materials are always present
    HarvestLevels.init();

    NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

    if(event.getSide().isClient()) {
      ClientProxy.initClient();
    }

    TinkerNetwork.instance.setup();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    pulseManager.init(event);
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    pulseManager.postInit(event);

    TinkerOredict.ensureOredict();
    TinkerOredict.registerTinkerOredict();

    if(event.getSide().isClient()) {
      ClientProxy.initRenderer();
    }
  }

  @Mod.EventHandler
  public void starting(FMLServerStartingEvent event) {
    event.registerServerCommand(new LocalizationCheckCommand());
    event.registerServerCommand(new DumpMaterialTest());
  }
}
