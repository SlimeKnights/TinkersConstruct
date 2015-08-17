package tconstruct.tools.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import tconstruct.common.network.AbstractPacketThreadsafe;

public class InventoryCraftingSyncPacket extends AbstractPacketThreadsafe {

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // serverside only
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    Container container = netHandler.playerEntity.openContainer;
    if(container != null) {
      container.onCraftMatrixChanged(null);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    // no data, yay
  }

  @Override
  public void toBytes(ByteBuf buf) {
    // no data, yay
  }
}
