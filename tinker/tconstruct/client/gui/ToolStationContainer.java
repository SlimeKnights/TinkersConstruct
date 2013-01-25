package tinker.tconstruct.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tinker.tconstruct.logic.ToolStationLogic;

public class ToolStationContainer extends Container
{
	InventoryPlayer invPlayer;
	ToolStationLogic logic;
	Slot[] slots;
	SlotTool toolSlot;
	
	public ToolStationContainer(InventoryPlayer inventoryplayer, ToolStationLogic builderlogic)
	{
		invPlayer = inventoryplayer;
		logic = builderlogic;
		
		toolSlot = new SlotTool(inventoryplayer.player, builderlogic, 0, 115, 38);
		this.addSlotToContainer(toolSlot);
		slots = new Slot[] { new Slot(builderlogic, 1, 57, 29), new Slot(builderlogic, 2, 39, 38), new Slot(builderlogic, 3, 57, 47) };
		
		for (int iter = 0; iter < 3; iter ++)
			this.addSlotToContainer(slots[iter]);
			
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
	
	//posX and posY must be the same length
	public void resetSlots(int[] posX, int[] posY)
	{
		/* Station inventory */
		inventorySlots.clear();
		inventoryItemStacks.clear();
		this.addSlotToContainer(toolSlot);
		for (int iter = 0; iter < posX.length; iter++)
		{
			slots[iter].xDisplayPosition = posX[iter]+1;
			slots[iter].yDisplayPosition = posY[iter]+1;
			addSlotToContainer(slots[iter]);
		}
		
		/* Player inventory */
		for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
            	this.addSlotToContainer(new Slot(invPlayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
        	this.addSlotToContainer(new Slot(invPlayer, column, 8 + column * 18, 142));
        }
        
        logic.dumpExtraItems(posX.length, invPlayer.player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotID < logic.getSizeInventory())
            {
            	System.out.println("Merging itemstack, true");
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 1, logic.getSizeInventory() - 1, false))
            {
                return null;
            }

            if (slotStack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
	
	/*@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
    {
		//this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true)
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(slotID);
        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();
            if (slotID == 0)
            {
                if (!mergeItemStack(slotStack, 4, 40, true)) //10 = size of crafting grid, 46 = total, 0 == output slot
                {
                    return null;
                }
            }
            else if (slotID >= 4 && slotID < 37)
            {
                if (!mergeItemStack(slotStack, 37, 40, false))
                {
                    return null;
                }
            }
            else if (slotID >= 37 && slotID < 40)
            {
                if (!mergeItemStack(slotStack, 4, 37, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(slotStack, 4, 40, false))
            {
                return null;
            }
            if (slotStack.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (slotStack.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(player, slotStack);
            }
            else
            {
                return null;
            }
        }
        return itemstack;
    }*/
}
