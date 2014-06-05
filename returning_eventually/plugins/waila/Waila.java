package tconstruct.plugins.waila;

import mantle.module.ILoadableModule;
import cpw.mods.fml.common.event.FMLInterModComms;
import tconstruct.TConstruct;

public class Waila implements ILoadableModule
{
    @SuppressWarnings("unused")
    public static String modId = "Waila";

    @Override
    public void preInit ()
    {
        // Nothing
    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("Waila detected. Registering TConstruct tank blocks with Waila registry.");

        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.WailaRegistrar.wailaCallback");
    }

    @Override
    public void postInit ()
    {
        // Nothing
    }
    


}
