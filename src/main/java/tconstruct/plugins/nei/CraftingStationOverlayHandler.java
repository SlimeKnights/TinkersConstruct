package tconstruct.plugins.nei;

import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import tconstruct.tools.gui.CraftingStationGui;
import tconstruct.tools.gui.ToolStationGui;
import tconstruct.tools.inventory.CraftingStationContainer;
import tconstruct.tools.inventory.ToolStationContainer;
import tconstruct.tools.logic.CraftingStationLogic;

/**
 * Modified copy of DefaultOverlayHandler from NotEnoughItems
 */
public class CraftingStationOverlayHandler extends DefaultOverlayHandler
{
    @Override
    public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        if(gui.inventorySlots instanceof CraftingStationContainer) {
            if(((CraftingStationContainer) gui.inventorySlots).logic.chest != null) {
                offsetx = 5 + CraftingStationGui.CHEST_WIDTH;
            }
            else {
                offsetx = 5;
            }
        }

        super.overlayRecipe(gui, recipe, recipeIndex, shift);
    }

    @Override
    public boolean canMoveFrom(Slot slot, GuiContainer gui) {
        if(gui.inventorySlots instanceof CraftingStationContainer) {
            CraftingStationLogic logic = ((CraftingStationContainer) gui.inventorySlots).logic;
            if(logic.chest != null && slot.inventory == logic.chest.get())
                return true;
            if(logic.doubleChest != null && slot.inventory == logic.doubleChest.get())
                return true;
        }

        return super.canMoveFrom(slot, gui);
    }
}
