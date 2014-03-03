package common.darkknight.jewelrycraft.tileentity;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySmelter extends TileEntity
{
    public int melting, flow, n = 0, p = 0;
    public boolean hasMetal, hasMoltenMetal, isDirty;
    public ItemStack metal, moltenMetal;
    
    public TileEntitySmelter()
    {
        this.melting = 0;
        this.flow = 0;
        this.hasMetal = false;
        this.hasMoltenMetal = false;
        this.metal = new ItemStack(0, 0, 0);
        this.moltenMetal = new ItemStack(0, 0, 0);
        this.isDirty = false;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("melting", melting);
        nbt.setBoolean("hasMetal", hasMetal);
        nbt.setBoolean("hasMoltenMetal", hasMoltenMetal);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound tag1 = new NBTTagCompound();
        this.metal.writeToNBT(tag);
        nbt.setCompoundTag("metal", tag);
        this.moltenMetal.writeToNBT(tag1);
        nbt.setCompoundTag("moltenMetal", tag1);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.melting = nbt.getInteger("melting");
        this.hasMetal = nbt.getBoolean("hasMetal");
        this.hasMoltenMetal = nbt.getBoolean("hasMoltenMetal");
        this.metal = new ItemStack(0, 0, 0);
        this.metal.readFromNBT(nbt.getCompoundTag("metal"));
        this.moltenMetal = new ItemStack(0, 0, 0);
        this.moltenMetal.readFromNBT(nbt.getCompoundTag("moltenMetal"));
    }
    
    @Override
    public void updateEntity()
    {
        super.updateEntity();
        Random rand = new Random();
        if(isDirty){
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            isDirty = true;
        }
        if (p > 0)
            --p;
        else
            p = 5;
        if (n == 0 && p == 0)
        {
            flow += 16;
            if (flow >= 16 * 20)
                n = 1;
        }
        if (n == 1 && p == 0)
        {
            flow -= 16;
            if (flow <= 0)
                n = 0;
        }
        if (this.hasMetal)
        {
            for (int l = 0; l < 2; ++l)
                this.worldObj.spawnParticle("flame", xCoord + rand.nextFloat(), (double) yCoord + 0.3F, zCoord + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
        if (rand.nextInt(65) == 0)
        {
            double d5 = this.xCoord + rand.nextFloat();
            double d7 = this.yCoord;
            double d6 = this.zCoord + rand.nextFloat();
            this.worldObj.playSound(d5, d7, d6, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
        }
        if (this.hasMetal)
        {
            if (melting > 0)
                this.melting--;
            if (melting == 0)
            {
                this.hasMetal = false;
                this.moltenMetal = metal;
                this.metal = new ItemStack(0, 0, 0);
                this.hasMoltenMetal = true;
                melting = -1;
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
