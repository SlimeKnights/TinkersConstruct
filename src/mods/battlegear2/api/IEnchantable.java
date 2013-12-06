package mods.battlegear2.api;

import mods.battlegear2.enchantments.BaseEnchantment;
import net.minecraft.item.ItemStack;

public interface IEnchantable {

	/**
	 * If a Battlegear {@link #BaseEnchantment} can be applied to this item, given the {@link #ItemStack}
	 * @param baseEnchantment 
	 * @param stack
	 * @return
	 */
	public boolean isEnchantable(BaseEnchantment baseEnchantment, ItemStack stack);
}
