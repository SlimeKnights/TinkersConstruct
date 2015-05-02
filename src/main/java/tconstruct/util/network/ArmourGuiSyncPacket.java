package tconstruct.util.network;

import tconstruct.TConstruct;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.player.TPlayerStats;
import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
/**
 * This Packet is for sending the players TPlayerStats from the server to the client.
 * 
 * @author covers1624
 */

public class ArmourGuiSyncPacket extends AbstractPacket
{
    NBTTagCompound playerStats = new NBTTagCompound();
    
    public ArmourGuiSyncPacket()
    {
    }
    /**
     * For sending the players TPlayerStats to the client.
     * @param tag, The players TPlayerStats written to NBT.
     */
    public ArmourGuiSyncPacket(NBTTagCompound tag)
    {
        playerStats = tag;
    }
    
    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        ByteBufUtils.writeTag(buffer, playerStats);
    }
    
    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        playerStats = ByteBufUtils.readTag(buffer);
    }
    
    @Override
    public void handleClientSide (EntityPlayer player)
    {
        TPlayerStats stats = new TPlayerStats(player);
        stats.loadNBTData(playerStats);
        TinkerArmor.proxy.updatePlayerStats(stats);
        
    }
    
    @Override
    public void handleServerSide (EntityPlayer player)
    {
        //NO-OP. this is a Server -> client packet. 
    }
    
}
