package tconstruct.plugins;

import cpw.mods.fml.common.Loader;
import tconstruct.TConstruct;
import tconstruct.plugins.fmp.ForgeMultiPart;
import tconstruct.plugins.minefactoryreloaded.MineFactoryReloaded;
import tconstruct.plugins.nei.NotEnoughItems;
import tconstruct.plugins.waila.Waila;

import java.util.ArrayList;
import java.util.List;

public class PluginController
{

    private enum Phase
    {
        PRELAUNCH, PREINIT, INIT, POSTINIT, DONE
    }

    private static PluginController instance;
    private List<ICompatPlugin> plugins = new ArrayList<ICompatPlugin>();
    private Phase currPhase = Phase.PRELAUNCH;

    private PluginController()
    {
    }

    public static PluginController getController ()
    {
        if (instance == null)
            instance = new PluginController();
        return instance;
    }

    public void registerPlugin (ICompatPlugin plugin)
    {
        if (Loader.isModLoaded(plugin.getModId()))
        {
            TConstruct.logger.info("Registering compat plugin for " + plugin.getModId());
            plugins.add(plugin);

            switch (currPhase)
            // Play catch-up if plugin is registered late
            {
            case DONE:
            case POSTINIT:
                plugin.preInit();
                plugin.init();
                plugin.postInit();
                break;
            case INIT:
                plugin.preInit();
                plugin.init();
                break;
            case PREINIT:
                plugin.preInit();
                break;
            default:
                break;
            }
        }
    }

    public void preInit ()
    {
        currPhase = Phase.PREINIT;
        for (ICompatPlugin plugin : plugins)
            plugin.preInit();
    }

    public void init ()
    {
        currPhase = Phase.INIT;
        for (ICompatPlugin plugin : plugins)
            plugin.init();
    }

    public void postInit ()
    {
        currPhase = Phase.POSTINIT;
        for (ICompatPlugin plugin : plugins)
            plugin.postInit();
        currPhase = Phase.DONE;
    }

    public void registerBuiltins ()
    {
        registerPlugin(new ForgeMultiPart());
        registerPlugin(new MineFactoryReloaded());
        registerPlugin(new NotEnoughItems());
        registerPlugin(new Waila());
    }

}
