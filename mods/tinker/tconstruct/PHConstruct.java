package mods.tinker.tconstruct;

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
        
        //removeToolRecipes = config.get("Diffuclty Changes", "Remove vanilla tool recipes", false).getBoolean(false);
        keepHunger = config.get("Difficulty Changes", "Keep hunger on death", true).getBoolean(true);
        keepLevels = config.get("Difficulty Changes", "Keep levels on death", true).getBoolean(true);
        
        disableWoodTools = config.get("Difficulty Changes", "Disable vanilla wooden tools", false).getBoolean(false);
        disableStoneTools = config.get("Difficulty Changes", "Disable vanilla stone tools", true).getBoolean(true);
        disableIronTools = config.get("Difficulty Changes", "Disable vanilla iron tools", true).getBoolean(true);
        disableDiamondTools = config.get("Difficulty Changes", "Disable vanilla diamond tools", true).getBoolean(true);
        disableGoldTools = config.get("Difficulty Changes", "Disable vanilla gold tools", true).getBoolean(true);
        
        enableTWood = config.get("Difficulty Changes", "Enable mod wooden tools", true).getBoolean(true);
        enableTStone = config.get("Difficulty Changes", "Enable mod stone tools", true).getBoolean(true);
        enableTCactus = config.get("Difficulty Changes", "Enable mod cactus tools", true).getBoolean(true);
        enableTBone = config.get("Difficulty Changes", "Enable mod bone tools", true).getBoolean(true);
        
        unstableCreeper = config.get("Mob Spawning", "Activate Nitro Creeper Spawns", true).getBoolean(true);
        blueSlime = config.get("Mob Spawning", "Activate Blue Slime Spawns", true).getBoolean(true);
        
        woodCrafter = config.getBlock("Wood Tool Station", 1471).getInt(1471);
        heldItemBlock = config.getBlock("Held Item Block", 1472).getInt(1472);
        lavaTank = config.getBlock("Lava Tank", 1473).getInt(1473);
        smeltery = config.getBlock("Smeltery", 1474).getInt(1474);
        oreSlag = config.getBlock("Ores Slag", 1475).getInt(1475);
        craftedSoil = config.getBlock("Special Soil", 1476).getInt(1476);
        searedTable = config.getBlock("Seared Table", 1477).getInt(1477);
        metalBlock = config.getBlock("Metal Storage", 1478).getInt(1478);
        metalFlowing = config.getBlock("Liquid Metal Flowing", 1479).getInt(1479);
        metalStill = config.getBlock("Liquid Metal Still", 1480).getInt(1480);
        //landmine = config.getBlock("Landmine", 1481).getInt(1481);
        
        manual = config.getItem("Patterns and Misc", "Tinker's Manual", 14018).getInt(14018);
        blankPattern = config.getItem("Patterns and Misc", "Blank Patterns", 14019).getInt(14019);
        materials = config.getItem("Patterns and Misc", "Materials", 14020).getInt(14020);
        toolRod = config.getItem("Patterns and Misc", "Tool Rod", 14021).getInt(14021);
        toolShard = config.getItem("Patterns and Misc", "Tool Shard", 14022).getInt(14022);
        woodPattern = config.getItem("Patterns and Misc", "Wood Pattern", 14023).getInt(14023);
        metalPattern = config.getItem("Patterns and Misc", "Metal Pattern", 14024).getInt(14024);
        
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
        
        buckets = config.getItem("Patterns and Misc", "Buckets", 14101).getInt(14101);
        uselessItem = config.getItem("Patterns and Misc", "Title Icon", 14102).getInt(14102);
        slimefood = config.getItem("Patterns and Misc", "Strange Food", 14103).getInt(14103);
        
        boolean ic2 = true;
        boolean xycraft = true;
        try
        {
        	Class c = Class.forName("ic2.core.IC2");
        	ic2 = false;
        }
        catch (Exception e) {}
        try
        {
        	Class c = Class.forName("soaryn.xycraft.core.XyCraft");
        	xycraft = false;
        }
        catch (Exception e) {}
        
        generateCopper = config.get("Worldgen", "Generate Copper", ic2).getBoolean(ic2);
        generateTin = config.get("Worldgen", "Generate Tin", ic2).getBoolean(ic2);
        generateAluminum = config.get("Worldgen", "Generate Aluminum", xycraft).getBoolean(xycraft);
        generateCobalt = config.get("Worldgen", "Generate Cobalt", true).getBoolean(true);
        generateArdite = config.get("Worldgen", "Generate Ardite", true).getBoolean(true);
        
        copperDensity = config.get("Worldgen", "Copper Density", 8).getInt(8);
        copperHeight = config.get("Worldgen", "Copper Height", 20).getInt(20);
        copperRange = config.get("Worldgen", "Copper Range", 40).getInt(40);
        tinDensity = config.get("Worldgen", "Tin Density", 8).getInt(8);
        tinHeight = config.get("Worldgen", "Tin Height", 0).getInt(0);
        tinRange = config.get("Worldgen", "Tin Range", 40).getInt(40);
        aluminumDensity = config.get("Worldgen", "Aluminum Density", 9).getInt(9);
        aluminumHeight = config.get("Worldgen", "Aluminum Height", 0).getInt(0);
        aluminumRange = config.get("Worldgen", "Aluminum Range", 64).getInt(64);
        netherDensity = config.get("Worldgen", "Nether Ores Density", 8).getInt(8);

        /* Save the configuration file */
        config.save();
    }
    
    //Blocks
    public static int woodCrafter;
    public static int heldItemBlock;
    public static int ores;
    public static int lavaTank;
    public static int smeltery;
    public static int searedTable;
    public static int craftedSoil;
    public static int oreSlag;
    public static int metalBlock;
    //public static int axle;

    //Traps
    //public static int landmine;
    
    //Liquids
    public static int metalFlowing;
    public static int metalStill;
    
    //Patterns and misc
    public static int blankPattern;
    public static int materials;
    public static int toolRod;
    public static int toolShard;
    public static int woodPattern;
    public static int metalPattern;
    
    public static int manual;
    public static int buckets;
    public static int uselessItem;
    
    //Food
    public static int slimefood;
    
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
    
    //Ore values
    public static boolean generateCopper;
    public static boolean generateTin;
    public static boolean generateAluminum;
    public static boolean generateCobalt;
    public static boolean generateArdite;
        
    public static int copperDensity;
    public static int copperHeight;
    public static int copperRange;
    public static int tinDensity;
    public static int tinHeight;
    public static int tinRange;
    public static int aluminumDensity;
    public static int aluminumHeight;
    public static int aluminumRange;
    public static int netherDensity;
    
    //Mobs
    public static boolean unstableCreeper;
    public static boolean blueSlime;
    
    //Difficulty modifiers
    public static boolean keepHunger;
    public static boolean keepLevels;
    
    public static boolean disableWoodTools;
    public static boolean disableStoneTools;
    public static boolean disableIronTools;
    public static boolean disableDiamondTools;
    public static boolean disableGoldTools;
    
    public static boolean enableTWood;
    public static boolean enableTStone;
    public static boolean enableTCactus;
    public static boolean enableTBone;
}