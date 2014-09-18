package tconstruct.smeltery.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import mantle.utils.ItemMetaWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import tconstruct.library.crafting.Smeltery;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class RecipeHandlerMelting extends RecipeHandlerBase
{

    public static final Rectangle MOLTEN_TANK = new Rectangle(115, 20, 18, 18);

    public class CachedMeltingRecipe extends CachedBaseRecipe
    {
        private PositionedStack input;
        private int temperature;
        private FluidTankElement output;

        public CachedMeltingRecipe(ItemStack input)
        {
            this.input = new PositionedStack(input, 28, 21);
            this.temperature = Smeltery.getLiquifyTemperature(input);
            this.output = new FluidTankElement(MOLTEN_TANK, 1, Smeltery.getSmelteryResult(input));
            this.output.capacity = this.output.fluid != null ? this.output.fluid.amount : 1000;
        }

        @Override
        public PositionedStack getIngredient ()
        {
            return this.input;
        }

        @Override
        public PositionedStack getResult ()
        {
            return null;
        }

        @Override
        public List<FluidTankElement> getFluidTanks ()
        {
            List<FluidTankElement> tanks = new ArrayList<FluidTankElement>();
            tanks.add(this.output);
            return tanks;
        }
    }

    @Override
    public String getRecipeName ()
    {
        return "Smeltery Melting";
    }

    @Override
    public String getRecipeID ()
    {
        return "tconstruct.smeltery.melting";
    }

    @Override
    public String getGuiTexture ()
    {
        return "tinker:textures/gui/nei/smeltery.png";
    }

    @Override
    public void loadTransferRects ()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(72, 20, 16, 34), this.getRecipeID(), new Object[0]));
    }

    @Override
    public void drawBackground (int recipe)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 160, 55);
    }

    @Override
    public void drawExtras (int recipe)
    {
        int temperature = ((CachedMeltingRecipe) this.arecipes.get(recipe)).temperature;
        GuiDraw.drawStringC(temperature + " C", 81, 9, 0x808080, false);
    }

    @Override
    public void loadCraftingRecipes (String outputId, Object... results)
    {
        if (outputId.equals(getRecipeID()))
        {
            for (ItemMetaWrapper key : Smeltery.getSmeltingList().keySet())
            {
                this.arecipes.add(new CachedMeltingRecipe(new ItemStack(key.item, 1, key.meta)));
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
        for (Entry<ItemMetaWrapper, FluidStack> pair : Smeltery.getSmeltingList().entrySet())
        {
            if (areFluidsEqual(pair.getValue(), result))
            {
                this.arecipes.add(new CachedMeltingRecipe(new ItemStack(pair.getKey().item, 1, pair.getKey().meta)));
            }
        }
    }

    @Override
    public void loadUsageRecipes (ItemStack ingred)
    {
        for (ItemMetaWrapper key : Smeltery.getSmeltingList().keySet())
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(new ItemStack(key.item, 1, key.meta), ingred))
            {
                this.arecipes.add(new CachedMeltingRecipe(new ItemStack(key.item, 1, key.meta)));
            }
        }
    }

}
