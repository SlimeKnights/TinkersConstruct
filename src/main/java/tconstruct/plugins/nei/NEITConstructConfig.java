package tconstruct.plugins.nei;

import tconstruct.tools.gui.CraftingStationGui;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEITConstructConfig implements IConfigureNEI
{

    @Override
    public void loadConfig ()
    {
        API.registerGuiOverlay(CraftingStationGui.class, "crafting", new CraftingStationStackPositioner());
        API.registerGuiOverlayHandler(CraftingStationGui.class, new CraftingStationOverlayHandler(), "crafting");

        registerHandler(new RecipeHandlerDryingRack());
        registerHandler(new RecipeHandlerToolMaterials());
        registerHandler(new RecipeHandlerMelting());
        registerHandler(new RecipeHandlerAlloying());
        registerHandler(new RecipeHandlerCastingTable());
        registerHandler(new RecipeHandlerCastingBasin());
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

    private static void registerHandler (RecipeHandlerBase handler)
    {
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

}
