package tconstruct.plugins.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.CastingRecipe;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public abstract class RecipeHandlerCastingBase extends RecipeHandlerBase
{
    public static final Rectangle MOLTEN_FLOW = new Rectangle(60, 8, 6, 11);
    public static final Rectangle MOLTEN_FLOW_NO_ITEM = new Rectangle(60, 8, 6, 27);

    public class CachedCastingRecipe extends CachedBaseRecipe
    {
        private List<PositionedStack> resources;
        private FluidTankElement metal;
        private PositionedStack output = null;

        public CachedCastingRecipe(CastingRecipe recipe)
        {
            this.metal = new FluidTankElement(MOLTEN_FLOW, recipe.castingMetal.amount, recipe.castingMetal);
            this.metal.flowingTexture = true;
            this.resources = new ArrayList<PositionedStack>();
            if (recipe.cast != null)
            {
                this.resources.add(new PositionedStack(recipe.cast, 55, 19));
            }
            else
            {
                this.metal.position = MOLTEN_FLOW_NO_ITEM;
            }
            this.output = new PositionedStack(recipe.output, 110, 18);
        }

        @Override
        public List<PositionedStack> getIngredients ()
        {
            return getCycledIngredients(cycleticks / 20, this.resources);
        }

        @Override
        public PositionedStack getResult ()
        {
            return this.output;
        }

        @Override
        public List<FluidTankElement> getFluidTanks ()
        {
            List<FluidTankElement> res = new ArrayList<FluidTankElement>();
            res.add(this.metal);
            return res;
        }
    }

    @Override
    public String getRecipeID ()
    {
        return "tconstruct.smeltery.casting";
    }

    @Override
    public String getGuiTexture ()
    {
        return "tinker:textures/gui/nei/casting.png";
    }

    @Override
    public void loadTransferRects ()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(76, 18, 22, 15), this.getRecipeID(), new Object[0]));
    }

    public abstract List<CastingRecipe> getCastingRecipes ();

    @Override
    public void loadCraftingRecipes (String outputId, Object... results)
    {
        if (outputId.equals(this.getRecipeID()))
        {
            for (CastingRecipe recipe : this.getCastingRecipes())
            {
                this.arecipes.add(new CachedCastingRecipe(recipe));
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes (ItemStack result)
    {
        for (CastingRecipe recipe : getCastingRecipes())
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(result, recipe.getResult()))
            {
                this.arecipes.add(new CachedCastingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes (ItemStack ingred)
    {
        for (CastingRecipe recipe : getCastingRecipes())
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.cast, ingred))
            {
                CachedCastingRecipe irecipe = new CachedCastingRecipe(recipe);
                irecipe.setIngredientPermutation(irecipe.resources, ingred);
                this.arecipes.add(irecipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes (FluidStack ingredient)
    {
        for (CastingRecipe recipe : getCastingRecipes())
        {
            if (areFluidsEqual(recipe.castingMetal, ingredient))
            {
                this.arecipes.add(new CachedCastingRecipe(recipe));
            }
        }
    }

}
