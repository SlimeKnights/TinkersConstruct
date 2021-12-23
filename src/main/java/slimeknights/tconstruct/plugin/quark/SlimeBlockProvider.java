package slimeknights.tconstruct.plugin.quark;

import slimeknights.tconstruct.plugin.quark.block.BlockSlimeQuark;
import slimeknights.tconstruct.shared.block.BlockSlime;

public class SlimeBlockProvider {
  /**
   * Provider for the extended slime block class.
   * Separate class needed to prevent class loading exceptions on constructing QuarkPlugin
   */
  public static BlockSlime get() {
    return new BlockSlimeQuark();
  }
}
