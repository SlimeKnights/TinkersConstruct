package tconstruct.util;

import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.Side;
import java.util.*;
import mantle.crash.*;
import net.minecraftforge.common.MinecraftForge;

public class EnvironmentChecks {

    private EnvironmentChecks() {} // Singleton

    /**
     * Checks for conflicting stuff in environment; adds callable to any crash
     * logs if so. Note: This code adds additional data to crashlogs. It does
     * not trigger any crashes.
     */
    private static List<String> incompatibilities = new ArrayList<String>();

    private static EnvironmentChecks instance = new EnvironmentChecks();

    public static void verifyEnvironmentSanity() {
        List<String> modIds = new ArrayList<String>();

        // Bukkit/Magic Launcher/Optifine are caught by Mantle, so we no longer
        // check for those.

        if (modIds.size() == 0) {
            ICrashCallable callable = new CallableSuppConfig("TConstruct");
            FMLCommonHandler.instance().registerCrashCallable(callable);
        } else {
            ICrashCallable callable = new CallableUnsuppConfig("TConstruct", modIds);
            FMLCommonHandler.instance().registerCrashCallable(callable);
        }

        if (incompatibilities.size() > 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    public static String modCompatDetails(String type, boolean consoleFormat) {
        String n = consoleFormat ? System.getProperty("line.separator") : "\n";
        return "";
    }
}
