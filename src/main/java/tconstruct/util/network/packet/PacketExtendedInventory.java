package tconstruct.util.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import tconstruct.TConstruct;

public class PacketExtendedInventory extends AbstractPacket
{

    byte[] type;

    public PacketExtendedInventory()
    {

    }

    public PacketExtendedInventory(byte[] bs)
    {
        this.type = bs;
        TConstruct.logger.error(type);
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        // buffer.writeBytes(type);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {

        // type = buffer.(buffer);
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
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(type));
        try
        {
            switch (inputStream.readByte())
            {
            case 0:
                player.openGui(TConstruct.instance, TConstruct.proxy.inventoryGui, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
                break;
            case 1:
                player.openGui(TConstruct.instance, TConstruct.proxy.armorGuiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
                break;
            case 2:
                player.openGui(TConstruct.instance, TConstruct.proxy.knapsackGuiID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
                break;
            }
        }
        catch (IOException e)
        {
            TConstruct.logger.error("Failed at reading server packet for TConstruct.");
            e.printStackTrace();
        }
    }

}
