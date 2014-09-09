package tconstruct.util.config;

import java.io.File;
import net.minecraftforge.common.config.*;
import tconstruct.TConstruct;
import tconstruct.library.tools.AbilityHelper;

public class PHConstruct
{

    public static void initProps (File location)
    {

        /* Here we will set up the config file for the mod
         * First: Create a folder inside the config folder
         * Make sure to read any old configs file if they exist
         * Second: Create the actual config file
         */
        File mainFile = new File(location + "/TinkersConstruct.cfg");
        File legacyFile16 = new File(location + "/TinkersWorkshop.txt");
        File legacyFile17 = new File(location + "/TConstruct.cfg");
        try
        {
            if (!mainFile.exists())
            {
                if (legacyFile16.exists())
                    legacyFile16.renameTo(mainFile);
                if (legacyFile17.exists())
                    legacyFile17.renameTo(mainFile);
            }
        }
        catch (Exception e)
        {
            TConstruct.logger.warn("Could not update legacy configuration file for TConstruct. Reason:");
            TConstruct.logger.warn(e.getLocalizedMessage());
        }

        Configuration config = new Configuration(mainFile);
        //config.load(); /* Load happens in the constructor */

        superfunWorld = config.get("Superfun", "All the world is Superfun", false).getBoolean(false);

        keepHunger = config.get("Difficulty Changes", "Keep hunger on death", true).getBoolean(true);
        keepLevels = config.get("Difficulty Changes", "Keep levels on death", true).getBoolean(true);
        beginnerBook = config.get("Difficulty Changes", "Spawn beginner book", true).getBoolean(true);

        enableTWood = config.get("Difficulty Changes", "Enable mod wooden tools", true).getBoolean(true);
        enableTStone = config.get("Difficulty Changes", "Enable mod stone tools", true).getBoolean(true);
        enableTCactus = config.get("Difficulty Changes", "Enable mod cactus tools", true).getBoolean(true);
        enableTBone = config.get("Difficulty Changes", "Enable mod bone tools", true).getBoolean(true);
        enableTFlint = config.get("Difficulty Changes", "Enable mod flint tools", true).getBoolean(true);
        enableTNetherrack = config.get("Difficulty Changes", "Enable mod netherrack tools", true).getBoolean(true);
        enableTSlime = config.get("Difficulty Changes", "Enable mod slime tools", true).getBoolean(true);
        enableTPaper = config.get("Difficulty Changes", "Enable mod paper tools", true).getBoolean(true);
        enableTBlueSlime = config.get("Difficulty Changes", "Enable mod blue slime tools", true).getBoolean(true);

        craftMetalTools = config.get("Difficulty Changes", "Craft metals with Wood Patterns", false).getBoolean(false);
        vanillaMetalBlocks = config.get("Difficulty Changes", "Craft vanilla metal blocks", true).getBoolean(true);
        lavaFortuneInteraction = config.get("Difficulty Changes", "Enable Auto-Smelt and Fortune interaction", true).getBoolean(true);
        removeVanillaToolRecipes = config.get("Difficulty Changes", "Remove Vanilla Tool Recipes", false).getBoolean(false);
        labotimizeVanillaTools = config.get("Difficulty Changes", "Remove Vanilla Tool Effectiveness", false).getBoolean(false);
        miningLevelIncrease = config.get("Difficulty Changes", "Modifiers increase Mining Level", true).getBoolean(true);
        denyMattock = config.get("Difficulty Changes", "Deny creation of non-metal mattocks", false).getBoolean(false);
        craftEndstone = config.get("Difficulty Changes", "Allow creation of endstone", true).getBoolean(true);

        naturalSlimeSpawn = config.get("Mobs", "Blue Slime spawn chance", 1, "Set to 0 to disable").getInt(1);

        ingotsPerOre = config.get("Smeltery Output Modification", "Ingots per ore", 2, "Number of ingots returned from smelting ores in the smeltery").getDouble(2);
        ingotsBronzeAlloy = config.get("Smeltery Output Modification", "Bronze ingot return", 4, "Number of ingots returned from smelting Bronze in the smeltery").getDouble(4);
        ingotsAluminumBrassAlloy = config.get("Smeltery Output Modification", "Aluminum Brass ingot return", 4, "Number of ingots returned from smelting Aluminum Brass in the smeltery").getDouble(4);
        ingotsAlumiteAlloy = config.get("Smeltery Output Modification", "Alumite ingot return", 3, "Number of ingots returned from smelting Alumite in the smeltery").getDouble(3);
        ingotsManyullynAlloy = config.get("Smeltery Output Modification", "Manyullyn ingot return", 1, "Number of ingots returned from smelting Manyullyn in the smeltery").getDouble(1);
        ingotsPigironAlloy = config.get("Smeltery Output Modification", "Pig Iron ingot return", 1, "Number of ingots returned from smelting Pig Iron in the smeltery").getDouble(1);

        capesEnabled = config.get("Superfun", "Enable-TCon-Capes", true).getBoolean(true);

        achievementsEnabled = config.get("Achievements", "Enable Achievements", true).getBoolean(true);

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
        generateEssenceBush = config.get("Worldgen Disabler", "Generate Essence Bushes", true).getBoolean(true);
        addToVillages = config.get("Worldgen Disabler", "Add Village Generation", true).getBoolean(true);

        copperuDensity = config.get("Worldgen", "Copper Underground Density", 2, "Density: Chances per chunk").getInt(2);
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
        coppersRarity = config.get("Worldgen", "Copper Surface Rarity", 100, "Rarity: 1/num to generate in chunk").getInt(100);
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
        essenceBushRarity = config.get("Worldgen", "Essence Bush Rarity", 6).getInt(6);

        copperBushMinY = config.get("Worldgen", "Copper Bush Min Y", 20).getInt(20);
        copperBushMaxY = config.get("Worldgen", "Copper Bush Max Y", 60).getInt(60);
        tinBushMinY = config.get("Worldgen", "Tin Bush Min Y", 0).getInt(0);
        tinBushMaxY = config.get("Worldgen", "Tin Bush Max Y", 40).getInt(40);
        aluminumBushMinY = config.get("Worldgen", "Aluminum Bush Min Y", 0).getInt(0);
        aluminumBushMaxY = config.get("Worldgen", "Aluminum Bush Max Y", 60).getInt(60);

        seaLevel = config.get("general", "Sea level", 64).getInt(64);
        tconComesFirst = config.get("general", "Always cast TConstruct ingots", true, "You will always get a TConstruct item from casting an ingot or block.").getBoolean();

        enableHealthRegen = config.get("Ultra Hardcore Changes", "Passive Health Regen", true).getBoolean(true);
        goldAppleRecipe = config.get("Ultra Hardcore Changes", "Change Crafting Recipes", false, "Makes recipes for gold apples, carrots, and melon potions more expensive").getBoolean(false);
        dropPlayerHeads = config.get("Ultra Hardcore Changes", "Players drop heads on death", false).getBoolean(false);
        uhcGhastDrops = config.get("Ultra Hardcore Changes", "Change Ghast drops to Gold Ingots", false).getBoolean(false);
        worldBorder = config.get("Ultra Hardcore Changes", "Add World Border", false).getBoolean(false);
        worldBorderSize = config.get("Ultra Hardcore Changes", "World Border Radius", 1000).getInt(1000);
        freePatterns = config.get("Ultra Hardcore Changes", "Add Patterns to Pattern Chests", false, "Gives all tier 1 patterns when pattern chest is placed").getBoolean(false);
        AbilityHelper.necroticUHS = config.get("Ultra Hardcore Changes", "Necrotic modifier only heals on hostile mob kills", false).getBoolean(false);

        // Slime pools
        islandRarity = config.get("Worldgen", "Slime Island Rarity", 1450).getInt(1450);

        // Looks
        Property conTexMode = config.get("Looks", "Connected Textures Enabled", true);
        conTexMode.comment = "0 = disabled, 1 = enabled, 2 = enabled + ignore stained glass meta";
        connectedTexturesMode = conTexMode.getInt(2);

        // dimension blacklist
        cfgDimBlackList = config.get("DimBlackList", "SlimeIslandDimBlacklist", new int[] {}, "Add dimension ID's to prevent slime islands from generating in them").getIntList();
        slimeIslGenDim0Only = config.get("DimBlackList", "GenerateSlimeIslandInDim0Only", false, "True: slime islands wont generate in any ages other than overworld(if enabled); False: will generate in all non-blackisted ages").getBoolean(false);
        slimeIslGenDim0 = config.get("DimBlackList", "slimeIslGenDim0", true, "True: slime islands generate in overworld; False they do not generate").getBoolean(true);
        genIslandsFlat = config.get("DimBlacklist", "genIslandsFlat", false, "Generate slime islands in flat worlds").getBoolean(false);

        // Experimental functionality
        throwableSmeltery = config.get("Experimental", "Items can be thrown into smelteries", true).getBoolean(true);
        newSmeltery = config.get("Experimental", "Use new adaptive Smeltery code", false, "Warning: Very buggy").getBoolean(false);
        meltableHorses = config.get("Experimental", "Allow horses to be melted down for glue", true).getBoolean(true);

        /* Save the configuration file only if it has changed */
        if (config.hasChanged())
            config.save();

        File gt = new File(location + "/GregTech");
        if (gt.exists())
        {
            File gtDyn = new File(location + "/GregTech/DynamicConfig.cfg");
            Configuration gtConfig = new Configuration(gtDyn);
            gtConfig.load();
            gregtech = gtConfig.get("smelting", "tile.anvil.slightlyDamaged", false).getBoolean(false);
        }
    }

