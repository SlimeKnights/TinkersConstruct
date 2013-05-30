package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomItem;

public class MobDrop extends WeightedRandomItem
{
	private ItemStack _stack;
	
	public MobDrop(int weight, ItemStack stack)
	{
		super(weight);
		_stack = stack;
	}
	
	public ItemStack getStack()
	{
		if(_stack == null) return null;
		return _stack.copy();
	}
}
