package tinker.tconstruct;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.Configuration;

public class PHConstruct {

    public static void initProps()
    {
        /* Here we will set up the config file for the mod 
         * First: Create a folder inside the config folder
         * Second: Create the actual config file
         * Note: Configs are a pain, but absolutely necessary for every mod.
         */
        File file = new File(TConstruct.proxy.getLocation() + "/config");
        file.mkdir();
        File newFile = new File(TConstruct.proxy.getLocation() + "/config/TinkersWorkshop.txt");

        /* Some basic debugging will go a long way */
        try
        {
            newFile.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println("Could not create configuration file for TConstruct. Reason:");
            System.out.println(e);
        }

        /* [Forge] Configuration class, used as config method */
        Configuration config = new Configuration(newFile);

        /* Load the configuration file */
        config.load();

        /* Define the mod's IDs. 
         * Avoid values below 4096 for items and in the 250-450 range for blocks
         */
        
        woodCrafter = config.getBlock("Wood Tool Station", 1471).getInt(1471);
        
        heldItemBlock = config.getBlock("Held Item Block", 1472).getInt(1472);
        lavaTank = config.getBlock("Lava Tank", 1473).getInt(1473);
        smeltery = config.getBlock("Smeltery", 1474).getInt(1474);
        ores = config.getBlock("Ore Block", 1475).getInt(1475);
        craftedSoil = config.getBlock("Special Soil", 1476).getInt(1476);
        searedBrick = config.getBlock("Seared Brick", 1477).getInt(1477);
        
        materials = config.getItem("Patterns and Misc", "Materials", 14020).getInt(14020);
        toolRod = config.getItem("Patterns and Misc", "Tool Rod", 14021).getInt(14021);
        toolShard = config.getItem("Patterns and Misc", "Tool Shard", 14022).getInt(14022);
        woodPattern = config.getItem("Patterns and Misc", "Wood Pattern", 14023).getInt(14023);
        //stonePattern = config.getItem("Patterns and Misc", "Stone Pattern", 14024).getInt(14024);
        //netherPattern = config.getItem("Patterns and Misc", "Nether Pattern", 14025).getInt(14025);
        
        pickaxeHead = config.getItem("Tool Parts", "Pickaxe Head", 14026).getInt(14026);
        shovelHead = config.getItem("Tool Parts", "Shovel Head", 14027).getInt(14027);
        axeHead = config.getItem("Tool Parts", "Axe Head", 14028).getInt(14028);
        hoeHead = config.getItem("Tool Parts", "Hoe Head", 14029).getInt(14029);
        
        swordBlade = config.getItem("Tool Parts", "Sword Blade", 14030).getInt(14030);
        largeGuard = config.getItem("Tool Parts", "Large Guard", 14031).getInt(14031);
        medGuard = config.getItem("Tool Parts", "Medium Guard", 14032).getInt(14032);
        crossbar = config.getItem("Tool Parts", "Crossbar", 14033).getInt(14033);
        binding = config.getItem("Tool Parts", "Tool Binding", 14034).getInt(14034);
        
        frypanHead = config.getItem("Tool Parts", "Frypan Head", 14035).getInt(14035);
        signHead = config.getItem("Tool Parts", "Sign Head", 14036).getInt(14036);
        
        lumberHead = config.getItem("Tool Parts", "Lumber Axe Head", 14037).getInt(14037);
        
        pickaxe = config.getItem("Tools", "Pickaxe", 14051).getInt(14051);
        shovel = config.getItem("Tools", "Shovel", 14052).getInt(14052);
        axe = config.getItem("Tools", "Axe", 14053).getInt(14053);
        hoe = config.getItem("Tools", "Hoe", 14054).getInt(14054);
        
        broadsword = config.getItem("Tools", "Broadsword", 14055).getInt(14055);
        longsword = config.getItem("Tools", "Longsword", 14056).getInt(14056);
        rapier = config.getItem("Tools", "Rapier", 14057).getInt(14057);
        
        frypan = config.getItem("Tools", "Frying Pan", 14058).getInt(14058);
        battlesign = config.getItem("Tools", "Battlesign", 14059).getInt(14059);
        mattock = config.getItem("Tools", "Mattock", 14060).getInt(14060);
        lumberaxe = config.getItem("Tools", "Lumber Axe", 14061).getInt(14061);
        longbow = config.getItem("Tools", "Longbow", 14062).getInt(14062);

        /* Save the configuration file */
        config.save();
    }
    
    //Blocks
    public static int woodCrafter;
    public static int stoneCrafter;
    public static int netherCrafter;
    public static int heldItemBlock;
    public static int ores;
    public static int lavaTank;
    public static int smeltery;
    public static int craftedSoil;
    public static int searedBrick;
    
    //Patterns and misc
    public static int materials;
    public static int toolRod;
    public static int toolShard;
    public static int woodPattern;
    //public static int stonePattern;
    //public static int netherPattern;
    
    //Tools
    public static int pickaxe;
    public static int shovel;
    public static int axe;
    public static int hoe;
    
    public static int broadsword;
    public static int longsword;
    public static int rapier;
    
    public static int frypan;
    public static int battlesign;
    public static int longbow;
    
    public static int mattock;
    public static int lumberaxe;

    //Tool parts
    public static int swordBlade;
    public static int largeGuard;
    public static int medGuard;
    public static int crossbar;

    public static int pickaxeHead;
    public static int axeHead;
    public static int shovelHead;
    public static int hoeHead;
    
    public static int frypanHead;
    public static int signHead;
    
    public static int lumberHead;
    
    public static int binding;
}