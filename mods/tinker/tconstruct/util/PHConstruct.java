package mods.tinker.tconstruct.util;

import java.io.File;
import java.io.IOException;

import mods.tinker.tconstruct.TConstruct;
import net.minecraftforge.common.Configuration;

public class PHConstruct
{

    public static void initProps ()
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
        clearWater = config.get("Difficulty Changes", "Turn water clear", true).getBoolean(true);
        voidFog = config.get("Difficulty Changes", "Remove Overworld void fog", true).getBoolean(true);
        
        superfunWorld = config.get("Superfun", "All the world is Superfun", false).getBoolean(false);

        /*disableWoodTools = config.get("Difficulty Changes", "Disable vanilla wooden tools", false).getBoolean(false);
        disableStoneTools = config.get("Difficulty Changes", "Disable vanilla stone tools", true).getBoolean(true);
        disableIronTools = config.get("Difficulty Changes", "Disable vanilla iron tools", true).getBoolean(true);
        disableDiamondTools = config.get("Difficulty Changes", "Disable vanilla diamond tools", true).getBoolean(true);
        disableGoldTools = config.get("Difficulty Changes", "Disable vanilla gold tools", true).getBoolean(true);*/

        enableTWood = config.get("Difficulty Changes", "Enable mod wooden tools", true).getBoolean(true);
        enableTStone = config.get("Difficulty Changes", "Enable mod stone tools", true).getBoolean(true);
        enableTCactus = config.get("Difficulty Changes", "Enable mod cactus tools", true).getBoolean(true);
        enableTBone = config.get("Difficulty Changes", "Enable mod bone tools", true).getBoolean(true);

        redCreeper = config.get("Mob Spawning", "Activate Nitro Creeper Spawns", true).getBoolean(true);
        blueSlime = config.get("Mob Spawning", "Activate Blue Slime Spawns", true).getBoolean(true);
        redCreeperWeight = config.get("Mob Spawning", "Spawn Weight for Nitro Creeper", 8).getInt(8);
        blueSlimeWeight = config.get("Mob Spawning", "Spawn Weight for Blue Slime", 8).getInt(8);

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
        
        multiBrick = config.getBlock("Multi Brick", 1481).getInt(1481);

        stoneTorch = config.getBlock("Stone Torch", 1484).getInt(1484);
        oreBerry = config.getBlock("Ore Berry One", 1485).getInt(1485);
        oreBerrySecond = config.getBlock("Ore Berry Two", 1486).getInt(1486);
        
        oreGravel = config.getBlock("Ores Gravel", 1488).getInt(1488);
        speedBlock = config.getBlock("Speed Block", 1489).getInt(1489);        

        //landmine = config.getBlock("Landmine", 1481).getInt(1481);

        /*golemCore = config.getBlock("Golem Core", 1481).getInt(1481);
        golemHead = config.getBlock("Golem Head", 1482).getInt(1482);*/
        //golemPedestal = config.getBlock("Golem Pedestal", 1483).getInt(1483);
        //redstoneBallRepeater = config.getBlock("Redstone Ball Repeater", 1483).getInt(1483);
        //netherOreBerry = config.getBlock("Ore Berry Nether", 1487).getInt(1487);

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
        knifeBlade = config.getItem("Tool Parts", "Knife Blade", 14038).getInt(14038);
        chiselHead = config.getItem("Tool Parts", "Chisel Head", 14039).getInt(14039);

        pickaxe = config.getItem("Tools", "Pickaxe", 14051).getInt(14051);
        shovel = config.getItem("Tools", "Shovel", 14052).getInt(14052);
        axe = config.getItem("Tools", "Axe", 14053).getInt(14053);
        hoe = config.getItem("Tools", "Hoe", 14054).getInt(14054);

        broadsword = config.getItem("Tools", "Broadsword", 14055).getInt(14055);
        longsword = config.getItem("Tools", "Longsword", 14056).getInt(14056);
        rapier = config.getItem("Tools", "Rapier", 14057).getInt(14057);
        dagger = config.getItem("Tools", "Dagger", 14065).getInt(14065);

        frypan = config.getItem("Tools", "Frying Pan", 14058).getInt(14058);
        battlesign = config.getItem("Tools", "Battlesign", 14059).getInt(14059);
        mattock = config.getItem("Tools", "Mattock", 14060).getInt(14060);
        lumberaxe = config.getItem("Tools", "Lumber Axe", 14061).getInt(14061);
        longbow = config.getItem("Tools", "Longbow", 14062).getInt(14062);
        shortbow = config.getItem("Tools", "Shortbow", 14063).getInt(14063);
        potionLauncher = config.getItem("Tools", "Potion Launcher", 14064).getInt(14064);
        
        chisel = config.getItem("Tools", "Chisel", 14066).getInt(14066);

        buckets = config.getItem("Patterns and Misc", "Buckets", 14101).getInt(14101);
        uselessItem = config.getItem("Patterns and Misc", "Title Icon", 14102).getInt(14102);
        slimefood = config.getItem("Patterns and Misc", "Strange Food", 14103).getInt(14103);
        oreChunks = config.getItem("Patterns and Misc", "Ore Chunks", 14104).getInt(14104);
        
