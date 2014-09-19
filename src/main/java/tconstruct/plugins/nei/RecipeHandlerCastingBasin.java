package tconstruct.plugins.nei;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.LiquidCasting;
import codechicken.lib.gui.GuiDraw;

public class RecipeHandlerCastingBasin extends RecipeHandlerCastingBase
{

    @Override
    public String getRecipeName ()
    {
        return "Casting Basin";
    }

    @Override
    public void drawBackground (int recipe)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(30, 0, 0, 62, 112, 55);
    }

    @Override
    public List<CastingRecipe> getCastingRecipes ()
    {
        LiquidCasting casting = TConstructRegistry.getBasinCasting();
        if (casting == null)
        {
            return new ArrayList<CastingRecipe>();
        }
        return casting.getCastingRecipes();
    }

}
