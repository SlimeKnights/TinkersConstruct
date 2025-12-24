package slimeknights.tconstruct.library.client.data.material;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.data.spritetransformer.IColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.client.materials.MaterialGeneratorInfo;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Logic for getting lists of materials for generating sprites, for use in {@link MaterialPartTextureGenerator}
 */
public abstract class AbstractMaterialSpriteProvider {
  /** All materials to generate */
  private final Map<ResourceLocation, MaterialSpriteInfoBuilder> materialBuilders = new HashMap<>();
  /** List of built materials */
  private Map<ResourceLocation, MaterialSpriteInfo> builtMaterials = null;

  /** Gets the name of this material list */
  public abstract String getName();

  /** Adds all materials to the list */
  protected abstract void addAllMaterials();

  /** Gets a list of all materials for this provider */
  public Map<ResourceLocation, MaterialSpriteInfo> getMaterials() {
    if (builtMaterials == null) {
      addAllMaterials();
      builtMaterials = materialBuilders.values().stream().map(MaterialSpriteInfoBuilder::build).collect(Collectors.toMap(MaterialSpriteInfo::getTexture, Function.identity()));
      materialBuilders.clear();
    }
    return builtMaterials;
  }

  /** Gets the info for the given material */
  @Nullable
  public MaterialSpriteInfo getMaterialInfo(ResourceLocation name) {
    return getMaterials().get(name);
  }

  /** Adds a new texture to the data generator */
  protected MaterialSpriteInfoBuilder buildMaterial(ResourceLocation name) {
    if (builtMaterials != null) {
      throw new IllegalStateException("Attempted to add a material when materials already built");
    }
    return materialBuilders.computeIfAbsent(name, MaterialSpriteInfoBuilder::new);
  }

  /** Adds a new material to the data generator */
  protected MaterialSpriteInfoBuilder buildMaterial(MaterialId name) {
    return buildMaterial((ResourceLocation)name);
  }

  /** Adds a new material variant to the data generator */
  protected MaterialSpriteInfoBuilder buildMaterial(MaterialVariantId name) {
    return buildMaterial(name.getLocation('_'));
  }

  /** Data for material rendering */
  public static class MaterialSpriteInfo extends MaterialGeneratorInfo {
    /** Material texture name for the material */
    @Getter
    private transient final ResourceLocation texture;
    /** List of fallbacks, first present one will be the base for building. If none exist, uses the default base */
    @Getter
    private transient final String[] fallbacks;

    public MaterialSpriteInfo(ResourceLocation texture, String[] fallbacks, MaterialGeneratorInfo generatorJson) {
      super(generatorJson);
      this.texture = texture;
      this.fallbacks = fallbacks;
    }

    public MaterialSpriteInfo(ResourceLocation texture, String[] fallbacks, ISpriteTransformer transformer, Set<MaterialStatsId> supportedStats, boolean variant) {
      super(transformer, supportedStats, false, variant);
      this.texture = texture;
      this.fallbacks = fallbacks;
    }

    @Override
    public boolean supportStatType(MaterialStatsId statType) {
      if (super.supportStatType(statType)) {
        return true;
      }
      // if material registry is loaded and we are not ignoring it, allow checking that
      if (!ignoreMaterialStats && MaterialRegistry.isFullyLoaded()) {
        return  MaterialRegistry.getInstance().getMaterialStats(new MaterialId(texture), statType).isPresent();
      }
      return false;
    }
  }

  /** Builder for material sprite info */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @CanIgnoreReturnValue
  @Accessors(fluent = true)
  protected static class MaterialSpriteInfoBuilder {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final ResourceLocation texture;
    private String[] fallbacks = EMPTY_STRING_ARRAY;
    private final ImmutableSet.Builder<MaterialStatsId> statTypes = ImmutableSet.builder();

    /** Transformer to modify textures */
    @Setter
    @Nullable
    private ISpriteTransformer transformer;
    @Setter
    private boolean variant = false;

    /** Sets the fallbacks */
    public MaterialSpriteInfoBuilder fallbacks(String... fallbacks) {
      this.fallbacks = fallbacks;
      return this;
    }

