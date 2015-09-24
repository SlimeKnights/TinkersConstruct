package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * An item that breaks multiple blocks at once
 */
public interface IAoeTool {
  ImmutableList<BlockPos> getExtraBlocksToBreak(ItemStack stack, World world, EntityPlayer player, BlockPos origin);
}
