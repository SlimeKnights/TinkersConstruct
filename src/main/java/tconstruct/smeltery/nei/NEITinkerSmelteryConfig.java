package tconstruct.smeltery.nei;

import tconstruct.TConstruct;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEITinkerSmelteryConfig implements IConfigureNEI
{

    @Override
    public void loadConfig ()
    {
        if (TConstruct.pulsar.isPulseLoaded("Tinkers' Smeltery"))
        {
            API.registerRecipeHandler(new RecipeHandlerMelting());
            API.registerUsageHandler(new RecipeHandlerMelting());
            API.registerRecipeHandler(new RecipeHandlerAlloying());
            API.registerUsageHandler(new RecipeHandlerAlloying());
            API.registerRecipeHandler(new RecipeHandlerCastingTable());
            API.registerUsageHandler(new RecipeHandlerCastingTable());
            API.registerRecipeHandler(new RecipeHandlerCastingBasin());
            API.registerUsageHandler(new RecipeHandlerCastingBasin());
        }
    }

    @Override
    public String getName ()
    {
        return "TinkerSmeltery";
    }

    @Override
    public String getVersion ()
    {
        return "${version}";
    }

}
