package tconstruct.library.weaponry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IAmmo {
    /**
     * Returns the current ammo amount of the item.
     */
    int getAmmoCount(ItemStack stack);

    /**
     * Returns the maximum amount of ammo the item can have
     */
    int getMaxAmmo(ItemStack stack);

    /**
     * Returns the maximum amount of ammo the item can have
     * @param tags InfiTool tag compound of the item
     */
    int getMaxAmmo(NBTTagCompound tags);

    /**
     * Adds the given amount of ammo cache. Will not go over max-ammo.
     * @param count How much to add
     * @param stack The itemstack to add the ammo to. Has to have the proper NBT.
     * @return The amount of ammo that couldn't be added.
     */
    int addAmmo(int count, ItemStack stack);

    /**
     * Removes the given amount fro the ammo cache. Will not go below zero.
     * @param count How much to remove
     * @param stack The itemstack to add the ammo to. Has to have the proper NBT.
     * @return The amount of ammo that couldn't be removed. If you try to remove 5, but only 3 are left, it'll return 2.
     */
    int consumeAmmo(int count, ItemStack stack);
}
