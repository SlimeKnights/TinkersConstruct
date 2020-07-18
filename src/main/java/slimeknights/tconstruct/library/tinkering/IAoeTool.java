package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * An item that breaks multiple blocks at once
 */
public interface IAoeTool {

  /** returns the blocks affected by the tool */
  ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, PlayerEntity player, BlockPos origin);

  /**
   * returns whether or not the tools AOE is breaking blocks
   * Mostly used for the extra block break rendering
   */
  boolean isAoeHarvestTool();
}
