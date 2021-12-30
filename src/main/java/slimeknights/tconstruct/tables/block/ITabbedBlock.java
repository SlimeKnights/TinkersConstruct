package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Blocks implementing this interface are part of the tinker table tab GUI system
 */
public interface ITabbedBlock {
  /**
   * Open the gui of this block for the given player. Same as {@link slimeknights.mantle.block.InventoryBlock#openGui(Player, Level, BlockPos)} coincidentally
   */
  @SuppressWarnings("UnusedReturnValue")
  boolean openGui(Player player, Level world, BlockPos pos);
}
