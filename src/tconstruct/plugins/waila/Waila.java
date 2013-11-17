package tconstruct.plugins.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import tconstruct.TConstruct;
import tconstruct.plugins.ICompatPlugin;

public class Waila implements ICompatPlugin
{
    @Override
    public String getModId() {
        return "Waila";
    }

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init() {
        TConstruct.logger.info("Waila detected. Registering TConstruct tank blocks with Waila registry.");

        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.WailaRegistrar.wailaCallback");
    }

    @Override
    public void postInit() {
        // Nothing
    }

}
