package cofh.core.item;

import net.minecraft.item.ItemStack;

public interface IEqualityOverrideItem {

	public boolean isLastHeldItemEqual(ItemStack current, ItemStack previous);

}