package tconstruct.util.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

public class PacketUpdateTE extends AbstractPacket {
	private int x, y, z;
	private NBTTagCompound data;

	public PacketUpdateTE() {

	}
	
	public PacketUpdateTE(int x, int y, int z, NBTTagCompound data) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer pbuff = new PacketBuffer(buffer);
		pbuff.writeInt(x);
		pbuff.writeShort(y);
		pbuff.writeInt(z);
		try {
			pbuff.writeNBTTagCompoundToBuffer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer pbuff = new PacketBuffer(buffer);
		x = pbuff.readInt();
		y = pbuff.readShort();
		z = pbuff.readInt();
		try {
			data = pbuff.readNBTTagCompoundFromBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		TileEntity te = player.worldObj.getTileEntity(x, y, z);

		if (te != null) {
			te.readFromNBT(data);
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
	}

}
