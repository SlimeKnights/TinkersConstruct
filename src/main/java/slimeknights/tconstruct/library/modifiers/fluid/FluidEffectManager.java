package slimeknights.tconstruct.library.modifiers.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.bus.api.EventPriority;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** Manager for spilling fluids for spilling, slurping, and wetting */
public class FluidEffectManager extends SimpleJsonResourceReloadListener {
  /** Recipe folder */
  public static final String FOLDER = "tinkering/fluid_effects";

  /** Singleton instance of the modifier manager */
  public static final FluidEffectManager INSTANCE = new FluidEffectManager();

  /** List of available fluids */
  @Getter
  private List<FluidEffects.Entry> fluids = List.of();
  /** Cache of fluid to recipe, recipe will be null client side */
  private final Map<Fluid,FluidEffects> cache = new ConcurrentHashMap<>();

  /** Empty spilling fluid instance */
  private static final FluidEffects EMPTY = new FluidEffects(FluidIngredient.EMPTY, List.of(), List.of(), true);

  /** Condition context for recipe loading */
  private IContext conditionContext = IContext.EMPTY;
  /** Registry access for condition parsing */
  private RegistryAccess registryAccess = RegistryAccess.EMPTY;

  private FluidEffectManager() {
    super(JsonHelper.DEFAULT_GSON, FOLDER);
  }

  /** For internal use only */
  public void init() {
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, this::addDataPackListeners);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, OnDatapackSyncEvent.class, e -> JsonUtils.syncPackets(e, new UpdateFluidEffectsPacket(this.fluids)));
  }

  /** Adds the managers as datapack listeners */
  private void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(this);
    conditionContext = event.getConditionContext();
    registryAccess = event.getRegistryAccess();
  }

  /** Creates context for modifier parsing */
  public static TypedMapBuilder contextBuilder(ResourceLocation key) {
    return TypedMapBuilder.builder().put(ContextKey.ID, key).put(ContextKey.DEBUG, "Fluid Effect " + key);
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    long time = System.nanoTime();

    // load spilling from JSON
    List<FluidEffects.Entry> fluids = new ArrayList<>(splashList.size());
    for (Entry<ResourceLocation,JsonElement> entry : splashList.entrySet()) {
      ResourceLocation key = entry.getKey();
      try {
        JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "fluid_effect");

        // want to parse condition without parsing effects, as the effect serializer may be missing
        if (!conditionsMatch(json)) {
          continue;
        }
        fluids.add(new FluidEffects.Entry(key, FluidEffects.LOADABLE.deserialize(json, contextBuilder(key)
          .put(ContextKey.CONDITION_CONTEXT, conditionContext)
          .put(ContextKey.REGISTRY_ACCESS, registryAccess)
          .build())));
      } catch (JsonSyntaxException e) {
        TConstruct.LOG.error("Failed to load fluid effect {}", key, e);
      }
    }
    this.fluids = List.copyOf(fluids);
    this.cache.clear();
    TConstruct.LOG.info("Loaded {} spilling fluids in {} ms", fluids.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Updates the modifiers from the server */
  @Internal
  void updateFromServer(List<FluidEffects.Entry> fluids) {
    this.fluids = fluids;
    this.cache.clear();
  }

  /** Finds a fluid without checking the cache, returns null if missing */
  private final Function<Fluid,FluidEffects> FIND_UNCACHED = fluid -> {
    // find all severing recipes for the entity
    for (FluidEffects.Entry entry : fluids) {
      FluidEffects effects = entry.effects();
      if (effects.matches(fluid)) {
        return effects;
      }
    }
    // cache null if nothing
    return EMPTY;
  };

  /**
   * Gets the recipe for the given fluid. Does not work client side
   * @param fluid    Fluid
   * @return  Fluid, or empty if none exists
   */
  public FluidEffects find(Fluid fluid) {
    return cache.computeIfAbsent(fluid, FIND_UNCACHED);
  }

  /** Checks both legacy "conditions" and modern "neoforge:conditions" blocks. */
  private boolean conditionsMatch(JsonObject json) {
    ConditionalOps<JsonElement> ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, registryAccess), conditionContext);
    if (json.has("conditions")) {
      List<ICondition> conditions = ICondition.LIST_CODEC.parse(ops, json.get("conditions")).getOrThrow(JsonSyntaxException::new);
      return conditions.stream().allMatch(condition -> condition.test(conditionContext));
    }
    return ICondition.conditionsMatched(ops, json);
  }
}
