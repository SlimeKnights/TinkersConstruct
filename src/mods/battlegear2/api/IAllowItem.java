package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

public interface IAllowItem {

	/**
     * Returns true if the mainhand {@link #ItemStack} allows the offhand {@link #ItemStack} to be placed in the partner offhand slot
     */
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand);
}
