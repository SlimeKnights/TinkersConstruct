package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * An item that breaks multiple blocks at once
 */
public interface IAoeTool {

  /** returns the blocks affected by the tool */
  ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, EntityPlayer player, BlockPos origin);

  /**
   * returns wether or not the tools AOE is breaking blocks
   * Mostly used for the extra blockbreak rendering
   */
  boolean isAoeHarvestTool();
}
