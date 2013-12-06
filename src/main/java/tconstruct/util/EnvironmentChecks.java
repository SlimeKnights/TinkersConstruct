package tconstruct.util;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

import tconstruct.TConstruct;
import mantle.crash.CallableSuppConfig;
import mantle.crash.CallableUnsuppConfig;

public class EnvironmentChecks
{

    // Used for aborting cloak renders with Optifine present (it misbehaves)
    public static boolean hasOptifine = false;

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

        // We keep this despite Mantle reporting it because we explicitly need Optifine marking for cloaks.
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine() || Loader.isModLoaded("optifine"))
        {
            TConstruct.logger.severe("[Environment Checks] Optifine detected. This is a Bad Thing(tm) and can crash Minecraft due to an Optifine bug during TCon armor renders! Capes have been disabled.");
            //modIds.add("optifine");
            hasOptifine = true;
        }

        // Bukkit/Magic Launcher are caught by Mantle, so we no longer check for those.

        if (modIds.size() == 0)
        {
            ICrashCallable callable = new CallableSuppConfig("TConstruct");
            FMLCommonHandler.instance().registerCrashCallable(callable);
        }
        else
        {
            ICrashCallable callable = new CallableUnsuppConfig("TConstruct", modIds);
            FMLCommonHandler.instance().registerCrashCallable(callable);
        }
    }

}
