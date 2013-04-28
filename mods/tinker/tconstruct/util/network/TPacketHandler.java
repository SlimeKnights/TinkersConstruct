package mods.tinker.tconstruct.util.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.ToolStationLogic;
import mods.tinker.tconstruct.inventory.SmelteryContainer;
import mods.tinker.tconstruct.library.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
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
			if (side == Side.SERVER)
				handleServerPacket(packet);
			else
				handleClientPacket(packet);
		}
	}

	void handleClientPacket (Packet250CustomPayload packet)
	{
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
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

		byte packetID;

		try
		{
			packetID = inputStream.readByte();

			if (packetID == 1) //Tool Station
			{
				int dimension = inputStream.readInt();
				World world = DimensionManager.getWorld(dimension);
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
				int dimension = inputStream.readInt();
				World world = DimensionManager.getWorld(dimension);
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
			
			else if (packetID == 3) //Armor
            {
                String user = inputStream.readUTF();
                EntityPlayer player = TConstruct.playerTracker.getEntityPlayer(user);
                player.openGui(TConstruct.instance, TGuiHandler.armor, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
            }
			
			else if (packetID == 10) //Double jump
			{
				String user = inputStream.readUTF();
				EntityPlayer player = TConstruct.playerTracker.getEntityPlayer(user);
				player.fallDistance = 0;
			}
			/*else if (packetID == 11)
			{
				String user = inputStream.readUTF();
				float size = inputStream.readFloat();
				TConstruct.playerTracker.updateSize(user, size);
			}*/
		}
		catch (IOException e)
		{
			System.out.println("Failed at reading server packet for TConstruct.");
			e.printStackTrace();
			return;
		}
	}
}
