package slimeknights.tconstruct.library.traits.json;

import com.google.gson.annotations.SerializedName;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.traits.TraitId;

import org.jetbrains.annotations.Nullable;
import net.minecraft.util.Identifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TraitMappingJson {

  // these should be MaterialIds, TraitIds and StatIds respectively, but are kept ResourceLocations to make deserialization easier
  private final Identifier materialId;
  @SerializedName("default")
  private final List<Identifier> defaultTraits;
  private final Map<Identifier, List<Identifier>> perStat;

  public TraitMappingJson(Identifier materialId, List<Identifier> defaultTraits, Map<Identifier, List<Identifier>> perStat) {
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

  private List<TraitId> transformTraitIds(@Nullable List<Identifier> ids) {
    if (ids == null) {
      return Collections.emptyList();
    }
    return ids.stream()
      .map(TraitId::new)
      .collect(Collectors.toList());
  }
}