        heartContainer = config.getItem("Equipables", "Heart Canister", 14105).getInt(14105);
        heavyHelmet = config.getItem("Equipables", "Heavy Helmet", 14106).getInt(14106);

        boolean ic2 = true;
        boolean xycraft = true;
        try
        {
            Class c = Class.forName("ic2.core.IC2");
            ic2 = false;
        }
        catch (Exception e)
        {
        }
        try
        {
            Class c = Class.forName("soaryn.xycraft.core.XyCraft");
            xycraft = false;
        }
        catch (Exception e)
        {
        }

        generateCopper = config.get("Worldgen Disabler", "Generate Copper", ic2).getBoolean(ic2);
        generateTin = config.get("Worldgen Disabler", "Generate Tin", ic2).getBoolean(ic2);
        generateAluminum = config.get("Worldgen Disabler", "Generate Aluminum", xycraft).getBoolean(xycraft);
        generateNetherOres = config.get("Worldgen Disabler", "Generate Cobalt and Ardite", true).getBoolean(true);

        generateIronSurface = config.get("Worldgen Disabler", "Generate Surface Iron", true).getBoolean(true);
        generateGoldSurface = config.get("Worldgen Disabler", "Generate Surface Gold", true).getBoolean(true);
        generateCopperSurface = config.get("Worldgen Disabler", "Generate Surface Copper", true).getBoolean(true);
        generateTinSurface = config.get("Worldgen Disabler", "Generate Surface Tin", true).getBoolean(true);
        generateAluminumSurface = config.get("Worldgen Disabler", "Generate Surface Aluminum", true).getBoolean(true);

        generateIronBush = config.get("Worldgen Disabler", "Generate Iron Bushes", true).getBoolean(true);
        generateGoldBush = config.get("Worldgen Disabler", "Generate Gold Bushes", true).getBoolean(true);
        generateCopperBush = config.get("Worldgen Disabler", "Generate Copper Bushes", true).getBoolean(true);
        generateTinBush = config.get("Worldgen Disabler", "Generate Tin Bushes", true).getBoolean(true);
        generateAluminumBush = config.get("Worldgen Disabler", "Generate Aluminum Bushes", true).getBoolean(true);
        generateSilverBush = config.get("Worldgen Disabler", "Generate Silver Bushes", true).getBoolean(true);
        addToVillages = config.get("Worldgen Disabler", "Add Village Generation", true).getBoolean(true);

        copperuDensity = config.get("Worldgen", "Copper Underground Density", 2).getInt(2);
        tinuDensity = config.get("Worldgen", "Tin Underground Density", 2).getInt(2);
        aluminumuDensity = config.get("Worldgen", "Aluminum Underground Density", 3).getInt(3);
        netherDensity = config.get("Worldgen", "Nether Ores Density", 8).getInt(8);
        
        copperuMinY = config.get("Worldgen", "Copper Underground Min Y", 20).getInt(20);
        copperuMaxY = config.get("Worldgen", "Copper Underground Max Y", 60).getInt(60);
        tinuMinY = config.get("Worldgen", "Tin Underground Min Y", 0).getInt(0);
        tinuMaxY = config.get("Worldgen", "Tin Underground Max Y", 40).getInt(40);
        aluminumuMinY = config.get("Worldgen", "Aluminum Underground Min Y", 0).getInt(0);
        aluminumuMaxY = config.get("Worldgen", "Aluminum Underground Max Y", 64).getInt(64);
        
        ironsRarity = config.get("Worldgen", "Iron Surface Rarity", 400).getInt(400);
        goldsRarity = config.get("Worldgen", "Gold Surface Rarity", 900).getInt(900);
        coppersRarity = config.get("Worldgen", "Copper Surface Rarity", 100).getInt(100);
        tinsRarity = config.get("Worldgen", "Tin Surface Rarity", 100).getInt(100);
        aluminumsRarity = config.get("Worldgen", "Aluminum Surface Rarity", 50).getInt(50);
        cobaltsRarity = config.get("Worldgen", "Cobalt Surface Rarity", 2000).getInt(2000);
        
        ironBushDensity = config.get("Worldgen", "Iron Bush Density", 1).getInt(1);
        goldBushDensity = config.get("Worldgen", "Gold Bush Density", 1).getInt(1);
        copperBushDensity = config.get("Worldgen", "Copper Bush Density", 2).getInt(2);
        tinBushDensity = config.get("Worldgen", "Tin Bush Density", 2).getInt(2);
        aluminumBushDensity = config.get("Worldgen", "Aluminum Bush Density", 2).getInt(2);
        silverBushDensity = config.get("Worldgen", "Silver Bush Density", 1).getInt(1);
        
