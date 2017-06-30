package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class InventoryCraftingSyncPacket extends AbstractPacketThreadsafe {

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // Serverside only
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    Container container = netHandler.player.openContainer;
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
