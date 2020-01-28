package slimeknights.tconstruct.library;

import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.traits.MaterialTraitsManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds all materials and the extra information registered for them (stat classes).
 * Materials are reset on every world load/join. Registered extra stuff is not.
 *
 * For the Server, materials are loaded on server start/reload from the data packs.
 * For the Client, materials are synced from the server on server join.
 */
class MaterialRegistryImpl {

  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;

  /**
   * Used for the defaults and for existence/class checks.
   * Basically all existing stat types need to be in this map.. or they don't exist
   */
  private Map<MaterialStatsId, IMaterialStats> materialStatDefaults = new HashMap<>();

  protected MaterialRegistryImpl(MaterialManager materialManager, MaterialStatsManager materialStatsManager, MaterialTraitsManager materialTraitsManager) {
    this.materialManager = materialManager;
    this.materialStatsManager = materialStatsManager;
    this.materialTraitsManager = materialTraitsManager;
  }

  public IMaterial getMaterial(MaterialId id) {
    return materialManager.getMaterial(id).orElse(IMaterial.UNKNOWN);
  }

  public Collection<IMaterial> getMaterials() {
    return materialManager.getAllMaterials();
  }

  public <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    return materialStatsManager.getStats(materialId, statsId);
  }

  public Collection<BaseMaterialStats> getAllStats(MaterialId materialId) {
    return materialStatsManager.getAllStats(materialId);
  }

  public <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    //noinspection unchecked
    return (T) materialStatDefaults.get(statsId);
  }

  /**
   * This method serves two purposes:
   * <ol>
   * <li>it makes the game aware of a new material stat type</li>
   * <li>it registers the default stats (=fallback) for the given type</li>
   * </ol><br/>
   * For stats to be usable they need to be registered, otherwise they can't be loaded.
   * The default stats are used when something tries to create something out of material with these stats,
   * but for some reason the material does not have the given stats.<br/>
   * e.g. building an arrow with stone fletchings (stone cannot be used for fletchings)
   *
   * All material stats for the same materialStatType <em>must</em> have the same class as its default after it's registered.
   *
   * @param type
   * @param defaultStats
   */
  public <T extends BaseMaterialStats> void registerMaterial(MaterialStatsId type, T defaultStats, Class<T> clazz) {
    if(materialStatDefaults.containsKey(type)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(type);
    }
    // todo: implement check if class is compatible with the requirements for a network syncable stats class
    materialStatsManager.registerMaterialStat(type, clazz);
    materialStatDefaults.put(type, defaultStats);
  }
}
