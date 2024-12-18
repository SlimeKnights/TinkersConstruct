package slimeknights.tconstruct;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.MissingMappingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.AdvancementsProvider;
import slimeknights.tconstruct.common.data.loot.GlobalLootModifiersProvider;
import slimeknights.tconstruct.common.data.loot.LootTableInjectionProvider;
import slimeknights.tconstruct.common.data.loot.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.tags.BiomeTagProvider;
import slimeknights.tconstruct.common.data.tags.BlockEntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.BlockTagProvider;
import slimeknights.tconstruct.common.data.tags.EnchantmentTagProvider;
import slimeknights.tconstruct.common.data.tags.EntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.FluidTagProvider;
import slimeknights.tconstruct.common.data.tags.ItemTagProvider;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.TinkerBookIDs;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.plugin.DietPlugin;
import slimeknights.tconstruct.plugin.ImmersiveEngineeringPlugin;
import slimeknights.tconstruct.plugin.craftingtweaks.CraftingTweaksPlugin;
import slimeknights.tconstruct.plugin.jsonthings.JsonThingsPlugin;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.ToolType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@SuppressWarnings("unused")
@Mod(TConstruct.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TConstruct {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  public static final Random RANDOM = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;

  @SuppressWarnings("removal")
  public TConstruct() {
    instance = this;

    Config.init();

    // initialize modules, done this way rather than with annotations to give us control over the order
    MinecraftForge.EVENT_BUS.addListener(TConstruct::missingMappings);
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    // base
    bus.register(new TinkerCommons());
    bus.register(new TinkerMaterials());
    bus.register(new TinkerFluids());
    bus.register(new TinkerGadgets());
    // world
    bus.register(new TinkerWorld());
    bus.register(new TinkerStructures());
    // tools
    bus.register(new TinkerTables());
    bus.register(new TinkerModifiers());
    bus.register(new TinkerToolParts());
    bus.register(new TinkerTools());
    // smeltery
    bus.register(new TinkerSmeltery());

    // init deferred registers
    TinkerModule.initRegisters();
    TinkerNetwork.setup();
    TinkerTags.init();
    TinkerBookIDs.registerCommandSuggestion();
    // init client logic
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkerClient::onConstruct);

    // compat
    ModList modList = ModList.get();
    if (modList.isLoaded("immersiveengineering")) {
      bus.register(new ImmersiveEngineeringPlugin());
    }
    if (modList.isLoaded("jsonthings")) {
      JsonThingsPlugin.onConstruct();
    }
    if (modList.isLoaded("diet")) {
      DietPlugin.onConstruct();
    }
    if (modList.isLoaded("craftingtweaks")) {
      CraftingTweaksPlugin.onConstruct();
    }
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
    ToolDefinitionLoader.init();
    StationSlotLayoutLoader.init();
  }

  @SubscribeEvent
  static void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    BlockTagProvider blockTags = new BlockTagProvider(datagenerator, existingFileHelper);
    datagenerator.addProvider(server, blockTags);
    datagenerator.addProvider(server, new ItemTagProvider(datagenerator, blockTags, existingFileHelper));
    datagenerator.addProvider(server, new FluidTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new EntityTypeTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new BlockEntityTypeTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new BiomeTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new EnchantmentTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new TConstructLootTableProvider(datagenerator));
    datagenerator.addProvider(server, new AdvancementsProvider(datagenerator));
    datagenerator.addProvider(server, new GlobalLootModifiersProvider(datagenerator));
    datagenerator.addProvider(server, new LootTableInjectionProvider(datagenerator));
  }

  /** Shared behavior between item and block missing mappings */
  @Nullable
  private static Block missingBlock(String name) {
    return switch(name) {
      // blood removal
      case "blood_slime" -> Blocks.SLIME_BLOCK;
      case "blood_congealed_slime" -> TinkerWorld.congealedSlime.get(SlimeType.EARTH);
      case "blood_fluid" -> TinkerFluids.earthSlime.getBlock();
      // lavawood removal
      case "lavawood" -> TinkerMaterials.blazewood.get();
      case "lavawood_slab" -> TinkerMaterials.blazewood.getSlab();
      case "lavawood_stairs" -> TinkerMaterials.blazewood.getStairs();
      // migrate mud bricks to vanilla
      case "mud_bricks" -> Blocks.MUD_BRICKS;
      case "mud_bricks_slab" -> Blocks.MUD_BRICK_SLAB;
      case "mud_bricks_stairs" -> Blocks.MUD_BRICK_STAIRS;
      default -> null;
    };
  }

  /** Handles missing mappings of all types */
  private static void missingMappings(MissingMappingsEvent event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registry.BLOCK_REGISTRY, name -> {
      // no item form so we handle it directly
      if (name.equals("blood_fluid")) {
        return TinkerFluids.earthSlime.getBlock();
      }
      return missingBlock(name);
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registry.ITEM_REGISTRY, name -> switch (name) {
      // slings are modifiers now
      case "earth_slime_sling" -> TinkerTools.earthStaff.get();
      case "sky_slime_sling" -> TinkerTools.skyStaff.get();
      case "ichor_slime_sling" -> TinkerTools.ichorStaff.get();
      case "ender_slime_sling" -> TinkerTools.enderStaff.get();
      // earthslime no longer needed due to forge feature
      case "earth_slime_spawn_egg" -> Items.SLIME_SPAWN_EGG;
      // blood removal
      case "bloodbone" -> TinkerMaterials.venombone.get();
      case "blood_slime_ball" -> Items.SLIME_BALL;
      case "blood_bucket" -> TinkerFluids.earthSlime.asItem();
      case "blood_bottle" -> TinkerFluids.slimeBottle.get(SlimeType.EARTH);
      // ID switched from non-generated to generated
      case "ichor_bottle" -> TinkerFluids.slimeBottle.get(SlimeType.ICHOR);
      // reinforcement rework, bronze was the only type dropped so map to the new type
      case "bronze_reinforcement" -> TinkerModifiers.obsidianReinforcement.get();
      default -> {
        Block block = missingBlock(name);
        yield block == null ? null : block.asItem();
      }
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registry.FLUID_REGISTRY, name -> switch (name) {
      case "blood" -> TinkerFluids.earthSlime.getStill();
      case "flowing_blood" -> TinkerFluids.earthSlime.getFlowing();
      default -> null;
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registry.ENTITY_TYPE_REGISTRY, name -> name.equals("earth_slime") ? EntityType.SLIME : null);
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registry.MOB_EFFECT_REGISTRY, name -> switch (name) {
      case "momentum" -> TinkerModifiers.momentumEffect.get(ToolType.HARVEST);
      case "insatiable" -> TinkerModifiers.insatiableEffect.get(ToolType.MELEE);
      default -> null;
    });
  }


  /* Utils */

  /**
   * Gets a resource location for Tinkers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(MOD_ID, name);
  }

  /**
   * Gets a data key for the capability, mainly used for modifier markers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static <T> TinkerDataKey<T> createKey(String name) {
    return TinkerDataKey.of(getResource(name));
  }

  /**
   * Gets a data key for the capability, mainly used for modifier markers
   * @param name         Resource path
   * @param constructor  Constructor for compute if absent
   * @return  Location for tinkers
   */
  public static <T> ComputableDataKey<T> createKey(String name, Supplier<T> constructor) {
    return ComputableDataKey.of(getResource(name), constructor);
  }

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
   * resource locations.
   */
  public static String resourceString(String res) {
    return String.format("%s:%s", MOD_ID, res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
   * namespace.
   */
  public static String prefix(String name) {
    return MOD_ID + "." + name.toLowerCase(Locale.US);
  }

  /** Makes a Tinker's description ID */
  public static String makeDescriptionId(String type, String name) {
    return type + "." + MOD_ID + "." + name;
  }

  /**
   * Makes a translation key for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeTranslationKey(String base, String name) {
    return Util.makeTranslationKey(base, getResource(name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name) {
    return Component.translatable(makeTranslationKey(base, name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base       Base name, such as "block" or "gui"
   * @param name       Object name
   * @param arguments  Additional arguments to the translation
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name, Object... arguments) {
    return Component.translatable(makeTranslationKey(base, name), arguments);
  }

  /**
   * This function is called in the constructor in some internal classes that are a common target for addons to wrongly extend.
   * These classes will cause issues if blindly used by the addon, and are typically trivial for the addon to implement
   * the parts they need if they just put in some effort understanding the code they are copying.
   *
   * As a reminder for addon devs, anything that is not in the library package can and will change arbitrarily. If you need to use a feature outside library, request it on our github.
   * @param self  Class to validate
   */
  public static void sealTinkersClass(Object self, String base, String solution) {
    // note for future maintainers: this does not use Java 9's sealed classes as unless you use modules those are restricted to the same package.
    // Dumb restriction but not like we can change it.
    String name = self.getClass().getName();
    if (!name.startsWith("slimeknights.tconstruct.")) {
      throw new IllegalStateException(base + " being extended from invalid package " + name + ". " + solution);
    }
  }
}
