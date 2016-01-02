package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import slimeknights.mantle.multiblock.MultiServantLogic;

public class TileSmelteryComponent extends MultiServantLogic {

  // we send all our info to the client on load
  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    readFromNBT(pkt.getNbtCompound());

  }

  protected TileSmeltery getSmeltery() {
    if(getHasMaster()) {
      TileEntity te = worldObj.getTileEntity(getMasterPosition());
      if(te instanceof TileSmeltery) {
        return (TileSmeltery) te;
      }
    }
    return null;
  }
}
