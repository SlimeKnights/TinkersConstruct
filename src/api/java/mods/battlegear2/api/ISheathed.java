package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

/**
 * When a {@link net.minecraft.entity.player.EntityPlayer} is not in "battlemode", the {@link ItemStack}s stored in Battlegear additional slots
 * and previously selected are displayed, either on the back (slightly moved depending on armor) or on the hip (opposite side to the hand slot it is stored in)
 * Note:Default behavior is dependent on Battlegear configuration
 * This interface can be implemented in a {@link net.minecraft.item.Item} instance to decide where to actually render it
 * See {@link mods.battlegear2.api.RenderPlayerEventChild.PreRenderSheathed} and {@link mods.battlegear2.api.RenderPlayerEventChild.PostRenderSheathed}
 * for more flexibility over the rendering
 */
public interface ISheathed {

    /**
     * Returns true if this item should always be sheathed on the back, false if it should be sheathed on the hip
     * @param item the {@link ItemStack} to be sheathed
     */
    public boolean sheatheOnBack(ItemStack item);
}
