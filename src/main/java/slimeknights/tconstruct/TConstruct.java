package slimeknights.tconstruct;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.control.PulseManager;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.TConstructBlockTagsProvider;
import slimeknights.tconstruct.common.data.TConstructEntityTypeTagsProvider;
import slimeknights.tconstruct.common.data.TConstructFluidTagsProvider;
import slimeknights.tconstruct.common.data.TConstructItemTagsProvider;
import slimeknights.tconstruct.common.data.TConstructRecipeProvider;
import slimeknights.tconstruct.debug.ToolDebugContainer;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.data.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.MaterialStatsDataProvider;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@Mod(TConstruct.modID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
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

    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);

    pulseManager = new PulseManager(Config.pulseConfig);
    pulseManager.registerPulse(new TinkerCommons());
    pulseManager.registerPulse(new TinkerFluids());
    pulseManager.registerPulse(new TinkerWorld());
    pulseManager.registerPulse(new TinkerGadgets());
    pulseManager.registerPulse(new TinkerSmeltery());
    pulseManager.enablePulses();

    DistExecutor.runWhenOn(Dist.CLIENT, () -> TinkerBook::initBook);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public static void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();
    MaterialRegistry.init();
  }

  @SubscribeEvent
  public static void init(final InterModEnqueueEvent event) {
    proxy.init();
  }

  @SubscribeEvent
  public static void postInit(final InterModProcessEvent event) {
    proxy.postInit();
    proxy.debug();
  }

  @SubscribeEvent
  public static void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();

    if (event.includeServer()) {
      datagenerator.addProvider(new TConstructBlockTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructItemTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructFluidTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructEntityTypeTagsProvider(datagenerator));
      //datagenerator.addProvider(new TConstructLootTableProvider(datagenerator));
      datagenerator.addProvider(new TConstructRecipeProvider(datagenerator));

      datagenerator.addProvider(new MaterialDataProvider());
      datagenerator.addProvider(new MaterialStatsDataProvider());
    }
  }

  @SubscribeEvent
  public void onServerStarting(final FMLServerStartingEvent event) {
    LiteralArgumentBuilder<CommandSource> executes = Commands.literal("tic_debug")
      .requires(commandSource -> commandSource.hasPermissionLevel(4))
      .executes(context -> {
        context.getSource().sendFeedback(new StringTextComponent("TiC debug"), false);
        context.getSource().asPlayer().openContainer(new INamedContainerProvider() {
          @Override
          public ITextComponent getDisplayName() {
            return new StringTextComponent("debug");
          }

          @Override
          public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
            return new ToolDebugContainer(p_createMenu_1_, p_createMenu_2_);
          }
        });
        return Command.SINGLE_SUCCESS;
      });
    event.getCommandDispatcher().register(executes);
  }
}
