package slimeknights.tconstruct.library.materials;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static slimeknights.tconstruct.TConstruct.getResource;

/** Loot table object to get a randomized material. */
public abstract class RandomMaterial implements IHaveLoader {
  /** Loader for random materials */
  public static final GenericLoaderRegistry<RandomMaterial> LOADER = new GenericLoaderRegistry<>("Random Material", false);

  /** If true, this has been initialized */
  private static boolean initialized = false;

  /** Initializes material types */
  public static void init() {
    if (initialized) return;
    initialized = true;
    LOADER.register(getResource("fixed"), Fixed.LOADER);
    LOADER.register(getResource("first"), First.LOADER);
    LOADER.register(getResource("random"), Randomized.LOADER);
  }

  /** Creates an instance for a fixed material */
  public static RandomMaterial fixed(MaterialId materialId) {
    return new Fixed(materialId);
  }

  /** Creates an instance for a fixed material */
  public static RandomMaterial firstWithStat() {
    return First.INSTANCE;
  }

  /** Creates a builder for a random material */
  public static RandomBuilder random() {
    return new RandomBuilder();
  }

  /** Gets a random material */
  public abstract MaterialVariantId getMaterial(MaterialStatsId statType, RandomSource random);

  /** Clears any cache associated with the random material */
  public void clearCache() {}

  /** Builds the material list from the given random materials and stat types */
  public static MaterialNBT build(List<MaterialStatsId> statTypes, List<RandomMaterial> materials, RandomSource random) {
    MaterialNBT.Builder builder = MaterialNBT.builder();
    int max = Math.min(materials.size(), statTypes.size());
    for (int i = 0; i < max; i++) {
      builder.add(materials.get(i).getMaterial(statTypes.get(i), random));
    }
    // anything unspecified just default to the first with the stat type
    for (int i = max; i < statTypes.size(); i++) {
      builder.add(MaterialRegistry.firstWithStatType(statTypes.get(i)));
    }
    return builder.build();
  }


  /** Constant material */
  @RequiredArgsConstructor
  private static class Fixed extends RandomMaterial {
    private static final RecordLoadable<Fixed> LOADER = RecordLoadable.create(MaterialVariantId.LOADABLE.requiredField("material", r -> r.material), Fixed::new);

    private final MaterialVariantId material;

    @Override
    public MaterialVariantId getMaterial(MaterialStatsId statType, RandomSource random) {
      return material;
    }

    @Override
    public RecordLoadable<Fixed> getLoader() {
      return LOADER;
    }
  }

  /** Constant material */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  private static class First extends RandomMaterial {
    private static final First INSTANCE = new First();
    public static final SingletonLoader<First> LOADER = new SingletonLoader<>(INSTANCE);


    @Override
    public MaterialVariantId getMaterial(MaterialStatsId statType, RandomSource random) {
      return MaterialRegistry.firstWithStatType(statType).getIdentifier();
    }

    @Override
    public SingletonLoader<First> getLoader() {
      return LOADER;
    }
  }

  /** Produces a random material from a material tier */
  @RequiredArgsConstructor
  private static class Randomized extends RandomMaterial implements Function<MaterialStatsId,List<MaterialId>> {
    public static final IntRange TIER_RANGE = new IntRange(0, Integer.MAX_VALUE);
    public static final RecordLoadable<Randomized> LOADER = RecordLoadable.create(
      TIER_RANGE.defaultField("tier", r -> r.tier),
      BooleanLoadable.INSTANCE.defaultField("allow_hidden", false, false, r -> r.allowHidden),
      TinkerLoadables.MATERIAL_TAGS.nullableField("tag", r -> r.tag),
      Randomized::new);

    /** Minimum material tier */
    private final IntRange tier;
    /** If true, hidden materials are allowed */
    private final boolean allowHidden;
    /** Material tag condition */
    @Nullable
    private final TagKey<IMaterial> tag;

    /** Cached list of material choices, automatically deleted when loot tables reload */
    private final Map<MaterialStatsId,List<MaterialId>> materialChoices = new ConcurrentHashMap<>();

    @Override
    public List<MaterialId> apply(MaterialStatsId statType) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      List<MaterialId> choices = MaterialRegistry
        .getInstance()
        .getAllMaterials()
        .stream()
        .filter(material -> {
          MaterialId id = material.getIdentifier();
          return this.tier.test(material.getTier()) && (allowHidden || !material.isHidden())
                 && (tag == null || registry.isInTag(id, tag))
                 && registry.getMaterialStats(id, statType).isPresent();
        })
        .map(IMaterial::getIdentifier)
        .toList();
      if (choices.isEmpty()) {
        TConstruct.LOG.warn("Random material found no options for statType={}, tier={}, allowHidden={}", statType, tier, allowHidden);
      }
      return choices;
    }

    @Override
    public void clearCache() {
      this.materialChoices.clear();
    }

    @Override
    public MaterialId getMaterial(MaterialStatsId statType, RandomSource random) {
      List<MaterialId> materialChoices = this.materialChoices.computeIfAbsent(statType, this);
      if (materialChoices.isEmpty()) {
        // if we have no options, just get the first with the stat type
        // either this stat type is empty (and thus we end up with unknown), or the filter is too strict (so we end up with a useful material at least)
        return MaterialRegistry.firstWithStatType(statType).getIdentifier();
      }
      return materialChoices.get(random.nextInt(materialChoices.size()));
    }

    @Override
    public RecordLoadable<Randomized> getLoader() {
      return LOADER;
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class RandomBuilder {
    /** Material tier */
    private IntRange tier = Randomized.TIER_RANGE;
    private boolean allowHidden = false;
    /** Material tag condition */
    @Nullable @Setter @Accessors(fluent = true)
    private TagKey<IMaterial> tag;

    /** Sets the required tier */
    public RandomBuilder tier(int tier) {
      this.tier = Randomized.TIER_RANGE.exactly(tier);
      return this;
    }

    /** Sets the required tier to a range between min and max, inclusive */
    public RandomBuilder tier(int min, int max) {
      this.tier = Randomized.TIER_RANGE.range(min, max);
      return this;
    }

    /** Sets the required tier to be at least min */
    public RandomBuilder minTier(int min) {
      this.tier = Randomized.TIER_RANGE.min(min);
      return this;
    }

    /** Sets the required tier to be at most max */
    public RandomBuilder maxTier(int max) {
      this.tier = Randomized.TIER_RANGE.max(max);
      return this;
    }

    /** Makes hidden materials allowed */
    public RandomBuilder allowHidden() {
      this.allowHidden = true;
      return this;
    }

    /** Builds the instance */
    public RandomMaterial build() {
      return new Randomized(tier, allowHidden, tag);
    }
  }
}
