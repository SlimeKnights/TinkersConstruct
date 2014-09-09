package tconstruct.library.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.client.FluidRenderProperties;

public class CastingRecipe
{
    public ItemStack output;
    public FluidStack castingMetal;
    public ItemStack cast;
    public boolean consumeCast;
    public int coolTime;
    public FluidRenderProperties fluidRenderProperties;

    public CastingRecipe(ItemStack replacement, FluidStack metal, ItemStack cast, boolean consume, int delay, FluidRenderProperties props)
    {
        castingMetal = metal;
        this.cast = cast;
        output = replacement;
        consumeCast = consume;
        coolTime = delay;
        fluidRenderProperties = props;
    }

    public boolean matches (FluidStack metal, ItemStack inputCast)
    {
        if (castingMetal.isFluidEqual(metal) && ((cast != null && cast.getItemDamage() == Short.MAX_VALUE && inputCast.getItem() == cast.getItem()) || ItemStack.areItemStacksEqual(this.cast, inputCast)))
        {
            return true;
        }
        else
            return false;
    }

    public ItemStack getResult ()
    {
        return output.copy();
    }
}