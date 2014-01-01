package tconstruct.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
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
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        field_145850_b.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
}
