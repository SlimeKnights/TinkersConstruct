package tconstruct.util;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import tconstruct.TConstruct;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentChecks
{

    private EnvironmentChecks()
    {
    } // Singleton

    /**
     * Checks for conflicting stuff in environment; adds callable to any crash logs if so.
     * Note: This code adds additional data to crashlogs. It does not trigger any crashes.
     */
    public static void verifyEnvironmentSanity ()
    {
        List<String> modIds = new ArrayList<String>();

        if (Loader.isModLoaded("gregtech_addon"))
        {
            TConstruct.logger.severe("[Environment Checks] Gelatinous iceberg dead ahead! Entering Greggy waters! Abandon hope all ye who enter here! (No, seriously, we don't support GT. Don't report any issues. Thanks.)");
            modIds.add("gregtech_addon");
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine() || Loader.isModLoaded("optifine"))
        {
            TConstruct.logger.severe("[Environment Checks] Optifine detected. This is a Bad Thing(tm) and can crash Minecraft due to an Optifine bug during TCon armor renders!");
            modIds.add("optifine");
        }

        try
        {
            Class cl = Class.forName("org.bukkit.Bukkit");
            if (cl != null)
            {
                TConstruct.logger.severe("[Environment Checks] Bukkit implementation detected. This may be crashy. Bukkit implementations include Craftbukkit and MCPC+.");
                modIds.add("bukkit");
            }
        }
        catch (Exception ex)
        {
            // No Bukkit in environment.
        }

        if (modIds.size() == 0)
        {
            ICrashCallable callable = new CallableSuppConfig();
            FMLCommonHandler.instance().registerCrashCallable(callable);
        }
        else
        {
            ICrashCallable callable = new CallableUnsuppConfig(modIds);
            FMLCommonHandler.instance().registerCrashCallable(callable);
        }
    }

}
