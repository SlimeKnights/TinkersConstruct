package slimeknights.tconstruct;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import slimeknights.mantle.compat.neoforged.neoforge.registries.MissingMappingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@Mod(TConstruct.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class TConstruct {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  public static final Random RANDOM = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;
  private static IEventBus modBus;

  public TConstruct(IEventBus bus, ModContainer container) {
    instance = this;
    modBus = bus;

    Config.init(container);
    TinkerItemDisplays.init();
    MaterialRegistry.init();

    // initialize modules, done this way rather than with annotations to give us control over the order
    NeoForge.EVENT_BUS.addListener(TConstruct::missingMappings);
    // base
    bus.register(new TinkerCommons());
    bus.register(new TinkerMaterials());
    new TinkerEffects();
    bus.register(new TinkerGadgets());
    bus.register(new TinkerAttributes());
    // world
    bus.register(new TinkerWorld());
    new TinkerStructures();
    // tools
    bus.register(new TinkerTables());
    bus.register(new TinkerModifiers());
    new TinkerToolParts();
    bus.register(new TinkerTools());
    // smeltery
    bus.register(new TinkerSmeltery());
    bus.register(new TinkerFluids());

    // init deferred registers
    TinkerModule.initRegisters(bus);
    TinkerNetwork.setup();
    bus.addListener(EventPriority.NORMAL, false, RegisterPayloadHandlersEvent.class, TinkerNetwork.getInstance()::registerPayloads);
    TinkerTags.init();
    // init client logic
    if (FMLEnvironment.dist == Dist.CLIENT) {
      TinkerClient.onConstruct();
    }

  }

  /** Gets the mod event bus for classes that still register themselves statically. */
  public static IEventBus getModBus() {
    return modBus;
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    ToolDefinitionLoader.init();
    StationSlotLayoutLoader.init();
  }

  /** Handles missing mappings of all types */
  private static void missingMappings(MissingMappingsEvent event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.BLOCK, name -> switch (name) {
      // silky jewel removal
      case "silky_jewel_block" -> Blocks.EMERALD_BLOCK;
      // piglin heads are vanilla
      case "piglin_head" -> Blocks.PIGLIN_HEAD;
      case "piglin_wall_head" -> Blocks.PIGLIN_WALL_HEAD;
      default -> null;
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.ITEM, name -> switch (name) {
      // silky jewel removal
      case "silky_jewel" -> Items.EMERALD;
      case "silky_jewel_block" -> Items.EMERALD_BLOCK;
      // piglin heads are vanilla
      case "piglin_head" -> Items.PIGLIN_HEAD;
      // round plate rename
      case "round_plate" -> TinkerToolParts.adzeHead.get();
      case "round_plate_cast" -> TinkerSmeltery.adzeHeadCast.get();
      case "round_plate_sand_cast" -> TinkerSmeltery.adzeHeadCast.getSand();
      case "round_plate_red_sand_cast" -> TinkerSmeltery.adzeHeadCast.getRedSand();
      // slimesuit rework
      case "slime_chestplate" -> TinkerTools.slimeWings.get();
      default -> null;
    });
  }

  /* Utils */

  /**
   * Gets a resource location for Tinkers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  @SuppressWarnings("removal")
  public static ResourceLocation getResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
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
