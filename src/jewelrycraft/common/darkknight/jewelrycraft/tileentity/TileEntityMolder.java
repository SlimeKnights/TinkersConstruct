package common.darkknight.jewelrycraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

import common.darkknight.jewelrycraft.item.ItemList;
import common.darkknight.jewelrycraft.util.JewelryNBT;

public class TileEntityMolder extends TileEntity
{
    public int cooling;
    public boolean hasMoltenMetal, hasJewelBase, hasMold, isDirty;
    public ItemStack mold, jewelBase, moltenMetal, ringMetal;
    
    public TileEntityMolder()
    {
        this.moltenMetal = new ItemStack(0, 0, 0);
        this.jewelBase = new ItemStack(0, 0, 0);
        this.mold = new ItemStack(0, 0, 0);
        this.ringMetal = new ItemStack(0, 0, 0);
        this.cooling = 0;
        this.hasJewelBase = false;
        this.hasMoltenMetal = false;
        this.hasMold = false;
        this.isDirty = false;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("cooling", cooling);
        nbt.setBoolean("hasJewelBase", hasJewelBase);
        nbt.setBoolean("hasMoltenMetal", hasMoltenMetal);
        nbt.setBoolean("hasMold", hasMold);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound tag1 = new NBTTagCompound();
        NBTTagCompound tag2 = new NBTTagCompound();
        NBTTagCompound tag3 = new NBTTagCompound();
        this.mold.writeToNBT(tag);
        nbt.setCompoundTag("mold", tag);
        this.jewelBase.writeToNBT(tag1);
        nbt.setCompoundTag("jewelBase", tag1);
        this.moltenMetal.writeToNBT(tag2);
        nbt.setCompoundTag("moltenMetal", tag2);
        this.ringMetal.writeToNBT(tag3);
        nbt.setCompoundTag("ringMetal", tag3);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.cooling = nbt.getInteger("cooling");
        this.hasJewelBase = nbt.getBoolean("hasJewelBase");
        this.hasMoltenMetal = nbt.getBoolean("hasMoltenMetal");
        this.hasMold = nbt.getBoolean("hasMold");
        this.mold = new ItemStack(0, 0, 0);
        this.mold.readFromNBT(nbt.getCompoundTag("mold"));
        this.jewelBase = new ItemStack(0, 0, 0);
        this.jewelBase.readFromNBT(nbt.getCompoundTag("jewelBase"));
        this.moltenMetal = new ItemStack(0, 0, 0);
        this.moltenMetal.readFromNBT(nbt.getCompoundTag("moltenMetal"));
        this.ringMetal = new ItemStack(0, 0, 0);
        this.ringMetal.readFromNBT(nbt.getCompoundTag("ringMetal"));
    }
    
    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if(isDirty){
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            isDirty = true;
        }
        if (moltenMetal.itemID != 0)
        {
            if(worldObj.rand.nextInt(20) == 0) this.worldObj.playSoundEffect(xCoord, yCoord + 0.5F, zCoord, "random.fizz", 0.5F, 1F);
            for (int l = 0; l < 2; ++l)
                this.worldObj.spawnParticle("reddust", xCoord + Math.random(), (double) yCoord + 0.2F, zCoord + Math.random(), 0.0D, 1.0D, 1.0D);
        }
        if (this.hasMoltenMetal && !this.hasJewelBase)
        {
            ringMetal = moltenMetal;
            if (cooling > 0)
                this.cooling--;
            if (cooling == 0)
            {
                this.hasMoltenMetal = false;
                if (mold.getItemDamage() == 0)
                    this.jewelBase = moltenMetal;
                else
                    this.jewelBase = new ItemStack(ItemList.ring);
                if(mold.getItemDamage() != 0 && jewelBase != new ItemStack(0, 0, 0))
                    JewelryNBT.addMetal(jewelBase, ringMetal);
                this.moltenMetal = new ItemStack(0, 0, 0);
                this.hasJewelBase = true;
                cooling = -1;
            }
        }
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
