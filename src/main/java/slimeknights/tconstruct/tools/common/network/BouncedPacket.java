package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class BouncedPacket extends AbstractPacketThreadsafe {

  public BouncedPacket() {
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // only sent to server
    throw new UnsupportedOperationException("Serverside only");
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    netHandler.player.fallDistance = 0;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }
}
