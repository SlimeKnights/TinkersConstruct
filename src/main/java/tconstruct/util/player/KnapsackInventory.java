package tconstruct.util.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class KnapsackInventory implements IInventory
{
    public ItemStack[] inventory = new ItemStack[27];
    public WeakReference<EntityPlayer> parent;

    public void init (EntityPlayer player)
    {
        parent = new WeakReference<EntityPlayer>(player);
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return inventory[slot];
    }

    public boolean isStackInSlot (int slot)
    {
        return inventory[slot] != null;
    }

    @Override
    public int getSizeInventory ()
    {
        return inventory.length;
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    public boolean canDropInventorySlot (int slot)
    {
        return true;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return stack;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            return split;
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getInventoryName ()
    {
        return "tconstruct.knapsack";
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    public ItemStack getStackInSlotOnClosing (int slot)
    {
        return null;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        return true;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return true;
    }

    /* Save/Load */
    public void saveToNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound invSlot;

        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                invSlot = new NBTTagCompound();
                invSlot.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(invSlot);
                tagList.appendTag(invSlot);
            }
        }

        tags.setTag("TConstruct.Knapsack", tagList);
    }

    public void readFromNBT (EntityPlayer entityplayer)
    {
        NBTTagCompound tags = entityplayer.getEntityData();
        NBTTagList tagList = tags.getTagList("TConstruct.Knapsack", 9);
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound) tagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                this.inventory[j] = itemstack;
            }
        }
    }

    public void dropItems ()
    {
        EntityPlayer player = parent.get();
        for (int i = 0; i < inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                player.dropPlayerItemWithRandomChoice(this.inventory[i], true);
                this.inventory[i] = null;
            }
        }
    }

    public void unequipItems ()
    {
        EntityPlayer player = parent.get();
        for (int i = 0; i < inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                dropItemEntity(player, inventory[i]);
                this.inventory[i] = null;
            }
        }
    }

    void dropItemEntity (Entity dropper, ItemStack dropStack)
    {
        EntityItem entityitem = new EntityItem(dropper.worldObj, dropper.posX, dropper.posY, dropper.posZ, dropStack);
        dropper.worldObj.spawnEntityInWorld(entityitem);
    }

    @Override
    public void markDirty ()
    {
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }

}