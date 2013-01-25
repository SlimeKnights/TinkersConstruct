package tinker.tconstruct.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tinker.common.InventoryLogic;
import tinker.tconstruct.TConstructContent;
import tinker.tconstruct.client.gui.PatternShaperContainer;

public class PatternShaperLogic extends InventoryLogic
{
	public PatternShaperLogic()
	{
		super(2);
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
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
    {
		super.setInventorySlotContents(slot, itemstack);
		if (slot == 0)
			setInventorySlotContents(1, new ItemStack(TConstructContent.woodPattern, 1, 1));
    }
	
	@Override
	public ItemStack decrStackSize(int slot, int quantity)
    {
		if (slot == 1)
		{
			super.decrStackSize(0, 1);
			if (getStackInSlot(0) != null)
				return super.decrStackSize(slot, 0);
		}
		else if (slot == 0)
		{
			ItemStack ret = super.decrStackSize(slot, quantity);
			if (getStackInSlot(0) == null)
				decrStackSize(1, 1);
			return ret;
		}
		return super.decrStackSize(slot, quantity);
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
