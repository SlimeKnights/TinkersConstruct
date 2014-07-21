package tconstruct.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

public class DoubleJumpPacket extends AbstractPacket
{

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {

    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {

    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        // String user = inputStream.readUTF();
        // EntityPlayer player = TConstruct.playerTracker.getEntityPlayer(user);
        player.fallDistance = 0;
    }

}
