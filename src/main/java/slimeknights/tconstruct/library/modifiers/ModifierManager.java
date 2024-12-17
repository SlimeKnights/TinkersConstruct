package slimeknights.tconstruct.library.modifiers;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.utils.GenericTagUtil;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Modifier registry and JSON loader */
@Log4j2
public class ModifierManager extends SimpleJsonResourceReloadListener {
  /** Location of dynamic modifiers */
  public static final String FOLDER = "tinkering/modifiers";
  /** Location of modifier tags */
  public static final String TAG_FOLDER = "tinkering/tags/modifiers";

  public static final ResourceLocation ENCHANTMENT_MAP = TConstruct.getResource("tinkering/enchantments_to_modifiers.json");
  /** Registry key to make tag keys */
  public static final ResourceKey<? extends Registry<Modifier>> REGISTRY_KEY = ResourceKey.createRegistryKey(TConstruct.getResource("modifiers"));

  /** GSON instance for loading dynamic modifiers */
  public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

  /** ID of the default modifier */
  public static final ModifierId EMPTY = new ModifierId(TConstruct.MOD_ID, "empty");

  /** Singleton instance of the modifier manager */
  public static final ModifierManager INSTANCE = new ModifierManager();

  /** Default modifier to use when a modifier is not found */
  @Getter
  private final Modifier defaultValue;

  /** If true, static modifiers have been registered, so static modifiers can safely be fetched */
  @Getter
  private boolean modifiersRegistered = false;
  /** All modifiers registered directly with the manager */
  @VisibleForTesting
  final Map<ModifierId,Modifier> staticModifiers = new HashMap<>();
  /** Map of all modifier types that are expected to load in datapacks */
  private final Map<ModifierId,Class<?>> expectedDynamicModifiers = new HashMap<>();
  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.ModifierModule#LOADER} */
  @Deprecated
  public static final GenericLoaderRegistry<Modifier> MODIFIER_LOADERS = new GenericLoaderRegistry<>("Modifier", false);

  /** Modifiers loaded from JSON */
  private Map<ModifierId,Modifier> dynamicModifiers = Collections.emptyMap();
  /** Modifier tags loaded from JSON */
  private Map<TagKey<Modifier>,List<Modifier>> tags = Collections.emptyMap();
  /** Map from modifier to tags on the modifier */
  private Map<ModifierId,Set<TagKey<Modifier>>> reverseTags = Collections.emptyMap();

  /** List of tag to modifier mappings to try */
  private Map<TagKey<Enchantment>, Modifier> enchantmentTagMap = Collections.emptyMap();
  /** Mapping from enchantment to modifiers, for conversions */
  private Map<Enchantment,Modifier> enchantmentMap = Collections.emptyMap();

  /** If true, dynamic modifiers have been loaded from datapacks, so its safe to fetch dynamic modifiers */
  @Getter
  boolean dynamicModifiersLoaded = false;
  private IContext conditionContext = IContext.EMPTY;

  private ModifierManager() {
    super(GSON, FOLDER);
    // create the empty modifier
    defaultValue = new EmptyModifier();
    defaultValue.setId(EMPTY);
    staticModifiers.put(EMPTY, defaultValue);
  }

