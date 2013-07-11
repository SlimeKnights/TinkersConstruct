package mods.tinker.tconstruct.library.util;

import mods.tinker.tconstruct.library.crafting.PatternBuilder.MaterialSet;
import net.minecraft.item.ItemStack;

public interface IPattern
{
    public int getPatternCost (ItemStack pattern);

    public ItemStack getPatternOutput (ItemStack pattern, ItemStack input, MaterialSet set);
}
