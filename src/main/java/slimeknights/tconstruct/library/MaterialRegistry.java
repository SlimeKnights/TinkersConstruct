package slimeknights.tconstruct.library;

import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public final class MaterialRegistry {

  private static MaterialRegistryImpl INSTANCE;

  void init(MaterialRegistryImpl materialRegistry) {
    INSTANCE = materialRegistry;
  }

  public static IMaterial getMaterial(MaterialId id) {
    return INSTANCE.getMaterial(id);
  }

  public static <T extends IMaterialStats> T getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    //noinspection unchecked
    return (T)INSTANCE.getMaterialStats(materialId, statsId);
  }
}
