package slimeknights.tconstruct.library.data.material;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialTraitsJson;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.traits.MaterialTraits;
import slimeknights.tconstruct.library.materials.traits.MaterialTraitsManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** Base data generator for use in addons */
@SuppressWarnings({"unused", "SameParameterValue"})  // API
public abstract class AbstractMaterialTraitDataProvider extends GenericDataProvider {
  /** Map of material ID to builder, there is at most one builder for each ID */
  private final Map<MaterialId,MaterialTraitsBuilder> allMaterialTraits = new HashMap<>();
  /* Materials data provider for validation */
  private final AbstractMaterialDataProvider materials;

  public AbstractMaterialTraitDataProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
    super(packOutput, Target.DATA_PACK, MaterialTraitsManager.FOLDER, MaterialTraitsManager.GSON);
    this.materials = materials;
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialTraits();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addMaterialTraits();

    // ensure we have traits for all materials
    // if you want no traits for your material, use an empty list
    Set<MaterialId> materialsGenerated = materials.getAllMaterials();
    for (MaterialId material : materialsGenerated) {
      if (!allMaterialTraits.containsKey(material)) {
        throw new IllegalStateException(String.format("Missing material traits for '%s'", material));
      }
    }

    // generate
    return allOf(allMaterialTraits.entrySet().stream().map(entry -> saveJson(cache, entry.getKey(), entry.getValue().build())));
  }


  /* Helpers */

  /**
   * Gets the material traits object from the map, or creates one if needed
   * @param location  Material ID
   * @return  MaterialTraits object, creating one if needed
   */
  protected MaterialTraitsBuilder material(MaterialId location) {
    return allMaterialTraits.computeIfAbsent(location, id -> new MaterialTraitsBuilder());
  }

  /**
   * Declares the given material with no traits
   * @param location  Material ID
   */
  protected void noTraits(MaterialId location) {
    material(location);
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param traits    Traits to add
   */
  protected void addDefaultTraits(MaterialId location, ModifierEntry... traits) {
    material(location).addDefaultTraits(traits);
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param traits    Traits to add
   */
  protected void addDefaultTraits(MaterialId location, ModifierId... traits) {
    material(location).addDefaultTraits(traits);
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param traits    Traits to add
   */
  protected void addDefaultTraits(MaterialId location, LazyModifier... traits) {
    material(location).addDefaultTraits(traits);
  }

  /**
   * Adds a set of material stats for the given material ID and stat ID
   * @param location  Material ID
   * @param statsId   Stats to add the trait for
   * @param traits    Traits to add
   */
  protected void addTraits(MaterialId location, MaterialStatsId statsId, ModifierEntry... traits) {
    material(location).addTraits(statsId, traits);
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param statsId   Stats to add the trait for
   * @param traits    Traits to add
   */
  protected void addTraits(MaterialId location, MaterialStatsId statsId, ModifierId... traits) {
    material(location).addTraits(statsId, traits);
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param statsId   Stats to add the trait for
   * @param traits    Traits to add
   */
  protected void addTraits(MaterialId location, MaterialStatsId statsId, LazyModifier... traits) {
    material(location).addTraits(statsId, traits);
  }

  /** Builder for {@link MaterialTraits}. Unlike {@link MaterialTraits.Builder}, uses additive list building rather than replacing lists */
  @CanIgnoreReturnValue
  public static class MaterialTraitsBuilder {
    private final List<ModifierEntry> defaultTraits = new ArrayList<>();
    private final Map<ResourceLocation,List<ModifierEntry>> perStats = new HashMap<>();

    /** Adds the given traits to the list */
    private static void addAll(List<ModifierEntry> list, LazyModifier[] traits) {
      for (LazyModifier trait : traits) {
        list.add(new ModifierEntry(trait, 1));
      }
    }

    /** Adds the given traits to the list */
    private static void addAll(List<ModifierEntry> list, ModifierId[] traits) {
      for (ModifierId trait : traits) {
        list.add(new ModifierEntry(trait, 1));
      }
    }


    /* Default traits */

    /** Adds the list of traits to the builder */
    public MaterialTraitsBuilder addDefaultTraits(ModifierEntry... traits) {
      Collections.addAll(defaultTraits, traits);
      return this;
    }

    /** Adds the list of traits to the builder */
    public MaterialTraitsBuilder addDefaultTraits(LazyModifier... traits) {
      addAll(defaultTraits, traits);
      return this;
    }

    /** Adds the list of traits to the builder */
    public MaterialTraitsBuilder addDefaultTraits(ModifierId... traits) {
      addAll(defaultTraits, traits);
      return this;
    }


    /* Per stat */

    /** Gets the list for the given stat type */
    private List<ModifierEntry> getList(MaterialStatsId statsId, int size) {
      return perStats.computeIfAbsent(statsId, k -> new ArrayList<>(size));
    }

    /** Adds the passed traits to the builder. */
    public MaterialTraitsBuilder addTraits(MaterialStatsId statsId, ModifierEntry... traits) {
      Collections.addAll(getList(statsId, traits.length), traits);
      return this;
    }

    /** Adds the passed traits to the builder. */
    public MaterialTraitsBuilder addTraits(MaterialStatsId statsId, LazyModifier... traits) {
      addAll(getList(statsId, traits.length), traits);
      return this;
    }

    /** Adds the passed traits to the builder. */
    public MaterialTraitsBuilder addTraits(MaterialStatsId statsId, ModifierId... traits) {
      addAll(getList(statsId, traits.length), traits);
      return this;
    }


    /** Builds the final material traits for serializing */
    @CheckReturnValue
    private MaterialTraitsJson build() {
      return new MaterialTraitsJson(
        defaultTraits.isEmpty() ? null : defaultTraits,
        perStats.isEmpty() ? null : perStats
      );
    }
  }
}
