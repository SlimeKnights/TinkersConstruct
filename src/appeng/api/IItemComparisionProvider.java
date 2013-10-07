package appeng.api;

import net.minecraft.item.ItemStack;

/**
 * Provider for special comparisons.
 * when an item is encountered AE Will request if the comparison function handles the item, by trying to request a IItemComparison class.
 */
public interface IItemComparisionProvider {
	
	/**
	 * should return a new IItemComparison, or return null if it dosn't handle the supplied item.
	 * @param is
	 * @return IItemComparison, or null
	 */
	IItemComparison getComparison( ItemStack is );
	
	/**
	 * Simple test for support ( AE generally skips this and calls the above function. ) 
	 * @param stack
	 * @return true, if getComparison will return a valid IItemComparison Object.
	 */
	public boolean canHandle( ItemStack stack);
	
}
