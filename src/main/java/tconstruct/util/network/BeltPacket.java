package tconstruct.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.armor.PlayerAbilityHelper;
import tconstruct.armor.player.TPlayerStats;

public class BeltPacket extends AbstractPacket
{
    public BeltPacket()
    {
    }

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
        PlayerAbilityHelper.swapBelt(player, TPlayerStats.get(player).armor);
    }

}
