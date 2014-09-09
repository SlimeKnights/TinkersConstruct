package tconstruct.mechworks.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * 
 * @author fuj1n
 * 
 */
public class TileEntityLandmine extends TileEntity implements IInventory
{

    private ItemStack[] inventory = new ItemStack[getSizeInventory()];
    private String containerName;

    public int soundcountythingmax = 10;
    public int soundcountything = 0;

    private boolean shouldUpdateLogic = true;

    public int triggerType;
    public boolean isExploding = false;

    public int getSizeTriggerInventory ()
    {
        return 3;
    }

    @Override
    public int getSizeInventory ()
    {
        return 4;
    }

    @Override
    public void writeToNBT (NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);

        par1NBTTagCompound.setInteger("triggerType", triggerType);

        if (this.hasCustomInventoryName())
        {
            par1NBTTagCompound.setString("CustomName", this.containerName);
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.inventory.length)
            {
                this.inventory[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        triggerType = par1NBTTagCompound.getInteger("triggerType");

        if (par1NBTTagCompound.hasKey("CustomName"))
        {
            this.containerName = par1NBTTagCompound.getString("CustomName");
        }
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
    }

    @Override
    public ItemStack getStackInSlot (int i)
    {
        if (i < inventory.length)
        {
            return inventory[i];
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize (int par1, int par2)
    {
        if (par1 < inventory.length)
        {
            if (inventory[par1] != null)
            {
                ItemStack itemstack;

                if (inventory[par1].stackSize <= par2)
                {
                    itemstack = inventory[par1];
                    inventory[par1] = null;
                    return itemstack;
                }
                else
                {
                    itemstack = inventory[par1].splitStack(par2);

                    if (inventory[par1].stackSize == 0)
                    {
                        inventory[par1] = null;
                    }

                    return itemstack;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int i)
    {
        if (i < inventory.length && this.inventory[i] != null)
        {
            ItemStack itemstack = this.inventory[i];
            this.inventory[i] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents (int i, ItemStack itemstack)
    {
        if (i < inventory.length)
        {
            inventory[i] = itemstack;
        }
    }

    @Override
    public String getInventoryName ()
    {
        return this.hasCustomInventoryName() ? this.containerName : "Landmine";
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return containerName != null && containerName.length() > 0;
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    public void setGuiDisplayName (String par1Str)
    {
        this.containerName = par1Str;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }

    @Override
    public void markDirty ()
    {
        super.markDirty();
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        // Should automatic camo insertion be allowed.
        if (i == 3)
        {
            return false;
        }
        else
        {
            // Here for mDiyo to decide if he wants automation for
            // insertion/extraction of the items to be an option.
            // Would be useful for building automated mine laying machines.
            return false;
        }
    }

    public void setSoundPlayed ()
    {
        soundcountythingmax = 35;
        soundcountything = soundcountythingmax;
    }

    @Override
    public void updateEntity ()
    {
        if (shouldUpdateLogic)
        {
            worldObj.func_147479_m(xCoord, yCoord, zCoord);
            shouldUpdateLogic = false;
        }
        if (soundcountything > 0)
        {
            soundcountything--;
        }
    }

}
