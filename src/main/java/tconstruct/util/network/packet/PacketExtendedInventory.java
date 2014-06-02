package tconstruct.util.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.TConstruct;
import tconstruct.common.TProxyCommon;

public class PacketExtendedInventory extends AbstractPacket
{

    int type;

    public PacketExtendedInventory()
    {

    }

    public PacketExtendedInventory(int type)
    {
        this.type = type;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(type);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        type = buffer.readInt();
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        switch (type)
        {
        case TProxyCommon.inventoryGui:
            player.openGui(TConstruct.instance, TConstruct.proxy.inventoryGui, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            break;
        case TProxyCommon.armorGuiID:
            player.openGui(TConstruct.instance, TConstruct.proxy.armorGuiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            break;
        case TProxyCommon.knapsackGuiID:
            player.openGui(TConstruct.instance, TConstruct.proxy.knapsackGuiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            break;
        }
    }
}
