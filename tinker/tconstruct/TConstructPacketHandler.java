package tinker.tconstruct;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import tinker.common.InventoryLogic;
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
		
		byte packetID;
		int dimension;
		int x;
		int y;
		int z;
		

		try
		{
			packetID = inputStream.readByte();
			dimension = inputStream.readInt();
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
			
			WorldServer world = DimensionManager.getWorld(dimension);
			TileEntity te = world.getBlockTileEntity(x, y, z);
			
			if (packetID == 1)
			{
				String toolName = inputStream.readUTF();
				if (te instanceof ToolStationLogic)
				{
					((ToolStationLogic)te).setToolname(toolName);
				}
			}
			else if (packetID == 2)
			{
				Short itemID = inputStream.readShort();
				Short itemDamage = inputStream.readShort();
				if (te instanceof InventoryLogic)
				{
					((InventoryLogic)te).setInventorySlotContents(1, new ItemStack(itemID, 1, itemDamage));
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("Failed at reading packet for TConstruct. Blarrrrrrrrrgh");
			e.printStackTrace();
			return;
		}
		
		
		
	}

}
