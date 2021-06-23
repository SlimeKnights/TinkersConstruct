package slimeknights.tconstruct.fixture;

import lombok.AllArgsConstructor;
import slimeknights.tconstruct.library.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.traits.MaterialTraits;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MaterialRegistryFixture implements IMaterialRegistry {

  private final Map<MaterialId, IMaterial> materials;
  private final Map<MaterialId, Map<MaterialStatsId, IMaterialStats>> stats;
  private final Map<MaterialStatsId, IMaterialStats> defaultStats;
  private final Map<MaterialId,MaterialTraits> traits;

  @Override
  public IMaterial getMaterial(MaterialId id) {
    return materials.computeIfAbsent(id,
      materialId -> {throw new IllegalArgumentException("No material with id " + materialId + " set up");});
  }

  @Override
  public Collection<IMaterial> getVisibleMaterials() {
    return materials.values().stream().filter(mat -> !mat.isHidden()).collect(Collectors.toList());
  }

  @Override
  public Collection<IMaterial> getAllMaterials() {
    return materials.values();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    return Optional.ofNullable((T)stats.getOrDefault(materialId, Collections.emptyMap()).get(statsId));
  }

  @Override
  public Collection<IMaterialStats> getAllStats(MaterialId materialId) {
    return stats.getOrDefault(materialId, Collections.emptyMap()).values();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    return Objects.requireNonNull((T)defaultStats.get(statsId));
  }

  @Override
  public <T extends IMaterialStats> void registerStatType(T defaultStats, Class<T> clazz) {
    throw new UnsupportedOperationException("No registration possible in test mock");
  }

  @Override
  public List<ModifierEntry> getDefaultTraits(MaterialId materialId) {
    MaterialTraits traits = this.traits.get(materialId);
    return traits == null ? Collections.emptyList() : traits.getDefaultTraits();
  }

  @Override
  public boolean hasUniqueTraits(MaterialId materialId, MaterialStatsId statsId) {
    MaterialTraits traits = this.traits.get(materialId);
    return traits != null && traits.hasUniqueTraits(statsId);
  }

  @Override
  public List<ModifierEntry> getTraits(MaterialId materialId, MaterialStatsId statsId) {
    MaterialTraits traits = this.traits.get(materialId);
    return traits == null ? Collections.emptyList() : traits.getTraits(statsId);
  }
}
