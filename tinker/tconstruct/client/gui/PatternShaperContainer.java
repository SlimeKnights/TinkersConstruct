package tinker.tconstruct.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tinker.tconstruct.logic.PatternChestLogic;
import tinker.tconstruct.logic.PatternShaperLogic;

public class PatternShaperContainer extends Container
{
	public PatternShaperLogic logic;

    public PatternShaperContainer(InventoryPlayer inventoryplayer, PatternShaperLogic shaper)
    {
    	logic = shaper;
        this.addSlotToContainer(new Slot(shaper, 0, 48, 35));
        this.addSlotToContainer(new SlotOnlyTake(shaper, 1, 106, 35));
        /*for (int i = 0; i < 3; i++)
        {
            for (int l = 0; l < 3; l++)
            {
            	this.addSlotToContainer(new Slot(craftMatrix, l + i * 3, 30 + l * 18, 17 + i * 18));
            }
        }*/

        /* Player inventory */
		for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
            	this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
        	this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 142));
        }
    }

    /*public void onCraftMatrixChanged(IInventory iinventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj));
    }*/

    /*@Override
    public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
        super.onCraftGuiClosed(entityplayer);
        if (logic.worldObj.isRemote)
        {
            return;
        }
        ItemStack itemstack = logic.getStackInSlot(0);
        if (itemstack != null)
        {
            entityplayer.dropPlayerItem(itemstack);
        }
    }*/

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityplayer, int slot)
    {
    	return null;
        /*ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i == 0)
            {
                if (!mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }
            }
            else if (i >= 10 && i < 37)
            {
                if (!mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (i >= 37 && i < 46)
            {
                if (!mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(itemstack1, 10, 46, false))
            {
                return null;
            }
            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(entityplayer, itemstack1);
            }
            else
            {
                return null;
            }
        }
        return itemstack;*/
    }
}
