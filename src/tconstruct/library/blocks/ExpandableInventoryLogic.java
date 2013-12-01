package tconstruct.library.blocks;

import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.world.World;

public abstract class ExpandableInventoryLogic extends InventoryLogic implements IInventory
{

    public ExpandableInventoryLogic()
    {
        super(0);
    }

    protected ArrayList<ItemStack> inventory = new ArrayList<ItemStack>();
    protected String invName;

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return slot < inventory.size() ? inventory.get(slot) : null;
    }

    public boolean isStackInSlot (int slot)
    {
        return slot < inventory.size() && inventory.get(slot) != null;
    }

    @Override
    public int getSizeInventory ()
    {
        return inventory.size();
    }

    public int getMaxSize ()
    {
        return 64;
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
        if (slot < inventory.size())
        {
            inventory.set(slot, itemstack);
        }
        else if (slot == inventory.size())
        {
            inventory.add(itemstack);
        }
        else if (slot < getMaxSize())
        {
            inventory.ensureCapacity(slot);
            inventory.set(slot, itemstack);
        }
        else
        {
            return;
        }
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        if (slot < inventory.size() && inventory.get(slot) != null)
        {
            if (inventory.get(slot).stackSize <= quantity)
            {
                ItemStack stack = inventory.get(slot);
                inventory.set(slot, null);
                return stack;
            }
            ItemStack split = inventory.get(slot).splitStack(quantity);
            if (inventory.get(slot).stackSize == 0)
            {
                inventory.set(slot, null);
            }
            return split;
        }
        else
        {
            return null;
        }
    }

    /* Supporting methods */
    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
            return false;

        else
            return entityplayer.getDistance((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;

    }

    public abstract Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z);

    /* NBT */
    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        this.invName = tags.getString("InvName");
        NBTTagList nbttaglist = tags.getTagList("Items");
        inventory = new ArrayList<ItemStack>();
        inventory.ensureCapacity(nbttaglist.tagCount() > getMaxSize() ? getMaxSize() : nbttaglist.tagCount());
        for (int iter = 0; iter < nbttaglist.tagCount(); iter++)
        {
            NBTTagCompound tagList = (NBTTagCompound) nbttaglist.tagAt(iter);
            byte slotID = tagList.getByte("Slot");
            if (slotID >= 0 && slotID < inventory.size())
            {
                inventory.set(slotID, ItemStack.loadItemStackFromNBT(tagList));
            }
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        if (invName != null)
            tags.setString("InvName", invName);
        NBTTagList nbttaglist = new NBTTagList();
        for (int iter = 0; iter < inventory.size(); iter++)
        {
            if (inventory.get(iter) != null)
            {
                NBTTagCompound tagList = new NBTTagCompound();
                tagList.setByte("Slot", (byte) iter);
                inventory.get(iter).writeToNBT(tagList);
                nbttaglist.appendTag(tagList);
            }
        }

        tags.setTag("Items", nbttaglist);
    }

    /* Default implementations of hardly used methods */
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

    protected abstract String getDefaultName ();

    public void setInvName (String name)
    {
        this.invName = name;
    }

    public String getInvName ()
    {
        return this.isInvNameLocalized() ? this.invName : getDefaultName();
    }

    public boolean isInvNameLocalized ()
    {
        return this.invName != null && this.invName.length() > 0;
    }

    public void cleanInventory ()
    {
        Iterator<ItemStack> i1 = inventory.iterator();
        while (i1.hasNext())
        {
            if (i1.next() == null)
            {
                i1.remove();
            }
        }
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
        if (slot < getSizeInventory())
        {
            if (inventory.get(slot) == null || itemstack.stackSize + inventory.get(slot).stackSize <= getInventoryStackLimit())
                return true;
        }
        else
        {
            return slot < getMaxSize();
        }
        return false;
    }

}
