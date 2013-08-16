package tconstruct.util;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.Configuration;

public class PHConstruct
{

    public static void initProps (File location)
    {
        /* Here we will set up the config file for the mod 
         * First: Create a folder inside the config folder
         * Second: Create the actual config file
         * Note: Configs are a pain, but absolutely necessary for every mod.
         */
        //File file = new File(TConstruct.proxy.getLocation() + "/config");
        //file.mkdir();
        //File newFile = new File(TConstruct.proxy.getLocation() + "/config/TinkersWorkshop.txt");
        File newFile = new File(location + "/TinkersWorkshop.txt");

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
        beginnerBook = config.get("Difficulty Changes", "Spawn beginner book", true).getBoolean(true);

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
        enableTFlint = config.get("Difficulty Changes", "Enable mod flint tools", true).getBoolean(true);
        enableTNetherrack = config.get("Difficulty Changes", "Enable mod netherrack tools", true).getBoolean(true);
        enableTSlime = config.get("Difficulty Changes", "Enable mod slime tools", true).getBoolean(true);
        enableTPaper = config.get("Difficulty Changes", "Enable mod paper tools", true).getBoolean(true);
        enableTBlueSlime = config.get("Difficulty Changes", "Enable mod blue slime tools", true).getBoolean(true);
        
        craftMetalTools = config.get("Difficulty Changes", "Craft metals with Wood Patterns", false).getBoolean(false);
        vanillaMetalBlocks = config.get("Difficulty Changes", "Craft vanilla metal blocks", true).getBoolean(true);
        lavaFortuneInteraction = config.get("Difficulty Changes", "Enable Auto-Smelt and Fortune interaction", true).getBoolean(true);
        removeVanillaToolRecipes = config.get("Difficulty Changes", "Remove Vanilla Tool Recipes", false).getBoolean(false);
        harderBronze = config.get("Difficulty Changes", "Lower bronze output to 2 ingots", false).getBoolean(false);
        stencilTableCrafting = config.get("Difficulty Changes", "Craft Stencil Tables", true).getBoolean(true);
        miningLevelIncrease = config.get("Difficulty Changes", "Modifiers increase Mining Level", true).getBoolean(true);
        denyMattock = config.get("Difficulty Changes", "Deny creation of non-metal mattocks", false).getBoolean(false);

        blueSlime = config.get("Mob Spawning", "Activate Blue Slime Spawns", true).getBoolean(true);
        blueSlimeWeight = config.get("Mob Spawning", "Spawn Weight for Blue Slime", 6).getInt(6);

        woodStation = config.getBlock("Wood Tool Station", 1471).getInt(1471);
        heldItemBlock = config.getBlock("Held Item Block", 1472).getInt(1472);
        lavaTank = config.getBlock("Lava Tank", 1473).getInt(1473);
        smeltery = config.getBlock("Smeltery", 1474).getInt(1474);
        oreSlag = config.getBlock("Ores Slag", 1475).getInt(1475);
        craftedSoil = config.getBlock("Special Soil", 1476).getInt(1476);
        searedTable = config.getBlock("Seared Table", 1477).getInt(1477);
        metalBlock = config.getBlock("Metal Storage", 1478).getInt(1478);
        /*metalFlowing = config.getBlock("Liquid Metal Flowing", 1479).getInt(1479);
        metalStill = config.getBlock("Liquid Metal Still", 1480).getInt(1480);*/

        multiBrick = config.getBlock("Multi Brick", 1481).getInt(1481);

        stoneTorch = config.getBlock("Stone Torch", 1484).getInt(1484);
        oreBerry = config.getBlock("Ore Berry One", 1485).getInt(1485);
        oreBerrySecond = config.getBlock("Ore Berry Two", 1486).getInt(1486);

        oreGravel = config.getBlock("Ores Gravel", 1488).getInt(1488);
        speedBlock = config.getBlock("Speed Block", 1489).getInt(1489);

        landmine = config.getBlock("Landmine", 1470).getInt(1470);
        toolForge = config.getBlock("Tool Forge", 1468).getInt(1468);
        multiBrickFancy = config.getBlock("Multi Brick Fancy", 1467).getInt(1467);
        
        barricadeOak = config.getBlock("Oak Barricade", 1469).getInt(1469);
        barricadeSpruce = config.getBlock("Spruce Barricade", 1482).getInt(1482);
        barricadeBirch = config.getBlock("Birch Barricade", 1483).getInt(1483);
        barricadeJungle = config.getBlock("Jungle Barricade", 1487).getInt(1487);
        
      //Thermal Expansion
        moltenSilver = config.getBlock("Molten Silver", 3195).getInt(3195);
        moltenLead = config.getBlock("Molten Lead", 3196).getInt(3196);
        moltenNickel = config.getBlock("Molten Nickel", 3197).getInt(3197);
        moltenShiny = config.getBlock("Molten Platinum", 3198).getInt(3198);
        moltenInvar = config.getBlock("Molten Invar", 3199).getInt(3199);
        moltenElectrum = config.getBlock("Molten Electrum", 3200).getInt(3200);
        moltenIron = config.getBlock("Molten Iron", 3201).getInt(3201);
        moltenGold = config.getBlock("Molten Gold", 3202).getInt(3202);
        moltenCopper = config.getBlock("Molten Copper", 3203).getInt(3203);
        moltenTin = config.getBlock("Molten Tin", 3204).getInt(3204);
        moltenAluminum = config.getBlock("Molten Aluminum", 3205).getInt(3205);
        moltenCobalt = config.getBlock("Molten Cobalt", 3206).getInt(3206);
        moltenArdite = config.getBlock("Molten Ardite", 3207).getInt(3207);
        moltenBronze = config.getBlock("Molten Bronze", 3208).getInt(3208);
        moltenAlubrass = config.getBlock("Molten Aluminum Brass", 3209).getInt(3209);
        moltenManyullyn = config.getBlock("Molten Manyullyn", 3210).getInt(3210);
        moltenAlumite = config.getBlock("Molten Alumite", 3211).getInt(3211);
        moltenObsidian = config.getBlock("Molten Obsidian", 3212).getInt(3212);
        moltenSteel = config.getBlock("Molten Steel", 3213).getInt(3213);
        moltenGlass = config.getBlock("Molten Glass", 3214).getInt(3214);
        moltenStone = config.getBlock("Molten Stone", 3215).getInt(3215);
        moltenEmerald = config.getBlock("Molten Emerald", 3216).getInt(3216);
        blood = config.getBlock("Liquid Cow", 3217).getInt(3217);
        moltenEnder = config.getBlock("Molten Ender", 3218).getInt(3218);

        aggregator = config.getBlock("Aggregator", 3221).getInt(3221);
        lightCrystalBase = config.getBlock("Light Crystal", 3222).getInt(3222);
        glass = config.getBlock("Clear Glass", 3223).getInt(3223);
        stainedGlass = config.getBlock("Stained Glass", 3224).getInt(3224);
        stainedGlassClear = config.getBlock("Clear Stained Glass", 3225).getInt(3225);
        redstoneMachine = config.getBlock("Redstone Machines", 3226).getInt(3226);
        dryingRack = config.getBlock("Drying Rack", 3227).getInt(3227);
        glassPane = config.getBlock("Glass Pane", 3228).getInt(3228);
        stainedGlassClearPane = config.getBlock("Clear Stained Glass Pane", 3229).getInt(3229);
        
        searedSlab = config.getBlock("Seared Slab", 3230).getInt(3230);
        speedSlab = config.getBlock("Speed Slab", 3231).getInt(3231);
        //grassSlab = config.getBlock("Grass Slab", 3230).getInt(3230);
        
        punji = config.getBlock("Punji", 3232).getInt(3232);
        woodCrafter = config.getBlock("Crafting Station", 3233).getInt(3233);
        essenceExtractor = config.getBlock("Essence Extractor", 3234).getInt(3234);
        
        slimePoolBlue = config.getBlock("Liquid Blue Slime", 3235).getInt(3235);
        slimeGel = config.getBlock("Congealed Slime", 3237).getInt(3237);
        slimeGrass = config.getBlock("Slime Grass", 3238).getInt(3238);
        slimeTallGrass = config.getBlock("Slime Tall Grass", 3239).getInt(3239);
        slimeLeaves = config.getBlock("Slime Grass Leaves", 3240).getInt(3240);
        slimeSapling = config.getBlock("Slime Tree Sapling", 3241).getInt(3241);
        
        meatBlock = config.getBlock("Meat Block", 3242).getInt(3242);
        woodCrafterSlab = config.getBlock("Crafting Slab", 3243).getInt(3243);
        woolSlab1 = config.getBlock("Wool Slab 1", 3244).getInt(3244);
        woolSlab2 = config.getBlock("Wool Slab 2", 3245).getInt(3245);
        castingChannel = config.getBlock("Casting Channel", 3249).getInt(3249);

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
        scytheBlade = config.getItem("Tool Parts", "Scythe Head", 14040).getInt(14040);
        toughBinding = config.getItem("Tool Parts", "Tough Binding", 14041).getInt(14041);
        toughRod = config.getItem("Tool Parts", "Tough Rod", 14042).getInt(14042);
        largeSwordBlade = config.getItem("Tool Parts", "Large Sword Blade", 14043).getInt(14043);
        largePlate = config.getItem("Tool Parts", "Large Plate", 14044).getInt(14044);
        excavatorHead = config.getItem("Tool Parts", "Excavator Head", 14045).getInt(14045);
        hammerHead = config.getItem("Tool Parts", "Hammer Head", 14046).getInt(14046);
        fullGuard = config.getItem("Tool Parts", "Full Guard", 14047).getInt(14047);
        bowstring = config.getItem("Tool Parts", "Bowstring", 14048).getInt(14048);
        arrowhead = config.getItem("Tool Parts", "Arrowhead", 14049).getInt(14049);
        fletching = config.getItem("Tool Parts", "Fletching", 14050).getInt(14050);

        pickaxe = config.getItem("Tools", "Pickaxe", 14051).getInt(14051);
        shovel = config.getItem("Tools", "Shovel", 14052).getInt(14052);
        axe = config.getItem("Tools", "Axe", 14053).getInt(14053);
        hoe = config.getItem("Tools", "Hoe", 14054).getInt(14054);

        broadsword = config.getItem("Tools", "Broadsword", 14055).getInt(14055);
        rapier = config.getItem("Tools", "Rapier", 14057).getInt(14057);
        longsword = config.getItem("Tools", "Longsword", 14056).getInt(14056);
        dagger = config.getItem("Tools", "Dagger", 14065).getInt(14065);

        frypan = config.getItem("Tools", "Frying Pan", 14058).getInt(14058);
        battlesign = config.getItem("Tools", "Battlesign", 14059).getInt(14059);
        mattock = config.getItem("Tools", "Mattock", 14060).getInt(14060);
        lumberaxe = config.getItem("Tools", "Lumber Axe", 14061).getInt(14061);
        longbow = config.getItem("Tools", "Longbow", 14062).getInt(14062);
        shortbow = config.getItem("Tools", "Shortbow", 14063).getInt(14063);
        potionLauncher = config.getItem("Tools", "Potion Launcher", 14064).getInt(14064);

        chisel = config.getItem("Tools", "Chisel", 14066).getInt(14066);
        scythe = config.getItem("Tools", "Scythe", 14067).getInt(14067);
        cleaver = config.getItem("Tools", "Cleaver", 14068).getInt(14068);
        excavator = config.getItem("Tools", "Excavator", 14069).getInt(14069);
        hammer = config.getItem("Tools", "Hammer", 14070).getInt(14070);
        battleaxe = config.getItem("Tools", "Battleaxe", 14071).getInt(14071);

        cutlass = config.getItem("Tools", "Cutlass", 14072).getInt(14072);
        arrow = config.getItem("Tools", "Arrow", 14073).getInt(14073);

        buckets = config.getItem("Patterns and Misc", "Buckets", 14101).getInt(14101);
        uselessItem = config.getItem("Patterns and Misc", "Title Icon", 14102).getInt(14102);
        slimefood = config.getItem("Patterns and Misc", "Strange Food", 14103).getInt(14103);
        oreChunks = config.getItem("Patterns and Misc", "Ore Chunks", 14104).getInt(14104);

        heartCanister = config.getItem("Equipables", "Heart Canister", 14105).getInt(14105);
        heavyHelmet = config.getItem("Equipables", "Heavy Helmet", 14106).getInt(14106);
        diamondApple = config.getItem("Patterns and Misc", "Jeweled Apple", 14107).getInt(14107);
        heavyChestplate = config.getItem("Equipables", "Heavy Chestplate", 14108).getInt(14108);
        heavyPants = config.getItem("Equipables", "Heavy Pants", 14109).getInt(14109);
        heavyBoots = config.getItem("Equipables", "Heavy Boots", 14110).getInt(14110);
        glove = config.getItem("Equipables", "Gloves", 14111).getInt(14111);
        knapsack = config.getItem("Equipables", "Knapsack", 14112).getInt(14112);
        goldHead = config.getItem("Patterns and Misc", "Golden Head", 14113).getInt(14113);
        essenceCrystal = config.getItem("Patterns and Misc", "Essence Crystal", 14114).getInt(14114);

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
        essenceBushRarity = config.get("Worldgen", "Essence Bush Rarity", 6).getInt(6);

        copperBushMinY = config.get("Worldgen", "Copper Bush Min Y", 20).getInt(20);
        copperBushMaxY = config.get("Worldgen", "Copper Bush Max Y", 60).getInt(60);
        tinBushMinY = config.get("Worldgen", "Tin Bush Min Y", 0).getInt(0);
        tinBushMaxY = config.get("Worldgen", "Tin Bush Max Y", 40).getInt(40);
        aluminumBushMinY = config.get("Worldgen", "Aluminum Bush Min Y", 0).getInt(0);
        aluminumBushMaxY = config.get("Worldgen", "Aluminum Bush Max Y", 60).getInt(60);

        seaLevel = config.get("general", "Sea level", 64).getInt(64);

        enableHealthRegen = config.get("Ultra Hardcore Changes", "Passive Health Regen", true).getBoolean(true); //Check
        goldAppleRecipe = config.get("Ultra Hardcore Changes", "Change Crafting Recipes", false).getBoolean(false); //Check
        dropPlayerHeads = config.get("Ultra Hardcore Changes", "Players drop heads on death", false).getBoolean(false); //Check
        uhcGhastDrops = config.get("Ultra Hardcore Changes", "Change Ghast drops to Gold Ingots", false).getBoolean(false); //Check
        worldBorder = config.get("Ultra Hardcore Changes", "Add World Border", false).getBoolean(false);
        worldBorderSize = config.get("Ultra Hardcore Changes", "World Border Radius", 1000).getInt(1000);
        freePatterns = config.get("Ultra Hardcore Changes", "Add Patterns to Pattern Chests", false).getBoolean(false); //Check
        
        //Slime pools
        islandRarity = config.get("Worldgen", "Slime Island Rarity", 450).getInt(450);

        /* Save the configuration file */
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

    //Blocks
    public static int woodStation;
    public static int toolForge;
    public static int heldItemBlock;
    public static int woodCrafter;
    public static int woodCrafterSlab;
    
    public static int ores;
    public static int lavaTank;
    public static int smeltery;
    public static int searedTable;
    public static int castingChannel;
    public static int craftedSoil;
    public static int oreSlag;
    public static int oreGravel;
    public static int metalBlock;

    public static int redstoneMachine;
    public static int dryingRack;

    /*public static int golemCore;
    public static int golemHead;
    public static int golemPedestal;*/

    //Crops
    public static int oreBerry;
    public static int oreBerrySecond;
    public static int netherOreBerry;

    //Traps
    public static int landmine;
    public static int punji;
    public static int barricadeOak;
    public static int barricadeSpruce;
    public static int barricadeBirch;
    public static int barricadeJungle;

    //InfiBlocks
    public static int speedBlock;
    public static int glass;
    public static int glassPane;
    public static int stainedGlass;
    public static int stainedGlassClear;
    public static int stainedGlassClearPane;

    //Crystalline
    public static int aggregator;
    public static int lightCrystalBase;
    public static int essenceExtractor;
    public static int essenceCrystal;

    //Liquids
    public static int metalFlowing;
    public static int metalStill;
    
    public static int moltenIron;
    public static int moltenGold;
    public static int moltenCopper;
    public static int moltenTin;
    public static int moltenAluminum;
    public static int moltenCobalt;
    public static int moltenArdite;
    public static int moltenBronze;
    public static int moltenAlubrass;
    public static int moltenManyullyn;
    public static int moltenAlumite;
    public static int moltenObsidian;
    public static int moltenSteel;
    public static int moltenGlass;
    public static int moltenStone;
    public static int moltenEmerald;
    public static int blood;
    public static int moltenEnder;
    public static int moltenSilver; //Thermal Expansion
    public static int moltenLead;
    public static int moltenNickel;
    public static int moltenShiny;
    public static int moltenInvar;
    public static int moltenElectrum;
    
    //Slime
    public static int slimePoolBlue;
    public static int slimeGel;
    public static int slimeGrass;
    public static int slimeTallGrass;
    public static int slimeLeaves;
    public static int slimeSapling;

    //Decoration
    public static int stoneTorch;
    public static int multiBrick;
    public static int multiBrickFancy;
    public static int redstoneBallRepeater;
    public static int searedSlab;
    public static int speedSlab;
    public static int meatBlock;
    public static int woolSlab1;
    public static int woolSlab2;

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
    public static int diamondApple;
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
    public static int cutlass;

    public static int frypan;
    public static int battlesign;
    public static int longbow;
    public static int shortbow;
    public static int potionLauncher;
    public static int mattock;

    public static int lumberaxe;
    public static int scythe;
    public static int cleaver;
    public static int excavator;
    public static int hammer;
    public static int battleaxe;

    public static int chisel;
    public static int arrow;

    //Tool parts
    public static int swordBlade;
    public static int largeGuard;
    public static int medGuard;
    public static int crossbar;
    public static int knifeBlade;
    public static int fullGuard;

    public static int pickaxeHead;
    public static int axeHead;
    public static int shovelHead;
    public static int hoeHead;

    public static int frypanHead;
    public static int signHead;
    public static int chiselHead;

    public static int scytheBlade;
    public static int lumberHead;
    public static int largeSwordBlade;
    public static int excavatorHead;
    public static int hammerHead;

    public static int binding;
    public static int toughBinding;
    public static int toughRod;
    public static int largePlate;

    public static int bowstring;
    public static int arrowhead;
    public static int fletching;

    //Wearables
    public static int heavyHelmet;
    public static int heavyChestplate;
    public static int heavyPants;
    public static int heavyBoots;
    public static int glove;
    public static int knapsack;

    public static int heartCanister;

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

    //Mobs
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
    public static boolean enableTFlint;
    public static boolean enableTNetherrack;
    public static boolean enableTSlime;
    public static boolean enableTPaper;
    public static boolean enableTBlueSlime;

    public static boolean craftMetalTools;
    public static boolean vanillaMetalBlocks;
    public static boolean removeVanillaToolRecipes;
    public static boolean harderBronze;
    public static boolean stencilTableCrafting;
    public static boolean miningLevelIncrease;
    public static boolean denyMattock;
    
    //Ultra Hardcore modifiers
    public static boolean enableHealthRegen;
    public static boolean goldAppleRecipe;
    public static boolean dropPlayerHeads;
    public static boolean uhcGhastDrops;
    public static boolean worldBorder;
    public static int worldBorderSize;
    public static boolean freePatterns;
    public static int goldHead;

    //Superfun
    public static boolean superfunWorld;
    public static boolean beginnerBook;

    public static boolean gregtech;
    public static boolean lavaFortuneInteraction;
    
    public static int islandRarity;
}
