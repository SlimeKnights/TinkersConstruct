package tconstruct.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.TConstruct;
import tconstruct.armor.ArmorProxyCommon;

public class AccessoryInventoryPacket extends AbstractPacket
{

    int type;

    public AccessoryInventoryPacket()
    {

    }

    public AccessoryInventoryPacket(int type)
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
        case ArmorProxyCommon.inventoryGui:
            player.openGui(TConstruct.instance, ArmorProxyCommon.inventoryGui, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            break;
        case ArmorProxyCommon.armorGuiID:
            player.openGui(TConstruct.instance, ArmorProxyCommon.armorGuiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            break;
        case ArmorProxyCommon.knapsackGuiID:
            player.openGui(TConstruct.instance, ArmorProxyCommon.knapsackGuiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
            break;
        }
    }
}
