package tinker.tconstruct;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import tinker.tconstruct.logic.ToolStationLogic;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class TConstructPacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (packet.channel.equals("TConstruct"))
			if (side == Side.SERVER)
				handleServerPacket(packet);
	}

	void handleServerPacket (Packet250CustomPayload packet)
	{
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int dimension;
		int x;
		int y;
		int z;
		String toolName;

		try
		{
			dimension = inputStream.readInt();
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
			toolName = inputStream.readUTF();
		}
		catch (IOException e)
		{
			System.out.println("Failed at reading packet");
			e.printStackTrace();
			return;
		}
		
		WorldServer world = DimensionManager.getWorld(dimension);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof ToolStationLogic)
		{
			((ToolStationLogic)te).setToolname(toolName);
			System.out.println("Successfully processed packet");
		}
	}

}
