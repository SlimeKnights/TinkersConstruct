package mods.tinker.tconstruct.common;

import java.util.Collection;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.*;
import mods.tinker.tconstruct.blocks.logic.*;
import mods.tinker.tconstruct.entity.*;
import mods.tinker.tconstruct.entity.projectile.*;
import mods.tinker.tconstruct.items.*;
import mods.tinker.tconstruct.items.armor.*;
import mods.tinker.tconstruct.items.blocks.*;
import mods.tinker.tconstruct.items.tools.*;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import mods.tinker.tconstruct.library.crafting.*;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.util.IPattern;
import mods.tinker.tconstruct.modifiers.*;
import mods.tinker.tconstruct.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import extrabiomes.api.BiomeManager;

public class TContent implements IFuelHandler
{
    //Patterns and other materials
    public static Item blankPattern;
    public static Item materials;
    public static Item toolRod;
    public static Item toolShard;
    public static Item woodPattern;
    public static Item metalPattern;

    public static Item manualBook;
    public static Item buckets;
    public static Item titleIcon;

    public static Item strangeFood;
    public static Item diamondApple;
    //public static Item stonePattern;
    //public static Item netherPattern;

    //Tools
    public static ToolCore pickaxe;
    public static ToolCore shovel;
    public static ToolCore hatchet;
    public static ToolCore broadsword;
    public static ToolCore longsword;
    public static ToolCore rapier;
    public static ToolCore dagger;
    public static ToolCore cutlass;

    public static ToolCore frypan;
    public static ToolCore battlesign;
    public static ToolCore chisel;
    public static ToolCore mattock;
    //public static ToolCore longbow;

    public static ToolCore scythe;
    public static ToolCore lumberaxe;
    public static ToolCore cleaver;
    public static ToolCore excavator;
    public static ToolCore hammer;
    public static ToolCore battleaxe;

    public static Item potionLauncher;

    //Tool parts
    public static Item binding;
    public static Item toughBinding;
    public static Item toughRod;
    public static Item heavyPlate;

    public static Item pickaxeHead;
    public static Item shovelHead;
    public static Item hatchetHead;
    public static Item frypanHead;
    public static Item signHead;
    public static Item chiselHead;
    public static Item scytheBlade;
    public static Item broadAxeHead;
    public static Item excavatorHead;
    public static Item hammerHead;

    public static Item swordBlade;
    public static Item largeSwordBlade;
    public static Item knifeBlade;

    public static Item wideGuard;
    public static Item handGuard;
    public static Item crossbar;
    public static Item fullGuard;

    //Crafting blocks
    public static Block toolStationWood;
    public static Block toolStationStone;
    public static Block toolForge;
    public static Block heldItemBlock;
    public static Block craftedSoil;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block metalBlock;

    //Decoration
    public static Block stoneTorch;
    public static Block multiBrick;
    public static Block multiBrickFancy;

    //Traps
    public static Block landmine;
    public static Block barricade;

    //InfiBlocks
    public static Block speedBlock;

    //Crystalline
    public static Block aggregator;
    public static Block lightCrystalBase;

    //Liquids
    public static Block liquidMetalFlowing;
    public static Block liquidMetalStill;
    public static Material liquidMetal;

    //Ores
    public static Block oreSlag;
    public static Block oreGravel;
    public static OreberryBush oreBerry;
    public static OreberryBush oreBerrySecond;
    public static Item oreBerries;

    //Tool modifiers
    public static ModElectric modE;
    public static ModLapis modL;

    //Golems
    /*public static Block glowSapling;
    public static Block glowLeaves;
    public static Block glowLog;*/

    public static Block redstoneBallRepeater;
    public static Item redstoneBallRepeaterItem;

    public static Block golemCore;
    public static Block golemPedestal;
    public static Block golemHead;

    public static Item golemWand;
    public static Item golemRecharger;
    public static Item corestone;
    public static Item notebook;
    public static Item note;

    //Wearables
    public static Item heavyHelmet;
    public static Item heavyChestplate;
    public static Item heavyPants;
    public static Item heavyBoots;
    public static Item glove;
    public static Item knapsack;

    public static Item heartCanister;
    public static Item goldHead;

    //Chest hooks
    public static ChestGenHooks tinkerHouseChest;
    public static ChestGenHooks tinkerHousePatterns;

    public TContent()
    {
        registerBlocks();
        registerItems();
        registerMaterials();
        addCraftingRecipes();
        setupToolTabs();
        addLoot();
    }

