package tconstruct.plugins;

import tconstruct.TConstruct;

public class NotEnoughItems implements ICompatPlugin
{
    @Override
    public String getModId() {
        return "NotEnoughItems";
    }

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init() {
        TConstruct.logger.info("NotEnoughItems detected. Registering TConstruct NEI plugin.");
        NEICompat.registerNEICompat();
    }

    @Override
    public void postInit() {
        // Nothing
    }

}
