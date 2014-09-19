package tconstruct.plugins.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import tconstruct.library.crafting.AlloyMix;
import tconstruct.library.crafting.Smeltery;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;

public class RecipeHandlerAlloying extends RecipeHandlerBase
{

    public static final Rectangle OUTPUT_TANK = new Rectangle(118, 9, 18, 32);

    public class CachedAlloyingRecipe extends CachedBaseRecipe
    {
        private List<FluidTankElement> fluidTanks;
        private int minAmount;

        public CachedAlloyingRecipe(AlloyMix recipe)
        {
            this.fluidTanks = new ArrayList<FluidTankElement>();

            int maxAmount = recipe.mixers.get(0).amount;
            int mult = 1;
            this.minAmount = maxAmount;
            for (FluidStack stack : recipe.mixers)
            {
                if (stack.amount > maxAmount)
                {
                    maxAmount = stack.amount;
                }
                if (stack.amount < this.minAmount)
                {
                    this.minAmount = stack.amount;
                }
            }
            FluidTankElement tank = new FluidTankElement(OUTPUT_TANK, maxAmount * mult, recipe.result);
            tank.fluid.amount *= mult;
            this.fluidTanks.add(tank);

            int width = 36 / recipe.mixers.size();
            int counter = 0;
            for (FluidStack stack : recipe.mixers)
            {
                if (counter == recipe.mixers.size() - 1)
                {
                    tank = new FluidTankElement(new Rectangle(21 + width * counter, 9, 36 - width * counter, 32), maxAmount * mult, stack);
                }
                else
                {
                    tank = new FluidTankElement(new Rectangle(21 + width * counter, 9, width, 32), maxAmount * mult, stack);
                }
                tank.fluid.amount *= mult;
                this.fluidTanks.add(tank);
                counter++;
            }
        }

        @Override
        public PositionedStack getIngredient ()
        {
            return null;
        }

        @Override
        public PositionedStack getResult ()
        {
            return null;
        }

        @Override
        public List<FluidTankElement> getFluidTanks ()
        {
            return this.fluidTanks;
        }
    }

    @Override
    public String getRecipeName ()
    {
        return "Smeltery Alloying";
    }

    @Override
    public String getRecipeID ()
    {
        return "tconstruct.smeltery.alloying";
    }

    @Override
    public String getGuiTexture ()
    {
        return "tinker:textures/gui/nei/smeltery.png";
    }

    @Override
    public void loadTransferRects ()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(76, 21, 22, 15), this.getRecipeID(), new Object[0]));
    }

    @Override
    public void drawBackground (int recipe)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 62, 160, 65);
    }

    @Override
    public void loadCraftingRecipes (String outputId, Object... results)
    {
        if (outputId.equals(this.getRecipeID()))
        {
            for (AlloyMix recipe : Smeltery.getAlloyList())
            {
                if (!recipe.mixers.isEmpty())
                {
                    this.arecipes.add(new CachedAlloyingRecipe(recipe));
                }
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes (FluidStack result)
    {
        for (AlloyMix recipe : Smeltery.getAlloyList())
        {
            if (areFluidsEqual(recipe.result, result) && !recipe.mixers.isEmpty())
            {
                this.arecipes.add(new CachedAlloyingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes (FluidStack ingredient)
    {
        for (Iterator<AlloyMix> i = Smeltery.getAlloyList().iterator(); i.hasNext();)
        {
            AlloyMix recipe = i.next();
            for (FluidStack liquid : recipe.mixers)
            {
                if (areFluidsEqual(liquid, ingredient) && !recipe.mixers.isEmpty())
                {
                    this.arecipes.add(new CachedAlloyingRecipe(recipe));
                    break;
                }
            }
        }
    }

}
