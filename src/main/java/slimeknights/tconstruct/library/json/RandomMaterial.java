package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Loot table object to get a randomized material
 */
public abstract class RandomMaterial {
  /** Map of all types */
  private static final Map<ResourceLocation,Function<JsonObject,RandomMaterial>> DESERIALIZERS = new HashMap<>();

  /** If true, this has been initialized */
  private static boolean initialized = false;

  /** Initializes material types */
  public static void init() {
    if (initialized) return;
    initialized = true;
    registerDeserializer(Fixed.ID, Fixed::fromJson);
    registerDeserializer(First.ID, First::fromJson);
    registerDeserializer(RandomInTier.ID, RandomInTier::fromJson);
  }

  /** Registers a deserializer */
  public static void registerDeserializer(ResourceLocation id, Function<JsonObject,RandomMaterial> deserializer) {
    DESERIALIZERS.putIfAbsent(id, deserializer);
  }

  /** Creates an instance for a fixed material */
  public static RandomMaterial fixed(MaterialId materialId) {
    return new Fixed(materialId);
  }

  /** Creates an instance for a fixed material */
  public static RandomMaterial firstWithStat(MaterialStatsId statsId) {
    return new First(statsId);
  }

  /** Creates a builder for a random material */
  public static RandomBuilder random(MaterialStatsId statType) {
    return new RandomBuilder(statType);
  }

  /** Gets a random material */
  public abstract MaterialVariantId getMaterial(Random random);

  /** Serializes the given material to json */
  public abstract JsonObject serialize();

  /** Deserializes the material from JSON */
  public static RandomMaterial deserialize(JsonObject json) {
    ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
    Function<JsonObject,RandomMaterial> parser = DESERIALIZERS.get(type);
    if (parser == null) {
      throw new JsonSyntaxException("Unknown random material type " + type);
    }
    return parser.apply(json);
  }

  /** Constant material */
  @RequiredArgsConstructor
  private static class Fixed extends RandomMaterial {
    private static final ResourceLocation ID = TConstruct.getResource("fixed");

    private final MaterialVariantId material;

    /** Creates an instance from JSON */
    public static Fixed fromJson(JsonObject json) {
      MaterialVariantId materialId = MaterialVariantId.fromJson(json, "name");
      return new Fixed(materialId);
    }

    @Override
    public MaterialVariantId getMaterial(Random random) {
      return material;
    }

    @Override
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("type", ID.toString());
      json.addProperty("name", material.toString());
      return json;
    }
  }

  /** Constant material */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  private static class First extends RandomMaterial implements Predicate<IMaterial> {
    private static final ResourceLocation ID = TConstruct.getResource("first");

    /** Stat type for random materials */
    private final MaterialStatsId statType;

    private MaterialId material;

    /** Creates an instance from JSON */
    public static First fromJson(JsonObject json) {
      MaterialStatsId statType = new MaterialStatsId(JsonHelper.getResourceLocation(json, "stat_type"));
      return new First(statType);
    }

    @Override
    public boolean test(IMaterial material) {
      return MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), statType).isPresent();
    }

    @Override
    public MaterialVariantId getMaterial(Random random) {
      if (material == null) {
        material = MaterialRegistry.getInstance().getVisibleMaterials().stream()
                                   .filter(this)
                                   .findFirst()
                                   .orElse(IMaterial.UNKNOWN)
                                   .getIdentifier();
      }
      return material;
    }

    @Override
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("type", ID.toString());
      json.addProperty("stat_type", statType.toString());
      return json;
    }
  }

  /** Produces a random material from a material tier */
  @RequiredArgsConstructor
  private static class RandomInTier extends RandomMaterial implements Predicate<IMaterial> {
    private static final ResourceLocation ID = TConstruct.getResource("random");

    /** Stat type for random materials */
    private final MaterialStatsId statType;
    /** Minimum material tier */
    private final int minTier;
    /** Maximum material tier */
    private final int maxTier;
    /** If true, hidden materials are allowed */
    private final boolean allowHidden;
    /** Material tag condition */
    @Nullable
    private final TagKey<IMaterial> tag;

    /** Cached list of material choices, automatically deleted when loot tables reload */
    private List<MaterialId> materialChoices;

    /** Creates an instance from JSON */
    public static RandomInTier fromJson(JsonObject json) {
      MaterialStatsId statType = new MaterialStatsId(JsonHelper.getResourceLocation(json, "stat_type"));
      int minTier = GsonHelper.getAsInt(json, "min_tier", 0);
      int maxTier = GsonHelper.getAsInt(json, "max_tier", Integer.MAX_VALUE);
      boolean allowHidden = GsonHelper.getAsBoolean(json, "allow_hidden", false);
      TagKey<IMaterial> tag  = null;
      if (json.has("tag")) {
        tag = MaterialManager.getTag(JsonHelper.getResourceLocation(json, "tag"));
      }
      return new RandomInTier(statType, minTier, maxTier, allowHidden, tag);
    }

    /** Checks if a material is valid */
    @Override
    public boolean test(IMaterial material) {
      int tier = material.getTier();
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialId id = material.getIdentifier();
      return tier >= minTier && tier <= maxTier && (allowHidden || !material.isHidden())
             && (tag == null || registry.isInTag(id, tag))
             && registry.getMaterialStats(id, statType).isPresent();
    }

    @Override
    public MaterialId getMaterial(Random random) {
      if (materialChoices == null) {
        materialChoices = MaterialRegistry.getInstance()
                                          .getAllMaterials()
                                          .stream()
                                          .filter(this)
                                          .map(IMaterial::getIdentifier)
                                          .collect(Collectors.toList());
        if (materialChoices.isEmpty()) {
          TConstruct.LOG.warn("Random material found no options for statType={}, minTier={}, maxTier={}, allowHidden={}", statType, minTier, maxTier, allowHidden);
        }
      }
      if (materialChoices.isEmpty()) {
        return IMaterial.UNKNOWN_ID;
      }
      return materialChoices.get(random.nextInt(materialChoices.size()));
    }

    @Override
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("type", ID.toString());
      json.addProperty("stat_type", statType.toString());
      if (minTier > 0) {
        json.addProperty("min_tier", minTier);
      }
      if (maxTier < Integer.MAX_VALUE) {
        json.addProperty("max_tier", maxTier);
      }
      if (allowHidden) {
        json.addProperty("allow_hidden", true);
      }
      if (tag != null) {
        json.addProperty("tag", tag.location().toString());
      }
      return json;
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class RandomBuilder {
    /** Stat type for random materials */
    private final MaterialStatsId statType;
    /** Minimum material tier */
    private int minTier = 0;
    /** Maximum material tier */
    private int maxTier = Integer.MAX_VALUE;
    private boolean allowHidden = false;
    /** Material tag condition */
    @Nullable @Setter @Accessors(fluent = true)
    private TagKey<IMaterial> tag;

    /** Sets the required tier */
    public RandomBuilder tier(int tier) {
      minTier = tier;
      maxTier = tier;
      return this;
    }

    /** Sets the required tier to a range between min and max, inclusive */
    public RandomBuilder tier(int min, int max) {
      if (min > max) {
        throw new IllegalArgumentException("Min must be smaller than or equal to max");
      }
      minTier = min;
      maxTier = max;
      return this;
    }

    /** Makes hidden materials allowed */
    public RandomBuilder allowHidden() {
      this.allowHidden = true;
      return this;
    }

    /** Builds the instance */
    public RandomMaterial build() {
      return new RandomInTier(statType, minTier, maxTier, allowHidden, tag);
    }
  }
}
