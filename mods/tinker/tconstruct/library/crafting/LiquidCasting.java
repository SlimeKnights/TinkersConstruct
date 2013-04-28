package mods.tinker.tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

/* Melting becomes hardened */
public class LiquidCasting
{
    public static LiquidCasting instance = new LiquidCasting();
    private ArrayList<CastingRecipe> casts = new ArrayList<CastingRecipe>();

    /** Adds a casting recipe
     * 
     * @param output Result of the cast
     * @param metal Liquid to be used in casting. This also decides how much metal is consumed
     * @param cast The empty item to cast with. ex Ingot Cast
     * @param consume Whether the item should be consumed while casting
     * @param delay Time to cast in ticks
     */
    public static void addCastingRecipe (ItemStack output, LiquidStack metal, ItemStack cast, boolean consume, int delay)
    {
        instance.casts.add(new CastingRecipe(output, metal, cast, consume, delay));
    }

    /** Adds a casting recipe. Does not consume the cast
     * 
     * @param output Result of the cast
     * @param metal Liquid to be used in casting. This also decides how much metal is consumed
     * @param cast The empty item to cast with. ex Ingot Cast
     * @param delay Time to cast in ticks
     */
    public static void addCastingRecipe (ItemStack output, LiquidStack metal, ItemStack cast, int delay)
    {
        addCastingRecipe(output, metal, cast, false, delay);
    }

    /** Adds a casting recipe. Does not consume the cast or have an item to cast against
     * 
     * @param output Result of the cast
     * @param metal Liquid to be used in casting. This also decides how much metal is consumed
     * @param delay Time to cast in ticks
     */
    public static void addCastingRecipe (ItemStack output, LiquidStack metal, int delay)
    {
        addCastingRecipe(output, metal, null, false, delay);
    }

    public int getCastingDelay (LiquidStack metal, ItemStack cast)
    {
        for (CastingRecipe recipe : casts)
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
        for (CastingRecipe recipe : casts)
        {
            if (recipe.matches(metal, cast))
                return recipe;
        }
        return null;
    }
    
  //Getter for public viewing
    public ArrayList<CastingRecipe> getCastingRecipes()
    {
        return casts;
    }
}
