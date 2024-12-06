package slimeknights.tconstruct.library.materials;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface IMaterialRegistry {
  /* Materials */

  /**
   * Resolves any redirects in the given material ID. Should be called internally by all methods in the registry, but exposed for the sake of using outside
   * @param id  Material ID
   * @return  Redirected ID, or original ID if no cange
   */
  default MaterialId resolve(MaterialId id) {
    return id;
  }

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


  /* Tags */

  /**
   * Checks if the given modifier is in the given tag
   * @return  True if the modifier is in the tag
   */
  boolean isInTag(MaterialId id, TagKey<IMaterial> tag);

  /**
   * Gets all values contained in the given tag
   * @param tag  Tag instance
   * @return  Contained values
   */
  List<IMaterial> getTagValues(TagKey<IMaterial> tag);


  /* Stats */

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

  /** Gets the loader for material stat types */
  Loadable<MaterialStatType<?>> getStatTypeLoader();

  /** Gets a lit of all material stat IDs */
  default Collection<ResourceLocation> getAllStatTypeIds() {
    return Collections.emptyList();
  }

  /**
   * Gets the stat type for the given stat ID, which handles the default instance, serializing, and deserializing.
   * @param statsId  Stat ID
   * @return  Stat type, or null if nothing is registered with the ID
   */
  @Nullable
  <T extends IMaterialStats> MaterialStatType<T> getStatType(MaterialStatsId statsId);

  /**
   * Gets the default stats for the given stats ID
   * @param statsId  Stats type
   * @param <T>      Stats class type
   * @return  Default stats for the type
   */
  @Nullable
  default <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    MaterialStatType<T> type = getStatType(statsId);
    return type != null ? type.getDefaultStats() : null;
  }

  /**
   * Gets the loadable for the given stat type
   * @param statsId  Stats type
   * @param <T>      Stats class type
   * @return  Loadable instance, or null if the stat type is not registered
   */
  @Nullable
  default <T extends IMaterialStats> RecordLoadable<T> getStatLoadable(MaterialStatsId statsId) {
    MaterialStatType<T> type = getStatType(statsId);
    return type != null ? type.getLoadable() : null;
  }

  /**
   * Checks if the given material stats ID can repair, this is equivelent to an instanceof check on a stat type for {@link slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats}
   * @param statsId  Stats ID
   * @return  True if it can repair
   */
  default boolean canRepair(MaterialStatsId statsId) {
    MaterialStatType<?> type = getStatType(statsId);
    return type != null && type.canRepair();
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
   * @param type  Stat type
   */
  void registerStatType(MaterialStatType<?> type);

  /**
   * This method serves three purposes:
   * <ol>
   * <li>it makes the game aware of a new material stat type</li>
   * <li>it registers the default stats (=fallback) for the given type</li>
   * <li>it adds a trait "category" to the stat type to make registering traits easier</li>
   * </ol><br/>
   * For stats to be usable they need to be registered, otherwise they can't be loaded.
   * The default stats are used when something tries to create something out of material with these stats,
   * but for some reason the material does not have the given stats.<br/>
   * e.g. building an arrow with stone fletchings (stone cannot be used for fletchings)
   * <p>
   * All material stats for the same materialStatType <em>must</em> have the same class as its default after it's registered.
   *
   * @param type           Stat type
   * @param traitFallback  Fallback to use if the traits are not set
   */
  default void registerStatType(MaterialStatType<?> type, @Nullable MaterialStatsId traitFallback) {
    registerStatType(type);
  }


  /* Traits */

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
