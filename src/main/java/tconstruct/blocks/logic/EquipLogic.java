package tconstruct.blocks.logic;

import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.item.ItemStack;

/* Slots
 * 0: Frying pan item
 * 1: Fuel
 * 2-9: Food
 */

public abstract class EquipLogic extends InventoryLogic
{

    public EquipLogic(int invSize)
    {
        super(invSize);
    }

    public void setEquipmentItem (ItemStack stack)
    {
        inventory[0] = stack.copy();
    }

    public boolean hasEquipmentItem ()
    {
        return inventory[0] != null;
    }

    public ItemStack getEquipmentItem ()
    {
        return inventory[0];
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack)
    {
        if (slot == 0)
        {
            return;
        }
        else
        {
            super.setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return slot != 0 ? inventory[slot] : null;
    }

    public boolean isStackInSlot (int slot)
    {
        return slot != 0 ? inventory[slot] != null : false;
    }
}
