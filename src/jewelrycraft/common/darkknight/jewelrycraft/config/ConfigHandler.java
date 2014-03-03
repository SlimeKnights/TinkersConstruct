package common.darkknight.jewelrycraft.config;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler
{
    private static Configuration config;
    public static int            idThiefGloves        = 17493;
    public static int            idShadowIngot        = 17494;
    public static int            idMolds              = 17495;
    public static int            idRing               = 17496;
    public static int            idClayMolds          = 17497;
    
    public static int            idShadowOre          = 1750;
    public static int            idSmelter            = 1751;
    public static int            idDisplayer          = 1752;
    public static int            idJewelCraftingTable = 1753;
    public static int            idMolder             = 1754;
    public static int            idGlow               = 1755;
    public static int            idShadowBlock        = 1756;
    
    public static int            ingotCoolingTime     = 200;
    public static int            ingotMeltingTime     = 1500;
    public static int            jewelryCraftingTime  = 2000;
    
    private static boolean       isInitialized        = false;
    
    public static void preInit(FMLPreInitializationEvent e)
    {
        if (!isInitialized)
        {
            config = new Configuration(e.getSuggestedConfigurationFile());
            
            config.load();
            
            idThiefGloves = config.getItem("Thief Gloves", idThiefGloves).getInt();
            idShadowIngot = config.getItem("Shadow Ingot", idShadowIngot).getInt();
            idMolds = config.getItem("Molds", idMolds).getInt();
            idClayMolds = config.getItem("Clay Molds", idClayMolds).getInt();
            idRing = config.getItem("Ring", idRing).getInt();
            
            idShadowOre = config.getBlock("Shadow Ore", idShadowOre).getInt();
            idShadowBlock = config.getBlock("Shadow Block", idShadowBlock).getInt();
            idSmelter = config.getBlock("Smelter", idSmelter).getInt();
            idMolder = config.getBlock("Molder", idMolder).getInt();
            idDisplayer = config.getBlock("Displayer", idDisplayer).getInt();
            idJewelCraftingTable = config.getBlock("Jeweler's Crafting Table", idJewelCraftingTable).getInt();
            idGlow = config.getBlock("Glow", idGlow).getInt();
            
            ingotCoolingTime = config.get("timers", "Molder Ingot Cooling Time", ingotCoolingTime).getInt();
            ingotMeltingTime = config.get("timers", "Ingot Melting Time", ingotMeltingTime).getInt();
            jewelryCraftingTime = config.get("timers", "Jewelry Crafting Time", jewelryCraftingTime).getInt();
            
            config.save();
            
            isInitialized = true;
        }
    }
}
