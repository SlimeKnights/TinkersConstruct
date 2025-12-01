package slimeknights.tconstruct.library.materials;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.LegacyLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.utils.Util;

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
    LOADER.register(getResource("random_variant"), RandomVariant.LOADER);
    LOADER.register(getResource("ancient"), Randomized.ANCIENT.getLoader());
  }

  /** Creates an instance for a fixed material */
  public static RandomMaterial fixed(MaterialVariantId materialId) {
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

  /** Creates a predicate for a random material variant. Will include material variants unlike {@link #random()} */
  public static RandomMaterial randomVariant(IJsonPredicate<MaterialVariantId> materials) {
    return new RandomVariant(materials);
  }

  /** Gets the ancient tool material instance */
  public static RandomMaterial ancient() {
    return Randomized.ANCIENT;
  }

  /** Creates a new conditional random material for datagen. */
  public static RandomMaterial conditional(RandomMaterial ifTrue, RandomMaterial ifFalse, ICondition... conditions) {
    return new Conditional(ifTrue, ifFalse, conditions);
  }


  /** Gets a random material */
  public abstract MaterialVariantId getMaterial(MaterialStatsId statType, RandomSource random);

  @Override
  public abstract RecordLoadable<? extends RandomMaterial> getLoader();

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
    public static final RecordLoadable<Randomized> LOADER = new LegacyLoadable<>(RecordLoadable.create(
      TIER_RANGE.defaultField("tier", r -> r.tier),
      BooleanLoadable.INSTANCE.defaultField("allow_hidden", false, false, r -> r.allowHidden),
      MaterialPredicate.LOADER.defaultField("material", r -> r.material),
      Randomized::new)) {

      @Override
      public Randomized deserialize(JsonObject json, TypedMap context) {
        if (json.has("tag")) {
          // warn of deprecated usage
          String debug = context.get(ContextKey.DEBUG);
          debug = debug != null ? " while parsing " + debug : "";
          TConstruct.LOG.warn("Using deprecated randomized material key 'tag' in {}, use 'material' instead", debug);
          // parse all properties that existed in parallel to the deprecated field
          IntRange tier = TIER_RANGE.getOrDefault(json, "tier");
          boolean allowHidden = GsonHelper.getAsBoolean(json, "allow_hidden", false);
          TagKey<IMaterial> tag = TinkerLoadables.MATERIAL_TAGS.getIfPresent(json, "tag");
          return new Randomized(tier, allowHidden, MaterialPredicate.tag(tag));
        }
        return base.deserialize(json, context);
      }
    };

    /** Minimum material tier */
    private final IntRange tier;
    /** If true, hidden materials are allowed */
    private final boolean allowHidden;
    /** Material condition */
    private final IJsonPredicate<MaterialVariantId> material;

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
                 && this.material.matches(material.getIdentifier())
                 && registry.getMaterialStats(id, statType).isPresent();
        })
        .map(IMaterial::getIdentifier)
        .toList();
      if (choices.isEmpty()) {
        TConstruct.LOG.warn("Random material found no options for statType={}, tier={}, allowHidden={}, predicate={}", statType, tier, allowHidden, material);
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
    public RecordLoadable<? extends RandomMaterial> getLoader() {
      return LOADER;
    }

    /** Singleton instance for commonly used ancient tool materials */
    public static final RandomMaterial ANCIENT = SingletonLoader.singleton(loader -> new Randomized(Randomized.TIER_RANGE, true, MaterialPredicate.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).inverted()) {
      @Override
      public RecordLoadable<? extends RandomMaterial> getLoader() {
        return loader;
      }
    });
  }

  /** Produces a random material from a material tier */
  @RequiredArgsConstructor
  private static class RandomVariant extends RandomMaterial implements Function<MaterialStatsId,List<List<MaterialVariantId>>> {
    public static final RecordLoadable<RandomVariant> LOADER = RecordLoadable.create(MaterialPredicate.LOADER.defaultField("material", r -> r.material), RandomVariant::new);

    /** Material condition */
    private final IJsonPredicate<MaterialVariantId> material;

    /** Cached list of material choices, each containing a list of variant choices. Ensures materials with more variants don't get weighted higher */
    private final Map<MaterialStatsId,List<List<MaterialVariantId>>> materialChoices = new ConcurrentHashMap<>();

    @Override
    public List<List<MaterialVariantId>> apply(MaterialStatsId statType) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      List<List<MaterialVariantId>> choices = MaterialRegistry
        .getInstance()
        .getAllMaterials()
        .stream()
        .map(material -> {
          MaterialId id = material.getIdentifier();
          if (registry.getMaterialStats(id, statType).isEmpty()) {
            return List.<MaterialVariantId>of();
          }
          return MaterialRecipeCache.getVariants(material.getIdentifier()).stream().filter(this.material::matches).toList();
        })
        .filter(list -> !list.isEmpty())
        .toList();
      if (choices.isEmpty()) {
        TConstruct.LOG.warn("Random variant found no options for statType={}, predicate={}", statType, material);
      }
      return choices;
    }

    @Override
    public void clearCache() {
      this.materialChoices.clear();
    }

    @Override
    public MaterialVariantId getMaterial(MaterialStatsId statType, RandomSource random) {
      List<List<MaterialVariantId>> materialChoices = this.materialChoices.computeIfAbsent(statType, this);
      if (materialChoices.isEmpty()) {
        // if we have no options, just get the first with the stat type
        // either this stat type is empty (and thus we end up with unknown), or the filter is too strict (so we end up with a useful material at least)
        return MaterialRegistry.firstWithStatType(statType).getIdentifier();
      }
      List<MaterialVariantId> variantChoices = materialChoices.get(random.nextInt(materialChoices.size()));
      return variantChoices.get(random.nextInt(variantChoices.size()));
    }

    @Override
    public RecordLoadable<RandomVariant> getLoader() {
      return LOADER;
    }
  }

  /** Conditional random material for datagen. */
  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor
  private static class Conditional extends RandomMaterial implements ConditionalObject<RandomMaterial> {
    private final RandomMaterial ifTrue;
    private final RandomMaterial ifFalse;
    private final ICondition[] conditions;

    @Override
    public MaterialVariantId getMaterial(MaterialStatsId statType, RandomSource random) {
      return (Util.testConditions(conditions) ? ifTrue : ifFalse).getMaterial(statType, random);
    }

    @Override
    public RecordLoadable<? extends RandomMaterial> getLoader() {
      return LOADER.getConditionalLoader();
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class RandomBuilder {
    /** Material tier */
    private IntRange tier = Randomized.TIER_RANGE;
    private boolean allowHidden = false;
    /** Material tag condition */
    @Setter @Accessors(fluent = true)
    private IJsonPredicate<MaterialVariantId> material = MaterialPredicate.ANY;

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

    /** Sets the material predicate to a tag predicate */
    public RandomBuilder tag(TagKey<IMaterial> tag) {
      this.material = MaterialPredicate.tag(tag);
      return this;
    }

    /** Builds the instance */
    public RandomMaterial build() {
      return new Randomized(tier, allowHidden, material);
    }
  }
}
