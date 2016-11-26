package slimeknights.tconstruct;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Random;

import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.TinkerOredict;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.config.ConfigSync;
import slimeknights.tconstruct.debug.TinkerDebug;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.plugin.ChiselAndBits;
import slimeknights.tconstruct.plugin.CraftingTweaks;
import slimeknights.tconstruct.plugin.theoneprobe.TheOneProbe;
import slimeknights.tconstruct.plugin.waila.Waila;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.AggregateModelRegistrar;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;
import slimeknights.tconstruct.world.TinkerWorld;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */


@Mod(modid = TConstruct.modID,
    name = TConstruct.modName,
    version = TConstruct.modVersion,
    guiFactory = "slimeknights.tconstruct.common.config.ConfigGui$ConfigGuiFactory",
    dependencies = "required-after:Forge@[12.18.1.2073,);"
                   + "required-after:mantle@[1.10.2-1.0.0,);"
                   + "after:JEI@[3.13.6.387,)",
    acceptedMinecraftVersions = "[1.10.2, 1.11)")
public class TConstruct {

  public static final String modID = Util.MODID;
  public static final String modVersion = "${version}";
  public static final String modName = "Tinkers' Construct";

  public static final Logger log = LogManager.getLogger(modID);
  public static final Random random = new Random();

  @Mod.Instance(modID)
  public static TConstruct instance;

  @SidedProxy(clientSide = "slimeknights.tconstruct.common.CommonProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);
  public static GuiHandler guiHandler = new GuiHandler();

  // Tinker pulses
  static {
    pulseManager.registerPulse(new TinkerCommons());
    pulseManager.registerPulse(new TinkerWorld());

    pulseManager.registerPulse(new TinkerTools());
    pulseManager.registerPulse(new TinkerHarvestTools());
    pulseManager.registerPulse(new TinkerMeleeWeapons());
    pulseManager.registerPulse(new TinkerRangedWeapons());
    pulseManager.registerPulse(new TinkerModifiers());

    pulseManager.registerPulse(new TinkerSmeltery());
    pulseManager.registerPulse(new TinkerGadgets());

    pulseManager.registerPulse(new TinkerOredict()); // oredict the items added in the pulses before, needed for integration
    pulseManager.registerPulse(new TinkerIntegration()); // takes care of adding all the fluids, materials, melting etc. together
    pulseManager.registerPulse(new TinkerFluids());
    pulseManager.registerPulse(new TinkerMaterials());

    pulseManager.registerPulse(new AggregateModelRegistrar());
    // Plugins/Integration
    //pulseManager.registerPulse(new TinkerVintageCraft());
    pulseManager.registerPulse(new ChiselAndBits());
    pulseManager.registerPulse(new CraftingTweaks());
    pulseManager.registerPulse(new Waila());
    pulseManager.registerPulse(new TheOneProbe());

    pulseManager.registerPulse(new TinkerDebug());
  }


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

    // we don't accept clients without TiC
    if(side == Side.CLIENT) {
      return remoteVersions.containsKey(modID);
    }
    // but we can connect to servers without TiC when TiC is present on the client
    return !remoteVersions.containsKey(modID) || modVersion.equals(remoteVersions.get(modID));
  }

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    Config.load(event);

    HarvestLevels.init();

    NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

    if(event.getSide().isClient()) {
      ClientProxy.initClient();
    }

    TinkerNetwork.instance.setup();
    CapabilityTinkerProjectile.register();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    if(event.getSide().isClient()) {
      ClientProxy.initRenderMaterials();
    }
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    if(event.getSide().isClient()) {
      ClientProxy.initRenderer();
    }
    else {
      // config syncing
      MinecraftForge.EVENT_BUS.register(new ConfigSync());
    }
  }

  // Old version compatibility
  @Mod.EventHandler
  public void onMissingMapping(FMLMissingMappingsEvent event) {
    for(FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
      // old universal bucket, got moved into Forge
      // glow is the leftover itemblock form which was removed
      if(mapping.type == GameRegistry.Type.ITEM
         && (mapping.name.equals(Util.resource("bucket")) || mapping.name.equals(Util.resource("glow")))) {
        mapping.ignore();
      }
    }
  }
}
