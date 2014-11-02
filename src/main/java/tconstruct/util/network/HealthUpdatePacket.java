package tconstruct.util.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.armor.player.TPlayerStats;

public class HealthUpdatePacket extends AbstractPacket {
    private float health;

    public HealthUpdatePacket() {
    }

    public HealthUpdatePacket(float health) {
        this.health = health;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeFloat(health);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        health = buffer.readFloat();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        // todo: fix this to use the proper armor extended.. once we sync that stuff...
        ArmorProxyClient.armorExtended.recalculateHealth(player, TPlayerStats.get(player));
        player.setHealth(health);
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
