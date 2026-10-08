package slimeknights.tconstruct.library.materials;

import slimeknights.tconstruct.library.materials.definition.MaterialId;

/** Common interface between {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsId} and {@link slimeknights.tconstruct.library.tools.part.IMaterialItem} */
public interface IMaterialUser {
  /** Checks if the given material can be used by this object. */
  boolean canUseMaterial(MaterialId material);
}
