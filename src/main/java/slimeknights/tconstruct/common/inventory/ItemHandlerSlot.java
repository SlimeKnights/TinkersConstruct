package slimeknights.tconstruct.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/** Forge still uses dumb vanilla logic for determining slot limits instead of their own method */
public class ItemHandlerSlot extends SlotItemHandler {
	public ItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return getItemHandler().getSlotLimit(getSlotIndex());
	}
}
