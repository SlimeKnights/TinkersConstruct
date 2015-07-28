package tconstruct.tools.block;

import net.minecraft.block.state.IBlockState;

/**
 * Blocks implementing this interface are part of the tinker station GUI system
 */
public interface ITinkerStationBlock {
  /** At least 1 master has to be present for a tinker station. Usually the crafting station. */
  boolean isMaster(IBlockState state);

  /**
   * Used for sorting the tabs in the UI. Tabs are sorted from low to high.
   * Duplicate entries will be treated as the same and their blocks will be ignored.
   *
   * Values used:
   * 10 - Stencil Table
   * 15 - Pattern Chest
   * 20 - Part Builder
   * 25 - Tool Station/Forge
   * 50 - Crafting Station
   */
  int getGuiNumber(IBlockState state);
}
