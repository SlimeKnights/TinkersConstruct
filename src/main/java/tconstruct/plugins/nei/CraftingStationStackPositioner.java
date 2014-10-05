package tconstruct.plugins.nei;

import java.util.ArrayList;

import tconstruct.TConstruct;
import tconstruct.tools.gui.CraftingStationGui;
import net.minecraft.client.Minecraft;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.GuiRecipe;

public class CraftingStationStackPositioner implements IStackPositioner
{

    @Override
    public ArrayList<PositionedStack> positionStacks (ArrayList<PositionedStack> stacks)
    {

        if (Minecraft.getMinecraft().currentScreen instanceof GuiRecipe)
        {
            GuiRecipe recipeGui = (GuiRecipe) Minecraft.getMinecraft().currentScreen;

            if (!(recipeGui.firstGui instanceof CraftingStationGui))
            {
                TConstruct.logger.warn("No CraftingStationGui found!");
                return stacks;
            }

            CraftingStationGui gui = (CraftingStationGui) recipeGui.firstGui;

            int offsetX = gui.hasChest() ? 5 + CraftingStationGui.CHEST_WIDTH : 5;
            int offsetY = 11;

            for (PositionedStack stack : stacks)
            {
                stack.relx += offsetX;
                stack.rely += offsetY;
            }
        }

        return stacks;
    }

}
