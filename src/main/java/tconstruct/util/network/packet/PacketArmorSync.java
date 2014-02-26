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
import tconstruct.util.player.ArmorExtended;
import tconstruct.util.player.KnapsackInventory;

public class PacketArmorSync extends AbstractPacket
{
    ArmorExtended armor;
    KnapsackInventory knapsack;

    public PacketArmorSync()
    {
        armor = new ArmorExtended();
        knapsack = new KnapsackInventory();
    }

    public PacketArmorSync(ArmorExtended armor, KnapsackInventory knapsack)
    {
        this.armor = armor;
        this.knapsack = knapsack;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        try {
            armor.writeInventoryToStream(buffer);
            knapsack.writeInventoryToStream(buffer);
        } catch (IOException e) {
            TConstruct.logger.warn("Failed at writing Server packet for TConstruct.");
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            try
            {
                armor.readInventoryFromStream(buffer);
                knapsack.readInventoryFromStream(buffer);
            }
            catch (Exception e)
            {
                TConstruct.logger.warn("Failed at reading Client packet for TConstruct.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {
        armor.saveToNBT(player);
        knapsack.saveToNBT(player);
        TProxyClient.armorExtended.recalculateHealth(player, TConstruct.playerTracker.getPlayerStats(player.getDisplayName()));


    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {

    }
}
