package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import slimeknights.tconstruct.TConstruct;

import java.util.List;

/**
 * Helper methods that are used when the tool interacts with the world or other things
 */
public class ToolInteractionUtil {

  /**
   * Returns true if the tool is effective for harvesting the given block.
   */
  public static boolean isToolEffectiveAgainstBlock(ItemStack stack, BlockState state) {
    return stack.getItem().getToolTypes(stack).stream()
      .anyMatch(toolType -> state.getBlock().isToolEffective(state, toolType));
  }

  /**
   * Attempts to shear a block using IForgeShearable logic
   * @param tool the tool stack
   * @param world the current world the block is in
   * @param player the player attempting to shear a block
   * @param pos the blockpos of the block
   * @return true if the block was successfully sheared
   */
  public static boolean shearBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos) {
    // only serverside since it creates entities
    if (world.isRemote) {
      return false;
    }

    BlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    if (block instanceof IForgeShearable) {
      IForgeShearable target = (IForgeShearable) block;

      if (target.isShearable(tool, world, pos)) {
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
        List<ItemStack> drops = target.onSheared(player, tool, world, pos, fortune);

        for (ItemStack stack : drops) {
          float f = 0.7F;
          double d = TConstruct.random.nextFloat() * f + (1.0F - f) * 0.5D;
          double d1 = TConstruct.random.nextFloat() * f + (1.0F - f) * 0.5D;
          double d2 = TConstruct.random.nextFloat() * f + (1.0F - f) * 0.5D;

          ItemEntity itemEntity = new ItemEntity(player.getEntityWorld(), pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, stack);

          itemEntity.setDefaultPickupDelay();

          world.addEntity(itemEntity);
        }

        tool.onBlockDestroyed(world, state, pos, player);

        world.removeBlock(pos, false);

        return true;
      }
    }

    return false;
  }
}
