package tconstruct.plugins.nei;

import java.awt.Rectangle;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import tconstruct.library.crafting.DryingRackRecipes;
import tconstruct.library.crafting.DryingRackRecipes.DryingRecipe;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class RecipeHandlerDryingRack extends RecipeHandlerBase
{

    public class CachedDryingRackRecipe extends CachedBaseRecipe
    {

        public PositionedStack input;
        public PositionedStack output;
        public int time;

        public CachedDryingRackRecipe(DryingRecipe drying)
        {
            this.input = new PositionedStack(drying.input, 44, 18);
            this.output = new PositionedStack(drying.result, 98, 18);
            this.time = drying.time;
        }

        @Override
        public PositionedStack getIngredient ()
        {
            return this.input;
        }

        @Override
        public PositionedStack getResult ()
        {
            return this.output;
        }

    }

    @Override
    public String getRecipeName ()
    {
        return "Drying Rack";
    }

    @Override
    public String getRecipeID ()
    {
        return "tconstruct.dryingrack";
    }

    @Override
    public void loadTransferRects ()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(68, 20, 22, 15), this.getRecipeID(), new Object[0]));
    }

    @Override
    public String getGuiTexture ()
    {
        return "tinker:textures/gui/nei/dryingrack.png";
    }

    @Override
    public void drawBackground (int recipe)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 160, 65);
    }

    @Override
    public void drawExtras (int recipe)
    {
        int time = ((CachedDryingRackRecipe) this.arecipes.get(recipe)).time;
        int seconds = time / 20;
        GuiDraw.drawStringC(time + " ticks (" + seconds + " secs)", 81, 40, 0x808080, false);
    }

    @Override
    public void loadCraftingRecipes (String outputId, Object... results)
    {
        if (outputId.equals(this.getRecipeID()))
        {
            for (DryingRecipe drying : DryingRackRecipes.recipes)
            {
                this.arecipes.add(new CachedDryingRackRecipe(drying));
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
        for (DryingRecipe drying : DryingRackRecipes.recipes)
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(drying.result, result))
            {
                this.arecipes.add(new CachedDryingRackRecipe(drying));
            }
        }
    }

    @Override
    public void loadUsageRecipes (ItemStack ingred)
    {
        for (DryingRecipe drying : DryingRackRecipes.recipes)
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(drying.input, ingred))
            {
                this.arecipes.add(new CachedDryingRackRecipe(drying));
            }
        }
    }

}
