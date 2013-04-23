package mods.tinker.tconstruct.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

/* Melting becomes hardened */
public class LiquidBlockCasting
{
    public static LiquidBlockCasting instance = new LiquidBlockCasting();
    private ArrayList<CastingRecipe> blockCasts = new ArrayList<CastingRecipe>();

    /** Adds a casting recipe
     * 
     * @param output Result of the cast
     * @param metal Liquid to be used in casting. This also decides how much metal is consumed
     * @param cast The empty item to cast with. ex Ingot Cast
     * @param consume Whether the item should be consumed while casting
     * @param delay Time to cast in ticks
     */
    public static void addBlockCastingRecipe (ItemStack output, LiquidStack metal, ItemStack cast, boolean consume, int delay)
    {
        instance.blockCasts.add(new CastingRecipe(output, metal, cast, consume, delay));
    }

    /** Adds a casting recipe. Does not consume the cast
     * 
     * @param output Result of the cast
     * @param metal Liquid to be used in casting. This also decides how much metal is consumed
     * @param cast The empty item to cast with. ex Ingot Cast
     * @param delay Time to cast in ticks
     */
    public static void addBlockCastingRecipe (ItemStack output, LiquidStack metal, ItemStack cast, int delay)
    {
        addBlockCastingRecipe(output, metal, cast, false, delay);
    }

    /** Adds a casting recipe. Does not consume the cast or have an item to cast against
     * 
     * @param output Result of the cast
     * @param metal Liquid to be used in casting. This also decides how much metal is consumed
     * @param delay Time to cast in ticks
     */
    public static void addBlockCastingRecipe (ItemStack output, LiquidStack metal, int delay)
    {
        addBlockCastingRecipe(output, metal, null, false, delay);
    }

    public int getCastingDelay (LiquidStack metal, ItemStack cast)
    {
        for (CastingRecipe recipe : blockCasts)
        {
            if (recipe.matches(metal, cast))
                return recipe.coolTime;
        }
        return -1;
    }

    public int getCastingAmount (LiquidStack metal, ItemStack cast)
    {
        CastingRecipe recipe = getCastingRecipe(metal, cast);
        if (recipe != null)
            return recipe.castingMetal.amount;
        return 0;
    }

    public CastingRecipe getCastingRecipe (LiquidStack metal, ItemStack cast)
    {
        for (CastingRecipe recipe : blockCasts)
        {
            if (recipe.matches(metal, cast))
                return recipe;
        }
        return null;
    }
    
    //Getter for public viewing
    public ArrayList<CastingRecipe> getBlockCastingRecipes()
    {
        return blockCasts;
    }
}
