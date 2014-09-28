package tconstruct.library.crafting;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.client.FluidRenderProperties;

/* Melting becomes hardened */
public class LiquidCasting
{
    // public static LiquidCasting instance = new LiquidCasting();
    private final ArrayList<CastingRecipe> casts = new ArrayList<CastingRecipe>();

    private boolean contains (CastingRecipe recipe)
    {
        for (CastingRecipe c : casts)
        {
            if (c.matches(recipe.castingMetal, recipe.cast))
                return true;
        }
        return false;
    }

    /**
     * Adds a casting recipe
     * 
     * @param output
     *            Result of the cast
     * @param metal
     *            Liquid to be used in casting. This also decides how much metal
     *            is consumed
     * @param cast
     *            The empty item to cast with. ex Ingot Cast
     * @param consume
     *            Whether the item should be consumed while casting
     * @param delay
     *            Time to cast in ticks
     * @param props
     *            The rendering properties for the casting fluid (may be null)
     */
    public void addCastingRecipe (ItemStack output, FluidStack metal, ItemStack cast, boolean consume, int delay, FluidRenderProperties props)
    {
        CastingRecipe cr = new CastingRecipe(output, metal, cast, consume, delay, props);
        if (!contains(cr))
            casts.add(cr);
    }

    /**
     * Adds a casting recipe
     * 
     * @param output
     *            Result of the cast
     * @param metal
     *            Liquid to be used in casting. This also decides how much metal
     *            is consumed
     * @param cast
     *            The empty item to cast with. ex Ingot Cast
     * @param consume
     *            Whether the item should be consumed while casting
     * @param delay
     *            Time to cast in ticks
     */
    public void addCastingRecipe (ItemStack output, FluidStack metal, ItemStack cast, boolean consume, int delay)
    {
        addCastingRecipe(output, metal, cast, consume, delay, null);
    }

    /**
     * Adds a casting recipe. Does not consume the cast
     * 
     * @param output
     *            Result of the cast
     * @param metal
     *            Liquid to be used in casting. This also decides how much metal
     *            is consumed
     * @param cast
     *            The empty item to cast with. ex Ingot Cast
     * @param delay
     *            Time to cast in ticks
     * @param props
     *            The rendering properties for the casting fluid (may be null)
     */
    public void addCastingRecipe (ItemStack output, FluidStack metal, ItemStack cast, int delay, FluidRenderProperties props)
    {
        addCastingRecipe(output, metal, cast, false, delay, props);
    }

    /**
     * Adds a casting recipe. Does not consume the cast
     * 
     * @param output
     *            Result of the cast
     * @param metal
     *            Liquid to be used in casting. This also decides how much metal
     *            is consumed
     * @param cast
     *            The empty item to cast with. ex Ingot Cast
     * @param delay
     *            Time to cast in ticks
     */
    public void addCastingRecipe (ItemStack output, FluidStack metal, ItemStack cast, int delay)
    {
        addCastingRecipe(output, metal, cast, false, delay, null);
    }

    /**
     * Adds a casting recipe. Does not consume the cast or have an item to cast
     * against
     * 
     * @param output
     *            Result of the cast
     * @param metal
     *            Liquid to be used in casting. This also decides how much metal
     *            is consumed
     * @param delay
     *            Time to cast in ticks
     * @param props
     *            The rendering properties for the casting fluid (may be null)
     */
    public void addCastingRecipe (ItemStack output, FluidStack metal, int delay, FluidRenderProperties props)
    {
        addCastingRecipe(output, metal, null, false, delay, props);
    }

    /**
     * Adds a casting recipe. Does not consume the cast or have an item to cast
     * against
     * 
     * @param output
     *            Result of the cast
     * @param metal
     *            Liquid to be used in casting. This also decides how much metal
     *            is consumed
     * @param delay
     *            Time to cast in ticks
     */
    public void addCastingRecipe (ItemStack output, FluidStack metal, int delay)
    {
        addCastingRecipe(output, metal, null, false, delay, null);
    }

    public void addCustomCastingRecipe (CastingRecipe recipe)
    {
        casts.add(recipe);
    }

    public int getCastingDelay (FluidStack metal, ItemStack cast)
    {
        CastingRecipe recipe = getCastingRecipe(metal, cast);
        if (recipe != null)
            return recipe.coolTime;
        return -1;
    }

    public int getCastingAmount (FluidStack metal, ItemStack cast)
    {
        CastingRecipe recipe = getCastingRecipe(metal, cast);
        if (recipe != null)
            return recipe.castingMetal.amount;
        return 0;
    }

    public CastingRecipe getCastingRecipe (FluidStack metal, ItemStack cast)
    {
        for (CastingRecipe recipe : casts)
        {
            if (recipe.matches(metal, cast))
                return recipe;
        }
        return null;
    }

    // Getter for public viewing
    public ArrayList<CastingRecipe> getCastingRecipes ()
    {
        return casts;
    }
}