    public void createEntities ()
    {
        EntityRegistry.registerModEntity(FancyEntityItem.class, "Fancy Item", 0, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(DaggerEntity.class, "Dagger", 1, TConstruct.instance, 32, 5, true);
        //EntityRegistry.registerModEntity(Crystal.class, "Crystal", 2, TConstruct.instance, 32, 5, true);
        //EntityRegistry.registerModEntity(LaunchedPotion.class, "Launched Potion", 1, TConstruct.instance, 32, 3, true);
        //EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1, TConstruct.instance, 32, 5, true);

        //EntityRegistry.registerModEntity(Skyla.class, "Skyla", 10, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(SlimeClone.class, "SlimeClone", 10, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(Automaton.class, "Automaton", 11, TConstruct.instance, 64, 3, true);
        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        //EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13, TConstruct.instance, 64, 5, true);

        BiomeGenBase[] plains = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.PLAINS);
        BiomeGenBase[] mountain = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.MOUNTAIN);
        BiomeGenBase[] hills = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.HILLS);
        BiomeGenBase[] swamp = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.SWAMP);
        BiomeGenBase[] desert = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.DESERT);
        BiomeGenBase[] frozen = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.FROZEN);
        BiomeGenBase[] jungle = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.JUNGLE);
        BiomeGenBase[] wasteland = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.WASTELAND);

        BiomeGenBase[] nether = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.NETHER);

        /*if (PHConstruct.superfunWorld)
        {
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, plains);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, mountain);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, hills);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, swamp);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, desert);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, frozen);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, jungle);
        	EntityRegistry.addSpawn(NitroCreeper.class, 1000, 100, 100, EnumCreatureType.monster, wasteland);
        	DimensionManager.unregisterProviderType(0);
        	DimensionManager.registerProviderType(0, OverworldProvider.class, true);
        }*/
        if (PHConstruct.blueSlime)
        {
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, plains);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, mountain);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, hills);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, swamp);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, desert);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, frozen);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, jungle);
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, wasteland);
        }

        try
        {
            Class.forName("extrabiomes.api.BiomeManager");
            Collection<BiomeGenBase> ebxlCollection = BiomeManager.getBiomes();
            BiomeGenBase[] ebxlBiomes = (BiomeGenBase[]) ebxlCollection.toArray();
            EntityRegistry.addSpawn(BlueSlime.class, PHConstruct.blueSlimeWeight, 4, 4, EnumCreatureType.monster, ebxlBiomes);
        }
        catch (Exception e)
        {

        }
        //EntityRegistry.addSpawn(MetalSlime.class, 1, 4, 4, EnumCreatureType.monster, overworldBiomes);
    }

    void registerBlocks ()
    {
        //Tool Station
        toolStationWood = new ToolStationBlock(PHConstruct.woodCrafter, Material.wood).setUnlocalizedName("ToolStation");
        GameRegistry.registerBlock(toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartCrafterLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");

        toolForge = new ToolForgeBlock(PHConstruct.toolForge, Material.iron).setUnlocalizedName("ToolForge");
        GameRegistry.registerBlock(toolForge, ToolForgeItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");

        heldItemBlock = new EquipBlock(PHConstruct.heldItemBlock, Material.wood).setUnlocalizedName("Frypan");
        GameRegistry.registerBlock(heldItemBlock, "HeldItemBlock");
        GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");

        craftedSoil = new SoilBlock(PHConstruct.craftedSoil).setUnlocalizedName("TConstruct.Soil");
        craftedSoil.stepSound = Block.soundGravelFootstep;
        GameRegistry.registerBlock(craftedSoil, CraftedSoilItemBlock.class, "CraftedSoil");

        metalBlock = new TMetalBlock(PHConstruct.metalBlock, Material.iron, 10.0F).setUnlocalizedName("tconstruct.metalblock");
        metalBlock.stepSound = Block.soundMetalFootstep;
        GameRegistry.registerBlock(metalBlock, MetalItemBlock.class, "MetalBlock");

        //Smeltery
        smeltery = new SmelteryBlock(PHConstruct.smeltery).setUnlocalizedName("Smeltery");
        GameRegistry.registerBlock(smeltery, SmelteryItemBlock.class, "Smeltery");
        GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
        GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");
        GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");

        lavaTank = new LavaTankBlock(PHConstruct.lavaTank).setUnlocalizedName("LavaTank");
        lavaTank.setStepSound(Block.soundGlassFootstep);
        GameRegistry.registerBlock(lavaTank, LavaTankItemBlock.class, "LavaTank");
        GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

        searedBlock = new SearedBlock(PHConstruct.searedTable).setUnlocalizedName("SearedBlock");
        GameRegistry.registerBlock(searedBlock, SearedTableItemBlock.class, "SearedBlock");
        GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "CastingBasin");

        //Traps
        /*landmine = new Landmine(PHConstruct.landmine, 0, EnumMobType.mobs, Material.cactus).setUnlocalizedName("landmine");
        GameRegistry.registerBlock(landmine, "landmine");*/

        //Liquids
        liquidMetal = new MaterialLiquid(MapColor.tntColor);
        liquidMetalFlowing = new LiquidMetalFlowing(PHConstruct.metalFlowing).setUnlocalizedName("liquid.metalFlow");
        liquidMetalStill = new LiquidMetalStill(PHConstruct.metalStill).setUnlocalizedName("liquid.metalStill");
        GameRegistry.registerBlock(liquidMetalFlowing, LiquidItemBlock.class, "metalFlow");
        GameRegistry.registerBlock(liquidMetalStill, LiquidItemBlock.class, "metalStill");
        GameRegistry.registerTileEntity(LiquidTextureLogic.class, "LiquidTexture");

        //Decoration
        stoneTorch = new StoneTorch(PHConstruct.stoneTorch).setUnlocalizedName("decoration.stonetorch");
        GameRegistry.registerBlock(stoneTorch, "decoration.stonetorch");

        multiBrick = new MultiBrick(PHConstruct.multiBrick).setUnlocalizedName("Decoration.Brick");
        GameRegistry.registerBlock(multiBrick, MultiBrickItem.class, "decoration.multibrick");
        multiBrickFancy = new MultiBrickFancy(PHConstruct.multiBrickFancy).setUnlocalizedName("Decoration.BrickFancy");
        GameRegistry.registerBlock(multiBrickFancy, MultiBrickFancyItem.class, "decoration.multibrickfancy");

        //Ores
        String[] berryOres = new String[] { "berry_iron", "berry_gold", "berry_copper", "berry_tin", "berry_iron_ripe", "berry_gold_ripe", "berry_copper_ripe", "berry_tin_ripe" };
        oreBerry = (OreberryBush) new OreberryBush(PHConstruct.oreBerry, berryOres, 0, 4, new String[] { "oreIron", "oreGold", "oreCopper", "oreTin" }).setUnlocalizedName("ore.berries.one");
        GameRegistry.registerBlock(oreBerry, OreberryBushItem.class, "ore.berries.one");
        String[] berryOresTwo = new String[] { "berry_aluminum", "berry_silver", "", "", "berry_aluminum_ripe", "berry_silver_ripe", "", "" };
        oreBerrySecond = (OreberryBush) new OreberryBush(PHConstruct.oreBerrySecond, berryOresTwo, 4, 2, new String[] { "oreAluminum", "oreSilver" }).setUnlocalizedName("ore.berries.two");
        GameRegistry.registerBlock(oreBerrySecond, OreberryBushSecondItem.class, "ore.berries.two");

        String[] oreTypes = new String[] { "nether_slag", "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin", "ore_aluminum", "ore_slag" };
        oreSlag = new MetalOre(PHConstruct.oreSlag, Material.iron, 10.0F, oreTypes).setUnlocalizedName("tconstruct.stoneore");
        GameRegistry.registerBlock(oreSlag, MetalOreItemBlock.class, "SearedBrick");
        MinecraftForge.setBlockHarvestLevel(oreSlag, 1, "pickaxe", 4);
        MinecraftForge.setBlockHarvestLevel(oreSlag, 2, "pickaxe", 4);
        MinecraftForge.setBlockHarvestLevel(oreSlag, 3, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(oreSlag, 4, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(oreSlag, 5, "pickaxe", 1);

        oreGravel = new GravelOre(PHConstruct.oreGravel).setUnlocalizedName("GravelOre").setUnlocalizedName("tconstruct.gravelore");
        GameRegistry.registerBlock(oreGravel, GravelOreItem.class, "GravelOre");

        MinecraftForge.setBlockHarvestLevel(oreGravel, 0, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(oreGravel, 1, "shovel", 2);
        MinecraftForge.setBlockHarvestLevel(oreGravel, 2, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(oreGravel, 3, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(oreGravel, 4, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(oreGravel, 5, "shovel", 4);

        speedBlock = new SpeedBlock(PHConstruct.speedBlock).setUnlocalizedName("SpeedBlock");
        GameRegistry.registerBlock(speedBlock, SpeedBlockItem.class, "SpeedBlock");

        aggregator = new Aggregator(PHConstruct.aggregator).setUnlocalizedName("Aggregator");
        aggregator.stepSound = Block.soundMetalFootstep;
        GameRegistry.registerBlock(aggregator, "Aggregator");
        GameRegistry.registerTileEntity(GlowstoneAggregator.class, "GlowstoneAggregator");
        
        lightCrystalBase = new LightCrystalBase(PHConstruct.lightCrystalBase).setUnlocalizedName("LightCrystalBase");
        lightCrystalBase.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(lightCrystalBase, LightCrystalItem.class, "LightCrystalBase");

        /*redstoneBallRepeater = new RedstoneBallRepeater(PHConstruct.redstoneBallRepeater, false).setUnlocalizedName("decoration.redstoneballrepeater");
        GameRegistry.registerBlock(redstoneBallRepeater, "decoration.redstoneballrepeater");
        Item.itemsList[PHConstruct.redstoneBallRepeater] = null;
        redstoneBallRepeaterItem = new RedstoneBallRepeaterItem(PHConstruct.redstoneBallRepeater-256).setUnlocalizedName("decoration.redstoneballrepeater");*/

        //Golems
        /*golemCore = new GolemCoreBlock(PHConstruct.golemCore).setUnlocalizedName("golemcore");
        GameRegistry.registerBlock(golemCore, "Golem Core");
        GameRegistry.registerTileEntity(GolemCoreLogic.class, "TConstruct.GolemCore");
        
        golemHead = new GolemHeadBlock(PHConstruct.golemHead).setUnlocalizedName("golemhead");
        GameRegistry.registerBlock(golemHead, "Golem Head");*/

        /*golemPedestal = new GolemPedestalBlock(PHConstruct.golemPedestal).setUnlocalizedName("golempedestal");
        GameRegistry.registerBlock(golemPedestal, "Golem Pedestal");
        GameRegistry.registerTileEntity(GolemPedestalLogic.class, "TConstruct.GolemPedestal");*/

        //Traps
        /*landmine = new Landmine(PHConstruct.landmine, EnumMobType.mobs, Material.wood).setUnlocalizedName("tconstruct.landmine");
        GameRegistry.registerBlock(landmine, "Landmine");
        barricade = new BarricadeBlock(PHConstruct.barricade).setUnlocalizedName("tconstruct.barricade");
        GameRegistry.registerBlock(barricade, "Barricade");*/
    }

    void registerItems ()
    {
        titleIcon = new TitleIcon(PHConstruct.uselessItem).setUnlocalizedName("tconstruct.titleicon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast" };
        blankPattern = new CraftingItem(PHConstruct.blankPattern, blanks, blanks, "materials/").setUnlocalizedName("tconstruct.Pattern");

        materials = new MaterialItem(PHConstruct.materials).setUnlocalizedName("tconstruct.Materials");
        toolRod = new ToolPart(PHConstruct.toolRod, "ToolRod", "_rod").setUnlocalizedName("tconstruct.ToolRod");
        toolShard = new ToolShard(PHConstruct.toolShard, "ToolShard", "_chunk").setUnlocalizedName("tconstruct.ToolShard");
        woodPattern = new Pattern(PHConstruct.woodPattern, "WoodPattern", "pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
        metalPattern = new MetalPattern(PHConstruct.metalPattern, "MetalPattern", "cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
        //netherPattern = new Pattern(PHTools.netherPattern, 128, patternTexture).setUnlocalizedName("tconstruct.Pattern");

        TConstructRegistry.addItemToDirectory("blankPattern", blankPattern);
        TConstructRegistry.addItemToDirectory("woodPattern", woodPattern);
        TConstructRegistry.addItemToDirectory("metalPattern", metalPattern);

        String[] patternTypes = { "ingot", "toolRod", "pickaxeHead", "shovelHead", "hatchetHead", "swordBlade", "wideGuard", "handGuard", "crossbar", "binding", "frypanHead", "signHead",
                "knifeBlade", "chiselHead", "toughRod", "toughBinding", "largePlate", "broadAxeHead", "scytheHead", "excavatorHead", "largeBlade", "hammerHead", "fullGuard" };

        for (int i = 1; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Pattern", new ItemStack(woodPattern, 1, i));
        }
        for (int i = 0; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Cast", new ItemStack(metalPattern, 1, i));
        }

        manualBook = new Manual(PHConstruct.manual);
        buckets = new FilledBucket(PHConstruct.buckets);

        pickaxe = new Pickaxe(PHConstruct.pickaxe);
        shovel = new Shovel(PHConstruct.shovel);
        hatchet = new Hatchet(PHConstruct.axe);
        broadsword = new Broadsword(PHConstruct.broadsword);
        longsword = new Longsword(PHConstruct.longsword);
        rapier = new Rapier(PHConstruct.rapier);
        dagger = new Dagger(PHConstruct.dagger);
        cutlass = new Cutlass(PHConstruct.cutlass);

        frypan = new FryingPan(PHConstruct.frypan);
        battlesign = new BattleSign(PHConstruct.battlesign);
        mattock = new Mattock(PHConstruct.mattock);
        chisel = new Chisel(PHConstruct.chisel);

        lumberaxe = new LumberAxe(PHConstruct.lumberaxe);
        cleaver = new Cleaver(PHConstruct.cleaver);
        scythe = new Scythe(PHConstruct.scythe);
        excavator = new Excavator(PHConstruct.excavator);
        hammer = new Hammer(PHConstruct.hammer);
        battleaxe = new Battleaxe(PHConstruct.battleaxe);

        Item[] tools = { pickaxe, shovel, hatchet, broadsword, longsword, rapier, cutlass, frypan, battlesign, mattock, chisel, lumberaxe, cleaver, scythe, excavator, hammer, battleaxe };
        String[] toolStrings = { "pickaxe", "shovel", "hatchet", "broadsword", "longsword", "rapier", "cutlass", "frypan", "battlesign", "mattock", "chisel", "lumberaxe", "cleaver", "scythe",
                "excavator", "hammer", "battleaxe" };

        for (int i = 0; i < tools.length; i++)
        {
            TConstructRegistry.addItemToDirectory(toolStrings[i], tools[i]);
        }

        potionLauncher = new PotionLauncher(PHConstruct.potionLauncher).setUnlocalizedName("tconstruct.PotionLauncher");

        pickaxeHead = new ToolPart(PHConstruct.pickaxeHead, "PickaxeHead", "_pickaxe_head").setUnlocalizedName("tconstruct.PickaxeHead");
        shovelHead = new ToolPart(PHConstruct.shovelHead, "ShovelHead", "_shovel_head").setUnlocalizedName("tconstruct.ShovelHead");
        hatchetHead = new ToolPart(PHConstruct.axeHead, "AxeHead", "_axe_head").setUnlocalizedName("tconstruct.AxeHead");
        binding = new ToolPart(PHConstruct.binding, "Binding", "_binding").setUnlocalizedName("tconstruct.Binding");
        toughBinding = new ToolPart(PHConstruct.toughBinding, "ThickBinding", "_toughbind").setUnlocalizedName("tconstruct.ThickBinding");
        toughRod = new ToolPart(PHConstruct.toughRod, "ThickRod", "_toughrod").setUnlocalizedName("tconstruct.ThickRod");
        heavyPlate = new ToolPart(PHConstruct.largePlate, "LargePlate", "_largeplate").setUnlocalizedName("tconstruct.LargePlate");

        swordBlade = new ToolPart(PHConstruct.swordBlade, "SwordBlade", "_sword_blade").setUnlocalizedName("tconstruct.SwordBlade");
        wideGuard = new ToolPart(PHConstruct.largeGuard, "LargeGuard", "_large_guard").setUnlocalizedName("tconstruct.LargeGuard");
        handGuard = new ToolPart(PHConstruct.medGuard, "MediumGuard", "_medium_guard").setUnlocalizedName("tconstruct.MediumGuard");
        crossbar = new ToolPart(PHConstruct.crossbar, "Crossbar", "_crossbar").setUnlocalizedName("tconstruct.Crossbar");
        knifeBlade = new ToolPart(PHConstruct.knifeBlade, "KnifeBlade", "_knife_blade").setUnlocalizedName("tconstruct.KnifeBlade");
        fullGuard = new ToolPartHidden(PHConstruct.fullGuard, "FullGuard", "_full_guard").setUnlocalizedName("tconstruct.FullGuard");

        frypanHead = new ToolPart(PHConstruct.frypanHead, "FrypanHead", "_frypan_head").setUnlocalizedName("tconstruct.FrypanHead");
        signHead = new ToolPart(PHConstruct.signHead, "SignHead", "_battlesign_head").setUnlocalizedName("tconstruct.SignHead");
        chiselHead = new ToolPart(PHConstruct.chiselHead, "ChiselHead", "_chisel_head").setUnlocalizedName("tconstruct.ChiselHead");

        scytheBlade = new ToolPart(PHConstruct.scytheBlade, "ScytheBlade", "_scythe_head").setUnlocalizedName("tconstruct.ScytheBlade");
        broadAxeHead = new ToolPart(PHConstruct.lumberHead, "LumberHead", "_lumberaxe_head").setUnlocalizedName("tconstruct.LumberHead");
        excavatorHead = new ToolPart(PHConstruct.excavatorHead, "ExcavatorHead", "_excavator_head").setUnlocalizedName("tconstruct.ExcavatorHead");
        largeSwordBlade = new ToolPart(PHConstruct.largeSwordBlade, "LargeSwordBlade", "_large_sword_blade").setUnlocalizedName("tconstruct.LargeSwordBlade");
        hammerHead = new ToolPart(PHConstruct.hammerHead, "HammerHead", "_hammer_head").setUnlocalizedName("tconstruct.HammerHead");

        Item[] toolParts = { toolRod, toolShard, pickaxeHead, shovelHead, hatchetHead, binding, toughBinding, toughRod, heavyPlate, swordBlade, wideGuard, handGuard, crossbar, knifeBlade, fullGuard,
                frypanHead, signHead, chiselHead, scytheBlade, broadAxeHead, excavatorHead, largeSwordBlade, hammerHead };
        String[] toolPartStrings = { "toolRod", "toolShard", "pickaxeHead", "shovelHead", "hatchetHead", "binding", "toughBinding", "toughRod", "heavyPlate", "swordBlade", "wideGuard", "handGuard",
                "crossbar", "knifeBlade", "fullGuard", "frypanHead", "signHead", "chiselHead", "scytheBlade", "broadAxeHead", "excavatorHead", "largeSwordBlade", "hammerHead" };

        for (int i = 0; i < toolParts.length; i++)
        {
            TConstructRegistry.addItemToDirectory(toolPartStrings[i], toolParts[i]);
        }

        diamondApple = new DiamondApple(PHConstruct.diamondApple).setUnlocalizedName("tconstruct.apple.diamond");
        strangeFood = new StrangeFood(PHConstruct.slimefood).setUnlocalizedName("tconstruct.strangefood");
        oreBerries = new OreBerries(PHConstruct.oreChunks).setUnlocalizedName("oreberry");

        //Wearables
        //heavyHelmet = new TArmorBase(PHConstruct.heavyHelmet, 0).setUnlocalizedName("tconstruct.HeavyHelmet");
        heartCanister = new HeartCanister(PHConstruct.heartCanister).setUnlocalizedName("tconstruct.canister");
        heavyBoots = new TArmorBase(PHConstruct.heavyBoots, 3).setUnlocalizedName("tconstruct.HeavyBoots");
        glove = new Glove(PHConstruct.glove).setUnlocalizedName("tconstruct.Glove");
        knapsack = new Knapsack(PHConstruct.knapsack).setUnlocalizedName("tconstruct.storage");
        /*public static Item heavyHelmet;
        public static Item heavyChestplate;
        public static Item heavyPants;
        public static Item heavyBoots;*/

        goldHead = new GoldenHead(PHConstruct.goldHead, 4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F).setUnlocalizedName("goldenhead");

        String[] materialStrings = { "paperStack", "greenSlimeCrystal", "searedBrick", "ingotCobalt", "ingotArdite", "ingotManyullyn", "mossBall", "lavaCrystal", "necroticBone", "ingotCopper",
                "ingotTin", "ingotAluminum", "rawAluminum", "ingotBronze", "ingotAluminumBrass", "ingotAlumite", "ingotSteel", "blueSlimeCrystal", "ingotObsidian", "nuggetIron", "nuggetCopper",
                "nuggetTin", "nuggetAluminum", "nuggetSilver", "nuggetAluminumBrass", "silkyCloth", "silkyJewel" };

        for (int i = 0; i < materialStrings.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(materials, 1, i));
        }

        String[] oreberries = { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Silver" };

        for (int i = 0; i < oreberries.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory("oreberry" + oreberries[i], new ItemStack(oreBerries, 1, i));
        }

        TConstructRegistry.addItemStackToDirectory("diamondApple", new ItemStack(diamondApple, 1, 0));
        TConstructRegistry.addItemStackToDirectory("blueSlimeFood", new ItemStack(strangeFood, 1, 0));

        TConstructRegistry.addItemStackToDirectory("canisterEmpty", new ItemStack(heartCanister, 1, 0));
        TConstructRegistry.addItemStackToDirectory("miniRedHeart", new ItemStack(heartCanister, 1, 1));
        TConstructRegistry.addItemStackToDirectory("canisterRedHeart", new ItemStack(heartCanister, 1, 2));

        //Vanilla stack sizes
        Item.doorWood.setMaxStackSize(16);
        Item.doorIron.setMaxStackSize(16);
        Item.snowball.setMaxStackSize(64);
        Item.boat.setMaxStackSize(16);
        Item.minecartEmpty.setMaxStackSize(3);
        Item.minecartCrate.setMaxStackSize(3);
        Item.minecartPowered.setMaxStackSize(3);
        Block.torchWood.setTickRandomly(false);
    }

    void registerMaterials ()
    {
        TConstructRegistry.addToolMaterial(0, "Wood", 0, 59, 200, 0, 1.0F, 0, 0f, "\u00A7e", "");
        TConstructRegistry.addToolMaterial(1, "Stone", 1, 131, 400, 1, 0.5F, 0, 1f, "", "Stonebound");
        TConstructRegistry.addToolMaterial(2, "Iron", 2, 250, 600, 2, 1.3F, 1, 0f, "\u00A7f", "");
        TConstructRegistry.addToolMaterial(3, "Flint", 1, 171, 525, 2, 0.7F, 0, 1f, "\u00A78", "Stonebound");
        TConstructRegistry.addToolMaterial(4, "Cactus", 1, 150, 500, 2, 1.0F, 0, -1f, "\u00A72", "Spiny");
        TConstructRegistry.addToolMaterial(5, "Bone", 1, 200, 400, 1, 1.0F, 0, 0f, "\u00A7e", "");
        TConstructRegistry.addToolMaterial(6, "Obsidian", 3, 89, 700, 2, 0.8F, 3, 0f, "\u00A7d", "");
        TConstructRegistry.addToolMaterial(7, "Netherrack", 2, 131, 400, 1, 1.2F, 0, 1f, "\u00A74", "Stonebound");
        TConstructRegistry.addToolMaterial(8, "Slime", 3, 1500, 150, 0, 2.0F, 0, 0f, "\u00A7a", "");
        TConstructRegistry.addToolMaterial(9, "Paper", 0, 30, 200, 0, 0.3F, 0, 0f, "\u00A7f", "Writable");
        TConstructRegistry.addToolMaterial(10, "Cobalt", 4, 800, 1100, 3, 1.75F, 2, 0f, "\u00A73", "");
        TConstructRegistry.addToolMaterial(11, "Ardite", 4, 600, 800, 3, 2.0F, 0, 2f, "\u00A74", "Stonebound");
        TConstructRegistry.addToolMaterial(12, "Manyullyn", 5, 1200, 900, 4, 2.5F, 0, 0f, "\u00A75", "");
        TConstructRegistry.addToolMaterial(13, "Copper", 1, 180, 500, 2, 1.15F, 0, 0f, "\u00A7c", "");
        TConstructRegistry.addToolMaterial(14, "Bronze", 2, 350, 700, 2, 1.3F, 1, 0f, "\u00A76", "");
        TConstructRegistry.addToolMaterial(15, "Alumite", 4, 550, 800, 3, 1.3F, 2, 0f, "\u00A7d", "");
        TConstructRegistry.addToolMaterial(16, "Steel", 4, 750, 800, 3, 1.3F, 2, 0f, "", "");
        TConstructRegistry.addToolMaterial(17, "BlueSlime", 1, 500, 150, 0, 1.5F, 0, 0f, "\u00A7b", "");

        //Thaumcraft
        //TConstructRegistry.addToolMaterial(21, "Thaumium", 2, 250, 600, 2, 1.3F, 1, 0f, "", "");

        /*//Metallurgy
        TConstructRegistry.addToolMaterial(22, "Heptazion", 2, 2, 300, 800, 1, 1.0F, 0, 0f);
        TConstructRegistry.addToolMaterial(23, "Damascus Steel", 2, 3, 500, 600, 2, 1.35F, 1, 0f);
        TConstructRegistry.addToolMaterial(24, "Angmallen", 2, 2, 300, 800, 2, 0.8F, 0, 0f);

        TConstructRegistry.addToolMaterial(25, "Promethium", 1, 1, 200, 400, 1, 1.0F, 0, 0.5f);
        TConstructRegistry.addToolMaterial(26, "Deep Iron", 1, 2, 250, 600, 2, 1.3F, 1, 0f);
        TConstructRegistry.addToolMaterial(27, "Oureclase", 2, 3, 750, 800, 2, 1.2F, 0, 0f);
        TConstructRegistry.addToolMaterial(28, "Aredrite", 2, 3, 1000, 400, 2, 1.5F, 0, 1.0f);
        TConstructRegistry.addToolMaterial(29, "Astral Silver", 1, 4, 35, 1200, 1, 0.5F, 0, 0f);
        TConstructRegistry.addToolMaterial(30, "Carmot", 1, 4, 50, 1200, 1, 0.5F, 0, 0f);
        TConstructRegistry.addToolMaterial(31, "Mithril", 2, 4, 1000, 900, 3, 1.25F, 3, 0f);
        TConstructRegistry.addToolMaterial(32, "Orichalcum", 2, 5, 1350, 900, 3, 1.25F, 0, 0f);
        TConstructRegistry.addToolMaterial(33, "Adamantine", 3, 6, 1550, 1000, 4, 1.5F, 1, 0f);
        TConstructRegistry.addToolMaterial(34, "Atlarus", 3, 6, 1750, 1000, 4, 1.6F, 2, 0f);

        TConstructRegistry.addToolMaterial(35, "Black Steel", 2, 2, 500, 800, 2, 1.3F, 2, 0f);
        TConstructRegistry.addToolMaterial(36, "Quicksilver", 2, 4, 1100, 1400, 3, 1.0F, 1, 0f);
        TConstructRegistry.addToolMaterial(37, "Haderoth", 2, 4, 1250, 1200, 3, 1.0F, 2, 0f);
        TConstructRegistry.addToolMaterial(38, "Celenegil", 3, 5, 1600, 1400, 3, 1.0F, 2, 0f);
        TConstructRegistry.addToolMaterial(39, "Tartarite", 3, 7, 3000, 1400, 5, 1.6667F, 4, 0f);*/

        PatternBuilder pb = PatternBuilder.instance;
        if (PHConstruct.enableTWood)
            pb.registerFullMaterial(Block.planks, 2, "Wood", new ItemStack(Item.stick), new ItemStack(Item.stick), 0);
        else
            pb.registerMaterialSet("Wood", new ItemStack(Item.stick, 2), new ItemStack(Item.stick), 0);
        if (PHConstruct.enableTStone)
        {
            pb.registerFullMaterial(Block.stone, 2, "Stone", new ItemStack(TContent.toolShard, 1, 1), new ItemStack(TContent.toolRod, 1, 1), 1);
            pb.registerMaterial(Block.cobblestone, 2, "Stone");
        }
        //else
        //pb.registerMaterialSet("Stone", new ItemStack(TContent.toolShard, 1, 1), new ItemStack(TContent.toolRod, 1, 1), 1);
        pb.registerFullMaterial(Item.ingotIron, 2, "Iron", new ItemStack(TContent.toolShard, 1, 2), new ItemStack(TContent.toolRod, 1, 2), 2);
        pb.registerFullMaterial(Item.flint, 2, "Flint", new ItemStack(TContent.toolShard, 1, 3), new ItemStack(TContent.toolRod, 1, 3), 3);
        if (PHConstruct.enableTCactus)
            pb.registerFullMaterial(Block.cactus, 2, "Cactus", new ItemStack(TContent.toolShard, 1, 4), new ItemStack(TContent.toolRod, 1, 4), 4);
        else
            pb.registerMaterialSet("Cactus", new ItemStack(TContent.toolShard, 1, 4), new ItemStack(TContent.toolRod, 1, 4), 4);
        if (PHConstruct.enableTBone)
            pb.registerFullMaterial(Item.bone, 2, "Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
        else
            pb.registerMaterialSet("Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
        pb.registerFullMaterial(Block.obsidian, 2, "Obsidian", new ItemStack(TContent.toolShard, 1, 6), new ItemStack(TContent.toolRod, 1, 6), 6);
        pb.registerMaterial(new ItemStack(materials, 1, 18), 2, "Obsidian");
        pb.registerFullMaterial(Block.netherrack, 2, "Netherrack", new ItemStack(TContent.toolShard, 1, 7), new ItemStack(TContent.toolRod, 1, 7), 7);
        pb.registerFullMaterial(new ItemStack(materials, 1, 1), 2, "Slime", new ItemStack(toolShard, 1, 8), new ItemStack(toolRod, 1, 8), 8);
        pb.registerFullMaterial(new ItemStack(materials, 1, 0), 2, "Paper", new ItemStack(Item.paper, 2), new ItemStack(toolRod, 1, 9), 9);
        pb.registerMaterialSet("Cobalt", new ItemStack(toolShard, 1, 10), new ItemStack(toolRod, 1, 10), 10);
        pb.registerMaterialSet("Ardite", new ItemStack(toolShard, 1, 11), new ItemStack(toolRod, 1, 11), 11);
        pb.registerMaterialSet("Manyullyn", new ItemStack(toolShard, 1, 12), new ItemStack(toolRod, 1, 12), 12);
        pb.registerMaterialSet("Copper", new ItemStack(toolShard, 1, 13), new ItemStack(toolRod, 1, 13), 13);
        pb.registerMaterialSet("Bronze", new ItemStack(toolShard, 1, 14), new ItemStack(toolRod, 1, 14), 14);
        pb.registerMaterialSet("Alumite", new ItemStack(toolShard, 1, 15), new ItemStack(toolRod, 1, 15), 15);
        pb.registerMaterialSet("Steel", new ItemStack(toolShard, 1, 16), new ItemStack(toolRod, 1, 16), 16);
        pb.registerFullMaterial(new ItemStack(materials, 1, 17), 2, "BlueSlime", new ItemStack(toolShard, 1, 17), new ItemStack(toolRod, 1, 17), 17);

        pb.addToolPattern((IPattern) woodPattern);
    }

    public static Item[] patternOutputs;
    public static LiquidStack[] liquids;

    void addCraftingRecipes ()
    {
        /* Tools */
        patternOutputs = new Item[] { toolRod, pickaxeHead, shovelHead, hatchetHead, swordBlade, wideGuard, handGuard, crossbar, binding, frypanHead, signHead, knifeBlade, chiselHead, toughRod,
                toughBinding, heavyPlate, broadAxeHead, scytheBlade, excavatorHead, largeSwordBlade, hammerHead, fullGuard };

        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };

        if (PHConstruct.craftMetalTools)
        {
            for (int mat = 0; mat < 18; mat++)
            {
                for (int meta = 0; meta < patternOutputs.length; meta++)
                {
                    TConstructRegistry.addPartMapping(woodPattern.itemID, meta + 1, mat, new ItemStack(patternOutputs[meta], 1, mat));
                }
                TConstructRegistry.addPartMapping(woodPattern.itemID, 22, mat, new ItemStack(fullGuard, 1, mat));
            }
        }
        else
        {
            for (int mat = 0; mat < nonMetals.length; mat++)
            {
                for (int meta = 0; meta < patternOutputs.length; meta++)
                {
                    TConstructRegistry.addPartMapping(woodPattern.itemID, meta + 1, nonMetals[mat], new ItemStack(patternOutputs[meta], 1, nonMetals[mat]));
                }
                TConstructRegistry.addPartMapping(woodPattern.itemID, 22, nonMetals[mat], new ItemStack(fullGuard, 1, nonMetals[mat]));
            }
        }

        ToolBuilder tb = ToolBuilder.instance;
        tb.addNormalToolRecipe(pickaxe, pickaxeHead, toolRod, binding);
        tb.addNormalToolRecipe(broadsword, swordBlade, toolRod, wideGuard);
        tb.addNormalToolRecipe(hatchet, hatchetHead, toolRod);
        tb.addNormalToolRecipe(shovel, shovelHead, toolRod);
        tb.addNormalToolRecipe(longsword, swordBlade, toolRod, handGuard);
        tb.addNormalToolRecipe(rapier, swordBlade, toolRod, crossbar);
        tb.addNormalToolRecipe(frypan, frypanHead, toolRod);
        tb.addNormalToolRecipe(battlesign, signHead, toolRod);
        tb.addNormalToolRecipe(mattock, hatchetHead, toolRod, shovelHead);
        tb.addNormalToolRecipe(dagger, knifeBlade, toolRod, crossbar);
        tb.addNormalToolRecipe(cutlass, swordBlade, toolRod, fullGuard);
        tb.addNormalToolRecipe(chisel, chiselHead, toolRod);
        tb.addNormalToolRecipe(scythe, scytheBlade, toughRod, toughBinding, toughRod);
        tb.addNormalToolRecipe(lumberaxe, broadAxeHead, toughRod, heavyPlate, toughBinding);
        tb.addNormalToolRecipe(cleaver, largeSwordBlade, toughRod, heavyPlate, toughRod);
        tb.addNormalToolRecipe(excavator, excavatorHead, toughRod, heavyPlate, toughBinding);
        tb.addNormalToolRecipe(hammer, hammerHead, toughRod, heavyPlate, heavyPlate);
        tb.addNormalToolRecipe(battleaxe, broadAxeHead, toughRod, broadAxeHead, toughBinding);

        ItemStack diamond = new ItemStack(Item.diamond);
        tb.registerToolMod(new ModRepair());
        tb.registerToolMod(new ModDurability(new ItemStack[] { diamond }, 0, 500, 0f, 3, "Diamond", "\u00a7bDurability +500", "\u00a7b"));
        tb.registerToolMod(new ModDurability(new ItemStack[] { new ItemStack(Item.emerald) }, 1, 0, 0.5f, 2, "Emerald", "\u00a72Durability +50%", "\u00a72"));

        modE = new ModElectric();
        tb.registerToolMod(modE);

        ItemStack redstoneItem = new ItemStack(Item.redstone);
        ItemStack redstoneBlock = new ItemStack(Block.blockRedstone);
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem }, 2, 1));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem, redstoneItem }, 2, 2));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneBlock }, 2, 9));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem, redstoneBlock }, 2, 10));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneBlock, redstoneBlock }, 2, 18));

        ItemStack lapisItem = new ItemStack(Item.dyePowder, 1, 4);
        ItemStack lapisBlock = new ItemStack(Block.blockLapis);
        modL = new ModLapis(new ItemStack[] { lapisItem }, 10, 1);
        tb.registerToolMod(modL);
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisItem, lapisItem }, 10, 2));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisBlock }, 10, 9));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisItem, lapisBlock }, 10, 10));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisBlock, lapisBlock }, 10, 18));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(materials, 1, 6) }, 4, "Moss", 3, "\u00a72", "Auto-Repair"));
        ItemStack blazePowder = new ItemStack(Item.blazePowder);
        tb.registerToolMod(new ModBlaze(new ItemStack[] { blazePowder }, 7, 1));
        tb.registerToolMod(new ModBlaze(new ItemStack[] { blazePowder, blazePowder }, 7, 2));
        tb.registerToolMod(new ModAutoSmelt(new ItemStack[] { new ItemStack(materials, 1, 7) }, 6, "Lava", "\u00a74", "Auto-Smelt"));
        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(materials, 1, 8) }, 8, "Necrotic", 1, "\u00a78", "Life Steal"));

        ItemStack quartzItem = new ItemStack(Item.netherQuartz);
        ItemStack quartzBlock = new ItemStack(Block.blockNetherQuartz, 1, Short.MAX_VALUE);
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem }, 11, 1));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem, quartzItem }, 11, 2));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzBlock }, 11, 4));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem, quartzBlock }, 11, 5));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzBlock, quartzBlock }, 11, 8));

        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { diamond, new ItemStack(Block.blockGold) }, "Tier1Free"));
        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { new ItemStack(Item.netherStar) }, "Tier2Free"));

        ItemStack silkyJewel = new ItemStack(materials, 1, 26);
        tb.registerToolMod(new ModButtertouch(new ItemStack[] { silkyJewel }, 12));

        ItemStack piston = new ItemStack(Block.pistonBase);
        tb.registerToolMod(new ModPiston(new ItemStack[] { piston }, 3, 1));
        tb.registerToolMod(new ModPiston(new ItemStack[] { piston, piston }, 3, 2));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl) }, 13, "Beheading", 1, "\u00a7d", "Beheading"));

        ItemStack holySoil = new ItemStack(craftedSoil, 1, 4);
        tb.registerToolMod(new ModSmite("Smite", new ItemStack[] { holySoil }, 14, 1));
        tb.registerToolMod(new ModSmite("Smite", new ItemStack[] { holySoil, holySoil }, 14, 2));

        ItemStack spidereyeball = new ItemStack(Item.fermentedSpiderEye);
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", new ItemStack[] { spidereyeball }, 15, 1));
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", new ItemStack[] { spidereyeball, spidereyeball }, 15, 2));

        ItemStack obsidianPlate = new ItemStack(heavyPlate, 1, 6);
        tb.registerToolMod(new ModReinforced(new ItemStack[] { obsidianPlate }, 16, 1));

        TConstructRegistry.registerActiveToolMod(new TActiveOmniMod());

        /* Smeltery */
        ItemStack ingotcast = new ItemStack(metalPattern, 1, 0);

        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        //Blank
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 1), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), 80);
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 1), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1), 80);

        //Ingots
        tableCasting.addCastingRecipe(new ItemStack(Item.ingotIron), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0), ingotcast, 80); //Iron
        tableCasting.addCastingRecipe(new ItemStack(Item.ingotGold), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1), ingotcast, 80); //gold
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 9), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 2), ingotcast, 80); //copper
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 10), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 3), ingotcast, 80); //tin
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 11), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 4), ingotcast, 80); //aluminum
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 3), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 5), ingotcast, 80); //cobalt
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 4), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 6), ingotcast, 80); //ardite
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 13), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 7), ingotcast, 80); //bronze
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 14), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), ingotcast, 80); //albrass
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 5), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 9), ingotcast, 80); //manyullyn
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 15), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 10), ingotcast, 80); //alumite
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 18), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 11), ingotcast, 80); //obsidian
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 16), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 12), ingotcast, 80); //steel

        //Buckets
        ItemStack bucket = new ItemStack(Item.bucketEmpty);
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 0), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 0), bucket, true, 10); //Iron
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 1), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 1), bucket, true, 10); //gold
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 2), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 2), bucket, true, 10); //copper
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 3), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 3), bucket, true, 10); //tin
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 4), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 4), bucket, true, 10); //aluminum
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 5), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 5), bucket, true, 10); //cobalt
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 6), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 6), bucket, true, 10); //ardite
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 7), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 7), bucket, true, 10); //bronze
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 8), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 8), bucket, true, 10); //alubrass
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 9), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 9), bucket, true, 10); //manyullyn
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 10), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 10), bucket, true, 10); //alumite
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 11), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 11), bucket, true, 10);// obsidian
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 12), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 12), bucket, true, 10); //steel

        liquids = new LiquidStack[] { new LiquidStack(liquidMetalStill.blockID, 1, 0), new LiquidStack(liquidMetalStill.blockID, 1, 2), new LiquidStack(liquidMetalStill.blockID, 1, 5),
                new LiquidStack(liquidMetalStill.blockID, 1, 6), new LiquidStack(liquidMetalStill.blockID, 1, 9), new LiquidStack(liquidMetalStill.blockID, 1, 7),
                new LiquidStack(liquidMetalStill.blockID, 1, 10), new LiquidStack(liquidMetalStill.blockID, 1, 11), new LiquidStack(liquidMetalStill.blockID, 1, 12) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16 };

        for (int iter = 0; iter < patternOutputs.length; iter++)
        {
            ItemStack cast = new ItemStack(metalPattern, 1, iter + 1);
            
            tableCasting.addCastingRecipe(cast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(woodPattern, 1, iter+1), true, 50);
            tableCasting.addCastingRecipe(cast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*2, 1), new ItemStack(woodPattern, 1, iter+1), true, 50);
            tableCasting.addCastingRecipe(cast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
            tableCasting.addCastingRecipe(cast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*2, 1), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
            
            for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
            {
                ItemStack metalCast = new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]);
                tableCasting.addCastingRecipe(metalCast, new LiquidStack(liquids[iterTwo].itemID, ((IPattern) metalPattern).getPatternCost(metalCast) * TConstruct.ingotLiquidValue / 2,
                        liquids[iterTwo].itemMeta), cast, 50);
            }
        }
        
        ItemStack fullguardCast = new ItemStack(metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(woodPattern, 1, 22), true, 50);
        tableCasting.addCastingRecipe(fullguardCast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*2, 1), new ItemStack(woodPattern, 1, 22), true, 50);
        tableCasting.addCastingRecipe(fullguardCast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue*2, 1), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);

        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        basinCasting.addCastingRecipe(new ItemStack(Block.blockIron), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 0), null, true, 100); //Iron
        basinCasting.addCastingRecipe(new ItemStack(Block.blockGold), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 1), null, true, 100); //gold
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 3), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 2), null, true, 100); //copper
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 5), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 3), null, true, 100); //tin
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 6), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 4), null, true, 100); //aluminum
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 0), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 5), null, true, 100); //cobalt
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 1), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 6), null, true, 100); //ardite
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 4), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 7), null, true, 100); //bronze
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 7), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 8), null, true, 100); //albrass
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 2), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 9), null, true, 100); //manyullyn
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 8), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 10), null, true, 100); //alumite
        basinCasting.addCastingRecipe(new ItemStack(Block.obsidian), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 11), null, true, 100);// obsidian
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 9), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 12), null, true, 100); //steel

        basinCasting.addCastingRecipe(new ItemStack(speedBlock, 1, 0), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 3), new ItemStack(Block.gravel), true, 100); //brownstone

        //Ore
        Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        Smeltery.addMelting(Block.oreGold, 0, 400, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));
        Smeltery.addMelting(oreGravel, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        //Smeltery.addMelting(oreGravel, 1, 400, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));

        //Items
        Smeltery.addMelting(new ItemStack(Item.ingotIron, 4), Block.blockIron.blockID, 0, 500, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
        Smeltery.addMelting(new ItemStack(Item.ingotGold, 4), Block.blockGold.blockID, 0, 300, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1));
        Smeltery.addMelting(new ItemStack(Item.goldNugget, 4), Block.blockGold.blockID, 0, 150, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 1));

        Smeltery.addMelting(new ItemStack(Item.bucketEmpty), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 3, 0));
        Smeltery.addMelting(new ItemStack(Item.minecartEmpty), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 5, 0));
        Smeltery.addMelting(new ItemStack(materials, 1, 18), Block.obsidian.blockID, 0, 750, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 11));//obsidian

        //Blocks
        Smeltery.addMelting(Block.blockIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 0));
        Smeltery.addMelting(Block.blockGold, 0, 400, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 1));
        Smeltery.addMelting(Block.obsidian, 0, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 11));
        Smeltery.addMelting(Block.ice, 0, 75, new LiquidStack(Block.waterStill.blockID, 1000, 0));

        //Alloys
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 7), new LiquidStack(liquidMetalStill.blockID, 24, 2), new LiquidStack(liquidMetalStill.blockID, 8, 3)); //Bronze
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 8), new LiquidStack(liquidMetalStill.blockID, 24, 4), new LiquidStack(liquidMetalStill.blockID, 8, 2)); //Aluminum Brass
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 9), new LiquidStack(liquidMetalStill.blockID, 32, 5), new LiquidStack(liquidMetalStill.blockID, 32, 6)); //Manyullyn
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 48, 10), new LiquidStack(liquidMetalStill.blockID, 80, 4), new LiquidStack(liquidMetalStill.blockID, 32, 0), new LiquidStack(
                liquidMetalStill.blockID, 32, 11)); //Alumite

        //Oreberries
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 0), Block.blockIron.blockID, 0, 100, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 0)); //Iron
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 1), Block.blockGold.blockID, 0, 100, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 1)); //Gold
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 2), metalBlock.blockID, 3, 100, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 2)); //Copper
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 3), metalBlock.blockID, 5, 100, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 3)); //Tin
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 4), metalBlock.blockID, 6, 100, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 4)); //Aluminum

        //Vanilla Armor
        Smeltery.addMelting(new ItemStack(Item.helmetIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 5, 0));
        Smeltery.addMelting(new ItemStack(Item.plateIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 8, 0));
        Smeltery.addMelting(new ItemStack(Item.legsIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 7, 0));
        Smeltery.addMelting(new ItemStack(Item.bootsIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 4, 0));

        Smeltery.addMelting(new ItemStack(Item.helmetGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 5, 1));
        Smeltery.addMelting(new ItemStack(Item.plateGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 8, 1));
        Smeltery.addMelting(new ItemStack(Item.legsGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 7, 1));
        Smeltery.addMelting(new ItemStack(Item.bootsGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 4, 1));

        Smeltery.addMelting(new ItemStack(Item.helmetChain, 1, 0), this.metalBlock.blockID, 9, 700, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 12));
        Smeltery.addMelting(new ItemStack(Item.plateChain, 1, 0), this.metalBlock.blockID, 9, 700, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 12));
        Smeltery.addMelting(new ItemStack(Item.legsChain, 1, 0), this.metalBlock.blockID, 9, 700, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 12));
        Smeltery.addMelting(new ItemStack(Item.bootsChain, 1, 0), this.metalBlock.blockID, 9, 700, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 12));

        /* Detailing */
        Detailing chiseling = TConstructRegistry.getChiselDetailing();
        chiseling.addDetailing(Block.stone, 0, Block.stoneBrick, 0, chisel);
        chiseling.addDetailing(speedBlock, 0, speedBlock, 1, chisel);
        chiseling.addDetailing(speedBlock, 2, speedBlock, 3, chisel);
        chiseling.addDetailing(speedBlock, 3, speedBlock, 4, chisel);
        chiseling.addDetailing(speedBlock, 4, speedBlock, 5, chisel);
        chiseling.addDetailing(speedBlock, 5, speedBlock, 6, chisel);

        chiseling.addDetailing(Block.obsidian, 0, multiBrick, 0, chisel);
        chiseling.addDetailing(Block.sandStone, 0, Block.sandStone, 2, chisel);
        chiseling.addDetailing(Block.sandStone, 2, Block.sandStone, 1, chisel);
        chiseling.addDetailing(Block.sandStone, 1, multiBrick, 1, chisel);
        //chiseling.addDetailing(Block.netherrack, 0, multiBrick, 2, chisel);
        //chiseling.addDetailing(Block.stone_refined, 0, multiBrick, 3, chisel);
        chiseling.addDetailing(Item.ingotIron, 0, multiBrick, 4, chisel);
        chiseling.addDetailing(Item.ingotGold, 0, multiBrick, 5, chisel);
        chiseling.addDetailing(Item.dyePowder, 4, multiBrick, 6, chisel);
        chiseling.addDetailing(Item.diamond, 0, multiBrick, 7, chisel);
        chiseling.addDetailing(Item.redstone, 0, multiBrick, 8, chisel);
        chiseling.addDetailing(Item.bone, 0, multiBrick, 9, chisel);
        chiseling.addDetailing(Item.slimeBall, 0, multiBrick, 10, chisel);
        chiseling.addDetailing(strangeFood, 0, multiBrick, 11, chisel);
        chiseling.addDetailing(Block.whiteStone, 0, multiBrick, 12, chisel);
        chiseling.addDetailing(materials, 18, multiBrick, 13, chisel);

        chiseling.addDetailing(multiBrick, 0, multiBrickFancy, 0, chisel);
        chiseling.addDetailing(multiBrick, 1, multiBrickFancy, 1, chisel);
        chiseling.addDetailing(multiBrick, 2, multiBrickFancy, 2, chisel);
        chiseling.addDetailing(multiBrick, 3, multiBrickFancy, 3, chisel);
        chiseling.addDetailing(multiBrick, 4, multiBrickFancy, 4, chisel);
        chiseling.addDetailing(multiBrick, 5, multiBrickFancy, 5, chisel);
        chiseling.addDetailing(multiBrick, 6, multiBrickFancy, 6, chisel);
        chiseling.addDetailing(multiBrick, 7, multiBrickFancy, 7, chisel);
        chiseling.addDetailing(multiBrick, 8, multiBrickFancy, 8, chisel);
        chiseling.addDetailing(multiBrick, 9, multiBrickFancy, 9, chisel);
        chiseling.addDetailing(multiBrick, 10, multiBrickFancy, 10, chisel);
        chiseling.addDetailing(multiBrick, 11, multiBrickFancy, 11, chisel);
        chiseling.addDetailing(multiBrick, 12, multiBrickFancy, 12, chisel);
        chiseling.addDetailing(multiBrick, 13, multiBrickFancy, 13, chisel);

        chiseling.addDetailing(Block.stoneBrick, 0, multiBrickFancy, 15, chisel);
        chiseling.addDetailing(multiBrickFancy, 15, multiBrickFancy, 14, chisel);
        chiseling.addDetailing(multiBrickFancy, 14, Block.stoneBrick, 3, chisel);
        /*chiseling.addDetailing(multiBrick, 14, multiBrickFancy, 14, chisel);
        chiseling.addDetailing(multiBrick, 15, multiBrickFancy, 15, chisel);*/

        GameRegistry.addRecipe(new ItemStack(toolForge, 1, 0), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm', Block.blockIron);
        GameRegistry.addRecipe(new ItemStack(toolForge, 1, 1), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm', Block.blockGold);
        GameRegistry.addRecipe(new ItemStack(toolForge, 1, 2), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm', Block.blockDiamond);
        GameRegistry.addRecipe(new ItemStack(toolForge, 1, 3), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm', Block.blockEmerald);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 4), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockCobalt"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 5), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockArdite"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 6), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockManyullyn"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 7), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockCopper"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 8), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockBronze"));
        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 9), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm', "blockTin"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 10), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockNaturalAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 11), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockAluminumBrass"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 12), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockAlumite"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, 13), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                "blockSteel"));

        /* Crafting */
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.workbench);
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 2), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 3), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 4), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 5), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.chest);
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 0));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 11), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 1));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 12), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 2));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 13), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 3));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blankPattern, 1, 0), "ps", "sp", 'p', "plankWood", 's', "stickWood"));
        GameRegistry.addRecipe(new ItemStack(manualBook), "wp", 'w', new ItemStack(blankPattern, 1, 0), 'p', Item.paper);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 2, 0), new ItemStack(manualBook, 1, 0), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 1, 1), new ItemStack(manualBook, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 2, 1), new ItemStack(manualBook, 1, 1), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 1, 2), new ItemStack(manualBook, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 2, 2), new ItemStack(manualBook, 1, 2), Item.book);

        GameRegistry.addRecipe(new ItemStack(materials, 1, 0), "pp", "pp", 'p', Item.paper); //Paper stack
        OreDictionary.registerOre("mossystone", new ItemStack(Block.stoneBrick, 1, 1));
        OreDictionary.registerOre("mossystone", new ItemStack(Block.cobblestoneMossy));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(materials, 1, 6), "ppp", "ppp", "ppp", 'p', "mossystone")); //Moss ball
        GameRegistry.addRecipe(new ItemStack(materials, 1, 6), "ppp", "ppp", "ppp", 'p', new ItemStack(Block.stoneBrick, 1, 1)); //Moss ball
        GameRegistry.addRecipe(new ItemStack(materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'c', Item.fireballCharge, 'x', Item.blazeRod); //Auto-smelt
        GameRegistry.addRecipe(new ItemStack(materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'x', Item.fireballCharge, 'c', Item.blazeRod); //Auto-smelt

        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 0), Item.slimeBall, Item.slimeBall, Item.slimeBall, Item.slimeBall, Block.sand, Block.dirt); //Slimy sand
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 2), strangeFood, strangeFood, strangeFood, strangeFood, Block.sand, Block.dirt); //Slimy sand
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 2, 1), Item.clay, Block.sand, Block.gravel); //Grout
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 8, 1), Block.blockClay, Block.sand, Block.gravel, Block.sand, Block.gravel, Block.sand, Block.gravel, Block.sand, Block.gravel); //Grout
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 3), Block.dirt, Item.rottenFlesh, new ItemStack(Item.dyePowder, 1, 15)); //Graveyard Soil
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 3, new ItemStack(craftedSoil, 1, 4), 0.2f); //Concecrated Soil

        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 0, new ItemStack(materials, 1, 1), 2f); //Slime
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 1, new ItemStack(materials, 1, 2), 2f); //Seared brick item
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 2, new ItemStack(materials, 1, 17), 2f); //Blue Slime
        //GameRegistry.addRecipe(new ItemStack(oreSlag, 1, 0), "pp", "pp", 'p', new ItemStack(materials, 1, 2)); //Seared brick block

        GameRegistry.addRecipe(new ItemStack(materials, 1, 25), "sss", "sns", "sss", 'n', new ItemStack(materials, 1, 24), 's', new ItemStack(Item.silk)); //Silky Cloth
        GameRegistry.addRecipe(new ItemStack(materials, 1, 25), "sss", "sns", "sss", 'n', new ItemStack(Item.goldNugget), 's', new ItemStack(Item.silk));
        GameRegistry.addRecipe(new ItemStack(materials, 1, 26), " c ", "cec", " c ", 'c', new ItemStack(materials, 1, 25), 'e', new ItemStack(Item.emerald)); //Silky Jewel

        //FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 1, new ItemStack(materials, 1, 3), 3f);
        //FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 2, new ItemStack(materials, 1, 4), 3f);
        FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 3, new ItemStack(materials, 1, 9), 0.5f);
        FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 4, new ItemStack(materials, 1, 10), 0.5f);
        FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 5, new ItemStack(materials, 1, 12), 0.5f);

        FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 0, new ItemStack(materials, 1, 19), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 1, new ItemStack(Item.goldNugget), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 2, new ItemStack(materials, 1, 20), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 3, new ItemStack(materials, 1, 21), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 4, new ItemStack(materials, 1, 22), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 5, new ItemStack(materials, 1, 23), 0.2f);

        FurnaceRecipes.smelting().addSmelting(oreGravel.blockID, 0, new ItemStack(Item.ingotIron), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreGravel.blockID, 1, new ItemStack(Item.ingotGold), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreGravel.blockID, 2, new ItemStack(materials, 1, 9), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreGravel.blockID, 3, new ItemStack(materials, 1, 10), 0.2f);
        FurnaceRecipes.smelting().addSmelting(oreGravel.blockID, 4, new ItemStack(materials, 1, 12), 0.2f);

        FurnaceRecipes.smelting().addSmelting(speedBlock.blockID, 0, new ItemStack(speedBlock, 1, 2), 0.2f);

        //Metal conversion
        GameRegistry.addRecipe(new ItemStack(materials, 9, 9), "m", 'm', new ItemStack(metalBlock, 1, 3)); //Copper
        GameRegistry.addRecipe(new ItemStack(materials, 9, 10), "m", 'm', new ItemStack(metalBlock, 1, 5)); //Tin
        GameRegistry.addRecipe(new ItemStack(materials, 9, 12), "m", 'm', new ItemStack(metalBlock, 1, 6)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(materials, 9, 13), "m", 'm', new ItemStack(metalBlock, 1, 4)); //Bronze
        GameRegistry.addRecipe(new ItemStack(materials, 9, 14), "m", 'm', new ItemStack(metalBlock, 1, 7)); //AluBrass
        GameRegistry.addRecipe(new ItemStack(materials, 9, 3), "m", 'm', new ItemStack(metalBlock, 1, 0)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(materials, 9, 4), "m", 'm', new ItemStack(metalBlock, 1, 1)); //Ardite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 5), "m", 'm', new ItemStack(metalBlock, 1, 2)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(materials, 9, 15), "m", 'm', new ItemStack(metalBlock, 1, 8)); //Alumite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 16), "m", 'm', new ItemStack(metalBlock, 1, 9)); //Steel

        GameRegistry.addRecipe(new ItemStack(Item.ingotIron), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 19)); //Iron
        GameRegistry.addRecipe(new ItemStack(materials, 1, 9), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 20)); //Copper
        GameRegistry.addRecipe(new ItemStack(materials, 1, 10), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 21)); //Tin
        GameRegistry.addRecipe(new ItemStack(materials, 1, 12), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 22)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(materials, 1, 14), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 24)); //Aluminum Brass

        GameRegistry.addRecipe(new ItemStack(materials, 9, 24), "m", 'm', new ItemStack(materials, 1, 14)); //Aluminum Brass Nugget

        /*OreDictionary.registerOre("ingotAluminumBrass", new ItemStack(materials, 1, 14));
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(materials, 1, 24));*/

        //Smeltery
        ItemStack searedBrick = new ItemStack(materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks

        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 0), "bbb", "bgb", "bbb", 'b', searedBrick, 'g', Block.glass); //Tank
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', Block.glass); //Glass
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', Block.glass); //Window

        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin

        GameRegistry.addRecipe(new ItemStack(Block.pumpkinLantern, 1, 0), "p", "s", 'p', new ItemStack(Block.pumpkin), 'w', new ItemStack(toolRod, 1, 1));
        GameRegistry.addRecipe(new ItemStack(stoneTorch, 4), "p", "w", 'p', new ItemStack(Item.coal, 1, Short.MAX_VALUE), 'w', new ItemStack(toolRod, 1, 1));
        GameRegistry.addRecipe(new ItemStack(toolRod, 4, 1), "c", "c", 'c', new ItemStack(Block.stone));
        GameRegistry.addRecipe(new ItemStack(toolRod, 2, 1), "c", "c", 'c', new ItemStack(Block.cobblestone));

        ItemStack aluBrass = new ItemStack(materials, 1, 14);
        GameRegistry.addRecipe(new ItemStack(Item.pocketSundial), " i ", "iri", " i ", 'i', aluBrass, 'r', new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ItemStack(Block.pressurePlateGold), "ii", 'i', aluBrass);

        ItemStack necroticBone = new ItemStack(materials, 1, 8);

        //Accessories
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(heartCanister, 1, 0), "##", "##", '#', "ingotNaturalAluminum"));
        GameRegistry.addRecipe(new ItemStack(diamondApple), " d ", "d#d", " d ", 'd', new ItemStack(Item.diamond), '#', new ItemStack(Item.appleRed));
        GameRegistry.addShapelessRecipe(new ItemStack(heartCanister, 1, 2), new ItemStack(diamondApple), necroticBone, new ItemStack(heartCanister, 1, 0), new ItemStack(heartCanister, 1, 1));
        GameRegistry.addRecipe(new ItemStack(knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(toughRod, 1, 2), 'm', new ItemStack(Item.ingotGold));
        GameRegistry.addRecipe(new ItemStack(knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(toughRod, 1, 2), 'm', new ItemStack(materials, 1, 14));

        //Remove vanilla recipes
        if (!PHConstruct.vanillaMetalBlocks)
        {
            RecipeRemover.removeShapedRecipe(new ItemStack(Block.blockIron));
            RecipeRemover.removeShapedRecipe(new ItemStack(Block.blockGold));
        }
        
        //Ultra hardcore recipes
        String[] surround = { "###", "#m#", "###" };
        if (PHConstruct.goldAppleRecipe)
        {
            RecipeRemover.removeShapedRecipe(new ItemStack(Item.appleGold));
            RecipeRemover.removeShapedRecipe(new ItemStack(Item.goldenCarrot));
            RecipeRemover.removeShapelessRecipe(new ItemStack(Item.speckledMelon));
            
            GameRegistry.addRecipe(new ItemStack(Item.appleGold), surround, '#', new ItemStack(Item.ingotGold), 'm', new ItemStack(Item.appleRed));
            GameRegistry.addRecipe(new ItemStack(Item.goldenCarrot), surround, '#', new ItemStack(Item.ingotGold), 'm', new ItemStack(Item.carrot));
            GameRegistry.addRecipe(new ItemStack(goldHead), surround, '#', new ItemStack(Item.ingotGold), 'm', new ItemStack(Item.skull, 1, 3));
            GameRegistry.addShapelessRecipe(new ItemStack(Item.speckledMelon), new ItemStack(Block.blockGold), new ItemStack(Item.melon));
        }
        else
        {
            GameRegistry.addRecipe(new ItemStack(goldHead), surround, '#', new ItemStack(Item.goldNugget), 'm', new ItemStack(Item.skull, 1, 3));
        }
    }

    void setupToolTabs ()
    {
        TConstructRegistry.materialTab.init(new ItemStack(titleIcon, 1, 255));
        TConstructRegistry.blockTab.init(new ItemStack(toolStationWood));
        ItemStack tool = new ItemStack(longsword, 1, 0);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setCompoundTag("InfiTool", new NBTTagCompound());
        compound.getCompoundTag("InfiTool").setInteger("RenderHead", 2);
        compound.getCompoundTag("InfiTool").setInteger("RenderHandle", 0);
        compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", 10);
        tool.setTagCompound(compound);

        //TConstruct.
        TConstructRegistry.toolTab.init(tool);
    }

    public void addLoot ()
    {
        //Item, min, max, weight
        ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(heartCanister, 1, 1), 1, 1, 5));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(heartCanister, 1, 1), 1, 1, 10));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(heartCanister, 1, 1), 1, 1, 10));

        tinkerHouseChest = new ChestGenHooks("TinkerHouse", new WeightedRandomChestContent[0], 3, 27);
        tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(heartCanister, 1, 1), 1, 1, 1));
        int[] validTypes = { 0, 1, 2, 3, 4, 5, 6, 8, 9, 13, 14, 17 };
        Item[] partTypes = { pickaxeHead, shovelHead, hatchetHead, binding, swordBlade, wideGuard, handGuard, crossbar, knifeBlade, frypanHead, signHead, chiselHead };

        for (int partIter = 0; partIter < partTypes.length; partIter++)
        {
            for (int typeIter = 0; typeIter < validTypes.length; typeIter++)
            {
                tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(partTypes[partIter], 1, validTypes[typeIter]), 1, 1, 15));
            }
        }

        tinkerHousePatterns = new ChestGenHooks("TinkerPatterns", new WeightedRandomChestContent[0], 5, 30);
        for (int i = 0; i < 13; i++)
        {
            tinkerHousePatterns.addItem(new WeightedRandomChestContent(new ItemStack(woodPattern, 1, i + 1), 1, 3, 20));
        }
        tinkerHousePatterns.addItem(new WeightedRandomChestContent(new ItemStack(woodPattern, 1, 22), 1, 3, 40));
    }

    public static LiquidStack[] liquidIcons = new LiquidStack[0];
    public static String[] liquidNames;

    public void oreRegistry ()
    {
        OreDictionary.registerOre("oreCobalt", new ItemStack(oreSlag, 1, 1));
        OreDictionary.registerOre("oreArdite", new ItemStack(oreSlag, 1, 2));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreSlag, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(oreSlag, 1, 4));
        OreDictionary.registerOre("oreNaturalAluminum", new ItemStack(oreSlag, 1, 5));

        OreDictionary.registerOre("oreIron", new ItemStack(oreGravel, 1, 0));
        OreDictionary.registerOre("oreGold", new ItemStack(oreGravel, 1, 1));
        OreDictionary.registerOre("oreCobalt", new ItemStack(oreGravel, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreGravel, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(oreGravel, 1, 3));
        OreDictionary.registerOre("oreNaturalAluminum", new ItemStack(oreGravel, 1, 4));

        OreDictionary.registerOre("ingotCobalt", new ItemStack(materials, 1, 3));
        OreDictionary.registerOre("ingotArdite", new ItemStack(materials, 1, 4));
        OreDictionary.registerOre("ingotManyullyn", new ItemStack(materials, 1, 5));
        OreDictionary.registerOre("ingotCopper", new ItemStack(materials, 1, 9));
        OreDictionary.registerOre("ingotTin", new ItemStack(materials, 1, 10));
        OreDictionary.registerOre("ingotNaturalAluminum", new ItemStack(materials, 1, 11));
        OreDictionary.registerOre("naturalAluminum", new ItemStack(materials, 1, 12));
        OreDictionary.registerOre("ingotBronze", new ItemStack(materials, 1, 13));
        OreDictionary.registerOre("ingotAluminumBrass", new ItemStack(materials, 1, 14));
        OreDictionary.registerOre("ingotAlumite", new ItemStack(materials, 1, 15));
        OreDictionary.registerOre("ingotSteel", new ItemStack(materials, 1, 16));

        OreDictionary.registerOre("blockCobalt", new ItemStack(metalBlock, 1, 0));
        OreDictionary.registerOre("blockArdite", new ItemStack(metalBlock, 1, 1));
        OreDictionary.registerOre("blockManyullyn", new ItemStack(metalBlock, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(metalBlock, 1, 3));
        OreDictionary.registerOre("blockBronze", new ItemStack(metalBlock, 1, 4));
        OreDictionary.registerOre("blockTin", new ItemStack(metalBlock, 1, 5));
        OreDictionary.registerOre("blockNaturalAluminum", new ItemStack(metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminumBrass", new ItemStack(metalBlock, 1, 7));
        OreDictionary.registerOre("blockAlumite", new ItemStack(metalBlock, 1, 8));
        OreDictionary.registerOre("blockSteel", new ItemStack(metalBlock, 1, 9));

        OreDictionary.registerOre("nuggetIron", new ItemStack(materials, 1, 19));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(materials, 1, 20));
        OreDictionary.registerOre("nuggetTin", new ItemStack(materials, 1, 21));
        OreDictionary.registerOre("nuggetNaturalAluminum", new ItemStack(materials, 1, 22));
        OreDictionary.registerOre("nuggetSilver", new ItemStack(materials, 1, 23));
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(materials, 1, 24));

        String[] names = new String[] { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Cobalt", "Ardite", "Bronze", "Aluminum Brass", "Manyullyn", "Alumite", "Obsidian", "Steel" };
        liquidIcons = new LiquidStack[names.length];
        liquidNames = new String[names.length];
        for (int iter = 0; iter < names.length; iter++)
        {
            LiquidStack liquidstack = new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, iter);
            String name = "Molten " + names[iter];
            liquidIcons[iter] = liquidstack;
            liquidNames[iter] = name;
            LiquidDictionary.getOrCreateLiquid(name, liquidstack);
            LiquidContainerRegistry.registerLiquid(new LiquidContainerData(liquidstack, new ItemStack(buckets, 1, iter), new ItemStack(Item.bucketEmpty)));
        }

        //Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Item.slimeBall));
        OreDictionary.registerOre("slimeball", new ItemStack(strangeFood, 1, 0));
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.pistonStickyBase));
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.magmaCream));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.pistonStickyBase), "slimeball", Block.pistonBase));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.magmaCream), "slimeball", Item.blazePowder));
    }

    public void intermodCommunication ()
    {
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 15));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerrySecond, 1, 13));
        
        /* FORESTRY
         * Edit these strings to change what items are added to the backpacks
         * Format info: "[backpack ID]@[item ID].[metadata or *]:[next item]" and so on
         * Avaliable backpack IDs: forester, miner, digger, hunter, adventurer, builder
         * May add more backpack items later - Spyboticsguy 
         */
        
        String builderItems = "builder@" + String.valueOf(stoneTorch.blockID) + ".*";
        
        FMLInterModComms.sendMessage("Forestry", "add-backpack-items", builderItems);
    }

    public void modIntegration ()
    {
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TContent.pickaxeHead, 1, 6), new ItemStack(TContent.toolRod, 1, 2), new ItemStack(TContent.binding, 1, 6), "");
        /* IC2 */
        ItemStack reBattery = ic2.api.item.Items.getItem("reBattery");
        if (reBattery != null)
            modE.batteries.add(reBattery);
        ItemStack chargedReBattery = ic2.api.item.Items.getItem("chargedReBattery");
        if (chargedReBattery != null)
        {
            modE.batteries.add(chargedReBattery);
        }
        ItemStack electronicCircuit = ic2.api.item.Items.getItem("electronicCircuit");
        if (electronicCircuit != null)
            modE.circuits.add(electronicCircuit);

        if (chargedReBattery != null && electronicCircuit != null)
            TConstructClientRegistry.registerManualModifier("electricmod", ironpick.copy(), chargedReBattery, electronicCircuit);

        /* Thaumcraft */
        //Object obj = getItem("itemResource", "thaumcraft.common.Config");
    }

    public static Object getItem (String name, String classPackage)
    {
        try
        {
            Class c = Class.forName(classPackage);
            Object ret = c.getField(name);
            if (ret != null && (ret instanceof ItemStack || ret instanceof Item))
                return ret;
            return null;
        }
        catch (Exception e)
        {
            System.out.println("[TConstruct] Could not find item for " + name);
            return null;
        }
    }

    @Override
    public int getBurnTime (ItemStack fuel)
    {
        if (fuel.itemID == materials.itemID && fuel.getItemDamage() == 7)
            return 26400;
        return 0;
    }
}