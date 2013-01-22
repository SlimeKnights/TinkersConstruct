package tinker.tconstruct.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tinker.common.InventoryLogic;
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
	public ItemStack decrStackSize(int slot, int quantity)
    {
		if (slot == 1)
			super.decrStackSize(0, 1);
		return super.decrStackSize(slot, quantity);
    }
}
