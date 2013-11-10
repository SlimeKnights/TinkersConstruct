package tconstruct.plugins.nei;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.common.FMLCommonHandler;

import codechicken.nei.recipe.DefaultOverlayHandler;

import codechicken.nei.api.API;
import tconstruct.client.gui.CraftingStationGui;

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
