package tconstruct.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.armor.PlayerAbilityHelper;

public class GogglePacket extends AbstractPacket
{
    boolean active;

    public GogglePacket()
    {
    }

    public GogglePacket(boolean active)
    {
        this.active = active;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeBoolean(active);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        active = buffer.readBoolean();
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        PlayerAbilityHelper.toggleGoggles(player, active);
        /*TPlayerStats stats = TPlayerStats.get(player);
        stats.activeGoggles = active;*/
    }

}
