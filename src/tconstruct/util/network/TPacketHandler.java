package tconstruct.util.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.DrawbridgeLogic;
import tconstruct.blocks.logic.ToolForgeLogic;
import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.library.blocks.InventoryLogic;
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
                handleServerPacket(packet, (EntityPlayerMP) player);
            else
                handleClientPacket(packet, (EntityPlayer) player);
        }
    }

    void handleClientPacket (Packet250CustomPayload packet, EntityPlayer player)
    {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte packetID;

        try
        {
            packetID = inputStream.readByte();
        }
        catch (Exception e)
        {
            System.out.println("Failed at reading client packet for TConstruct.");
            e.printStackTrace();
            return;
        }
    }

    void handleServerPacket (Packet250CustomPayload packet, EntityPlayerMP player)
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
                if (te instanceof ToolForgeLogic)
                {
                    ((ToolForgeLogic) te).setToolname(toolName);
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
                //String user = inputStream.readUTF();
                //EntityPlayer player = TConstruct.playerTracker.getEntityPlayer(user);
                switch (inputStream.readByte())
                {
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

            else if (packetID == 5) //Drawbridge
            {
                int dimension = inputStream.readInt();
                World world = DimensionManager.getWorld(dimension);
                int x = inputStream.readInt();
                int y = inputStream.readInt();
                int z = inputStream.readInt();
                TileEntity te = world.getBlockTileEntity(x, y, z);

                byte direction = inputStream.readByte();
                if (te instanceof DrawbridgeLogic)
                {
                    ((DrawbridgeLogic) te).setPlacementDirection(direction);
                }
            }

            else if (packetID == 10) //Double jump
            {
                //String user = inputStream.readUTF();
                //EntityPlayer player = TConstruct.playerTracker.getEntityPlayer(user);
                player.fallDistance = 0;
            }
        }
        catch (IOException e)
        {
            System.out.println("Failed at reading server packet for TConstruct.");
            e.printStackTrace();
            return;
        }
    }

    Entity getEntity (World world, int id)
    {
        for (Object o : world.loadedEntityList)
        {
            if (((Entity) o).entityId == id)
                return (Entity) o;
        }
        return null;
    }
}
