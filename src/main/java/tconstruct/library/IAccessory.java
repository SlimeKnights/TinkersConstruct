package tconstruct.library;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
