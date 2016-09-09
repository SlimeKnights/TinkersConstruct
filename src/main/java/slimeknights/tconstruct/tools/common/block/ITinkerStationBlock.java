package slimeknights.tconstruct.tools.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Blocks implementing this interface are part of the tinker station GUI system
 */
public interface ITinkerStationBlock {

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

  /**
   * Open the gui of this block for the given player. Same as BlockInventory.openGui, coincidentally! ;)
   */
  boolean openGui(EntityPlayer player, World world, BlockPos pos);
}
