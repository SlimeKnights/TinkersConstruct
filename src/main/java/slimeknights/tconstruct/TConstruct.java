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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.TConstructBlockTagsProvider;
import slimeknights.tconstruct.common.data.TConstructEntityTypeTagsProvider;
import slimeknights.tconstruct.common.data.TConstructFluidTagsProvider;
import slimeknights.tconstruct.common.data.TConstructItemTagsProvider;
import slimeknights.tconstruct.common.data.TConstructLootTableProvider;
import slimeknights.tconstruct.debug.ToolDebugContainer;
import slimeknights.tconstruct.debug.ToolDebugScreen;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@SuppressWarnings("unused")
@Mod(TConstruct.modID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TConstruct {

  public static final String modID = Util.MODID;

  public static final Logger log = LogManager.getLogger(modID);
  public static final Random random = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;

  public TConstruct() {
    instance = this;

    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);

    // initialize modules, done this way rather than with annotations to give us control over the order
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    // base
    bus.register(new TinkerCommons());
    bus.register(new TinkerFluids());
    bus.register(new TinkerGadgets());
    // world
    bus.register(new TinkerWorld());
    bus.register(new TinkerStructures());
    // tools
    bus.register(new TinkerTables());
    bus.register(new TinkerMaterials());
    bus.register(new TinkerModifiers());
    bus.register(new TinkerToolParts());
    bus.register(new TinkerTools());
    // smeltery
    bus.register(new TinkerSmeltery());

    // init deferred registers
    TinkerModule.initRegisters();
    // init client logic
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkerClient::onConstruct);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    // TODO: this belongs in the debug module, not here
    ScreenManager.registerFactory(ToolDebugContainer.TOOL_DEBUG_CONTAINER_TYPE, ToolDebugScreen::new);
  }

  @SubscribeEvent
  static void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      TConstructBlockTagsProvider blockTags = new TConstructBlockTagsProvider(datagenerator, existingFileHelper);
      datagenerator.addProvider(blockTags);
      datagenerator.addProvider(new TConstructItemTagsProvider(datagenerator, blockTags, existingFileHelper));
      datagenerator.addProvider(new TConstructFluidTagsProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new TConstructEntityTypeTagsProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new TConstructLootTableProvider(datagenerator));
    }
  }

  @SubscribeEvent
  void onServerStarting(final FMLServerStartingEvent event) {
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
    event.getServer().getCommandManager().getDispatcher().register(executes);
  }

  @Nullable
  private static Block missingBlock(String name) {
    switch (name) {
      case "slimy_mud_green":
        return TinkerWorld.congealedSlime.get(SlimeType.GREEN);
      case "slimy_mud_blue":
        return TinkerWorld.congealedSlime.get(SlimeType.BLUE);
      case "slimy_mud_magma":
        return TinkerWorld.congealedSlime.get(SlimeType.MAGMA);
    }
    return null;
  }

  @SubscribeEvent
  void missingItems(final MissingMappings<Item> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch (name) {
        case "wide_guard": return TinkerToolParts.toolRod.get();
        case "wide_guard_cast": return TinkerSmeltery.toolRodCast.get();
      }
      IItemProvider block = missingBlock(name);
      return block == null ? null : block.asItem();
    });
  }

  @SubscribeEvent
  void missingFluids(final MissingMappings<Fluid> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      if ("milk".equals(name)) {
        assert ForgeMod.MILK.isPresent();
        return ForgeMod.MILK.get();
      }
      return null;
    });
  }

  @SubscribeEvent
  void missingBlocks(final MissingMappings<Block> event) {
    RegistrationHelper.handleMissingMappings(event, modID, TConstruct::missingBlock);
  }
}
