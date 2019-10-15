package slimeknights.tconstruct.gadgets.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileWoodenHopper extends TileEntityHopper {

  public TileWoodenHopper() {
    this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
  }

  @Override
  public void setTransferCooldown(int ticks) {
    super.setTransferCooldown(ticks*2);
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    // has to use the vanilla method. We could also check if only the rotation differs, but we only have 1 property
    // so it should be sufficient to only use the block itself
    return oldState.getBlock() != newSate.getBlock();
  }
}