  /** For internal use only */
  public void init() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent.class, e -> e.enqueueWork(this::fireRegistryEvent));
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, this::addDataPackListeners);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, OnDatapackSyncEvent.class, e -> JsonUtils.syncPackets(e, new UpdateModifiersPacket(this.dynamicModifiers, this.tags, this.enchantmentMap, this.enchantmentTagMap)));
  }

  /** Fires the modifier registry event */
  private void fireRegistryEvent() {
    ModLoader.get().runEventGenerator(ModifierRegistrationEvent::new);
    modifiersRegistered = true;
  }

  /** Adds the managers as datapack listeners */
  private void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(this);
    conditionContext = event.getConditionContext();
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    long time = System.nanoTime();

    // load modifiers from JSON
    Map<ModifierId,ModifierId> redirects = new HashMap<>();
    this.dynamicModifiers = splashList.entrySet().stream()
                                      .map(entry -> loadModifier(entry.getKey(), entry.getValue().getAsJsonObject(), redirects))
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.toMap(Modifier::getId, mod -> mod));

    // process redirects
    Map<ModifierId,Modifier> resolvedRedirects = new HashMap<>(); // handled as a separate map to prevent redirects depending on order (no double redirects)
    for (Entry<ModifierId, ModifierId> redirect : redirects.entrySet()) {
      ModifierId from = redirect.getKey();
      ModifierId to = redirect.getValue();
      if (!contains(to)) {
        log.error("Invalid modifier redirect {} as modifier {} does not exist", from, to);
      } else {
        resolvedRedirects.put(from, get(to));
      }
    }
    int modifierSize = this.dynamicModifiers.size();
    this.dynamicModifiers.putAll(resolvedRedirects);

    // validate required modifiers
    for (Entry<ModifierId,Class<?>> entry : expectedDynamicModifiers.entrySet()) {
      Modifier modifier = dynamicModifiers.get(entry.getKey());
      if (modifier == null) {
        log.error("Missing expected modifier '" + entry.getKey() + "'");
      } else if (!entry.getValue().isInstance(modifier)) {
        log.error("Modifier '" + entry.getKey() + "' was loaded with the wrong class type. Expected " + entry.getValue().getName() + ", got " + modifier.getClass().getName());
      }
    }

    // TODO: this should be set back to false at some point
    dynamicModifiersLoaded = true;
    long timeStep = System.nanoTime();
    log.info("Loaded {} dynamic modifiers and {} modifier redirects in {} ms", modifierSize, redirects.size(), (timeStep - time) / 1000000f);
    time = timeStep;

    // load modifier tags
    TagLoader<Modifier> tagLoader = new TagLoader<>(id -> {
      Modifier modifier = ModifierManager.getValue(new ModifierId(id));
      if (modifier == defaultValue) {
        return Optional.empty();
      }
      return Optional.of(modifier);
    }, TAG_FOLDER);
    this.tags = GenericTagUtil.mapLoaderResults(REGISTRY_KEY, tagLoader.loadAndBuild(pResourceManager));
    this.reverseTags = GenericTagUtil.reverseTags(Modifier::getId, tags);
    timeStep = System.nanoTime();
    log.info("Loaded {} modifier tags for {} modifiers in {} ms", tags.size(), this.reverseTags.size(), (timeStep - time) / 1000000f);

    // load modifier to enchantment mapping
    enchantmentMap = new HashMap<>();
    this.enchantmentTagMap = new LinkedHashMap<>();
    for (Resource resource : pResourceManager.getResourceStack(ENCHANTMENT_MAP)) {
      JsonObject enchantmentJson = JsonHelper.getJson(resource, ENCHANTMENT_MAP);
      if (enchantmentJson != null) {
        for (Entry<String,JsonElement> entry : enchantmentJson.entrySet()) {
          try {
            // parse the modifier first, its the same in both cases
            String key = entry.getKey();
            ModifierId modifierId = new ModifierId(JsonHelper.convertToResourceLocation(entry.getValue(), "modifier"));
            Modifier modifier = get(modifierId);
            if (modifier == defaultValue) {
              throw new JsonSyntaxException("Unknown modifier " + modifierId + " for enchantment " + key);
            }

            // if it starts with #, it's a tag
            if (key.startsWith("#")) {
              ResourceLocation tagId = ResourceLocation.tryParse(key.substring(1));
              if (tagId == null) {
                throw new JsonSyntaxException("Invalid enchantment tag ID " + key.substring(1));
              }
              this.enchantmentTagMap.put(TagKey.create(Registry.ENCHANTMENT_REGISTRY, tagId), modifier);
            } else {
              // assume its an ID
              ResourceLocation enchantId = ResourceLocation.tryParse(key);
              if (enchantId == null || !ForgeRegistries.ENCHANTMENTS.containsKey(enchantId)) {
                throw new JsonSyntaxException("Invalid enchantment ID " + key);
              }
              enchantmentMap.put(ForgeRegistries.ENCHANTMENTS.getValue(enchantId), modifier);
            }
          } catch (JsonSyntaxException e) {
            log.info("Invalid enchantment to modifier mapping", e);
          }
        }
      }
    }
    log.info("Loaded {} enchantment to modifier mappings in {} ms", enchantmentMap.size() + enchantmentTagMap.size(), (System.nanoTime() - timeStep) / 1000000f);

    MinecraftForge.EVENT_BUS.post(new ModifiersLoadedEvent());
  }

  /** Loads a modifier from JSON */
  @Nullable
  private Modifier loadModifier(ResourceLocation key, JsonElement element, Map<ModifierId, ModifierId> redirects) {
    try {
      JsonObject json = GsonHelper.convertToJsonObject(element, "modifier");

      // processed first so a modifier can both conditionally redirect and fallback to a conditional modifier
      if (json.has("redirects")) {
        for (JsonRedirect redirect : JsonHelper.parseList(json, "redirects", JsonRedirect::fromJson)) {
          ICondition redirectCondition = redirect.getCondition();
          if (redirectCondition == null || redirectCondition.test(conditionContext)) {
            ModifierId redirectTarget = new ModifierId(redirect.getId());
            log.debug("Redirecting modifier {} to {}", key, redirectTarget);
            redirects.put(new ModifierId(key), redirectTarget);
            return null;
          }
        }
      }

      // conditions
      if (json.has("condition") && !CraftingHelper.getCondition(GsonHelper.getAsJsonObject(json, "condition")).test(conditionContext)) {
        return null;
      }

      // fallback to actual modifier
      Modifier modifier = MODIFIER_LOADERS.deserialize(json);
      modifier.setId(new ModifierId(key));
      return modifier;
    } catch (JsonSyntaxException e) {
      log.error("Failed to load modifier {}", key, e);
      return null;
    }
  }

  /** Updates the modifiers from the server */
  void updateModifiersFromServer(Map<ModifierId,Modifier> modifiers, Map<TagKey<Modifier>,List<Modifier>> tags, Map<Enchantment,Modifier> enchantmentMap, Map<TagKey<Enchantment>,Modifier> enchantmentTagMappings) {
    this.dynamicModifiers = modifiers;
    this.dynamicModifiersLoaded = true;
    this.tags = tags;
    this.reverseTags = GenericTagUtil.reverseTags(Modifier::getId, tags);
    this.enchantmentMap = enchantmentMap;
    this.enchantmentTagMap = enchantmentTagMappings;
    MinecraftForge.EVENT_BUS.post(new ModifiersLoadedEvent());
  }


  /* Query the registry */

  /** Fetches a static modifier by ID, only use if you need access to modifiers before the world loads*/
  public Modifier getStatic(ModifierId id) {
    return staticModifiers.getOrDefault(id, defaultValue);
  }

  /** Checks if the given static modifier exists */
  public boolean containsStatic(ModifierId id) {
    return staticModifiers.containsKey(id) || expectedDynamicModifiers.containsKey(id);
  }

  /** Checks if the registry contains the given modifier */
  public boolean contains(ModifierId id) {
    return staticModifiers.containsKey(id) || dynamicModifiers.containsKey(id);
  }

  /** Gets the modifier for the given ID */
  public Modifier get(ModifierId id) {
    // highest priority is static modifiers, cannot be replaced
    Modifier modifier = staticModifiers.get(id);
    if (modifier != null) {
      return modifier;
    }
    // second priority is dynamic modifiers, fallback to the default
    return dynamicModifiers.getOrDefault(id, defaultValue);
  }

  /**
   * Gets the modifier for a given enchantment. Not currently synced to client side
   * @param enchantment  Enchantment
   * @return Closest modifier to the enchantment, or null if no match
   */
  @Nullable
  public Modifier get(Enchantment enchantment) {
    // if we saw it before, return the last value
    if (enchantmentMap.containsKey(enchantment)) {
      return enchantmentMap.get(enchantment);
    }
    // did not find, check the tags
    for (Entry<TagKey<Enchantment>,Modifier> mapping : enchantmentTagMap.entrySet()) {
      if (RegistryHelper.contains(Registry.ENCHANTMENT, mapping.getKey(), enchantment)) {
        return mapping.getValue();
      }
    }
    return null;
  }

  /** Gets a stream of all enchantments that match the given modifiers */
  public Stream<Enchantment> getEquivalentEnchantments(Predicate<ModifierId> modifiers) {
    Predicate<Entry<?,Modifier>> predicate = entry -> modifiers.test(entry.getValue().getId());
    return Stream.concat(
      enchantmentMap.entrySet().stream().filter(predicate).map(Entry::getKey),
      enchantmentTagMap.entrySet().stream().filter(predicate).flatMap(entry -> RegistryHelper.getTagValueStream(Registry.ENCHANTMENT, entry.getKey()))
    ).distinct().sorted(Comparator.comparing(enchantment -> Objects.requireNonNull(Registry.ENCHANTMENT.getKey(enchantment))));
  }

  /** Gets a list of all modifier IDs */
  public Stream<ResourceLocation> getAllLocations() {
    // filter out redirects (redirects are any modifiers where the ID does not match the key
    return Stream.concat(staticModifiers.entrySet().stream(), dynamicModifiers.entrySet().stream())
                 .filter(entry -> entry.getKey().equals(entry.getValue().getId()))
                 .map(Entry::getKey);
  }

  /** Gets a stream of all modifier values */
  public Stream<Modifier> getAllValues() {
    return Stream.concat(staticModifiers.values().stream(), dynamicModifiers.values().stream()).distinct();
  }


  /* Helpers */

  /** Gets the modifier for the given ID */
  public static Modifier getValue(ModifierId name) {
    return INSTANCE.get(name);
  }

  /**
   * Parses a modifier from JSON
   * @param element   Element to deserialize
   * @param key       Json key
   * @return  Registry value
   * @throws JsonSyntaxException  If something failed to parse
   * @deprecated use {@link ModifierId} or {@link slimeknights.tconstruct.library.modifiers.util.LazyModifier}
   */
  @Deprecated(forRemoval = true)
  public static Modifier convertToModifier(JsonElement element, String key) {
    ModifierId name = new ModifierId(JsonHelper.convertToResourceLocation(element, key));
    if (INSTANCE.contains(name)) {
      return INSTANCE.get(name);
    }
    throw new JsonSyntaxException("Unknown modifier " + name);
  }

  /**
   * Parses a modifier from JSON
   * @param parent    Parent JSON object
   * @param key       Json key
   * @return  Registry value
   * @throws JsonSyntaxException  If something failed to parse
   * @deprecated use {@link ModifierId} or {@link slimeknights.tconstruct.library.modifiers.util.LazyModifier}
   */
  @Deprecated(forRemoval = true)
  public static Modifier deserializeModifier(JsonObject parent, String key) {
    return convertToModifier(JsonHelper.getElement(parent, key), key);
  }

  /**
   * Reads a modifier from the buffer
   * @param buffer  Buffer instance
   * @return  Modifier instance
   * @deprecated use {@link ModifierId} or {@link slimeknights.tconstruct.library.modifiers.util.LazyModifier}
   */
  @Deprecated(forRemoval = true)
  public static Modifier fromNetwork(FriendlyByteBuf buffer) {
    return INSTANCE.get(new ModifierId(buffer.readUtf(Short.MAX_VALUE)));
  }

  /**
   * Reads a modifier from the buffer
   * @param modifier  Modifier instance
   * @param buffer    Buffer instance
   * @deprecated use {@link ModifierId} or {@link slimeknights.tconstruct.library.modifiers.util.LazyModifier}
   */
  @Deprecated(forRemoval = true)
  public static void toNetwork(Modifier modifier, FriendlyByteBuf buffer) {
    buffer.writeUtf(modifier.getId().toString());
  }


  /* Tags */

  /** Creates a tag key for a modifier */
  public static TagKey<Modifier> getTag(ResourceLocation id) {
    return TagKey.create(REGISTRY_KEY, id);
  }

  /**
   * Checks if the given modifier is in the given tag
   * @return  True if the modifier is in the tag
   */
  public static boolean isInTag(ModifierId modifier, TagKey<Modifier> tag) {
    return INSTANCE.reverseTags.getOrDefault(modifier, Collections.emptySet()).contains(tag);
  }

  /**
   * Gets all values contained in the given tag
   * @param tag  Tag instance
   * @return  Contained values
   */
  public static List<Modifier> getTagValues(TagKey<Modifier> tag) {
    return INSTANCE.tags.getOrDefault(tag, List.of());
  }


  /* Events */

  /** Event for registering modifiers */
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public class ModifierRegistrationEvent extends Event implements IModBusEvent {
    /** Container receiving this event */
    private final ModContainer container;

    /** Validates the namespace of the container registering */
    private void checkModNamespace(ResourceLocation name) {
      // check mod container, should be the active mod
      // don't want mods registering stuff in Tinkers namespace, or Minecraft
      String activeMod = container.getNamespace();
      if (!name.getNamespace().equals(activeMod)) {
        TConstruct.LOG.warn("Potentially Dangerous alternative prefix for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", name, activeMod);
      }
    }

    /**
     * Registers a static modifier with the manager. Static modifiers cannot be configured by datapacks, so its generally encouraged to use dynamic modifiers
     * @param name      Modifier name
     * @param modifier  Modifier instance
     */
    public void registerStatic(ModifierId name, Modifier modifier) {
      checkModNamespace(name);

      // should not include under both types
      if (expectedDynamicModifiers.containsKey(name)) {
        throw new IllegalArgumentException(name + " is already expected as a dynamic modifier");
      }

      // set the name and register it
      modifier.setId(name);
      Modifier existing = staticModifiers.putIfAbsent(name, modifier);
      if (existing != null) {
        throw new IllegalArgumentException("Attempting to register a duplicate static modifier, this is not supported. Original value " + existing);
      }
    }

    /**
     * Registers that the given modifier is expected to be loaded in datapacks, without a class type check
     * @param name  Modifier name
     */
    public void registerExpected(ModifierId name) {
      registerExpected(name, Modifier.class);
    }

    /**
     * Registers that the given modifier is expected to be loaded in datapacks
     * @param name         Modifier name
     * @param classFilter  Class type the modifier is expected to have. Can be an interface
     * @deprecated use {@link #registerExpected(ModifierId)}, class specific modifier serializers are being phased out.
     */
    @Deprecated
    public void registerExpected(ModifierId name, Class<?> classFilter) {
      checkModNamespace(name);

      // should not include under both types
      if (staticModifiers.containsKey(name)) {
        throw new IllegalArgumentException(name + " is already registered as a static modifier");
      }

      // register it
      Class<?> existing = expectedDynamicModifiers.putIfAbsent(name, classFilter);
      if (existing != null) {
        throw new IllegalArgumentException("Attempting to register a duplicate expected modifier, this is not supported. Original value " + existing);
      }
    }
  }

  /** Event fired when modifiers reload */
  public static class ModifiersLoadedEvent extends Event {}

  /** Class for the empty modifier instance, mods should not need to extend this class */
  private static class EmptyModifier extends Modifier {
    @Override
    public boolean shouldDisplay(boolean advanced) {
      return false;
    }
  }
}
