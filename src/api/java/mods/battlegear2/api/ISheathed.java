package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

public interface ISheathed {

    /**
     * Returns true if the item should always be sheathed on the back, false if it should be sheathed on the hip
     * @param item the {@link #ItemStack} to be sheathed
     */
    public boolean sheatheOnBack(ItemStack item);
}
