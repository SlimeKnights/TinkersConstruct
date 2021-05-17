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
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.loot.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.tags.BlockTagProvider;
import slimeknights.tconstruct.common.data.tags.EntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.FluidTagProvider;
import slimeknights.tconstruct.common.data.tags.ItemTagProvider;
import slimeknights.tconstruct.common.data.tags.TileEntityTypeTagProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
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
    TinkerTags.init();
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
      BlockTagProvider blockTags = new BlockTagProvider(datagenerator, existingFileHelper);
      datagenerator.addProvider(blockTags);
      datagenerator.addProvider(new ItemTagProvider(datagenerator, blockTags, existingFileHelper));
      datagenerator.addProvider(new FluidTagProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new EntityTypeTagProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new TileEntityTypeTagProvider(datagenerator, existingFileHelper));
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
      // prefix with seared
      case "faucet": return TinkerSmeltery.searedFaucet.get();
      case "channel": return TinkerSmeltery.searedChannel.get();
      case "casting_table": return TinkerSmeltery.searedTable.get();
      case "casting_basin": return TinkerSmeltery.searedBasin.get();
      case "melter": return TinkerSmeltery.searedMelter.get();
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
        // expanders use 5 items now
        case "ichor_expander": return TinkerMaterials.tinkersBronze.getIngot();
        case "ender_expander": return TinkerMaterials.manyullyn.getIngot();
        // tool rods -> tool handles
        case "tool_rod": return TinkerToolParts.toolHandle.get();
        case "tough_tool_rod": return TinkerToolParts.toughHandle.get();
        case "tool_rod_cast": return TinkerSmeltery.toolHandleCast.get();
        case "tool_rod_sand_cast": return TinkerSmeltery.toolHandleCast.getSand();
        case "tool_rod_red_sand_cast": return TinkerSmeltery.toolHandleCast.getRedSand();
        case "tough_tool_rod_cast": return TinkerSmeltery.toughHandleCast.get();
        case "tough_tool_rod_sand_cast": return TinkerSmeltery.toughHandleCast.getSand();
        case "tough_tool_rod_red_sand_cast": return TinkerSmeltery.toughHandleCast.getRedSand();
        // axe -> hand_axe, axe_head -> small_axe_head
        case "axe": return TinkerTools.handAxe.get();
        case "axe_head": return TinkerToolParts.smallAxeHead.get();
        case "axe_head_cast": return TinkerSmeltery.smallAxeHeadCast.get();
        case "axe_head_sand_cast": return TinkerSmeltery.smallAxeHeadCast.getSand();
        case "axe_head_red_sand_cast": return TinkerSmeltery.smallAxeHeadCast.getRedSand();
        // kama head removed
        case "kama_head": return TinkerToolParts.swordBlade.get();
        case "kama_head_cast": return TinkerSmeltery.swordBladeCast.get();
        case "kama_head_sand_cast": return TinkerSmeltery.swordBladeCast.getSand();
        case "kama_head_red_sand_cast": return TinkerSmeltery.swordBladeCast.getRedSand();
        // broadsword -> sword
        case "broad_sword": return TinkerTools.sword.get();
        //  reinforcement splitting
        case "reinforcement": return TinkerModifiers.ironReinforcement.get();
      }
      IItemProvider block = missingBlock(name);
      return block == null ? null : block.asItem();
    });
  }

  @SubscribeEvent
  void missingBlocks(final MissingMappings<Block> event) {
    RegistrationHelper.handleMissingMappings(event, modID, TConstruct::missingBlock);
  }

  @SubscribeEvent
  void missingModifiers(final MissingMappings<Modifier> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch(name) {
        case "axe_transform": return TinkerModifiers.stripping.get();
        case "shovel_transform": return TinkerModifiers.pathing.get();
        case "hoe_transform": return TinkerModifiers.tilling.get();
      }
      return null;
    });
  }
}
