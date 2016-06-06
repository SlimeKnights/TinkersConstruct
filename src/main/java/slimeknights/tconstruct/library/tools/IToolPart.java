package slimeknights.tconstruct.library.tools;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {
  /**
   * Returns the cost to craft the tool. Values match the ingot values<br>
   * 72 = 1 shard<br>
   * 144 = 1 ingot<br>
   * etc.<br>
   * Check the Material class for values
   */
  int getCost();

  /**
   * Retruns true if the material can be used for this toolpart
   */
  boolean canUseMaterial(Material mat);

  boolean hasUseForStat(String stat);
}
