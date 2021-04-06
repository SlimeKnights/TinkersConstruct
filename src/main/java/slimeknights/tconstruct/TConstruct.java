package slimeknights.tconstruct;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
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
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

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
      // slimy mud
      case "slimy_mud_green": return TinkerWorld.congealedSlime.get(SlimeType.EARTH);
      case "slimy_mud_blue": return TinkerWorld.congealedSlime.get(SlimeType.SKY);
      case "slimy_mud_magma": return TinkerWorld.congealedSlime.get(SlimeType.ICHOR);
      // ardite
      case "ardite_ore": return TinkerWorld.cobaltOre.get();
      case "ardite_block": return TinkerMaterials.cobalt.get();
      case "molten_ardite_fluid": return TinkerFluids.moltenCobalt.getBlock();
      // slime vine rename
      case "blue_slime_vine": return TinkerWorld.skySlimeVine.get();
      case "purple_slime_vine": return TinkerWorld.enderSlimeVine.get();
      case "green_slime_fluid": return TinkerFluids.earthSlime.getBlock();
      case "blue_slime_fluid": return TinkerFluids.skySlime.getBlock();
      case "purple_slime_fluid": return TinkerFluids.enderSlime.getBlock();
      // pig iron underscore
      case "pigiron_block": return TinkerMaterials.pigIron.get();
      // soils
      case "graveyard_soil": case "consecrated_soil": return Blocks.DIRT;
      // firewood to blazewood
      case "firewood": return TinkerCommons.blazewood.get();
      case "firewood_slab": return TinkerCommons.blazewood.getSlab();
      case "firewood_stairs": return TinkerCommons.blazewood.getStairs();
    }
    // other slime changes:
    // green -> earth
    // blue -> sky
    // magma -> ichor
    // purple -> ender
    for (SlimeType type : SlimeType.TRUE_SLIME) {
      String typeName = type.getOriginalName();
      if (name.equals(typeName + "_slime")) return TinkerWorld.slime.get(type);
      if (name.equals(typeName + "_congealed_slime")) return TinkerWorld.congealedSlime.get(type);
      if (name.equals(typeName + "_slime_dirt")) return TinkerWorld.allDirt.get(type);
    }
    for (FoliageType foliage : FoliageType.ORIGINAL) {
      String foliageName = foliage.getOriginalName();
      if (name.equals(foliageName + "_slime_fern")) return TinkerWorld.slimeFern.get(foliage);
      if (name.equals(foliageName + "_slime_tall_grass")) return TinkerWorld.slimeTallGrass.get(foliage);
      if (name.equals(foliageName + "_slime_sapling")) return TinkerWorld.slimeSapling.get(foliage);
      if (name.equals(foliageName + "_slime_leaves")) return TinkerWorld.slimeLeaves.get(foliage);
      // note blood is included in the loop, blood = vanilla here
      for (SlimeType type : SlimeType.values()) {
        if (name.equals(foliageName + "_" + type.getOriginalName() + "_slime_grass")) {
          return TinkerWorld.slimeGrass.get(type).get(foliage);
        }
      }
    }
    return null;
  }

  @SubscribeEvent
  void missingItems(final MissingMappings<Item> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch (name) {
        case "wide_guard": return TinkerToolParts.toolRod.get();
        case "wide_guard_cast": return TinkerSmeltery.toolRodCast.get();
        // shovel -> mattock
        case "shovel": return TinkerTools.mattock.get();
        case "shovel_head": return TinkerToolParts.axeHead.get();
        case "shovel_head_cast": return TinkerSmeltery.axeHeadCast.get();
        case "shovel_head_sand_cast": return TinkerSmeltery.axeHeadCast.getSand();
        case "shovel_head_red_sand_cast": return TinkerSmeltery.axeHeadCast.getRedSand();
        // small binding -> tool binding
        case "small_binding": return TinkerToolParts.toolBinding.get();
        case "small_binding_cast": return TinkerSmeltery.toolBindingCast.get();
        case "small_binding_sand_cast": return TinkerSmeltery.toolBindingCast.getSand();
        case "small_binding_red_sand_cast": return TinkerSmeltery.toolBindingCast.getRedSand();
        // tough binding/excavator head -> large plate
        case "tough_binding":
        case "excavator_head":
          return TinkerToolParts.largePlate.get();
        case "tough_binding_cast":
        case "excavator_head_cast":
          return TinkerSmeltery.largePlateCast.get();
        case "tough_binding_sand_cast":
        case "excavator_head_sand_cast":
          return TinkerSmeltery.largePlateCast.getSand();
        case "tough_binding_red_sand_cast":
        case "excavator_head_red_sand_cast":
          return TinkerSmeltery.largePlateCast.getRedSand();
        // modifiers
        case "width_expander": return TinkerModifiers.ichorExpander.get();
        case "height_expander": return TinkerModifiers.enderExpander.get();
        case "creative_modifier": return TinkerModifiers.creativeUpgradeItem.get();
        // ardite
        case "ardite_ingot": return TinkerMaterials.cobalt.getIngot();
        case "ardite_nugget": return TinkerMaterials.cobalt.getNugget();
        case "molten_ardite_bucket": return TinkerFluids.moltenCobalt.asItem();
        // mud bricks
        case "mud_brick": return TinkerCommons.mudBricks.asItem();
        // hammer more specific name
        case "hammer": return TinkerTools.sledgeHammer.get();
        // slime renames
        case "magma_expander": return TinkerModifiers.ichorExpander.get();
        case "blue_slime_ball": return TinkerCommons.slimeball.get(SlimeType.SKY);
        case "magma_slime_ball": return TinkerCommons.slimeball.get(SlimeType.ICHOR);
        case "purple_slime_ball": return TinkerCommons.slimeball.get(SlimeType.ENDER);
        case "earth_slime_bucket": return TinkerFluids.earthSlime.asItem();
        case "sky_slime_bucket": return TinkerFluids.skySlime.asItem();
        case "ender_slime_bucket": return TinkerFluids.enderSlime.asItem();
        case "green_slime_sling":
        case "blood_slime_sling": 
          return TinkerGadgets.slimeSling.get(SlimeType.EARTH);
        case "blue_slime_sling": return TinkerGadgets.slimeSling.get(SlimeType.SKY);
        case "magma_slime_sling": return TinkerGadgets.slimeSling.get(SlimeType.ICHOR);
        case "purple_slime_sling": return TinkerGadgets.slimeSling.get(SlimeType.ENDER);
        // pig iron underscore
        case "pigiron_ingot": return TinkerMaterials.pigIron.getIngot();
        case "pigiron_nugget": return TinkerMaterials.pigIron.getNugget();
        // moss removed
        case "moss": case "mending_moss": return Items.MOSSY_COBBLESTONE;
      }
      IItemProvider block = missingBlock(name);
      return block == null ? null : block.asItem();
    });
  }

  @SubscribeEvent
  void missingEntity(final MissingMappings<EntityType<?>> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      if ("blue_slime".equals(name)) {
        return TinkerWorld.skySlimeEntity.get();
      }
      return null;
    });
  }

  @SubscribeEvent
  void missingFluids(final MissingMappings<Fluid> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch (name) {
        case "milk":
          assert ForgeMod.MILK.isPresent();
          return ForgeMod.MILK.get();
        case "molten_ardite": return TinkerFluids.moltenCobalt.get();
        case "flowing_molten_ardite": return TinkerFluids.moltenCobalt.getFlowing();
          // slime renames
        case "green_slime":          return TinkerFluids.earthSlime.get();
        case "flowing_green_slime":  return TinkerFluids.earthSlime.getFlowing();
        case "blue_slime":           return TinkerFluids.skySlime.get();
        case "flowing_blue_slime":   return TinkerFluids.skySlime.getFlowing();
        case "purple_slime":         return TinkerFluids.enderSlime.get();
        case "flowing_purple_slime": return TinkerFluids.enderSlime.getFlowing();
      }
      return null;
    });
  }

  @SubscribeEvent
  void missingBlocks(final MissingMappings<Block> event) {
    RegistrationHelper.handleMissingMappings(event, modID, TConstruct::missingBlock);
  }
}
