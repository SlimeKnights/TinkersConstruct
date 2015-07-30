package tconstruct.common.network;

import com.google.common.base.Throwables;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkWrapper {
  public final SimpleNetworkWrapper network;
  private int id = 0;

  public NetworkWrapper(String channelName) {
    network = new SimpleNetworkWrapper(channelName);
  }

  /**
   * Packet will be received on both cliend and server side.
   */
  public void registerPacket(Class<? extends AbstractPacket> packetClazz) {
    registerPacketClient(packetClazz);
    registerPacketServer(packetClazz);
  }

  /**
   * Packet will only be received on the client side
   */
  public void registerPacketClient(Class<? extends AbstractPacket> packetClazz) {
    registerPacketImpl(packetClazz, Side.CLIENT);
  }

  /**
   * Packet will only be received on the server side
   */
  public void registerPacketServer(Class<? extends AbstractPacket> packetClazz) {
    registerPacketImpl(packetClazz, Side.SERVER);
  }

  private void registerPacketImpl(Class<? extends AbstractPacket> packetClazz, Side side) {
    IMessageHandler<AbstractPacket, AbstractPacket> handler;
    try
    {
      handler = packetClazz.newInstance();
    } catch (Exception e)
    {
      throw Throwables.propagate(e);
    }

    network.registerMessage(handler, packetClazz, id++, side);
  }
}
