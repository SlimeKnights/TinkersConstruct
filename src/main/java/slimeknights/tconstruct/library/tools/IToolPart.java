package slimeknights.tconstruct.library.tools;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * Any Class that's used as a tool part needs to implement this.
 */
public interface IToolPart extends IMaterialItem {
  /**
   * Returns the cost to craft the tool in 0.5 steps.<br>
   * 1 = 1 shard<br>
   * 2 = 1 ingot<br>
   * 3 = 1 shard + 1 ingot<br>
   * etc.
   */
  int getCost();

  /**
   * Retruns true if the material can be used for this toolpart
   */
  boolean canUseMaterial(Material mat);

  boolean hasUseForStat(String stat);
}
