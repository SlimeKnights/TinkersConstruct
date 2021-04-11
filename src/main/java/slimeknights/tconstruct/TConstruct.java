package slimeknights.tconstruct;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.loot.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.tags.TConstructBlockTagsProvider;
import slimeknights.tconstruct.common.data.tags.TConstructEntityTypeTagsProvider;
import slimeknights.tconstruct.common.data.tags.TConstructFluidTagsProvider;
import slimeknights.tconstruct.common.data.tags.TConstructItemTagsProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
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
    TinkerNetwork.setup();
    // init client logic
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkerClient::onConstruct);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
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

  @Nullable
  private static Block missingBlock(String name) {
    switch (name) {
      // soils
      case "graveyard_soil": case "consecrated_soil": return Blocks.DIRT;
      // firewood to blazewood
      case "firewood": return TinkerCommons.blazewood.get();
      case "firewood_slab": return TinkerCommons.blazewood.getSlab();
      case "firewood_stairs": return TinkerCommons.blazewood.getStairs();
    }
    return null;
  }

  @SubscribeEvent
  void missingItems(final MissingMappings<Item> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch (name) {
        // moss removed
        case "moss": case "mending_moss": return Items.MOSSY_COBBLESTONE;
        // book spliting
        case "book": return TinkerCommons.materialsAndYou.get();
      }
      IItemProvider block = missingBlock(name);
      return block == null ? null : block.asItem();
    });
  }

  @SubscribeEvent
  void missingBlocks(final MissingMappings<Block> event) {
    RegistrationHelper.handleMissingMappings(event, modID, TConstruct::missingBlock);
  }
}
