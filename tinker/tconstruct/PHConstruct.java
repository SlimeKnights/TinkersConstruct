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
        searedBrick = config.getBlock("Seared Brick", 1477).getInt(1477);
        craftedSoil = config.getBlock("Special Soil", 1476).getInt(1476);
        
        ironFlowing = config.getBlock("Liquid Iron Flowing", 1478).getInt(1478);
        ironStill = config.getBlock("Liquid Iron Still", 1479).getInt(1479);
        goldFlowing = config.getBlock("Liquid Gold Flowing", 1480).getInt(1480);
        goldStill = config.getBlock("Liquid Gold Still", 1481).getInt(1481);
        copperFlowing = config.getBlock("Liquid Copper Flowing", 1482).getInt(1482);
        copperStill = config.getBlock("Liquid Copper Still", 1483).getInt(1483);
        tinFlowing = config.getBlock("Liquid Tin Flowing", 1484).getInt(1484);
        tinStill = config.getBlock("Liquid Tin Still", 1485).getInt(1485);
        aluminumFlowing = config.getBlock("Liquid Aluminum Flowing", 1486).getInt(1486);
        aluminumStill = config.getBlock("Liquid Aluminum Still", 1487).getInt(1487);
        cobaltFlowing = config.getBlock("Liquid Cobalt Flowing", 1488).getInt(1488);
        cobaltStill = config.getBlock("Liquid Cobalt Still", 1489).getInt(1489);
        arditeFlowing = config.getBlock("Liquid Ardite Flowing", 1490).getInt(1490);
        arditeStill = config.getBlock("Liquid Ardite Still", 1491).getInt(1491);
        bronzeFlowing = config.getBlock("Liquid Bronze Flowing", 1492).getInt(1492);
        bronzeStill = config.getBlock("Liquid Bronze Still", 1493).getInt(1493);
        brassFlowing = config.getBlock("Liquid Brass Flowing", 1494).getInt(1494);
        brassStill = config.getBlock("Liquid Brass Still", 1495).getInt(1495);
        manyullynFlowing = config.getBlock("Liquid Manyullyn Flowing", 1496).getInt(1496);
        manyullynStill = config.getBlock("Liquid Manyullyn Still", 1497).getInt(1497);
        alumiteFlowing = config.getBlock("Liquid Alumite Flowing", 1498).getInt(1498);
        alumiteStill = config.getBlock("Liquid Alumite Still", 1499).getInt(1499);
        obsidianFlowing = config.getBlock("Liquid Obsidian Flowing", 1500).getInt(1500);
        obsidianStill = config.getBlock("Liquid Obsidian Still", 1501).getInt(1501);
        
        manual = config.getItem("Patterns and Misc", "Tinker's Manual", 14018).getInt(14018);
        blankPattern = config.getItem("Patterns and Misc", "Blank Patterns", 14019).getInt(14019);
        materials = config.getItem("Patterns and Misc", "Materials", 14020).getInt(14020);
        toolRod = config.getItem("Patterns and Misc", "Tool Rod", 14021).getInt(14021);
        toolShard = config.getItem("Patterns and Misc", "Tool Shard", 14022).getInt(14022);
        woodPattern = config.getItem("Patterns and Misc", "Wood Pattern", 14023).getInt(14023);
        
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
        	Class c = Class.forName("soaryn.xycraft.XyCraft");
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
    public static int craftedSoil;
    public static int searedBrick;
    public static int axle;

    public static int ironFlowing;
    public static int ironStill;
    public static int goldFlowing;
    public static int goldStill;
    public static int copperFlowing;
    public static int copperStill;
    public static int tinFlowing;
    public static int tinStill;
    public static int aluminumFlowing;
    public static int aluminumStill;
    public static int cobaltFlowing;
    public static int cobaltStill;
    public static int arditeFlowing;
    public static int arditeStill;

    public static int bronzeFlowing;
    public static int bronzeStill;
    public static int brassFlowing;
    public static int brassStill;
    public static int manyullynFlowing;
    public static int manyullynStill;
    public static int alumiteFlowing;
    public static int alumiteStill;

    public static int obsidianFlowing;
    public static int obsidianStill;
    
    //Patterns and misc
    public static int blankPattern;
    public static int materials;
    public static int toolRod;
    public static int toolShard;
    public static int woodPattern;
    
    public static int manual;
    
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
}