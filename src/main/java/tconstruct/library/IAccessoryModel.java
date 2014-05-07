package tconstruct.library;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
    public ResourceLocation getWearbleTexture(Entity entity, ItemStack stack, int slot);
}
