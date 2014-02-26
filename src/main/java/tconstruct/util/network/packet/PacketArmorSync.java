package tconstruct.util.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import tconstruct.TConstruct;
import tconstruct.client.TProxyClient;
import tconstruct.common.TProxyCommon;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class PacketArmorSync extends AbstractPacket
{
    ByteBuf b;

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {

    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            try
            {
                byte packetID = buffer.readByte();
            }
            catch (Exception e)
            {
                TConstruct.logger.warn("Failed at reading server packet for TConstruct.");
                e.printStackTrace();
            }
            try
            {
                TProxyClient.armorExtended.readInventoryFromStream(buffer);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {


    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        TProxyClient.armorExtended.recalculateHealth(player, TConstruct.playerTracker.getPlayerStats(player.getDisplayName()));

    }
}
