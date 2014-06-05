package tconstruct.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mantle.module.ILoadableModule;
import tconstruct.TConstruct;
import tconstruct.client.gui.CraftingStationGui;

public class NotEnoughItems implements ILoadableModule
{
    @SuppressWarnings("unused")
    public static String modId = "NotEnoughItems";

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init() {
        TConstruct.logger.info("NotEnoughItems detected. Registering TConstruct NEI plugin.");
        registerNEICompat();
    }

    @Override
    public void postInit() {
        // Nothing
    }
    public static void registerNEICompat ()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            API.registerGuiOverlay(CraftingStationGui.class, "crafting");
            API.registerGuiOverlayHandler(CraftingStationGui.class, new DefaultOverlayHandler(), "crafting");
        }
    }


}
