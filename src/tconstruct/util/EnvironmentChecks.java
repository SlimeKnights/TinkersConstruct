package tconstruct.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentChecks {

    private EnvironmentChecks() {} // Singleton

    /**
     * Checks for conflicting stuff in environment; adds callable to any crash logs if so.
     */
    public static void verifyEnvironmentSanity() {
        if (Loader.isModLoaded("gregtech_addon")) {
            List<String> modIds = new ArrayList<String>();
            modIds.add("gregtech_addon");

            ICrashCallable callable = new CallableUnsuppConfig(modIds);
            FMLCommonHandler.instance().registerCrashCallable(callable);
        }
    }

}
