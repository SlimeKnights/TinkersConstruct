package slimeknights.tconstruct;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
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
import slimeknights.tconstruct.common.TinkerModule;
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
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.MaterialStatsDataProvider;
import slimeknights.tconstruct.world.TinkerStructures;
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
        String path = entry.key.getPath();
        // slime is prefixed with color instead of suffixed
        for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
          // Remap slime_sling_$slime
          if (path.equals("slime_sling_" + slime.getName())) {
            entry.remap(TinkerGadgets.slimeSling.get(slime));
          }
          // Remap slime_boots_$slime
          if (path.equals("slime_boots_" + slime.getName())) {
            entry.remap(TinkerGadgets.slimeBoots.get(slime));
          }
          // Remap congealed_$slime_slime
          if (path.equals(String.format("congealed_%s_slime", slime.getName()))) {
            entry.remap(TinkerWorld.congealedSlime.get(slime).asItem());
          }
        }
        switch (path) {
          // alubrass removed, fallback
          case "alubrass_block":
            entry.remap(TinkerMaterials.copperBlock.asItem());
            break;
          case "alubrass_ingot":
            entry.remap(TinkerMaterials.copperIngot.get());
            break;
          case "alubrass_nugget":
            entry.remap(TinkerMaterials.copperNugget.get());
            break;
          case "aluminum_brass_item_frame":
            entry.remap(TinkerGadgets.itemFrame.get(FrameType.JEWEL));
            break;
          // old tool before we had a proper model
          case "test_tool":
            entry.remap(TinkerTools.pickaxe.get());
            break;
          case "test_part":
            entry.remap(TinkerToolParts.pickaxeHead.get());
            break;
        }
      }
    }
  }

  @SubscribeEvent // TODO: Remove after a while, maybe at release.
  public void missingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
    for (RegistryEvent.MissingMappings.Mapping<Block> entry : event.getAllMappings()) {
      if (entry.key.getNamespace().equals(TConstruct.modID)) {
        // congealed is congealed_color_slime instead of color_congealed_slime
        String path = entry.key.getPath();
        for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
          // Remap congealed_$slime_slime
          if (path.equals(String.format("congealed_%s_slime", slime.getName()))) {
            entry.remap(TinkerWorld.congealedSlime.get(slime));
          }
        }
        switch(path) {
          // slime fluids renamed to remove "fluid"
          case "purple_slime_fluid_block":
            entry.remap(TinkerFluids.purpleSlime.getBlock());
            break;
          case "blue_slime_fluid_block":
            entry.remap(TinkerFluids.blueSlime.getBlock());
            break;
          // alubrass removed, fallback
          case "alubrass_block":
            entry.remap(TinkerMaterials.copperBlock.get());
            break;
        }
      }
    }
  }

  @SubscribeEvent // TODO: Remove after a while, maybe at release.
  public void missingFluidMappings(RegistryEvent.MissingMappings<Fluid> event) {
    for (RegistryEvent.MissingMappings.Mapping<Fluid> entry : event.getAllMappings()) {
      if (entry.key.getNamespace().equals(TConstruct.modID)) {
        switch (entry.key.getPath()) {
          // slime fluids renamed to remove fluid, flowing is now prefix
          case "blue_slime_fluid":
            entry.remap(TinkerFluids.blueSlime.getStill());
            break;
          case "blue_slime_fluid_flowing":
            entry.remap(TinkerFluids.blueSlime.getFlowing());
            break;
          case "purple_slime_fluid":
            entry.remap(TinkerFluids.purpleSlime.getStill());
            break;
          case "purple_slime_fluid_flowing":
            entry.remap(TinkerFluids.purpleSlime.getFlowing());
            break;
        }
      }
    }
  }

  @SubscribeEvent // TODO: Remove after a while, maybe at release.
  public void missingEntityMappings(RegistryEvent.MissingMappings<EntityType<?>> event) {
    for (RegistryEvent.MissingMappings.Mapping<EntityType<?>> entry : event.getAllMappings()) {
      if (entry.key.getNamespace().equals(TConstruct.modID)) {
        if (entry.key.getPath().equals("blue_slime_entity")) {
          entry.remap(TinkerWorld.blueSlimeEntity.get());
        }
      }
    }
  }
}
