package tconstruct.library.util;

import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;

public interface IPattern
{
    public int getPatternCost (ItemStack pattern);

    public ItemStack getPatternOutput (ItemStack pattern, ItemStack input, MaterialSet set);
}