    //Modules
    public static boolean worldModule;
    public static boolean toolModule;
    public static boolean smelteryModule;
    public static boolean mechworksModule;
    public static boolean armorModule;
    public static boolean prayerModule;
    public static boolean cropifyModule;

    public static boolean capesEnabled;

    // Achievements
    public static boolean achievementsEnabled;

    // Ore values
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
    public static boolean generateEssenceBush;

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
    public static int essenceBushRarity;

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
    public static boolean tconComesFirst;

    // Mobs
    public static int naturalSlimeSpawn;

    // Difficulty modifiers
    public static boolean keepHunger;
    public static boolean keepLevels;
    public static boolean alphaRegen;
    public static boolean alphaHunger;

    public static boolean disableWoodTools;
    public static boolean disableStoneTools;
    public static boolean disableIronTools;
    public static boolean disableDiamondTools;
    public static boolean disableGoldTools;

    public static boolean enableTWood;
    public static boolean enableTStone;
    public static boolean enableTCactus;
    public static boolean enableTBone;
    public static boolean enableTFlint;
    public static boolean enableTNetherrack;
    public static boolean enableTSlime;
    public static boolean enableTPaper;
    public static boolean enableTBlueSlime;

    public static boolean craftMetalTools;
    public static boolean vanillaMetalBlocks;
    public static boolean removeVanillaToolRecipes;
    public static boolean labotimizeVanillaTools;
    public static boolean miningLevelIncrease;
    public static boolean denyMattock;

    // Smeltery Output Modification
    public static double ingotsPerOre;
    public static double ingotsBronzeAlloy;
    public static double ingotsAluminumBrassAlloy;
    public static double ingotsAlumiteAlloy;
    public static double ingotsManyullynAlloy;
    public static double ingotsPigironAlloy;

    public static boolean craftEndstone;
    // Ultra Hardcore modifiers
    public static boolean enableHealthRegen;
    public static boolean goldAppleRecipe;
    public static boolean dropPlayerHeads;
    public static boolean uhcGhastDrops;
    public static boolean worldBorder;
    public static int worldBorderSize;
    public static boolean freePatterns;
    public static int goldHead;

    // Superfun
    public static boolean superfunWorld;
    public static boolean beginnerBook;

    public static boolean gregtech;
    public static boolean lavaFortuneInteraction;

    public static int islandRarity;

    // Looks
    public static int connectedTexturesMode;

    // dimensionblacklist
    public static boolean slimeIslGenDim0Only;
    public static int[] cfgDimBlackList;
    public static boolean slimeIslGenDim0;
    public static boolean genIslandsFlat;

    // Experimental functionality
    public static boolean throwableSmeltery;
    public static boolean newSmeltery;
    public static boolean meltableHorses;

}
