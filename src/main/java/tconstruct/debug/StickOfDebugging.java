package tconstruct.debug;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import mantle.items.iface.IDebugItem;

public class StickOfDebugging extends Item implements IDebugItem {

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
                           EnumFacing side, float hitX, float hitY, float hitZ) {
    return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
  }

  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    return super.getDigSpeed(itemstack, state);
  }
}
