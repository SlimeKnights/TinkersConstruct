package slimeknights.tconstruct.library.traits.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.traits.TraitId;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TraitMappingJson {

  // these should be MaterialIds, TraitIds and StatIds respectively, but are kept ResourceLocations to make deserialization easier
  private final ResourceLocation materialId;
  @SerializedName("default")
  private final List<ResourceLocation> defaultTraits;
  private final Map<ResourceLocation, List<ResourceLocation>> perStat;

  public TraitMappingJson(ResourceLocation materialId, List<ResourceLocation> defaultTraits, Map<ResourceLocation, List<ResourceLocation>> perStat) {
    this.materialId = materialId;
    this.defaultTraits = defaultTraits;
    this.perStat = perStat;
  }

  @Nullable
  public MaterialId getMaterialId() {
    return materialId != null ? new MaterialId(materialId) : null;
  }

  public List<TraitId> getDefaultTraits() {
    return transformTraitIds(defaultTraits);
  }

  public Map<MaterialStatsId, List<TraitId>> getPerStat() {
    if (perStat == null) {
      return Collections.emptyMap();
    }
    return perStat.entrySet().stream()
      .collect(Collectors.toMap(
        entry -> new MaterialStatsId(entry.getKey()),
        entry -> transformTraitIds(entry.getValue())
      ));
  }

  private List<TraitId> transformTraitIds(@Nullable List<ResourceLocation> ids) {
    if (ids == null) {
      return Collections.emptyList();
    }
    return ids.stream()
      .map(TraitId::new)
      .collect(Collectors.toList());
  }
}
