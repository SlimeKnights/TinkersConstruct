package tconstruct.plugins.nei;

import tconstruct.tools.gui.CraftingStationGui;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;

public class NEITConstructConfig implements IConfigureNEI
{

    @Override
    public void loadConfig ()
    {
        API.registerGuiOverlay(CraftingStationGui.class, "crafting");
        API.registerGuiOverlayHandler(CraftingStationGui.class, new DefaultOverlayHandler(), "crafting");

        API.registerRecipeHandler(new RecipeHandlerDryingRack());
        API.registerUsageHandler(new RecipeHandlerDryingRack());

        API.registerRecipeHandler(new RecipeHandlerMelting());
        API.registerUsageHandler(new RecipeHandlerMelting());
        API.registerRecipeHandler(new RecipeHandlerAlloying());
        API.registerUsageHandler(new RecipeHandlerAlloying());
        API.registerRecipeHandler(new RecipeHandlerCastingTable());
        API.registerUsageHandler(new RecipeHandlerCastingTable());
        API.registerRecipeHandler(new RecipeHandlerCastingBasin());
        API.registerUsageHandler(new RecipeHandlerCastingBasin());
    }

    @Override
    public String getName ()
    {
        return "TConstruct";
    }

    @Override
    public String getVersion ()
    {
        return "${version}";
    }

}
