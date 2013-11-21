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
     */
    public static void verifyEnvironmentSanity ()
    {
        List<String> modIds = new ArrayList<String>();

        if (Loader.isModLoaded("gregtech_addon"))
        {
            TConstruct.logger.severe("[Environment Checks] Gelatinous iceberg dead ahead! Entering Greggy waters! Abandon hope all ye who enter here! (No, seriously, we don't support GT. Don't report any issues. Thanks.)");
            TConstruct.logger.severe("[Environment Checks] A NOTE TO THE USER: Gregtech has been known to crash the game with Metallurgy and for certain specific 'banished' players.");
            TConstruct.logger.severe("[Environment Checks]                     For this reason, the TCon dev team urge you to seek your tedium fix from someone else, as Greg disobeys");
            TConstruct.logger.severe("[Environment Checks]                     the basic tenets of good modders conduct. We log this message rather than sabotage. We'd hope he'd do the same.");
            TConstruct.logger.severe("[Environment Checks]                     In the meantime, we recommend not playing anything but IC2 alongside Gregtech. ~ Sunstrike");
            TConstruct.logger.severe("[Environment Checks] GT Appendum I: And now he's going after Forge Mod Loader too...");
            TConstruct.logger.severe("[Environment Checks]                http://forum.industrial-craft.net/index.php?page=Thread&postID=138551#post138551 ~ SlimeKnights");
            modIds.add("gregtech_addon");
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine() || Loader.isModLoaded("optifine"))
        {
            TConstruct.logger.severe("[Environment Checks] Optifine detected. This is a Bad Thing(tm) and can crash Minecraft due to an Optifine bug during TCon armor renders!");
            modIds.add("optifine");
        }

        if (Loader.isModLoaded("DragonAPI"))
        {
            TConstruct.logger.severe("[Environment Checks] DragonAPI detected. Currently this just logs a warning, however if Reika continues his plan to implement DRM-like");
            TConstruct.logger.severe("[Environment Checks] code in his mod then it may become marked as fully unsupported. This is just a warning at this stage.");
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
