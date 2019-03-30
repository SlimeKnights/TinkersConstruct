package slimeknights.tconstruct;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

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
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.plugin.Chisel;
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
     dependencies = "required-after:forge@[14.23.1.2577,);"
                    + "required-after:mantle@[1.12-1.3.3.49,);"
                    + "after:jei@[4.8,);"
                    + "before:taiga@(1.3.0,);"
                    + "after:chisel",
     acceptedMinecraftVersions = "[1.12, 1.13)")
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
    pulseManager.registerPulse(new Chisel());
    pulseManager.registerPulse(new ChiselAndBits());
    pulseManager.registerPulse(new CraftingTweaks());
    pulseManager.registerPulse(new Waila());
    pulseManager.registerPulse(new TheOneProbe());

    pulseManager.registerPulse(new TinkerDebug());

    if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
    {
      TinkerBook.init();
    }
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
  @NetworkCheckHandler
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
      ClientProxy.initRenderMaterials();
    }

    TinkerNetwork.instance.setup();
    CapabilityTinkerPiggyback.register();
    CapabilityTinkerProjectile.register();

    MinecraftForge.EVENT_BUS.register(this);
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


  private static final String TINKERS_SKYBLOCK_MODID = "tinkerskyblock";
  private static final String WOODEN_HOPPER = "wooden_hopper";

  //Old version compatibility
  @SubscribeEvent
  public void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
    for(RegistryEvent.MissingMappings.Mapping<Item> entry : event.getAllMappings()) {
      @Nonnull
      String path = entry.key.toString();
      if(path.equals(Util.resource("bucket")) || path.equals(Util.resource("glow")) || path.equals(Util.resource("blood")) || path.equals(Util.resource("milk")) || path.equals(Util.resource("purpleslime")) || path.equals(Util.resource("blueslime")) || path.contains(Util.resource("molten"))) {
        entry.ignore();
      }

      // wooder hopper, moved from skyblock to tic
      if(entry.key.getResourceDomain().equals(TINKERS_SKYBLOCK_MODID) && entry.key.getResourcePath().equals(WOODEN_HOPPER)) {
        entry.remap(Item.getItemFromBlock(TinkerGadgets.woodenHopper));
      }
    }
  }

  @SubscribeEvent
  public void missingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
    for(RegistryEvent.MissingMappings.Mapping<Block> entry : event.getAllMappings()) {
      // wooder hopper, moved from skyblock to tic
      if(entry.key.getResourceDomain().equals(TINKERS_SKYBLOCK_MODID) && entry.key.getResourcePath().equals(WOODEN_HOPPER)) {
        entry.remap(TinkerGadgets.woodenHopper);
      }
    }
  }
}
