package slimeknights.tconstruct.library.tools.part;

import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {
  /**
   * Gets the stat type for the given item, limits which materials are supported
   * @return  Stat type for the given item
   */
  MaterialStatsId getStatType();

  @Override
  default boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance().getMaterialStats(material, this.getStatType()).isPresent();
  }
}
