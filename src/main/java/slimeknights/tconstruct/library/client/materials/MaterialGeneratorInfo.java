package slimeknights.tconstruct.library.client.materials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.gson.ResourceLocationSerializer;
import slimeknights.mantle.data.loadable.common.GsonLoadable;
import slimeknights.mantle.data.loadable.field.LegacyField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.spritetransformer.IColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Set;

/**
 * Component of {@link MaterialRenderInfo} used during datagen and the generate part textures command to describe how to generate the material
 */
@RequiredArgsConstructor
public class MaterialGeneratorInfo {
  /** GSON adapter for generator deserializing. TODO: migrate ISpriteTransformer to loadables? */
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(MaterialStatsId.class, new ResourceLocationSerializer<>(MaterialStatsId::new, TConstruct.MOD_ID))
    .registerTypeHierarchyAdapter(ISpriteTransformer.class, ISpriteTransformer.SERIALIZER)
    .registerTypeHierarchyAdapter(IColorMapping.class, IColorMapping.SERIALIZER)
    .create();
  public static final RecordLoadable<MaterialGeneratorInfo> LOADABLE = RecordLoadable.create(
    new GsonLoadable<>(GSON, ISpriteTransformer.class).requiredField("transformer", g -> g.transformer),
    new LegacyField<>(MaterialStatsId.PARSER.set(0).requiredField("supported_stats", g -> g.supportedStats), "supportedStats"),
    new LegacyField<>(BooleanLoadable.INSTANCE.defaultField("ignore_material_stats", false, false, g -> g.ignoreMaterialStats), "ignoreMaterialStats"),
    BooleanLoadable.INSTANCE.defaultField("variant", false, false, g -> g.variant),
    MaterialGeneratorInfo::new);

  /** Transformer to update images */
  @Getter
  private final ISpriteTransformer transformer;
  /** List of stat types supported by this material */
  private final Set<MaterialStatsId> supportedStats;
  /** If true, this ignores the material stats when determining applicable stat types for the command. Only affects the command, not datagen */
  protected final boolean ignoreMaterialStats;
  /** If true, this tool will not generate variant textures. Used on ancient tools to skip textures that will never appear. */
  @Getter
  private final boolean variant;

  public MaterialGeneratorInfo(MaterialGeneratorInfo other) {
    this(other.transformer, other.supportedStats, other.ignoreMaterialStats, other.variant);
  }

  /** If true, this stat type is supported */
  public boolean supportStatType(MaterialStatsId statType) {
    return supportedStats.contains(statType);
  }
}
