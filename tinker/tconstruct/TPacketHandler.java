package tinker.tconstruct;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tinker.armory.content.EntityEquipment;
import tinker.common.InventoryLogic;
import tinker.tconstruct.entity.CartEntity;
import tinker.tconstruct.logic.ToolStationLogic;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class TPacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (packet.channel.equals("TConstruct"))
		{
			//System.out.println("Recieved a packet for TConstruct");
			if (side == Side.SERVER)
				handleServerPacket(packet);
			else
				handleClientPacket(packet);
		}
	}

	void handleClientPacket (Packet250CustomPayload packet)
	{
		//System.out.println("Handling client packet");
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

		byte packetType;
		int dimension;
		byte packetID;

		try
		{
			packetID = inputStream.readByte();
			dimension = inputStream.readInt();

			World world = DimensionManager.getWorld(dimension);
		}
		catch (IOException e)
		{
			System.out.println("Failed at reading client packet for TConstruct.");
			e.printStackTrace();
			return;
		}
	}

	void handleServerPacket (Packet250CustomPayload packet)
	{
		//System.out.println("Handling server packet");
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

		byte packetType;
		int dimension;
		byte packetID;

		try
		{
			packetID = inputStream.readByte();
			dimension = inputStream.readInt();

			World world = DimensionManager.getWorld(dimension);

			if (packetID == 1) //Tool Station
			{
				int x = inputStream.readInt();
				int y = inputStream.readInt();
				int z = inputStream.readInt();
				TileEntity te = world.getBlockTileEntity(x, y, z);
				
				String toolName = inputStream.readUTF();
				if (te instanceof ToolStationLogic)
				{
					((ToolStationLogic) te).setToolname(toolName);
				}
			}
			else if (packetID == 2) //Stencil Table
			{
				int x = inputStream.readInt();
				int y = inputStream.readInt();
				int z = inputStream.readInt();
				TileEntity te = world.getBlockTileEntity(x, y, z);
				
				Short itemID = inputStream.readShort();
				Short itemDamage = inputStream.readShort();
				if (te instanceof InventoryLogic)
				{
					((InventoryLogic) te).setInventorySlotContents(1, new ItemStack(itemID, 1, itemDamage));
				}
			}

		}
		catch (IOException e)
		{
			System.out.println("Failed at reading server packet for TConstruct.");
			e.printStackTrace();
			return;
		}
	}
}
