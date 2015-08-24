package slimeknights.mantle.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractPacket implements IMessage, IMessageHandler {

  @Override
  public IMessage onMessage(IMessage message, MessageContext ctx) {
    if(!(message instanceof AbstractPacket))
      return null;

    AbstractPacket packet = (AbstractPacket) message;

    if(ctx.side == Side.SERVER)
      return packet.handleServer(ctx.getServerHandler());
    else
      return packet.handleClient(ctx.getClientHandler());
  }

  public abstract IMessage handleClient(NetHandlerPlayClient netHandler);
  public abstract IMessage handleServer(NetHandlerPlayServer netHandler);
}
