package tinker.armory.content;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tinker.common.InventoryLogic;

public abstract class DisplayLogic extends InventoryLogic
{
	public DisplayLogic(int invSize)
	{
		super(invSize + 2); //Skin blocks
	}

	public ItemStack getFrontDisplay()
	{
		if (inventory[0] == null || !(inventory[0].getItem() instanceof ItemBlock))
			return new ItemStack(Block.planks, 1, 1);
		else
			return inventory[0];
	}

	public ItemStack getBackDisplay()
	{
		if (inventory[1] == null || !(inventory[1].getItem() instanceof ItemBlock))
			return new ItemStack(Block.cloth, 1, 3);
		else
			return inventory[1];
	}
}
