package tconstruct.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import tconstruct.tools.gui.CraftingStationGui;

public class NEICompat
{

    public static void registerNEICompat ()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            API.registerGuiOverlay(CraftingStationGui.class, "crafting");
            API.registerGuiOverlayHandler(CraftingStationGui.class, new DefaultOverlayHandler(), "crafting");
        }
    }

}
