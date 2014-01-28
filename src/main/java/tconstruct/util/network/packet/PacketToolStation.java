package tconstruct.util.network.packet;

import mantle.util.network.StringEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.blocks.logic.*;

public class PacketToolStation extends AbstractPacket {

	private int x, y, z;
	private String toolName;
	
	public PacketToolStation(){}
	
	public PacketToolStation(int x, int y, int z, String toolName){
		this.x = x;
		this.y = y;
		this.z = z;
		this.toolName = toolName;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		StringEncoder.encodeString(buffer, toolName);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		toolName = StringEncoder.decodeString(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {}

	@Override
	public void handleServerSide(EntityPlayer player) {
        World world = player.worldObj;
        TileEntity te = world.func_147438_o(x, y, z);

        if (te instanceof ToolStationLogic)
        {
            ((ToolStationLogic) te).setToolname(toolName);
        }
        if (te instanceof ToolForgeLogic)
        {
            ((ToolForgeLogic) te).setToolname(toolName);
        }
	}

}
