package tconstruct.plugins.nei;

import codechicken.nei.recipe.DefaultOverlayHandler;

import codechicken.nei.api.API;
import tconstruct.client.gui.CraftingStationGui;

public class NEICompat {

	public static void registerNEICompat(){
		API.registerGuiOverlay(CraftingStationGui.class, "crafting");
		API.registerGuiOverlayHandler(CraftingStationGui.class, new DefaultOverlayHandler(), "crafting");
	}
	
}
