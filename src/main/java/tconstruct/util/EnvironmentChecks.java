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
                    + "- The mod makes broad assumptions and changes to how the ore dictionary works" + n 
                    + "- The author is not a positive member of the modding community";
            /*return  "- GregTech is an \"IndustrialCraft 2 addon\". People do not expect a total conversion meta-mod that alters every other mod when they install it the first time." + n
                    + "- The ore dictionary's purpose is interchangable items and mod compatibility. It is not a tool to bend people to your will." + n 
                    + "- GregTech actively breaks mods with \"invalid ore dictionary entries\"." + n
                    + "- The mod has a blacklist that can corrupt worlds of people on it." + n
                    + "- The author blames any problems introduced by his mod on others. See \"NotMyFaultException\" for intentional crashes.";*/
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
