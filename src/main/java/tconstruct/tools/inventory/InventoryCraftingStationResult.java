package tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import tconstruct.tools.logic.CraftingStationLogic;

public class InventoryCraftingStationResult extends InventoryCraftResult
{
    CraftingStationLogic logic;

    public InventoryCraftingStationResult(CraftingStationLogic logic)
    {
        this.logic = logic;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory ()
    {
        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot (int par1)
    {
        return logic.getStackInSlot(0);// this.stackResult[0];
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName ()
    {
        return "Result";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized
     * name, and translated into the player's language. Otherwise it will be
     * used directly.
     */
    public boolean isInvNameLocalized ()
    {
        return false;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize (int par1, int par2)
    {
        ItemStack stack = logic.getStackInSlot(0);
        if (stack != null)
        {
            ItemStack itemstack = stack;
            logic.setInventorySlotContents(0, null);
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing (int par1)
    {
        return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents (int par1, ItemStack par2ItemStack)
    {
        logic.setInventorySlotContents(0, par2ItemStack);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void markDirty ()
    {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    @Override
    public boolean isUseableByPlayer (EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    public boolean isStackValidForSlot (int par1, ItemStack par2ItemStack)
    {
        return true;
    }
}
