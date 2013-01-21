package tinker.tconstruct.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PatternShaperInventory implements IInventory
{
    /** A list of one item containing the result of the crafting formula */
    private ItemStack[] inventory = new ItemStack[2];

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return inventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int slot)
    {
        return this.inventory[slot];
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "crafters.PatternShaper";
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int slot, int number)
    {
        if (this.inventory[slot] != null)
        {
            ItemStack stack = this.inventory[slot];
            this.inventory[slot] = null;
            if (slot == 1)
            	decrStackSize(0, 1);
            return stack;
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.inventory[slot] != null)
        {
            ItemStack stack = this.inventory[slot];
            this.inventory[slot] = null;
            return stack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.inventory[slot] = stack;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged() {}

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public void openChest() {}

    public void closeChest() {}
}
