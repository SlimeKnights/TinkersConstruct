package slimeknights.tconstruct.library.modifiers.spilling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.ConditionSerializer;
import slimeknights.tconstruct.library.json.FluidIngredientSerializer;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Manager for spilling fluids for spilling, slurping, and wetting */
@Log4j2
public class SpillingFluidManager extends SimpleJsonResourceReloadListener {
  /** Recipe folder */
  public static final String FOLDER = "tinkering/spilling";
  /** GSON instance */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ICondition.class, ConditionSerializer.INSTANCE)
    .registerTypeHierarchyAdapter(ISpillingEffect.class, ISpillingEffect.LOADER)
    .registerTypeAdapter(FluidIngredient.class, FluidIngredientSerializer.INSTANCE)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Singleton instance of the modifier manager */
  public static final SpillingFluidManager INSTANCE = new SpillingFluidManager();

  /** List of available fluids, only exists serverside */
  private List<SpillingFluid> fluids = Collections.emptyList();
  /** Cache of fluid to recipe, recipe will be null client side */
  private final Map<Fluid,SpillingFluid> cache = new HashMap<>();

  /** Empty spilling fluid instance */
  private static final SpillingFluid EMPTY = new SpillingFluid(FluidIngredient.EMPTY, Collections.emptyList());

  /** Condition context for recipe loading */
  private IContext conditionContext = IContext.EMPTY;

  private SpillingFluidManager() {
    super(GSON, FOLDER);
  }

  /** For internal use only */
  @Deprecated
  public void init() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, this::addDataPackListeners);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, OnDatapackSyncEvent.class, e -> JsonUtils.syncPackets(e, new UpdateSpillingFluidsPacket(this.fluids)));
  }

  /** Adds the managers as datapack listeners */
  private void addDataPackListeners(final AddReloadListenerEvent event) {
    event.addListener(this);
    conditionContext = event.getConditionContext();
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    long time = System.nanoTime();

    // load spilling from JSON
    this.fluids = splashList.entrySet().stream()
                            .map(entry -> loadFluid(entry.getKey(), entry.getValue().getAsJsonObject()))
                            .filter(Objects::nonNull)
                            .toList();
    this.cache.clear();
    log.info("Loaded {} spilling fluids in {} ms", fluids.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Loads a modifier from JSON */
  @Nullable
  private SpillingFluid loadFluid(ResourceLocation key, JsonElement element) {
    try {
      JsonObject json = GsonHelper.convertToJsonObject(element, "fluid");

      // want to parse condition without parsing effects, as the effect serializer may be missing
      if (json.has("condition") && !CraftingHelper.getCondition(GsonHelper.getAsJsonObject(json, "condition")).test(conditionContext)) {
        return null;
      }
      FluidIngredient ingredient = FluidIngredient.deserialize(json, "fluid");
      List<ISpillingEffect> effects = JsonHelper.parseList(json, "effects", obj -> GSON.fromJson(obj, ISpillingEffect.class));
      return new SpillingFluid(ingredient, effects);
    } catch (JsonSyntaxException e) {
      log.error("Failed to load modifier {}", key, e);
      return null;
    }
  }

  /** Updates the modifiers from the server */
  void updateFromServer(List<SpillingFluid> fluids) {
    this.fluids = fluids;
    this.cache.clear();
  }

  /** Finds a fluid without checking the cache, returns null if missing */
  @Nullable
  private SpillingFluid findUncached(Fluid fluid) {
    // find all severing recipes for the entity
    for (SpillingFluid recipe : fluids) {
      if (recipe.matches(fluid)) {
        cache.put(fluid, recipe);
        return recipe;
      }
    }
    // cache null if nothing
    cache.put(fluid, null);
    return null;
  }

  /** Checks if the given fluid has a recipe */
  public boolean contains(Fluid fluid) {
    if (cache.containsKey(fluid)) {
      return cache.get(fluid) != null;
    }
    return findUncached(fluid) != null;
  }

  /**
   * Gets the recipe for the given fluid. Does not work client side
   * @param fluid    Fluid
   * @return  Fluid, or empty if none exists
   */
  public SpillingFluid find(Fluid fluid) {
    if (cache.containsKey(fluid)) {
      return cache.getOrDefault(fluid, EMPTY);
    }
    return Objects.requireNonNullElse(findUncached(fluid), EMPTY);
  }
}
