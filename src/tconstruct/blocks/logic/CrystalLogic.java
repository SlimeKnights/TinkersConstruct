package tconstruct.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import tconstruct.library.util.IActiveLogic;

public class CrystalLogic extends TileEntity implements IActiveLogic
{
    boolean active;
    boolean growing;
    int crystalValue;

    public int getCrystalValue ()
    {
        return crystalValue;
    }

    public void setCrystalValue (int value)
    {
        this.crystalValue = value;
    }

    @Override
    public boolean getActive ()
    {
        return active;
    }

    @Override
    public void setActive (boolean flag)
    {
        System.out.println("Activating");
        active = flag;
    }

    public boolean growing ()
    {
        return growing;
    }

    public void setGrowth (boolean flag)
    {
        growing = flag;
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

    public void readCustomNBT (NBTTagCompound tags)
    {
        active = tags.getBoolean("Active");
        growing = tags.getBoolean("Growing");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setBoolean("Active", active);
        tags.setBoolean("Growing", growing);
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
        readCustomNBT(packet.customParam1);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
}
