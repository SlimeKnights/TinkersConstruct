package tinker.tconstruct.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tinker.common.InventoryLogic;
import tinker.tconstruct.TContent;
import tinker.tconstruct.container.PatternShaperContainer;

public class PatternShaperLogic extends InventoryLogic
{
	public PatternShaperLogic()
	{
		super(2);
	}
	
	public boolean canUpdate()
    {
        return false;
    }

	@Override
	public String getInvName ()
	{
		return "toolstation.PatternShaper";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new PatternShaperContainer(inventoryplayer, this);
	}
	
	/*@Override
	public void onInventoryChanged()
    {
		if (inventory[0] == null)
			inventory[1] = null;
		super.onInventoryChanged();
    }*/
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
    {
		super.setInventorySlotContents(slot, itemstack);
		if (slot == 0)
			setInventorySlotContents(1, new ItemStack(TContent.woodPattern, 1, 1));
    }
	
	@Override
	public ItemStack decrStackSize(int slot, int quantity)
    {
		if (slot == 1)
		{
			super.decrStackSize(0, 1);
			if (inventory[0] == null)
				return super.decrStackSize(slot, quantity);
			else
				return inventory[1].copy();
		}
		else
		{
			ItemStack ret = super.decrStackSize(slot, quantity);
			if (inventory[0] == null)
				super.decrStackSize(1, 1);
			return ret;
		}
    }
	
	public void altDecrStackSize(int slot, int quantity)
    {
        if (inventory[slot] != null)
        {
            if (inventory[slot].stackSize <= quantity)
            {
                ItemStack stack = inventory[slot];
                inventory[slot] = null;
                return;
            }
            ItemStack split = inventory[slot].splitStack(quantity);
            if (inventory[slot].stackSize == 0)
            {
                inventory[slot] = null;
            }
            return;
        }
    }
	
	@Override
	public boolean canDropInventorySlot(int slot)
	{
		if (slot == 0)
			return true;
		else
			return false;
	}
}
