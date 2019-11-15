package slimeknights.tconstruct.library;

import com.google.common.collect.ImmutableMap;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.MaterialManager;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;

import java.util.Map;

public final class MaterialRegistry {

  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;

  /**
   * Used for the defaults and for existance/class checks.
   */
  private Map<MaterialStatType, IMaterialStats> materialStatDefaults = ImmutableMap.of();

  protected MaterialRegistry(MaterialManager materialManager, MaterialStatsManager materialStatsManager) {
    this.materialManager = materialManager;
    this.materialStatsManager = materialStatsManager;
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
  public void registerMaterial(MaterialStatType type, IMaterialStats defaultStats) {
    if(materialStatDefaults.containsKey(type)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(type.getIdentifier());
    }
    materialStatDefaults.put(type, defaultStats);
  }
}
