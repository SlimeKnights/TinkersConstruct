package tconstruct.util;

import java.util.ArrayList;
import java.util.List;

import mantle.crash.CallableSuppConfig;
import mantle.crash.CallableUnsuppConfig;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import tconstruct.TConstruct;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnvironmentChecks
{

    private EnvironmentChecks()
    {
    } // Singleton

    /**
     * Checks for conflicting stuff in environment; adds callable to any crash logs if so.
     * Note: This code adds additional data to crashlogs. It does not trigger any crashes.
     */
    private static List<String> incompatibilities = new ArrayList<String>();
    private static EnvironmentChecks instance = new EnvironmentChecks();

    public static void verifyEnvironmentSanity ()
    {
        List<String> modIds = new ArrayList<String>();

        if (Loader.isModLoaded("gregtech_addon"))
        {
            TConstruct.logger.severe("Tinkers' Construct and GregTech are incompatible for the following reasons:");
            TConstruct.logger.severe(modCompatDetails("GregTech", true));
            modIds.add("gregtech_addon");
            incompatibilities.add("GregTech");
        }

        // Bukkit/Magic Launcher/Optifine are caught by Mantle, so we no longer check for those.

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

        if (incompatibilities.size() > 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    public static String modCompatDetails (String type, boolean consoleFormat)
    {
        String n = consoleFormat ? System.getProperty("line.separator") : "\n";
        if (type.equals("GregTech"))
        {
            return "- GregTech is a meta-mod that changes how a lot of mods interact with the base game and with each other." + n
                    + "- The mod restructures the registration of various ores within the Ore Dictionary. This may alter or break the original design intention of various other mods." + n
                    + "- This mod alters various fundamental recipes from vanilla Minecraft.";
        }
        return "";
    }

    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void openMainMenu (GuiOpenEvent event)
    {
        if (event.gui instanceof GuiMainMenu)
        {
            if (incompatibilities.size() > 0)
            {
                event.gui = new EnvironmentGui(event.gui, incompatibilities);
            }
            MinecraftForge.EVENT_BUS.unregister(instance);
        }
    }
}
