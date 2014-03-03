package common.darkknight.jewelrycraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDisplayer extends TileEntity
{
    public float ringTranslation1, ringTranslation2, ringTranslation3, rotAngle;
    public boolean isDescending1, isDescending2, isDescending3, isDirty, hasObject;
    public ItemStack object;
    public int quantity;

    public TileEntityDisplayer()
    {
        this.ringTranslation1 = 0;
        this.ringTranslation2 = 0;
        this.ringTranslation3 = 0;
        this.rotAngle = 0;
        this.quantity = 0;
        this.isDescending1 = false;
        this.isDescending2 = false;
        this.isDescending3 = false;
        this.isDirty = false;
        this.hasObject = false;
        this.object = new ItemStack(0, 0, 0);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("translation1", ringTranslation1);
        nbt.setFloat("translation2", ringTranslation2);
        nbt.setFloat("translation3", ringTranslation3);
        nbt.setFloat("angle", rotAngle);
        nbt.setInteger("quantity", quantity);
        nbt.setBoolean("descending1", isDescending1);
        nbt.setBoolean("descending2", isDescending2);
        nbt.setBoolean("descending3", isDescending3);
        nbt.setBoolean("hasObject", hasObject);
        NBTTagCompound tag = new NBTTagCompound();
        this.object.writeToNBT(tag);
        nbt.setCompoundTag("object", tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.ringTranslation1 = nbt.getFloat("translation1");
        this.ringTranslation2 = nbt.getFloat("translation2");
        this.ringTranslation3 = nbt.getFloat("translation3");
        this.rotAngle = nbt.getFloat("angle");
        this.quantity = nbt.getInteger("quantity");
        this.isDescending1 = nbt.getBoolean("descending1");
        this.isDescending2 = nbt.getBoolean("descending2");
        this.isDescending3 = nbt.getBoolean("descending3");
        this.hasObject = nbt.getBoolean("hasObject");
        this.object = new ItemStack(0, 0, 0);
        this.object.readFromNBT(nbt.getCompoundTag("object"));
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if(isDirty)
        {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            isDirty = true;
        }
        if(ringTranslation1 >= 0.6) isDescending1 = true;
        if(ringTranslation1 <= 0) isDescending1 = false;
        if(!isDescending1) ringTranslation1 += 0.05;
        if(isDescending1) ringTranslation1 -= 0.05;

        if(ringTranslation2 >= 0.6) isDescending2 = true;
        if(ringTranslation2 <= 0) isDescending2 = false;
        if(!isDescending2) ringTranslation2 += 0.04;
        if(isDescending2) ringTranslation2 -= 0.04;

        if(ringTranslation3 >= 0.6) isDescending3 = true;
        if(ringTranslation3 <= 0) isDescending3 = false;
        if(!isDescending3) ringTranslation3 += 0.03;
        if(isDescending3) ringTranslation3 -= 0.03;
        if(rotAngle < 360F) rotAngle += 6F;
        if(rotAngle>=360F) rotAngle = 0F;
    }

    @Override
    public Packet getDescriptionPacket() 
    {
        Packet132TileEntityData packet = (Packet132TileEntityData) super.getDescriptionPacket();
        NBTTagCompound dataTag = packet != null ? packet.data : new NBTTagCompound();
        writeToNBT(dataTag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, dataTag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) 
    {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt != null ? pkt.data : new NBTTagCompound();
        readFromNBT(tag);
    }
}
