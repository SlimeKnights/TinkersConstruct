package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import slimeknights.mantle.multiblock.MultiServantLogic;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;

import javax.annotation.Nonnull;

public class TileSmelteryComponent extends MultiServantLogic {

  // we send all our info to the client on load
  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    readFromNBT(pkt.getNbtCompound());
  }

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
    readFromNBT(tag);
  }

  /**
   * Gets a tile entity at the position of the master that contains a ISmelteryTankHandler
   * @return null if the TE is not an ISmelteryTankHandler or if the master is missing
   */
  protected ISmelteryTankHandler getSmelteryTankHandler() {
    if(getHasMaster()) {
      TileEntity te = getWorld().getTileEntity(getMasterPosition());
      if(te instanceof ISmelteryTankHandler) {
        return (ISmelteryTankHandler)te;
      }
    }
    return null;
  }
}
