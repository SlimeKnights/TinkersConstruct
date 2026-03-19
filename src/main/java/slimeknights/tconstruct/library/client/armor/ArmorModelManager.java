package slimeknights.tconstruct.library.client.armor;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.tools.client.material.CombatFishingHookRenderer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ArmorModelManager extends SimpleJsonResourceReloadListener {
  /** Folder containing the logic */
  public static final String FOLDER = "tinkering/armor_models";

  /** Object representing parsed models */
  public record ArmorModel(List<ArmorTextureSupplier> layers) {
    /** Empty instace for fallback */
    public static final ArmorModel EMPTY = new ArmorModel(List.of());
    /** Loadable for JSON parsing */
    public static final RecordLoadable<ArmorModel> LOADABLE = RecordLoadable.create(ArmorTextureSupplier.LOADER.list(1).requiredField("layers", ArmorModel::layers), ArmorModel::new);
  }

  /* Instance data */
  public static final ArmorModelManager INSTANCE = new ArmorModelManager();
  /** Map of location to texture suppliers */
  private Map<ResourceLocation,ArmorModel> models = Collections.emptyMap();

  private static final List<ArmorModelDispatcher> DISPATCHERS = new ArrayList<>();

  /**
   * Initializes this manager, registering it with the resource manager
   * @param manager  Manager
   */
  public static void init(RegisterClientReloadListenersEvent manager) {
    manager.registerReloadListener(INSTANCE);
  }

  private ArmorModelManager() {
    super(JsonHelper.DEFAULT_GSON, FOLDER);
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager manager, ProfilerFiller pProfiler) {
    long time = System.nanoTime();

    // first, load in all fluid textures, means we are allowed to reference them in fluid texture supplier constructors
    ArmorTextureSupplier.TEXTURE_VALIDATOR.onReloadSafe(manager);
    // reuses armor model logic for simplicity
    CombatFishingHookRenderer.clearCache();

    // load all models
    ImmutableMap.Builder<ResourceLocation,ArmorModel> builder = ImmutableMap.builder();
    for (Entry<ResourceLocation,JsonElement> entry : splashList.entrySet()) {
      ResourceLocation key = entry.getKey();
      JsonElement element = entry.getValue();
      try {
        builder.put(key, ArmorModel.LOADABLE.convert(element, key.toString()));
      } catch (JsonSyntaxException e) {
        TConstruct.LOG.error("Failed to load armor model from {}", key, e);
      }
    }

    this.models = builder.build();
    // clear dispatcher model cache
    Set<ResourceLocation> missing = new HashSet<>();
    for (ArmorModelDispatcher dispatcher : DISPATCHERS) {
      dispatcher.model = null;
      ResourceLocation name = dispatcher.getName();
      if (!this.models.containsKey(name)) {
        missing.add(name);
      }
    }
    if (!missing.isEmpty()) {
      TConstruct.LOG.error("Missing armor models used by items: {}", missing);
    }
    TConstruct.LOG.info("Loaded {} armor models in {} ms", models.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Gets the armor model for the given location. Location typically corresponds to armor material name */
  public ArmorModel getModel(ResourceLocation name) {
    return models.getOrDefault(name, ArmorModel.EMPTY);
  }

  /** Helper to cache armor models in the item */
  public abstract static class ArmorModelDispatcher implements IClientItemExtensions {
    private ArmorModel model;

    public ArmorModelDispatcher() {
      DISPATCHERS.add(this);
    }

    /**
     * Gets the name of the model to use.
     * Not a constructor parameter as forge initializes client extensions before we can store fields from the parent constructor.
     */
    protected abstract ResourceLocation getName();

    /** Fetches the model from the cache */
    protected ArmorModel getModel(ItemStack stack) {
      if (model == null) {
        model = ArmorModelManager.INSTANCE.getModel(getName());
        if (model == ArmorModel.EMPTY) {
          TConstruct.LOG.warn("Failed to find armor model {}, will skip rendering {}", getName(), stack);
        }
      }
      return model;
    }

    @Nonnull
    @Override
    public Model getGenericArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
      return MultilayerArmorModel.INSTANCE.setup(living, stack, slot, original, getModel(stack));
    }
  }
}
