package tconstruct.library.accessory;

import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IAccessoryModel
{
    /** Similar to how armor is rendered.
     * 
     * @param stack
     * @param entity
     * @param slot
     * @return Resource location of the texture. Return null for none
     */

    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearbleTexture (Entity entity, ItemStack stack, int slot);
}
