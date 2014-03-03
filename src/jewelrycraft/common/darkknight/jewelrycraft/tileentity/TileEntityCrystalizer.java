package common.darkknight.jewelrycraft.tileentity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

import common.darkknight.jewelrycraft.config.ConfigHandler;
import common.darkknight.jewelrycraft.util.JewelryNBT;

public class TileEntityCrystalizer extends TileEntity
{
    public boolean hasJewelry, hasModifier, hasEndItem, isDirty, hasJewel;
    public ItemStack jewelry, modifier, endItem, jewel;
    public int       timer, effect;
    public float angle;

    public TileEntityCrystalizer()
    {
        this.jewelry = new ItemStack(0, 0, 0);
        this.modifier = new ItemStack(0, 0, 0);
        this.endItem = new ItemStack(0, 0, 0);
        this.jewel = new ItemStack(0, 0, 0);
        this.hasJewelry = false;
        this.hasModifier = false;
        this.hasEndItem = false;
        this.hasJewel = false;
        this.timer = 0;
        this.effect = 0;
        this.angle = 0;
        this.isDirty = false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("hasJewelry", hasJewelry);
        nbt.setBoolean("hasModifier", hasModifier);
        nbt.setBoolean("hasEndItem", hasEndItem);
        nbt.setBoolean("hasJewel", hasJewel);
        nbt.setInteger("timer", timer);
        nbt.setInteger("effect", effect);
        nbt.setFloat("angle", angle);

        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound tag1 = new NBTTagCompound();
        NBTTagCompound tag2 = new NBTTagCompound();
        NBTTagCompound tag3 = new NBTTagCompound();

        this.jewelry.writeToNBT(tag);
        nbt.setCompoundTag("jewelry", tag);
        this.modifier.writeToNBT(tag1);
        nbt.setCompoundTag("modifier", tag1);
        this.endItem.writeToNBT(tag2);
        nbt.setCompoundTag("endItem", tag2);
        this.jewel.writeToNBT(tag3);
        nbt.setCompoundTag("jewel", tag3);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.hasJewelry = nbt.getBoolean("hasJewelry");
        this.hasModifier = nbt.getBoolean("hasModifier");
        this.hasEndItem = nbt.getBoolean("hasEndItem");
        this.hasJewel = nbt.getBoolean("hasJewel");

        this.timer = nbt.getInteger("timer");
        this.effect = nbt.getInteger("effect");
        this.angle = nbt.getFloat("angle");
        this.jewelry = new ItemStack(0, 0, 0);
        this.jewelry.readFromNBT(nbt.getCompoundTag("jewelry"));
        this.modifier = new ItemStack(0, 0, 0);
        this.modifier.readFromNBT(nbt.getCompoundTag("modifier"));
        this.endItem = new ItemStack(0, 0, 0);
        this.endItem.readFromNBT(nbt.getCompoundTag("endItem"));
        this.jewel = new ItemStack(0, 0, 0);
        this.jewel.readFromNBT(nbt.getCompoundTag("jewel"));
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if(isDirty){
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            isDirty = true;
        }
        if(angle<360F)angle+=3F;
        else angle=0F;
        if (this.hasJewelry && (this.hasModifier || this.hasJewel) && !this.hasEndItem)
        {
            if (timer > 0)
            {
                timer--;
                for (int l = 0; l < ConfigHandler.jewelryCraftingTime/(timer + 2); ++l)
                {
                    if(this.getBlockMetadata() == 0) this.worldObj.spawnParticle("witchMagic", xCoord + 0.5F, (double) yCoord + 0.8F, zCoord + 0.2F, 0.0D, 0.0D, 0.0D);
                    if(this.getBlockMetadata() == 1) this.worldObj.spawnParticle("witchMagic", xCoord + 0.8F, (double) yCoord + 0.8F, zCoord + 0.5F, 0.0D, 0.0D, 0.0D);
                    if(this.getBlockMetadata() == 2) this.worldObj.spawnParticle("witchMagic", xCoord + 0.5F, (double) yCoord + 0.8F, zCoord + 0.8F, 0.0D, 0.0D, 0.0D);
                    if(this.getBlockMetadata() == 3) this.worldObj.spawnParticle("witchMagic", xCoord + 0.2F, (double) yCoord + 0.8F, zCoord + 0.5F, 0.0D, 0.0D, 0.0D);
                }
            }
            if (timer == 0)
            {
                this.hasEndItem = true;
                this.endItem = jewelry.copy();
                if (hasModifier && modifier != new ItemStack(0, 0, 0)) JewelryNBT.addModifier(endItem, modifier);
                if (hasJewel && jewel != new ItemStack(0, 0, 0)) JewelryNBT.addJewel(endItem, jewel);
                if (hasJewel && hasModifier && JewelryNBT.isJewelX(endItem, new ItemStack(Item.netherStar)) && JewelryNBT.isModifierX(endItem, new ItemStack(Item.book))) JewelryNBT.addMode(endItem, "Disenchant");
                if (hasModifier && JewelryNBT.isModifierEffectType(endItem)) JewelryNBT.addMode(endItem, "Activated");
                this.hasJewelry = false;
                this.jewelry = new ItemStack(0, 0, 0);
                this.hasModifier = false;
                this.modifier = new ItemStack(0, 0, 0);
                this.hasJewel = false;
                this.jewel = new ItemStack(0, 0, 0);
                timer = -1;
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
