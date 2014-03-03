package common.darkknight.jewelrycraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;

public class ContainerRingChest extends Container
{
    public TileEntityChest theChest;
    
    public ContainerRingChest(InventoryPlayer inv, TileEntityChest chest)
    {
        theChest = chest;
        
        int x, y;
        
        for (x = 0; x < 9; x++)
            addSlotToContainer(new SlotRingChest(inv, x, 8 + (18 * x), 142, x == inv.currentItem));
        for (y = 0; y < 3; y++)
            for (x = 0; x < 9; x++)
                addSlotToContainer(new Slot(inv, x + 9 + (y * 9), 8 + (18 * x), 84 + (y * 18)));

        for (y = 0; y < 3; y++)
            for (x = 0; x < 9; x++)
                addSlotToContainer(new SlotRingChest(chest, 26 - (x + (y * 9)), 8 + (18 * (8 - x)), 17 + ((2 - y) * 18), false));
    }
    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < 27)
            {
                if (!this.mergeItemStack(itemstack1, 27, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 27, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
