package slimeknights.tconstruct;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.control.PulseManager;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.blocks.SmelteryBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.TConstructBlockTagsProvider;
import slimeknights.tconstruct.common.data.TConstructEntityTypeTagsProvider;
import slimeknights.tconstruct.common.data.TConstructFluidTagsProvider;
import slimeknights.tconstruct.common.data.TConstructItemTagsProvider;
import slimeknights.tconstruct.common.data.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.TConstructRecipeProvider;
import slimeknights.tconstruct.debug.ToolDebugContainer;
import slimeknights.tconstruct.debug.ToolDebugScreen;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.items.ToolItems;
import slimeknights.tconstruct.items.ToolParts;
import slimeknights.tconstruct.items.WorldItems;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.tables.TinkerTables;
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

    ToolParts.init();
    ToolItems.init();
    GadgetItems.init();
    GadgetBlocks.init();
    WorldItems.init();
    WorldBlocks.init();
    TinkerWorld.init();
    DecorativeBlocks.init();
    SmelteryBlocks.init();
    CommonBlocks.init();
    CommonItems.init();
    FoodItems.init();

    pulseManager = new PulseManager(Config.pulseConfig);
    pulseManager.registerPulse(new TinkerCommons());
    pulseManager.registerPulse(new TinkerFluids());
    pulseManager.registerPulse(new TinkerWorld());
    pulseManager.registerPulse(new TinkerGadgets());
    pulseManager.registerPulse(new TinkerTables());
    pulseManager.enablePulses();

    DistExecutor.runWhenOn(Dist.CLIENT, () -> TinkerBook::initBook);

    MinecraftForge.EVENT_BUS.register(this);

    //DistExecutor.runWhenOn(Dist.CLIENT, ModelLoaderRegisterHelper::registerModelLoader);
  }

  @SubscribeEvent
  public static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
  }

  @SubscribeEvent
  public static void clientSetup(final FMLClientSetupEvent event) {
    // TODO: this belongs in the debug module, not here
    ScreenManager.registerFactory(ToolDebugContainer.TOOL_DEBUG_CONTAINER_TYPE, ToolDebugScreen::new);
  }

  @SubscribeEvent
  public static void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();

    if (event.includeServer()) {
      datagenerator.addProvider(new TConstructBlockTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructItemTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructFluidTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructEntityTypeTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructLootTableProvider(datagenerator));
      datagenerator.addProvider(new TConstructRecipeProvider(datagenerator));

      datagenerator.addProvider(new MaterialDataProvider(datagenerator));
      datagenerator.addProvider(new MaterialStatsDataProvider(datagenerator));
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

  @SubscribeEvent // TODO: Remove after a while, maybe at release.
  public void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
    for (RegistryEvent.MissingMappings.Mapping<Item> entry : event.getAllMappings()) {
      if (entry.key.getNamespace().equals(TConstruct.modID)) {
        for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
          // Remap slime_sling_$slime
          if (entry.key.getPath().equals("slime_sling_" + slime.getName())) {
            entry.remap(GadgetItems.slime_sling.get(slime));
          }
          // Remap slime_boots_$slime
          if (entry.key.getPath().equals("slime_boots_" + slime.getName())) {
            entry.remap(GadgetItems.slime_boots.get(slime));
          }
          // Remap congealed_$slime_slime
          if (entry.key.getNamespace().equals(TConstruct.modID) && entry.key.getPath().equals(String.format("congealed_%s_slime", slime.getName()))) {
            entry.remap(WorldBlocks.congealed_slime.get(slime).asItem());
          }
        }
      }
    }
  }

  @SubscribeEvent // TODO: Remove after a while, maybe at release.
  public void missingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
    for (RegistryEvent.MissingMappings.Mapping<Block> entry : event.getAllMappings()) {
      if (entry.key.getNamespace().equals(TConstruct.modID)) {
        for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
          // Remap congealed_$slime_slime
          if (entry.key.getPath().equals(String.format("congealed_%s_slime", slime.getName()))) {
            entry.remap(WorldBlocks.congealed_slime.get(slime));
          }
        }

        if (entry.key.getPath().equals("purple_slime_fluid_block")) {
          entry.remap(TinkerFluids.purple_slime.getBlock());
        }

        if (entry.key.getPath().equals("blue_slime_fluid_block")) {
          entry.remap(TinkerFluids.blue_slime.getBlock());
        }
      }
    }
  }

  @SubscribeEvent // TODO: Remove after a while, maybe at release.
  public void missingFluidMappings(RegistryEvent.MissingMappings<Fluid> event) {
    for (RegistryEvent.MissingMappings.Mapping<Fluid> entry : event.getAllMappings()) {
      if (entry.key.getNamespace().equals(TConstruct.modID)) {
        if (entry.key.getPath().equals("blue_slime_fluid")) {
          entry.remap(TinkerFluids.blue_slime.getStill());
        }

        if (entry.key.getPath().equals("blue_slime_fluid_flowing")) {
          entry.remap(TinkerFluids.blue_slime.getFlowing());
        }

        if (entry.key.getPath().equals("purple_slime_fluid")) {
          entry.remap(TinkerFluids.purple_slime.getStill());
        }

        if (entry.key.getPath().equals("purple_slime_fluid_flowing")) {
          entry.remap(TinkerFluids.purple_slime.getFlowing());
        }
      }
    }
  }
}
