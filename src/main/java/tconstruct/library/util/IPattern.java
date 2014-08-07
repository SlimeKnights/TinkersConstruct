package tconstruct.library.util;

import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;

public interface IPattern
{
    public int getPatternCost (int patternID);

    public ItemStack getPatternOutput (int patternID, ItemStack input, MaterialSet set);
}
