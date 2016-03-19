package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

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
    if(netHandler.playerEntity.openContainer instanceof ContainerSmeltery) {
      TileSmeltery smeltery = ((ContainerSmeltery) netHandler.playerEntity.openContainer).getTile();
      smeltery.getTank().moveFluidToBottom(index);
      smeltery.onTankChanged(smeltery.getTank().getFluids(), null);
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
