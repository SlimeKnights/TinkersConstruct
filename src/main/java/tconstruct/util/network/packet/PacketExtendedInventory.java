package tconstruct.util.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.TConstruct;

public class PacketExtendedInventory extends AbstractPacket {

	byte type;

	public PacketExtendedInventory() {

	}

	public PacketExtendedInventory(byte type) {
		this.type = type;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeByte(type);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		type = buffer.readByte();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		//String user = inputStream.readUTF();
		//EntityPlayer player = TConstruct.playerTracker.getEntityPlayer(user);
		switch (type) {
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

}
