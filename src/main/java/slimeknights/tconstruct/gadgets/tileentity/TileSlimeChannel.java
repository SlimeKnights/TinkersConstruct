package slimeknights.tconstruct.gadgets.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.gadgets.block.BlockSlimeChannel;

/**
 * This tile entity is simply an extra data 
 */
public class TileSlimeChannel extends TileEntity {
  
  // don't delete the TE if the state changes
  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != oldState.getBlock();
  }
  
  public void setSide(EnumFacing side, boolean update) {
   getTileData().setInteger("side", side.getIndex());
   if(update) {
     worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockSlimeChannel.SIDE, side));
   }
  }
  
  public void setSide(EnumFacing side) {
    setSide(side, true);
  }

  @Nonnull
  public EnumFacing getSide() {
    return EnumFacing.VALUES[getTileData().getInteger("side")];
  }
  
  public void setFacing(EnumFacing facing, boolean update) {
    getTileData().setInteger("facing", facing.getHorizontalIndex());
    if(update) {
      worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockSlimeChannel.FACING, facing));
    }
  }
  
  public void setFacing(EnumFacing facing) {
    setFacing(facing, true);
  }

  @Nonnull
  public EnumFacing getFacing() {
    return EnumFacing.HORIZONTALS[getTileData().getInteger("facing")];
  }
  
  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    // just a safety check, should ever not have a world by this point
    if(hasWorldObj()) {
      worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockSlimeChannel.SIDE, getSide())
                                                             .withProperty(BlockSlimeChannel.FACING, getFacing()), 2);
    }
  }
}