    /** Sets the transformer to a color mapping transform */
    public MaterialSpriteInfoBuilder colorMapper(IColorMapping mapping) {
      return transformer(new RecolorSpriteTransformer(mapping));
    }

    /** Marks this as a variant texture, which is skipped by some sprites such as ancient tools (which can never obtain them) */
    public MaterialSpriteInfoBuilder variant() {
      return variant(true);
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(MaterialStatsId statsId) {
      statTypes.add(statsId);
      return this;
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(MaterialStatsId... statsId) {
      statTypes.add(statsId);
      return this;
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(IMaterialStats... stats) {
      for (IMaterialStats stat : stats) {
        statTypes.add(stat.getIdentifier());
      }
      return this;
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(MaterialStatType<?>... stats) {
      for (MaterialStatType<?> stat : stats) {
        statTypes.add(stat.getId());
      }
      return this;
    }

    /** Adds a stat type as supported */
    public MaterialSpriteInfoBuilder statType(List<? extends MaterialStatType<?>> stats) {
      for (MaterialStatType<?> stat : stats) {
        statTypes.add(stat.getId());
      }
      return this;
    }

    /** Adds repair kits */
    public MaterialSpriteInfoBuilder repairKit() {
      return statType(StatlessMaterialStats.REPAIR_KIT.getIdentifier());
    }

    /** Adds stat types for melee and harvest tools - head, handle and extra */
    public MaterialSpriteInfoBuilder meleeHarvest() {
      statType(HeadMaterialStats.ID);
      statType(HandleMaterialStats.ID);
      statType(StatlessMaterialStats.BINDING.getIdentifier());
      repairKit();
      return this;
    }

    /** Adds stat types for ranged tools - includes limb and grip */
    public MaterialSpriteInfoBuilder ranged() {
      statType(LimbMaterialStats.ID);
      statType(GripMaterialStats.ID);
      repairKit();
      return this;
    }

    /** Adds stat types for maille */
    public MaterialSpriteInfoBuilder maille() {
      statType(StatlessMaterialStats.MAILLE.getIdentifier());
      statType(TinkerPartSpriteProvider.ARMOR_MAILLE);
      return this;
    }

    /** Adds stat types for maille */
    public MaterialSpriteInfoBuilder cuirass() {
      statType(StatlessMaterialStats.CUIRASS.getIdentifier());
      statType(TinkerPartSpriteProvider.ARMOR_CUIRASS);
      repairKit(); // used by traveler's gear
      return this;
    }

    /** Adds all plating stat types */
    public MaterialSpriteInfoBuilder plating() {
      statType(TinkerPartSpriteProvider.ARMOR_PLATING);
      for (MaterialStatType<?> type : PlatingMaterialStats.TYPES) {
        statType(type.getId());
      }
      repairKit();
      return this;
    }

    /** Adds stat types for armor, all plating plus maille */
    public MaterialSpriteInfoBuilder armor() {
      plating();
      maille();
      return this;
    }

    /** Makes this work as the wood part for a shield */
    public MaterialSpriteInfoBuilder shieldCore() {
      statType(StatlessMaterialStats.SHIELD_CORE);
      repairKit(); // used by traveler's shields
      return this;
    }

    /** Makes this work as the head for an arrow or shuriken */
    public MaterialSpriteInfoBuilder arrowHead() {
      statType(StatlessMaterialStats.ARROW_HEAD);
      return this;
    }

    /** Makes this work as the shaft for an arrow */
    public MaterialSpriteInfoBuilder arrowShaft() {
      statType(StatlessMaterialStats.ARROW_SHAFT);
      return this;
    }

    /** Makes this work as the shaft for an arrow */
    public MaterialSpriteInfoBuilder fletching() {
      statType(StatlessMaterialStats.FLETCHING);
      return this;
    }

    /** Builds a material sprite info */
    @CheckReturnValue
    private MaterialSpriteInfo build() {
      if (transformer == null) {
        throw new IllegalStateException("Material must have a transformer for a sprite provider");
      }
      Set<MaterialStatsId> supportedStats = this.statTypes.build();
      if (supportedStats.isEmpty()) {
        throw new IllegalStateException("Material must support at least one stat type");
      }
      return new MaterialSpriteInfo(texture, fallbacks, transformer, supportedStats, variant);
    }
  }
}
