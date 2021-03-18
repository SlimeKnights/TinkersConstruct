package slimeknights.tconstruct.tables.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Blocks implementing this interface is part of the tinker station GUI system
 */
public interface ITinkerStationBlock {
  /**
   * Open the gui of this block for the given player. Same as BlockInventory.openGui, coincidentally! ;)
   */
  boolean openGui(PlayerEntity player, World world, BlockPos pos);
}
