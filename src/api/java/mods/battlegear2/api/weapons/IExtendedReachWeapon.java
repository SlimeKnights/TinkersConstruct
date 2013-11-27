package mods.battlegear2.api.weapons;

import net.minecraft.item.ItemStack;

public interface IExtendedReachWeapon {
	/**
	 * The distance the weapon will hit (note this will ONLY work for main hand weapons)
	 * @param stack
	 * @return
	 */
	public float getReachModifierInBlocks(ItemStack stack);
}
