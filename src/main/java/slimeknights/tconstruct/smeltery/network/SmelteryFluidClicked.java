package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;

// Fired when a player clicks a fluid in the smeltery GUI to move it to the bottom
public class SmelteryFluidClicked extends AbstractPacketThreadsafe {

  public int index; // index of the clicked fluid

  public SmelteryFluidClicked() {
  }

  public SmelteryFluidClicked(int index) {
    this.index = index;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // Serverside only
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    if(netHandler.playerEntity.openContainer instanceof BaseContainer) {
      TileEntity te = ((BaseContainer<?>) netHandler.playerEntity.openContainer).getTile();
      if(te instanceof ISmelteryTankHandler) {
        ISmelteryTankHandler smeltery = (ISmelteryTankHandler)te;

        smeltery.getTank().moveFluidToBottom(index);
        smeltery.onTankChanged(smeltery.getTank().getFluids(), null);
      }
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    index = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(index);
  }
}
