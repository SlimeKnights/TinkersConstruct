package tinker.tconstruct.container;

import tinker.tconstruct.logic.SmelteryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class SmelteryContainer extends Container
{
    public SmelteryLogic logic;
    public int fuel = 0;

    public SmelteryContainer(InventoryPlayer inventoryplayer, SmelteryLogic frypan)
    {
        logic = frypan;
    	for (int y = 0; y < 3; y++)
    		for (int x = 0; x < 3; x++)
        		this.addSlotToContainer(new Slot(frypan, x + y*3, 62 + x*18, 15 + y*18));
        
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

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        /*for (int i = 0; i < crafters.size(); i++)
        {
            ICrafting icrafting = (ICrafting)crafters.get(i);
            if (progress != logic.progress)
            {
                icrafting.sendProgressBarUpdate(this, 0, logic.progress);
            }
            if (fuel != logic.fuel)
            {
                icrafting.sendProgressBarUpdate(this, 1, logic.fuel);
            }
            if (fuelGague != logic.fuelGague)
            {
                icrafting.sendProgressBarUpdate(this, 2, logic.fuelGague);
            }
        }

        progress = logic.progress;
        fuel = logic.fuel;
        fuelGague = logic.fuelGague;*/
    }

    public void updateProgressBar(int id, int value)
    {
        if (id == 0)
        {
            logic.fuelGague = value;
        }
       /* if (id == 1)
        {
            logic.fuel = value;
        }*/
        /*if (id == 2)
        {
            logic.fuelGague = value;
        }*/
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return logic.isUseableByPlayer(entityplayer);
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
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory(), false))
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
}
