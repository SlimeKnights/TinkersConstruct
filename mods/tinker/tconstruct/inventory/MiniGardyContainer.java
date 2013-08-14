package mods.tinker.tconstruct.inventory;

import mods.tinker.tconstruct.entity.Gardeslime;
import mods.tinker.tconstruct.library.util.IPattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MiniGardyContainer extends Container
{
    public Gardeslime gardy;

    public MiniGardyContainer(InventoryPlayer inventoryplayer, Gardeslime entity)
    {
        gardy = entity;
        for (int column = 0; column < 2; column++)
        {
            for (int row = 0; row < 7; row++)
            {
                this.addSlotToContainer(new Slot(entity, row + column * 7, 44 + row * 18, 36 + column * 18));
            }
        }

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 86 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 144));
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        return gardy.isUseableByPlayer(entityplayer);
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

            if (slotID < gardy.getSizeInventory())
            {
                if (!this.mergeItemStack(slotStack, gardy.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, gardy.getSizeInventory(), false))
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
