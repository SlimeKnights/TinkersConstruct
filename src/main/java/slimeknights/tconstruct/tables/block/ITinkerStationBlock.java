package slimeknights.tconstruct.tables.block;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Blocks implementing this interface is part of the tinker station GUI system
 */
public interface ITinkerStationBlock {
  /**
   * Open the gui of this block for the given player. Same as BlockInventory.openGui, coincidentally! ;)
   */
  boolean openGui(Player player, Level world, BlockPos pos);
}
