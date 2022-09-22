package tconstruct.library.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.client.FluidRenderProperties;

public class CastingRecipe {
    public ItemStack output;
    public FluidStack castingMetal;
    public ItemStack cast;
    public boolean consumeCast;
    public int coolTime;
    public FluidRenderProperties fluidRenderProperties;
    public boolean ignoreNBT;

    public CastingRecipe(
            ItemStack replacement,
            FluidStack metal,
            ItemStack cast,
            boolean consume,
            int delay,
            FluidRenderProperties props,
            boolean ignoreNBT) {
        castingMetal = metal;
        this.cast = cast;
        output = replacement;
        consumeCast = consume;
        coolTime = delay;
        fluidRenderProperties = props;
        this.ignoreNBT = ignoreNBT;
    }

    public CastingRecipe(
            ItemStack replacement,
            FluidStack metal,
            ItemStack cast,
            boolean consume,
            int delay,
            FluidRenderProperties props) {
        this(replacement, metal, cast, consume, delay, props, false);
    }

    public boolean matches(FluidStack metal, ItemStack inputCast) {
        if (castingMetal.isFluidEqual(metal)) {
            if (cast != null
                    && cast.getItemDamage() == OreDictionary.WILDCARD_VALUE
                    && inputCast.getItem() == cast.getItem()) {
                return true;
            } else if (!ignoreNBT && ItemStack.areItemStacksEqual(cast, inputCast)) {
                return true;
            } else if (ignoreNBT && cast != null && inputCast != null && cast.isItemEqual(inputCast)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getResult() {
        return output.copy();
    }
}
