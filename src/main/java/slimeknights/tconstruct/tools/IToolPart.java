package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {

  /**
   * Workaround for dual-materials like crossbow-bolts.
   * E.g. Obsidian is not an "acceptable" material because those are only shaft materials
   * but we still need to generate the texture for it.
   */
  default boolean canUseMaterialForRendering(Material mat) {
    return canUseMaterial(mat);
  }

  boolean hasUseForStat(MaterialStatsId stat);
}
