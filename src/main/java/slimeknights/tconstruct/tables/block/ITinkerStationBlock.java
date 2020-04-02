package slimeknights.tconstruct.tables.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Blocks implementing this interface is part of the tinker station GUI system
 */
public interface ITinkerStationBlock {

  /**
   * Used for sorting the tabs in the UI. Tabs are sorted from low to high.
   * Duplicate entries will be treated as the same and their blocks will be ignored.
   * <p>
   * Values used:
   * 10 - Stencil Table
   * 15 - Pattern Chest
   * 20 - Part Builder
   * 25 - Tool Station/Forge
   * 50 - Crafting Station
   */
  int getGuiNumber(BlockState state);

  /**
   * Open the gui of this block for the given player. Same as BlockInventory.openGui, coincidentally! ;)
   */
  boolean openGui(PlayerEntity player, World world, BlockPos pos);

  boolean isMaster();
}
