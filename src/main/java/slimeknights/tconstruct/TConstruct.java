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
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;

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
      TConstructBlockTagsProvider blockTags = new TConstructBlockTagsProvider(datagenerator);
      datagenerator.addProvider(blockTags);
      datagenerator.addProvider(new TConstructItemTagsProvider(datagenerator, blockTags));
      datagenerator.addProvider(new TConstructFluidTagsProvider(datagenerator));
      datagenerator.addProvider(new TConstructEntityTypeTagsProvider(datagenerator));
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


  @SubscribeEvent
  static void missingBlocks(final MissingMappings<Block> event) {
    handleMissingMappings(event, TConstruct::missingBlock);
  }

  @SubscribeEvent
  static void missingItems(final MissingMappings<Item> event) {
    handleMissingMappings(event, name -> {
      IItemProvider provider = missingBlock(name);
      return provider == null ? null : provider.asItem();
    });
  }

  /**
   * Handles missing block remapping
   * @param name  Block name
   * @return New block replacement, or null if no replacement
   */
  @Nullable
  private static Block missingBlock(String name) {
    switch (name) {
      // square/small/road removed
      case "seared_square_bricks":
      case "seared_small_bricks":
      case "seared_road":
        return TinkerSmeltery.searedBricks.get();
      // cracked and fancy stairs/slabs removed
      case "seared_cracked_bricks_slab":
      case "seared_fancy_bricks_slab":
      case "seared_square_bricks_slab":
      case "seared_small_bricks_slab":
      case "seared_road_slab":
        return TinkerSmeltery.searedBricks.getSlab();
      case "seared_cracked_bricks_stairs":
      case "seared_fancy_bricks_stairs":
      case "seared_square_bricks_stairs":
      case "seared_small_bricks_stairs":
      case "seared_road_stairs":
        return TinkerSmeltery.searedBricks.getStairs();
      // creeper and tile removed
      case "seared_creeper":
      case "seared_tile":
        return TinkerSmeltery.searedPaver.get();
      // triangle staris/slabs removed
      case "seared_triangle_bricks_slab":
      case "seared_creeper_slab":
      case "seared_tile_slab":
        return TinkerSmeltery.searedPaver.getSlab();
      case "seared_triangle_bricks_stairs":
      case "seared_creeper_stairs":
      case "seared_tile_stairs":
        return TinkerSmeltery.searedPaver.getStairs();
      // pattern chest
      case "pattern_chest":
        return TinkerTables.modifierChest.get();
    }
    return null;
  }

  /**
   * Handles missing mappings for the given registry
   * @param event    Mappings event
   * @param handler  Mapping handler
   * @param <T>      Event type
   */
  private static <T extends IForgeRegistryEntry<T>> void handleMissingMappings(MissingMappings<T> event, Function<String, T> handler) {
    for (Mapping<T> mapping : event.getAllMappings()) {
      if (modID.equals(mapping.key.getNamespace())) {
        @Nullable T value = handler.apply(mapping.key.getPath());
        if (value != null) {
          mapping.remap(value);
        }
      }
    }
  }
}
