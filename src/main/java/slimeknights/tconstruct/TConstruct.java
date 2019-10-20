package slimeknights.tconstruct;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import slimeknights.mantle.pulsar.control.PulseManager;
import slimeknights.mantle.util.BlankBlockDropJsonGenerator;
import slimeknights.mantle.util.BlockStateJsonGenerator;
import slimeknights.mantle.util.LanguageJsonGenerator;
import slimeknights.mantle.util.ModelJsonGenerator;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.TConstructBlockTagsProvider;
import slimeknights.tconstruct.common.data.TConstructItemTagsProvider;
import slimeknights.tconstruct.common.data.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.TConstructRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@Mod(TConstruct.modID)
public class TConstruct {

  public static final String modID = Util.MODID;

  public static final Logger log = LogManager.getLogger(modID);
  public static final Random random = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;

  public static ServerProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

  public static PulseManager pulseManager;

  public TConstruct() {
    instance = this;
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);

    pulseManager = new PulseManager(Config.pulseConfig);
    pulseManager.registerPulse(new TinkerCommons());
    pulseManager.registerPulse(new TinkerFluids());
    pulseManager.registerPulse(new TinkerWorld());
    pulseManager.registerPulse(new TinkerGadgets());
    pulseManager.enablePulses();

    DistExecutor.runWhenOn(Dist.CLIENT, () -> TinkerBook::initBook);
  }

  private void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();

    TinkerNetwork.instance.setup();
  }

  private void init(final InterModEnqueueEvent event) {
    proxy.init();
  }

  private void postInit(final InterModProcessEvent event) {
    proxy.postInit();
  }

  private void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();

    if (event.includeServer()) {
      datagenerator.addProvider(new TConstructBlockTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructItemTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructLootTableProvider(datagenerator));
      datagenerator.addProvider(new TConstructRecipeProvider(datagenerator));

      if (false) {
        datagenerator.addProvider(new BlockStateJsonGenerator(datagenerator, modID));
        datagenerator.addProvider(new ModelJsonGenerator(datagenerator, modID));
        datagenerator.addProvider(new LanguageJsonGenerator(datagenerator, modID));
        datagenerator.addProvider(new BlankBlockDropJsonGenerator(datagenerator, modID));
      }
    }
  }
}
