package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.library.InventoryLogic;
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

	public void setEquipmentItem(ItemStack stack)
	{
		inventory[0] = stack.copy();
	}
	
	public boolean hasEquipmentItem()
	{
		return inventory[0] != null;
	}
	
	public ItemStack getEquipmentItem()
	{
		return inventory[0];
	}
	
	/*@Override
	public ItemStack getStackInSlot(int slot)
    {
        return inventory[slot +1];
    }
    
    public boolean isStackInSlot(int slot)
    {
    	return inventory[slot +1] != null;
    }
	
    @Override
	public int getSizeInventory()
    {
        return inventory.length - 1;
    }*/
}
