package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;

import javax.annotation.Nonnull;

/**
 * An item that breaks multiple blocks at once
 */
public interface IAoeTool {

  /**
   * Gets a list of blocks that the AOE tool can affect.
   *
   * @param stack the tool stack
   * @param world the current world
   * @param player the player using the tool
   * @param origin the origin block spot to start from
   * @return A list of BlockPoses that the AOE tool can affect.
   */
  default ImmutableList<BlockPos> getAOEBlocks(@Nonnull ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return AoeToolInteractionUtil.calculateAOEBlocks(stack, world, player, origin, 1, 1, 1);
  }
}
