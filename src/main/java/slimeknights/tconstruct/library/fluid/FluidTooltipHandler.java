package slimeknights.tconstruct.library.fluid;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.FluidIngredientSerializer;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.SafeClientAccess;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;

/** Handles fluid units displaying in tooltips */
@Log4j2
public class FluidTooltipHandler extends SimpleJsonResourceReloadListener {
  public static final Component HOLD_SHIFT = new TranslatableComponent(TConstruct.makeTranslationKey("gui", "fluid.hold_shift")).withStyle(ChatFormatting.GRAY);
  /** Folder for saving the logic */
  public static final String FOLDER = "tinkering/fluid_tooltips";
  /** GSON instance */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(FluidIngredient.class, FluidIngredientSerializer.INSTANCE)
    .registerTypeAdapter(TagKey.class, new TagKeySerializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** ID of the default fallback */
  public static final ResourceLocation DEFAULT_ID = TConstruct.getResource("fallback");

  /* Base units */
  private static final FluidUnit BUCKET = new FluidUnit(TConstruct.makeTranslationKey("gui", "fluid.bucket"), 1000);
  private static final FluidUnit MILLIBUCKET = new FluidUnit(TConstruct.makeTranslationKey("gui", "fluid.millibucket"), 1);
  @Deprecated
  private static final FluidUnit INGOT = new FluidUnit(TConstruct.makeTranslationKey("gui", "fluid.ingot"), FluidValues.INGOT);
  /** Default fallback in case resource pack has none */
  private static final FluidUnitList DEFAULT_LIST = new FluidUnitList(null, Collections.singletonList(BUCKET));

  /** Formatter as a biconsumer, shows up in a few places */
  public static final BiConsumer<Integer,List<Component>> BUCKET_FORMATTER = FluidTooltipHandler::appendBuckets;

  /* Instance data */
  public static final FluidTooltipHandler INSTANCE = new FluidTooltipHandler();

  /** Fallback to use when no list matches */
  private FluidUnitList fallback = DEFAULT_LIST;
  /** List of tooltip options */
  private Map<ResourceLocation,FluidUnitList> unitLists = Collections.emptyMap();
  /** Cache of fluid to entry */
  private final Map<Fluid,FluidUnitList> listCache = new HashMap<>();

  /**
   * Initializes this manager, registering it with the resource manager
   * @param manager  Manager
   */
  public static void init(RegisterClientReloadListenersEvent manager) {
    manager.registerReloadListener(INSTANCE);
  }

  private FluidTooltipHandler() {
    super(GSON, FOLDER);
  }

  /** Loads from JSON */
  @Nullable
  private static FluidUnitList loadList(ResourceLocation key, JsonElement json) {
    try {
      return GSON.fromJson(json, FluidUnitList.class);
    } catch (JsonSyntaxException e) {
      log.error("Failed to load fluid container transfer info from {}", key, e);
      return null;
    }
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager manager, ProfilerFiller profiler) {
    long time = System.nanoTime();
    ImmutableMap.Builder<ResourceLocation,FluidUnitList> builder = ImmutableMap.builder();
    Map<ResourceLocation,ResourceLocation> redirects = new HashMap<>();
    for (Entry<ResourceLocation,JsonElement> entry : splashList.entrySet()) {
      ResourceLocation key = entry.getKey();
      JsonElement element = entry.getValue();

      // if a redirect, store in the map for later
      if (element.isJsonObject()) {
        JsonObject object = element.getAsJsonObject();
        if (object.has("redirect")) {
          ResourceLocation redirect = JsonHelper.getResourceLocation(object, "redirect");
          redirects.put(key, redirect);
          continue;
        }
      }
      // parse list regularly
      FluidUnitList list = loadList(key, element);
      if (list != null) {
        builder.put(key, list);
      }
    }
    // process redirects
    Map<ResourceLocation,FluidUnitList> mapBeforeRedirects = builder.build();
    builder = ImmutableMap.builder();
    builder.putAll(mapBeforeRedirects);
    for (Entry<ResourceLocation,ResourceLocation> entry : redirects.entrySet()) {
      ResourceLocation from = entry.getKey();
      ResourceLocation to = entry.getValue();
      FluidUnitList list = mapBeforeRedirects.get(to);
      if (list != null) {
        builder.put(from, list);
      } else {
        log.error("Invalid fluid tooltip redirect {} as unit list {} does not exist", from, to);
      }
    }
    // find the fallback
    unitLists = builder.build();
    fallback = this.unitLists.getOrDefault(DEFAULT_ID, DEFAULT_LIST);
    listCache.clear();
    log.info("Loaded {} fluid unit lists in {} ms", listCache.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Gets the unit list for the given fluid */
  private FluidUnitList getUnitList(Fluid fluid) {
    FluidUnitList cached = listCache.get(fluid);
    if (cached != null) {
      return cached;
    }
    for (FluidUnitList list : unitLists.values()) {
      if (list.matches(fluid)) {
        listCache.put(fluid, list);
        return list;
      }
    }
    listCache.put(fluid, fallback);
    return fallback;
  }

  /** Gets the unit list for the given ID */
  private FluidUnitList getUnitList(ResourceLocation id) {
    return unitLists.getOrDefault(id, fallback);
  }


  /* External utilities */

  /**
   * Gets the tooltip for a fluid stack
   * @param fluid  Fluid stack instance
   * @return  Fluid tooltip
   */
  public static List<Component> getFluidTooltip(FluidStack fluid) {
    return getFluidTooltip(fluid, fluid.getAmount());
  }

  /**
   * Gets the tooltip for a fluid stack
   * @param fluid  Fluid stack instance
   * @param amount Amount override
   * @return  Fluid tooltip
   */
  public static List<Component> getFluidTooltip(FluidStack fluid, int amount) {
    List<Component> tooltip = new ArrayList<>();
    // fluid name, not sure if there is a cleaner way to do this
    tooltip.add(fluid.getDisplayName().plainCopy().withStyle(ChatFormatting.WHITE));
    // material
    appendMaterial(fluid.getFluid(), amount, tooltip);
    // add mod display name
    ModList.get().getModContainerById(Objects.requireNonNull(fluid.getFluid().getRegistryName()).getNamespace())
           .map(container -> container.getModInfo().getDisplayName())
           .ifPresent(name -> tooltip.add(new TextComponent(name).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
    return tooltip;
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid    Input fluid stack
   * @param tooltip  Tooltip to append information
   */
  public static void appendMaterial(FluidStack fluid, List<Component> tooltip) {
    appendMaterial(fluid.getFluid(), fluid.getAmount(), tooltip);
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid      Input fluid
   * @param original   Input amount
   * @param tooltip    Tooltip to append information
   */
  public static void appendMaterial(Fluid fluid, int original, List<Component> tooltip) {
    if (appendMaterialNoShift(fluid, original, tooltip)) {
      appendShift(tooltip);
    }
  }

  /**
   * Adds information for the tooltip based on material units, does not show "hold shift for buckets"
   * @param fluid      Input fluid
   * @param original   Input amount
   * @param tooltip    Tooltip to append information
   * @return  True if the amount is not in buckets
   */
  public static boolean appendMaterialNoShift(Fluid fluid, int original, List<Component> tooltip) {
    // if holding shift, skip specific units
    if (SafeClientAccess.getTooltipKey() != TooltipKey.SHIFT) {
      int amount = original;
      amount = INSTANCE.getUnitList(fluid).getText(tooltip, amount);
      MILLIBUCKET.getText(tooltip, amount);
      return INSTANCE.listCache.get(fluid) != INSTANCE.fallback;
    } else {
      // standard display stuff: bucket amounts
      appendBuckets(original, tooltip);
      return false;
    }
  }

  /**
   * Appends the hold shift message to the tooltip
   * @param tooltip  Tooltip to append information
   */
  public static void appendShift(List<Component> tooltip) {
    if(!SafeClientAccess.getTooltipKey().isShiftOrUnknown()) {
      tooltip.add(TextComponent.EMPTY);
      tooltip.add(HOLD_SHIFT);
    }
  }

  /**
   * Adds information to the tooltip based on ingot units
   * @param amount   Fluid amount
   * @param tooltip  Tooltip to append information
   * @deprecated use {@link #appendNamedList(ResourceLocation, int, List)}
   */
  @Deprecated
  public static void appendIngots(int amount, List<Component> tooltip) {
    amount = INGOT.getText(tooltip, amount);
    appendBuckets(amount, tooltip);
  }

  /**
   * Adds information to the tooltip based on a named list, allows customizing display for a specific location
   * @param id       ID of the list to append
   * @param amount   Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendNamedList(ResourceLocation id, int amount, List<Component> tooltip) {
    amount = INSTANCE.getUnitList(id).getText(tooltip, amount);
    appendBuckets(amount, tooltip);
  }

  /**
   * Adds information to the tooltip based on the fluid using bucket units
   * @param amount     Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendBuckets(int amount, List<Component> tooltip) {
    amount = INSTANCE.fallback.getText(tooltip, amount);
    MILLIBUCKET.getText(tooltip, amount);
  }

  /** Single entry for text options */
  @SuppressWarnings("ClassCanBeRecord") // needed in GSON
  @RequiredArgsConstructor
  public static class FluidUnit {
    private final String key;
    private final int needed;

    /**
     * Gets the display text for this fluid entry
     * @return  Display text
     */
    private int getText(List<Component> tooltip, int amount) {
      int full = amount / needed;
      if (full > 0) {
        tooltip.add(new TranslatableComponent(key, full).withStyle(ChatFormatting.GRAY));
      }
      return amount % needed;
    }
  }

  /** Represents a list of tooltip unit types for a fluid */
  @SuppressWarnings("ClassCanBeRecord") // needed in GSON
  @RequiredArgsConstructor
  public static class FluidUnitList {
    @Nullable
    private final TagKey<Fluid> tag;
    private final List<FluidUnit> units;

    /** Checks if this matches the given fluid */
    public boolean matches(Fluid fluid) {
      return this.tag != null && RegistryHelper.contains(this.tag, fluid);
    }

    /** Applies the text of all child units */
    public int getText(List<Component> tooltip, int amount) {
      if (units != null) {
        for (FluidUnit unit : units) {
          amount = unit.getText(tooltip, amount);
        }
      }
      return amount;
    }
  }

  private static class TagKeySerializer implements JsonSerializer<TagKey<Fluid>>, JsonDeserializer<TagKey<Fluid>> {
    @Override
    public TagKey<Fluid> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return TagKey.create(Registry.FLUID_REGISTRY, JsonHelper.convertToResourceLocation(json, "tag"));
    }

    @Override
    public JsonElement serialize(TagKey<Fluid> src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.location().toString());
    }
  }
}
