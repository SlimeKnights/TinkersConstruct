package tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Threadsafe integration of the abstract packet.
 * Basically if you're doing something that has any influence on the world you should use this.
 * (That's ~everything)
 */
public abstract class AbstractPacketThreadsafe extends AbstractPacket {

  @Override
  public final IMessage handleClient(final NetHandlerPlayClient netHandler) {
    Minecraft.getMinecraft().addScheduledTask(new Runnable() {
      @Override
      public void run() {
        handleClientSafe(netHandler);
      }
    });
    return null;
  }

  @Override
  public final IMessage handleServer(final NetHandlerPlayServer netHandler) {
    MinecraftServer.getServer().addScheduledTask(new Runnable() {
      @Override
      public void run() {
        handleServerSafe(netHandler);
      }
    });
    return null;
  }

  public abstract void handleClientSafe(NetHandlerPlayClient netHandler);
  public abstract void handleServerSafe(NetHandlerPlayServer netHandler);
}
