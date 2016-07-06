package slimeknights.tconstruct.gadgets.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This tile entity is simply an extra data 
 */
public class TileSlimeChannel extends TileEntity {
  
  public static final String SIDE_TAG = "side";
  public static final String FACING_TAG = "facing";
  
  // don't delete the TE if the state changes
  // we want to keep our side and facing data if it becomes powered
  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != oldState.getBlock();
  }
  
  public void setSide(EnumFacing side) {
    getTileData().setInteger(SIDE_TAG, side.getIndex());
  }

  @Nonnull
  public EnumFacing getSide() {
    int side = getTileData().getInteger(SIDE_TAG);
    // no indexOutOfBounds please
    if(side > 5 || side < 0) {
      side = 0;
    }
    return EnumFacing.VALUES[side];
  }
  
  public void setFacing(EnumFacing facing) {
    getTileData().setInteger(FACING_TAG, facing.getHorizontalIndex());
  }

  @Nonnull
  public EnumFacing getFacing() {
    int facing = getTileData().getInteger(FACING_TAG);
    // no indexOutOfBounds please
    if(facing > 3 || facing < 0) {
      facing = 0;
    }
    return EnumFacing.HORIZONTALS[facing];
  }
  
  /* Client sync stuff */
  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    // note that this sends all of the tile data. you should change this if you use additional tile data
    NBTTagCompound tag = getTileData().copy();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }
  
  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    NBTTagCompound tag = pkt.getNbtCompound();
    getTileData().setInteger(SIDE_TAG, tag.getInteger(SIDE_TAG));
    getTileData().setInteger(FACING_TAG, tag.getInteger(FACING_TAG));
    readFromNBT(tag);
  }
}
