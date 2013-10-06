package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;

public interface IDeepStorageUnit
{
	/**
	 * @return A populated ItemStack with stackSize for the full amount of materials in the DSU. May have a stackSize > getMaxStackSize().
	 */
	ItemStack getStoredItemType();
	
	/**
	 * Sets the total amount of the item currently being stored, or zero if it wants to remove all items.
	 */
	void setStoredItemCount(int amount);
	
	/**
	 * Sets the type of the stored item and initializes the number of stored items to count. Will overwrite any existing stored items.
	 */
	void setStoredItemType(int itemID, int meta, int Count);
	
	/**
	 * @return The maximum number of items the DSU can hold.
	 */
	int getMaxStoredCount();
}
