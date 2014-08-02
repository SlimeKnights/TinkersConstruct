package tconstruct.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;
import tconstruct.tools.gui.CraftingStationGui;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import cpw.mods.fml.relauncher.Side;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers Not Enough Items Addon", description = "The NEI addon for Tinkers.", modsRequired = "NotEnoughItems", forced = true)
public class TinkerNEI {
    
    @Handler
    public void init (FMLInitializationEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            API.registerGuiOverlay(CraftingStationGui.class, "crafting");
            API.registerGuiOverlayHandler(CraftingStationGui.class, new DefaultOverlayHandler(), "crafting");
        }
    }
    
}
