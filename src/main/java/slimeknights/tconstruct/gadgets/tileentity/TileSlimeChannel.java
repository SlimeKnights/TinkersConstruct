package slimeknights.tconstruct.gadgets.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.gadgets.block.BlockSlimeChannel.ChannelDirection;

/**
 * This tile entity is simply an extra data
 */
public class TileSlimeChannel extends TileEntity {

  public static final String SIDE_TAG = "side";
  public static final String DIRECTION_TAG = "direction";

  // don't delete the TE if the state changes
  // we want to keep our side and facing data if it becomes powered
  @Override
  public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
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

  public void setDirection(ChannelDirection direction) {
    getTileData().setInteger(DIRECTION_TAG, direction.getIndex());
  }

  @Nonnull
  public ChannelDirection getDirection() {
    int direction = getTileData().getInteger(DIRECTION_TAG);
    return ChannelDirection.fromIndex(direction);
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
    getTileData().setInteger(DIRECTION_TAG, tag.getInteger(DIRECTION_TAG));
    readFromNBT(tag);
  }
}
