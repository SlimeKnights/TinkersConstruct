package slimeknights.tconstruct.library;

import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Collection;
import java.util.Optional;

public interface IMaterialRegistry {

  IMaterial getMaterial(MaterialId id);

  Collection<IMaterial> getMaterials();

  <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId);

  Collection<IMaterialStats> getAllStats(MaterialId materialId);

  <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId);

  <T extends IMaterialStats> void registerMaterial(T defaultStats, Class<T> clazz);
}