        ironBushRarity = config.get("Worldgen", "Iron Bush Rarity", 5).getInt(5);
        goldBushRarity = config.get("Worldgen", "Gold Bush Rarity", 8).getInt(8);
        copperBushRarity = config.get("Worldgen", "Copper Bush Rarity", 3).getInt(3);
        tinBushRarity = config.get("Worldgen", "Tin Bush Rarity", 3).getInt(3);
        aluminumBushRarity = config.get("Worldgen", "Aluminum Bush Rarity", 2).getInt(2);
        silverBushRarity = config.get("Worldgen", "Silver Bush Rarity", 8).getInt(8);
        
        copperBushMinY = config.get("Worldgen", "Copper Bush Min Y", 20).getInt(20);
        copperBushMaxY = config.get("Worldgen", "Copper Bush Max Y", 60).getInt(60);
        tinBushMinY = config.get("Worldgen", "Tin Bush Min Y", 0).getInt(0);
        tinBushMaxY = config.get("Worldgen", "Tin Bush Max Y", 40).getInt(40);
        aluminumBushMinY = config.get("Worldgen", "Aluminum Bush Min Y", 0).getInt(0);
        aluminumBushMaxY = config.get("Worldgen", "Aluminum Bush Max Y", 60).getInt(60);
        
        seaLevel = config.get("general", "Sea level", 64).getInt(64);

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
    public static int oreGravel;
    public static int metalBlock;
    //public static int axle;

    public static int golemCore;
    public static int golemHead;
    public static int golemPedestal;
    
    //Crops
    public static int oreBerry;
    public static int oreBerrySecond;
    public static int netherOreBerry;

    //Traps
    //public static int landmine;
    
    //InfiBlocks
    public static int speedBlock;

    //Liquids
    public static int metalFlowing;
    public static int metalStill;

    //Decoration
    public static int stoneTorch;
    public static int multiBrick;
    public static int redstoneBallRepeater;

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
    public static int oreChunks;

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
    public static int dagger;

    public static int frypan;
    public static int battlesign;
    public static int longbow;
    public static int shortbow;
    public static int potionLauncher;

    public static int mattock;
    public static int lumberaxe;
    
    public static int chisel;

    //Tool parts
    public static int swordBlade;
    public static int largeGuard;
    public static int medGuard;
    public static int crossbar;
    public static int knifeBlade;

    public static int pickaxeHead;
    public static int axeHead;
    public static int shovelHead;
    public static int hoeHead;

    public static int frypanHead;
    public static int signHead;
    public static int chiselHead;

    public static int lumberHead;

    public static int binding;
    
    //Wearables
    public static int heavyHelmet;
    public static int heavyChestplate;
    public static int heavyPants;
    public static int heavyBoots;
    
    public static int heartContainer;

    //Ore values
    public static boolean generateCopper;
    public static boolean generateTin;
    public static boolean generateAluminum;
    public static boolean generateNetherOres;
    
    public static boolean generateIronSurface;
    public static boolean generateGoldSurface;
    public static boolean generateCopperSurface;
    public static boolean generateTinSurface;
    public static boolean generateAluminumSurface;
    public static boolean generateCobaltSurface;
    
    public static boolean generateIronBush;
    public static boolean generateGoldBush;
    public static boolean generateCopperBush;
    public static boolean generateTinBush;
    public static boolean generateAluminumBush;
    public static boolean generateSilverBush;
    
    public static boolean addToVillages;
    
    public static int copperuDensity;
    public static int tinuDensity;
    public static int aluminumuDensity;
    public static int netherDensity;
    
    public static int ironsRarity;
    public static int goldsRarity;
    public static int coppersRarity;
    public static int tinsRarity;
    public static int aluminumsRarity;
    public static int cobaltsRarity;
    
    public static int ironBushDensity;
    public static int goldBushDensity;
    public static int copperBushDensity;
    public static int tinBushDensity;
    public static int aluminumBushDensity;
    public static int silverBushDensity;
    
    public static int ironBushRarity;
    public static int goldBushRarity;
    public static int copperBushRarity;
    public static int tinBushRarity;
    public static int aluminumBushRarity;
    public static int silverBushRarity;
    
    public static int copperuMinY;
    public static int copperuMaxY;
    public static int tinuMinY;
    public static int tinuMaxY;    
    public static int aluminumuMinY;
    public static int aluminumuMaxY;
    
    public static int copperBushMinY;
    public static int copperBushMaxY;
    public static int tinBushMinY;
    public static int tinBushMaxY;    
    public static int aluminumBushMinY;
    public static int aluminumBushMaxY;
    
    public static int seaLevel;

    //Mobs
    public static boolean redCreeper;
    public static int redCreeperWeight;
    public static boolean blueSlime;
    public static int blueSlimeWeight;

    //Difficulty modifiers
    public static boolean keepHunger;
    public static boolean keepLevels;
    public static boolean clearWater;
    public static boolean voidFog;

    public static boolean disableWoodTools;
    public static boolean disableStoneTools;
    public static boolean disableIronTools;
    public static boolean disableDiamondTools;
    public static boolean disableGoldTools;

    public static boolean enableTWood;
    public static boolean enableTStone;
    public static boolean enableTCactus;
    public static boolean enableTBone;
    
    //Superfun
    public static boolean superfunWorld;
}