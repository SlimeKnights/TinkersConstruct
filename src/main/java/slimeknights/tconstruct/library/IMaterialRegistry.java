package slimeknights.tconstruct.library;

import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IMaterialRegistry {
  /**
   * Gets a material by ID
   * @param id  Material ID
   * @return  Material, or IMaterial.UNKNOWN if missing
   */
  IMaterial getMaterial(MaterialId id);

  /**
   * Gets all currently registered materials
   * @return  Collection of all materials
   */
  Collection<IMaterial> getVisibleMaterials();

  /**
   * Gets all currently registered materials
   * @return  Collection of all materials
   */
  Collection<IMaterial> getAllMaterials();

  /**
   * Gets the material stats for the given material and type
   * @param materialId  Material ID
   * @param statsId     Stats type
   * @param <T>         Stat class type
   * @return  Material stats if present
   */
  <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId);

  /**
   * Gets all stats for the given material
   * @param materialId  Material ID
   * @return  Collection of all stats
   */
  Collection<IMaterialStats> getAllStats(MaterialId materialId);

  /**
   * Gets the default stats for the given stats ID
   * @param statsId  Stats type
   * @param <T>      Stats class type
   * @return  Default stats for the type
   */
  @Nullable
  <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId);

  /**
   * Checks if the given material stats ID can repair, this is equivelent to an instanceof check on a stat type for {@link slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats}
   * @param statsId  Stats ID
   * @return  True if it can repair
   */
  default boolean canRepair(MaterialStatsId statsId) {
    return false;
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
   * <p>
   * All material stats for the same materialStatType <em>must</em> have the same class as its default after it's registered.
   *
   * @param defaultStats  Default stats instance
   * @param clazz         Stat type class
   */
  <T extends IMaterialStats> void registerStatType(T defaultStats, Class<T> clazz);

  /**
   * Gets the default material traits for the given material
   * @param materialId  Material ID
   * @return  Material traits
   */
  List<ModifierEntry> getDefaultTraits(MaterialId materialId);

  /**
   * Checks if the given material and stat pair have unique traits
   * @param materialId  Material ID
   * @param statsId     Stats type
   * @return  If the traits for this stat type are unique
   */
  boolean hasUniqueTraits(MaterialId materialId, MaterialStatsId statsId);

  /**
   * Gets the material traits for the given material and type
   * @param materialId  Material ID
   * @param statsId     Stats type
   * @return  Material traits
   */
  List<ModifierEntry> getTraits(MaterialId materialId, MaterialStatsId statsId);
}
