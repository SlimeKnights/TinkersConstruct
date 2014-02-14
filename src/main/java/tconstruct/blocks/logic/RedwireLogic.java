package tconstruct.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class RedwireLogic extends TileEntity
{
    byte facesUsed;
    byte type;
    byte distanceTravelled;
    byte maxDistance;

    public void readCustomNBT (NBTTagCompound tags)
    {
        /*hasMaster = tags.getBoolean("HasMaster");
        if (hasMaster)
        {
        	int xCenter = tags.getInteger("xCenter");
        	int yCenter = tags.getInteger("yCenter");
        	int zCenter = tags.getInteger("zCenter");
        	master = new CoordTuple(xCenter, yCenter, zCenter);
        	masterID = tags.getShort("MasterID");
        	masterMeat = tags.getByte("masterMeat");
        }*/
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        /*tags.setBoolean("HasMaster", hasMaster);
        if (hasMaster)
        {
        	tags.setInteger("xCenter", master.x);
        	tags.setInteger("yCenter", master.y);
        	tags.setInteger("zCenter", master.z);
        	tags.setShort("MasterID", masterID);
        	tags.setByte("masterMeat", masterMeat);
        }*/
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readCustomNBT(packet.func_148857_g());
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }
}
