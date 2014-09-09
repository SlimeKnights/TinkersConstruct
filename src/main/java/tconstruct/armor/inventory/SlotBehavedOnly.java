package tconstruct.armor.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.mechworks.landmine.behavior.Behavior;

public class SlotBehavedOnly extends Slot
{

    public SlotBehavedOnly(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid (ItemStack par1ItemStack)
    {
        Behavior b = Behavior.getBehaviorFromStack(par1ItemStack);

        if (b == null)
        {
            return false;
        }

        int stackL = b.getStackLimit(par1ItemStack);

        if (b != null && this.inventory.getStackInSlot(this.slotNumber) != null && !this.inventory.getStackInSlot(this.slotNumber).isItemEqual(par1ItemStack))
        {
            ItemStack is = this.inventory.getStackInSlot(this.slotNumber);
            if (is.stackSize + par1ItemStack.stackSize <= stackL)
            {
                return true;
            }
        }
        else
        {
            return par1ItemStack.stackSize <= stackL;
        }

        return false;
        // return b != null;
    }

    public int getSlotStackLimit (ItemStack par1ItemStack)
    {
        return Behavior.getBehaviorFromStack(par1ItemStack) != null ? Behavior.getBehaviorFromStack(par1ItemStack).getStackLimit(par1ItemStack) : 1;
    }
}
