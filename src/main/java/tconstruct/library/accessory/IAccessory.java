package tconstruct.library.accessory;

import net.minecraft.item.ItemStack;

public interface IAccessory
{
    /** Accessory slots are not the same as armor slots!
     * 
     * @param item Instance of the item
     * @param slot Accessory slot
     * @return Whether the accessory can be inserted into the slot
     */
    public boolean canEquipAccessory (ItemStack item, int slot);
}
