package tconstruct.plugins;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.Configuration;
import tconstruct.TConstruct;
import tconstruct.plugins.fmp.ForgeMultiPart;
import tconstruct.plugins.minefactoryreloaded.MineFactoryReloaded;
import tconstruct.plugins.nei.NotEnoughItems;
import tconstruct.plugins.waila.Waila;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginController
{

    private enum Phase {
        PRELAUNCH, PREINIT, INIT, POSTINIT, DONE
    }

    private static PluginController instance;
    private Configuration conf = null;
    private Map<ICompatPlugin, Boolean> plugins = new HashMap<ICompatPlugin, Boolean>();
    private Phase currPhase = Phase.PRELAUNCH;

    private PluginController() {
        String path = Loader.instance().getConfigDir().toString() + File.separator + "TDynstruct.cfg";
        TConstruct.logger.info("[PluginController] Using config path: " + path);
        conf = new Configuration(new File(path));
    }

    public static PluginController getController()
    {
        if (instance == null) instance = new PluginController();
        return instance;
    }

    /**
     * Register a plugin with the controller.
     *
     * Warning: Make sure your plugin class imports no APIs directly! Any API interaction should be done by handlers called in pre/init/post so
     *          merely creating an instance to check the mod ID isn't a hazard to the controller.
     *
     * @param plugin Plugin to register
     */
    public void registerPlugin(ICompatPlugin plugin)
    {
        if (Loader.isModLoaded(plugin.getModId()))
        {
            TConstruct.logger.info("[PluginController] Attempting registration of compat plugin for " + plugin.getModId());
            plugins.put(plugin, true);

            conf.load();
            boolean shouldLoad = conf.get("Plugins", plugin.getModId(), true).getBoolean(true);
            plugins.put(plugin, shouldLoad);
            conf.save();

            if (!shouldLoad)
            {
                TConstruct.logger.info("[PluginController] Aborting registration of compat plugin for " + plugin.getModId() + "; disabled in configuration.");
                return;
            }

            switch (currPhase) // Play catch-up if plugin is registered late
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

    public void preInit()
    {
        currPhase = Phase.PREINIT;
        for (Map.Entry<ICompatPlugin, Boolean> entry : plugins.entrySet()){
            if (entry.getValue())
                entry.getKey().preInit();
        }
    }

    public void init()
    {
        currPhase = Phase.INIT;
        for (Map.Entry<ICompatPlugin, Boolean> entry : plugins.entrySet()){
            if (entry.getValue())
                entry.getKey().init();
        }
    }

    public void postInit()
    {
        currPhase = Phase.POSTINIT;
        for (Map.Entry<ICompatPlugin, Boolean> entry : plugins.entrySet()){
            if (entry.getValue())
                entry.getKey().postInit();
        }
        currPhase = Phase.DONE;
    }

    public void registerBuiltins()
    {
        registerPlugin(new ForgeMultiPart());
        registerPlugin(new MineFactoryReloaded());
        registerPlugin(new NotEnoughItems());
        registerPlugin(new Waila());
    }

}
