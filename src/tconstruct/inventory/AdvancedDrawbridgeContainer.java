package tconstruct.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.blocks.logic.*;

public class AdvancedDrawbridgeContainer extends Container
{
    public AdvancedDrawbridgeLogic logic;
    public int progress = 0;
    public int fuel = 0;
    public int fuelGague = 0;

    public AdvancedDrawbridgeContainer(InventoryPlayer inventoryplayer, AdvancedDrawbridgeLogic logic)
    {
        this.logic = logic;

//        this.addSlotToContainer(new DrawbridgeSlot(logic, logic.selSlot < logic.getSizeInventory() ? logic.selSlot : 0, 80, 36, logic));
        this.addSlotToContainer(new SlotOpaqueBlocksOnly(logic.camoInventory, 0, 35, 36));

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
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        return logic.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int slotID)
    {
        ItemStack stack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotID);

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
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}
