package slimeknights.tconstruct.library;

import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Optional;

public final class MaterialRegistry {

  private static MaterialRegistryImpl INSTANCE;

  void init(MaterialRegistryImpl materialRegistry) {
    INSTANCE = materialRegistry;
  }

  public static IMaterial getMaterial(MaterialId id) {
    return INSTANCE.getMaterial(id);
  }

  public static <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    return INSTANCE.getMaterialStats(materialId, statsId);
  }

  public <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    return INSTANCE.getDefaultStats(statsId);
  }
}
