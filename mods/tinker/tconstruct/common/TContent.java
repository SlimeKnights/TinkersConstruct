package mods.tinker.tconstruct.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.Aggregator;
import mods.tinker.tconstruct.blocks.CraftingStationBlock;
import mods.tinker.tconstruct.blocks.DryingRack;
import mods.tinker.tconstruct.blocks.EquipBlock;
import mods.tinker.tconstruct.blocks.GlassBlockConnected;
import mods.tinker.tconstruct.blocks.GlassBlockConnectedMeta;
import mods.tinker.tconstruct.blocks.GlassPane;
import mods.tinker.tconstruct.blocks.GlassPaneStained;
import mods.tinker.tconstruct.blocks.GravelOre;
import mods.tinker.tconstruct.blocks.LavaTankBlock;
import mods.tinker.tconstruct.blocks.LightCrystalBase;
import mods.tinker.tconstruct.blocks.LiquidMetalFlowing;
import mods.tinker.tconstruct.blocks.LiquidMetalStill;
import mods.tinker.tconstruct.blocks.MetalOre;
import mods.tinker.tconstruct.blocks.MultiBrick;
import mods.tinker.tconstruct.blocks.MultiBrickFancy;
import mods.tinker.tconstruct.blocks.OreberryBush;
import mods.tinker.tconstruct.blocks.OreberryBushEssence;
import mods.tinker.tconstruct.blocks.RedstoneMachine;
import mods.tinker.tconstruct.blocks.SearedBlock;
import mods.tinker.tconstruct.blocks.SearedSlab;
import mods.tinker.tconstruct.blocks.SmelteryBlock;
import mods.tinker.tconstruct.blocks.SoilBlock;
import mods.tinker.tconstruct.blocks.SpeedBlock;
import mods.tinker.tconstruct.blocks.SpeedSlab;
import mods.tinker.tconstruct.blocks.StoneTorch;
import mods.tinker.tconstruct.blocks.TMetalBlock;
import mods.tinker.tconstruct.blocks.ToolForgeBlock;
import mods.tinker.tconstruct.blocks.ToolStationBlock;
import mods.tinker.tconstruct.blocks.logic.CastingBasinLogic;
import mods.tinker.tconstruct.blocks.logic.CastingTableLogic;
import mods.tinker.tconstruct.blocks.logic.CraftingStationLogic;
import mods.tinker.tconstruct.blocks.logic.DrawbridgeLogic;
import mods.tinker.tconstruct.blocks.logic.DryingRackLogic;
import mods.tinker.tconstruct.blocks.logic.FaucetLogic;
import mods.tinker.tconstruct.blocks.logic.FirestarterLogic;
import mods.tinker.tconstruct.blocks.logic.FrypanLogic;
import mods.tinker.tconstruct.blocks.logic.GlowstoneAggregator;
import mods.tinker.tconstruct.blocks.logic.LavaTankLogic;
import mods.tinker.tconstruct.blocks.logic.LiquidTextureLogic;
import mods.tinker.tconstruct.blocks.logic.MultiServantLogic;
import mods.tinker.tconstruct.blocks.logic.PartCrafterLogic;
import mods.tinker.tconstruct.blocks.logic.PatternChestLogic;
import mods.tinker.tconstruct.blocks.logic.SmelteryDrainLogic;
import mods.tinker.tconstruct.blocks.logic.SmelteryLogic;
import mods.tinker.tconstruct.blocks.logic.StencilTableLogic;
import mods.tinker.tconstruct.blocks.logic.ToolForgeLogic;
import mods.tinker.tconstruct.blocks.logic.ToolStationLogic;
import mods.tinker.tconstruct.blocks.traps.BarricadeBlock;
import mods.tinker.tconstruct.blocks.traps.Punji;
import mods.tinker.tconstruct.entity.Automaton;
import mods.tinker.tconstruct.entity.BlueSlime;
import mods.tinker.tconstruct.entity.Crystal;
import mods.tinker.tconstruct.entity.FancyEntityItem;
import mods.tinker.tconstruct.entity.Gardeslime;
import mods.tinker.tconstruct.entity.SlimeClone;
import mods.tinker.tconstruct.entity.projectile.ArrowEntity;
import mods.tinker.tconstruct.entity.projectile.DaggerEntity;
import mods.tinker.tconstruct.entity.projectile.LaunchedPotion;
import mods.tinker.tconstruct.items.Bowstring;
import mods.tinker.tconstruct.items.CraftingItem;
import mods.tinker.tconstruct.items.DiamondApple;
import mods.tinker.tconstruct.items.FilledBucket;
import mods.tinker.tconstruct.items.Fletching;
import mods.tinker.tconstruct.items.GoldenHead;
import mods.tinker.tconstruct.items.Manual;
import mods.tinker.tconstruct.items.MaterialItem;
import mods.tinker.tconstruct.items.MetalPattern;
import mods.tinker.tconstruct.items.OreBerries;
import mods.tinker.tconstruct.items.Pattern;
import mods.tinker.tconstruct.items.StrangeFood;
import mods.tinker.tconstruct.items.TitleIcon;
import mods.tinker.tconstruct.items.ToolPart;
import mods.tinker.tconstruct.items.ToolPartHidden;
import mods.tinker.tconstruct.items.ToolShard;
import mods.tinker.tconstruct.items.armor.Glove;
import mods.tinker.tconstruct.items.armor.HeartCanister;
import mods.tinker.tconstruct.items.armor.Knapsack;
import mods.tinker.tconstruct.items.armor.TArmorBase;
import mods.tinker.tconstruct.items.blocks.BarricadeItem;
import mods.tinker.tconstruct.items.blocks.CraftedSoilItemBlock;
import mods.tinker.tconstruct.items.blocks.GlassBlockItem;
import mods.tinker.tconstruct.items.blocks.GlassPaneItem;
import mods.tinker.tconstruct.items.blocks.GravelOreItem;
import mods.tinker.tconstruct.items.blocks.LavaTankItemBlock;
import mods.tinker.tconstruct.items.blocks.LightCrystalItem;
import mods.tinker.tconstruct.items.blocks.LiquidItemBlock;
import mods.tinker.tconstruct.items.blocks.MetalItemBlock;
import mods.tinker.tconstruct.items.blocks.MetalOreItemBlock;
import mods.tinker.tconstruct.items.blocks.MultiBrickFancyItem;
import mods.tinker.tconstruct.items.blocks.MultiBrickItem;
import mods.tinker.tconstruct.items.blocks.OreberryBushItem;
import mods.tinker.tconstruct.items.blocks.OreberryBushSecondItem;
import mods.tinker.tconstruct.items.blocks.RedstoneMachineItem;
import mods.tinker.tconstruct.items.blocks.SearedSlabItem;
import mods.tinker.tconstruct.items.blocks.SearedTableItemBlock;
import mods.tinker.tconstruct.items.blocks.SmelteryItemBlock;
import mods.tinker.tconstruct.items.blocks.SpeedBlockItem;
import mods.tinker.tconstruct.items.blocks.SpeedSlabItem;
import mods.tinker.tconstruct.items.blocks.StainedGlassClearItem;
import mods.tinker.tconstruct.items.blocks.StainedGlassClearPaneItem;
import mods.tinker.tconstruct.items.blocks.ToolForgeItemBlock;
import mods.tinker.tconstruct.items.blocks.ToolStationItemBlock;
import mods.tinker.tconstruct.items.tools.Arrow;
import mods.tinker.tconstruct.items.tools.BattleSign;
import mods.tinker.tconstruct.items.tools.Battleaxe;
import mods.tinker.tconstruct.items.tools.Broadsword;
import mods.tinker.tconstruct.items.tools.Chisel;
import mods.tinker.tconstruct.items.tools.Cleaver;
import mods.tinker.tconstruct.items.tools.Cutlass;
import mods.tinker.tconstruct.items.tools.Dagger;
import mods.tinker.tconstruct.items.tools.Excavator;
import mods.tinker.tconstruct.items.tools.FryingPan;
import mods.tinker.tconstruct.items.tools.Hammer;
import mods.tinker.tconstruct.items.tools.Hatchet;
import mods.tinker.tconstruct.items.tools.Longsword;
import mods.tinker.tconstruct.items.tools.LumberAxe;
import mods.tinker.tconstruct.items.tools.Mattock;
import mods.tinker.tconstruct.items.tools.Pickaxe;
import mods.tinker.tconstruct.items.tools.PotionLauncher;
import mods.tinker.tconstruct.items.tools.Rapier;
import mods.tinker.tconstruct.items.tools.Scythe;
import mods.tinker.tconstruct.items.tools.Shortbow;
import mods.tinker.tconstruct.items.tools.Shovel;
import mods.tinker.tconstruct.landmine.block.BlockLandmine;
import mods.tinker.tconstruct.landmine.item.ItemBlockLandmine;
import mods.tinker.tconstruct.landmine.tileentity.TileEntityLandmine;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import mods.tinker.tconstruct.library.crafting.Detailing;
import mods.tinker.tconstruct.library.crafting.DryingRackRecipes;
import mods.tinker.tconstruct.library.crafting.LiquidCasting;
import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import mods.tinker.tconstruct.library.crafting.Smeltery;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.util.IPattern;
import mods.tinker.tconstruct.modifiers.ModAntiSpider;
import mods.tinker.tconstruct.modifiers.ModAttack;
import mods.tinker.tconstruct.modifiers.ModAutoSmelt;
import mods.tinker.tconstruct.modifiers.ModBlaze;
import mods.tinker.tconstruct.modifiers.ModButtertouch;
import mods.tinker.tconstruct.modifiers.ModDurability;
import mods.tinker.tconstruct.modifiers.ModElectric;
import mods.tinker.tconstruct.modifiers.ModExtraModifier;
import mods.tinker.tconstruct.modifiers.ModInteger;
import mods.tinker.tconstruct.modifiers.ModLapis;
import mods.tinker.tconstruct.modifiers.ModPiston;
import mods.tinker.tconstruct.modifiers.ModRedstone;
import mods.tinker.tconstruct.modifiers.ModReinforced;
import mods.tinker.tconstruct.modifiers.ModRepair;
import mods.tinker.tconstruct.modifiers.ModSmite;
import mods.tinker.tconstruct.modifiers.TActiveOmniMod;
import mods.tinker.tconstruct.util.PHConstruct;
import mods.tinker.tconstruct.util.RecipeRemover;
import mods.tinker.tconstruct.util.TDispenserBehaviorSpawnEgg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.RecipesTools;
import net.minecraft.item.crafting.RecipesWeapons;
import net.minecraft.item.crafting.ShapedRecipes;
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

    public static ToolCore scythe;
    public static ToolCore lumberaxe;
    public static ToolCore cleaver;
    public static ToolCore excavator;
    public static ToolCore hammer;
    public static ToolCore battleaxe;

    public static ToolCore shortbow;
    public static ToolCore arrow;

    public static Item potionLauncher;

    //Tool parts
    public static Item binding;
    public static Item toughBinding;
    public static Item toughRod;
    public static Item largePlate;

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

    public static Item bowstring;
    public static Item arrowhead;
    public static Item fletching;

    //Crafting blocks
    public static Block toolStationWood;
    public static Block toolStationStone;
    public static Block toolForge;
    public static Block craftingStationWood;
    
    public static Block heldItemBlock;
    public static Block craftedSoil;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block metalBlock;

    public static Block redstoneMachine;
    public static Block dryingRack;

    //Decoration
    public static Block stoneTorch;
    public static Block multiBrick;
    public static Block multiBrickFancy;

    public static Block searedSlab;
    public static Block speedSlab;

    //Traps
    public static Block landmine;
    public static Block punji;
    public static Block barricadeOak;
    public static Block barricadeSpruce;
    public static Block barricadeBirch;
    public static Block barricadeJungle;

    //InfiBlocks
    public static Block speedBlock;
    public static Block glass;
    //public static Block stainedGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    //public static Block stainedGlassPane;
    public static Block stainedGlassClearPane;
    public static Block glassMagicSlab;
    public static Block stainedGlassMagicSlab;
    public static Block stainedGlassClearMagicSlab;

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
        EntityRegistry.registerModEntity(Crystal.class, "Crystal", 2, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(LaunchedPotion.class, "Launched Potion", 3, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 4, TConstruct.instance, 32, 5, true);
        //EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1, TConstruct.instance, 32, 5, true);

        //EntityRegistry.registerModEntity(Skyla.class, "Skyla", 10, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(SlimeClone.class, "SlimeClone", 10, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(Automaton.class, "Automaton", 11, TConstruct.instance, 64, 3, true);
        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        EntityRegistry.registerModEntity(Gardeslime.class, "MiniGardy", 13, TConstruct.instance, 64, 3, true);
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
        toolStationWood = new ToolStationBlock(PHConstruct.woodStation, Material.wood).setUnlocalizedName("ToolStation");
        GameRegistry.registerBlock(toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartCrafterLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");

        toolForge = new ToolForgeBlock(PHConstruct.toolForge, Material.iron).setUnlocalizedName("ToolForge");
        GameRegistry.registerBlock(toolForge, ToolForgeItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");
        
        craftingStationWood = new CraftingStationBlock(PHConstruct.woodCrafter, Material.wood).setUnlocalizedName("CraftingStation");
        GameRegistry.registerBlock(craftingStationWood, "CraftingStation");
        GameRegistry.registerTileEntity(CraftingStationLogic.class, "CraftingStation");

        heldItemBlock = new EquipBlock(PHConstruct.heldItemBlock, Material.wood).setUnlocalizedName("Frypan");
        GameRegistry.registerBlock(heldItemBlock, "HeldItemBlock");
        GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");

        craftedSoil = new SoilBlock(PHConstruct.craftedSoil).setUnlocalizedName("TConstruct.Soil");
        craftedSoil.stepSound = Block.soundGravelFootstep;
        GameRegistry.registerBlock(craftedSoil, CraftedSoilItemBlock.class, "CraftedSoil");

        searedSlab = new SearedSlab(PHConstruct.searedSlab).setUnlocalizedName("SearedSlab");
        searedSlab.stepSound = Block.soundStoneFootstep;
        GameRegistry.registerBlock(searedSlab, SearedSlabItem.class, "SearedSlab");

        speedSlab = new SpeedSlab(PHConstruct.speedSlab).setUnlocalizedName("SpeedSlab");
        speedSlab.stepSound = Block.soundStoneFootstep;
        GameRegistry.registerBlock(speedSlab, SpeedSlabItem.class, "SpeedSlab");

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

        //Redstone machines
        redstoneMachine = new RedstoneMachine(PHConstruct.redstoneMachine).setUnlocalizedName("Redstone.Machine");
        GameRegistry.registerBlock(redstoneMachine, RedstoneMachineItem.class, "Redstone.Machine");
        GameRegistry.registerTileEntity(DrawbridgeLogic.class, "Drawbridge");
        GameRegistry.registerTileEntity(FirestarterLogic.class, "Firestarter");
        
        //Traps
        landmine = new BlockLandmine(PHConstruct.landmine).setHardness(0.5F).setResistance(0F).setStepSound(Block.soundMetalFootstep).setCreativeTab(TConstructRegistry.blockTab).setUnlocalizedName("landmine");
        GameRegistry.registerBlock(landmine, ItemBlockLandmine.class, "Redstone.Landmine");
        GameRegistry.registerTileEntity(TileEntityLandmine.class, "Landmine");
        
        punji = new Punji(PHConstruct.punji).setUnlocalizedName("trap.punji");
        GameRegistry.registerBlock(punji, "trap.punji");
        
        barricadeOak = new BarricadeBlock(PHConstruct.barricadeOak, Block.wood, 0).setUnlocalizedName("trap.barricade.oak");
        GameRegistry.registerBlock(barricadeOak, BarricadeItem.class, "trap.barricade.oak");
        
        barricadeSpruce = new BarricadeBlock(PHConstruct.barricadeSpruce, Block.wood, 1).setUnlocalizedName("trap.barricade.spruce");
        GameRegistry.registerBlock(barricadeSpruce, BarricadeItem.class, "trap.barricade.spruce");
        
        barricadeBirch = new BarricadeBlock(PHConstruct.barricadeBirch, Block.wood, 2).setUnlocalizedName("trap.barricade.birch");
        GameRegistry.registerBlock(barricadeBirch, BarricadeItem.class, "trap.barricade.birch");
        
        barricadeJungle = new BarricadeBlock(PHConstruct.barricadeJungle, Block.wood, 3).setUnlocalizedName("trap.barricade.jungle");
        GameRegistry.registerBlock(barricadeJungle, BarricadeItem.class, "trap.barricade.jungle");

        dryingRack = new DryingRack(PHConstruct.dryingRack).setUnlocalizedName("Armor.DryingRack");
        GameRegistry.registerBlock(dryingRack, "Armor.DryingRack");
        GameRegistry.registerTileEntity(DryingRackLogic.class, "Armor.DryingRack");

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
        String[] berryOresTwo = new String[] { "berry_aluminum", "berry_essence", "", "", "berry_aluminum_ripe", "berry_essence_ripe", "", "" };
        oreBerrySecond = (OreberryBush) new OreberryBushEssence(PHConstruct.oreBerrySecond, berryOresTwo, 4, 2, new String[] { "oreAluminum", "oreSilver" }).setUnlocalizedName("ore.berries.two");
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

        //Glass
        glass = new GlassBlockConnected(PHConstruct.glass, "clear", false).setUnlocalizedName("GlassBlock");
        glass.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(glass, GlassBlockItem.class, "GlassBlock");

        glassPane = new GlassPane(PHConstruct.glassPane);
        GameRegistry.registerBlock(glassPane, GlassPaneItem.class, "GlassPane");

        stainedGlassClear = new GlassBlockConnectedMeta(PHConstruct.stainedGlassClear, "stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple",
                "blue", "brown", "green", "red", "black").setUnlocalizedName("GlassBlock.StainedClear");
        stainedGlassClear.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");

        stainedGlassClearPane = new GlassPaneStained(PHConstruct.stainedGlassClearPane);
        GameRegistry.registerBlock(stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        /*public static Block stainedGlass;
        public static Block stainedGlassClear;*/

        aggregator = new Aggregator(PHConstruct.aggregator).setUnlocalizedName("Aggregator");
        aggregator.stepSound = Block.soundMetalFootstep;
        GameRegistry.registerBlock(aggregator, "Aggregator");
        GameRegistry.registerTileEntity(GlowstoneAggregator.class, "GlowstoneAggregator");

        lightCrystalBase = new LightCrystalBase(PHConstruct.lightCrystalBase).setUnlocalizedName("LightCrystalBase");
        lightCrystalBase.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(lightCrystalBase, LightCrystalItem.class, "LightCrystalBase");
    }

    void registerItems ()
    {
        titleIcon = new TitleIcon(PHConstruct.uselessItem).setUnlocalizedName("tconstruct.titleicon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast", "blank_cast" };
        blankPattern = new CraftingItem(PHConstruct.blankPattern, blanks, blanks, "materials/").setUnlocalizedName("tconstruct.Pattern");

        materials = new MaterialItem(PHConstruct.materials).setUnlocalizedName("tconstruct.Materials");
        toolRod = new ToolPart(PHConstruct.toolRod, "_rod").setUnlocalizedName("tconstruct.ToolRod");
        toolShard = new ToolShard(PHConstruct.toolShard, "_chunk").setUnlocalizedName("tconstruct.ToolShard");
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

        shortbow = new Shortbow(PHConstruct.shortbow);
        arrow = new Arrow(PHConstruct.arrow);

        Item[] tools = { pickaxe, shovel, hatchet, broadsword, longsword, rapier, cutlass, frypan, battlesign, mattock, chisel, lumberaxe, cleaver, scythe, excavator, hammer, battleaxe };
        String[] toolStrings = { "pickaxe", "shovel", "hatchet", "broadsword", "longsword", "rapier", "cutlass", "frypan", "battlesign", "mattock", "chisel", "lumberaxe", "cleaver", "scythe",
                "excavator", "hammer", "battleaxe" };

        for (int i = 0; i < tools.length; i++)
        {
            TConstructRegistry.addItemToDirectory(toolStrings[i], tools[i]);
        }

        potionLauncher = new PotionLauncher(PHConstruct.potionLauncher).setUnlocalizedName("tconstruct.PotionLauncher");

        pickaxeHead = new ToolPart(PHConstruct.pickaxeHead, "_pickaxe_head").setUnlocalizedName("tconstruct.PickaxeHead");
        shovelHead = new ToolPart(PHConstruct.shovelHead, "_shovel_head").setUnlocalizedName("tconstruct.ShovelHead");
        hatchetHead = new ToolPart(PHConstruct.axeHead, "_axe_head").setUnlocalizedName("tconstruct.AxeHead");
        binding = new ToolPart(PHConstruct.binding, "_binding").setUnlocalizedName("tconstruct.Binding");
        toughBinding = new ToolPart(PHConstruct.toughBinding, "_toughbind").setUnlocalizedName("tconstruct.ThickBinding");
        toughRod = new ToolPart(PHConstruct.toughRod, "_toughrod").setUnlocalizedName("tconstruct.ThickRod");
        largePlate = new ToolPart(PHConstruct.largePlate, "_largeplate").setUnlocalizedName("tconstruct.LargePlate");

        swordBlade = new ToolPart(PHConstruct.swordBlade, "_sword_blade").setUnlocalizedName("tconstruct.SwordBlade");
        wideGuard = new ToolPart(PHConstruct.largeGuard, "_large_guard").setUnlocalizedName("tconstruct.LargeGuard");
        handGuard = new ToolPart(PHConstruct.medGuard, "_medium_guard").setUnlocalizedName("tconstruct.MediumGuard");
        crossbar = new ToolPart(PHConstruct.crossbar, "_crossbar").setUnlocalizedName("tconstruct.Crossbar");
        knifeBlade = new ToolPart(PHConstruct.knifeBlade, "_knife_blade").setUnlocalizedName("tconstruct.KnifeBlade");
        fullGuard = new ToolPartHidden(PHConstruct.fullGuard, "_full_guard").setUnlocalizedName("tconstruct.FullGuard");

        frypanHead = new ToolPart(PHConstruct.frypanHead, "_frypan_head").setUnlocalizedName("tconstruct.FrypanHead");
        signHead = new ToolPart(PHConstruct.signHead, "_battlesign_head").setUnlocalizedName("tconstruct.SignHead");
        chiselHead = new ToolPart(PHConstruct.chiselHead, "_chisel_head").setUnlocalizedName("tconstruct.ChiselHead");

        scytheBlade = new ToolPart(PHConstruct.scytheBlade, "_scythe_head").setUnlocalizedName("tconstruct.ScytheBlade");
        broadAxeHead = new ToolPart(PHConstruct.lumberHead, "_lumberaxe_head").setUnlocalizedName("tconstruct.LumberHead");
        excavatorHead = new ToolPart(PHConstruct.excavatorHead, "_excavator_head").setUnlocalizedName("tconstruct.ExcavatorHead");
        largeSwordBlade = new ToolPart(PHConstruct.largeSwordBlade, "_large_sword_blade").setUnlocalizedName("tconstruct.LargeSwordBlade");
        hammerHead = new ToolPart(PHConstruct.hammerHead, "_hammer_head").setUnlocalizedName("tconstruct.HammerHead");

        bowstring = new Bowstring(PHConstruct.bowstring).setUnlocalizedName("tconstruct.Bowstring");
        arrowhead = new ToolPart(PHConstruct.arrowhead, "_arrowhead").setUnlocalizedName("tconstruct.Arrowhead");
        fletching = new Fletching(PHConstruct.fletching).setUnlocalizedName("tconstruct.Fletching");

        Item[] toolParts = { toolRod, toolShard, pickaxeHead, shovelHead, hatchetHead, binding, toughBinding, toughRod, largePlate, swordBlade, wideGuard, handGuard, crossbar, knifeBlade, fullGuard,
                frypanHead, signHead, chiselHead, scytheBlade, broadAxeHead, excavatorHead, largeSwordBlade, hammerHead, bowstring, fletching, arrowhead };
        String[] toolPartStrings = { "toolRod", "toolShard", "pickaxeHead", "shovelHead", "hatchetHead", "binding", "toughBinding", "toughRod", "heavyPlate", "swordBlade", "wideGuard", "handGuard",
                "crossbar", "knifeBlade", "fullGuard", "frypanHead", "signHead", "chiselHead", "scytheBlade", "broadAxeHead", "excavatorHead", "largeSwordBlade", "hammerHead", "bowstring",
                "fletching", "arrowhead" };

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
                "nuggetTin", "nuggetAluminum", "nuggetSilver", "nuggetAluminumBrass", "silkyCloth", "silkyJewel", "nuggetObsidian", "nuggetCobalt", "nuggetArdite", "nuggetManyullyn", "nuggetBronze",
                "nuggetAlumite", "nuggetSteel" };

        for (int i = 0; i < materialStrings.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(materials, 1, i));
        }

        String[] oreberries = { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Essence" };

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
        //Block.torchWood.setTickRandomly(false);
    }

    void registerMaterials ()
    {
        TConstructRegistry.addToolMaterial(0, "Wood", "Wooden ", 0, 59, 200, 0, 1.0F, 0, 0f, "\u00A7e", "");
        TConstructRegistry.addToolMaterial(1, "Stone", 1, 131, 400, 1, 0.5F, 0, 1f, "", "Stonebound");
        TConstructRegistry.addToolMaterial(2, "Iron", 2, 250, 600, 2, 1.3F, 1, 0f, "\u00A7f", "");
        TConstructRegistry.addToolMaterial(3, "Flint", 1, 171, 525, 2, 0.7F, 0, 0f, "\u00A78", "");
        TConstructRegistry.addToolMaterial(4, "Cactus", 1, 150, 500, 2, 1.0F, 0, -1f, "\u00A72", "Jagged");
        TConstructRegistry.addToolMaterial(5, "Bone", 1, 200, 400, 1, 1.0F, 0, 0f, "\u00A7e", "");
        TConstructRegistry.addToolMaterial(6, "Obsidian", 3, 89, 700, 2, 0.8F, 3, 0f, "\u00A7d", "");
        TConstructRegistry.addToolMaterial(7, "Netherrack", 2, 131, 400, 1, 1.2F, 0, 1f, "\u00A74", "Stonebound");
        TConstructRegistry.addToolMaterial(8, "Slime", PHConstruct.miningLevelIncrease ? 3 : 0, 1500, 150, 0, 2.0F, 0, 0f, "\u00A7a", "");
        TConstructRegistry.addToolMaterial(9, "Paper", 0, 30, 200, 0, 0.3F, 0, 0f, "\u00A7f", "Writable");
        TConstructRegistry.addToolMaterial(10, "Cobalt", 4, 800, 1100, 3, 1.75F, 2, 0f, "\u00A73", "");
        TConstructRegistry.addToolMaterial(11, "Ardite", 4, 600, 800, 3, 2.0F, 0, 2f, "\u00A74", "Stonebound");
        TConstructRegistry.addToolMaterial(12, "Manyullyn", 5, 1200, 900, 4, 2.5F, 0, 0f, "\u00A75", "");
        TConstructRegistry.addToolMaterial(13, "Copper", 1, 180, 500, 2, 1.15F, 0, 0f, "\u00A7c", "");
        TConstructRegistry.addToolMaterial(14, "Bronze", 2, 350, 700, 2, 1.3F, 1, 0f, "\u00A76", "");
        TConstructRegistry.addToolMaterial(15, "Alumite", 4, 550, 800, 3, 1.3F, 2, 0f, "\u00A7d", "");
        TConstructRegistry.addToolMaterial(16, "Steel", 4, 750, 800, 3, 1.3F, 2, 0f, "", "");
        TConstructRegistry.addToolMaterial(17, "BlueSlime", "Slime ", PHConstruct.miningLevelIncrease ? 1 : 0, 500, 150, 0, 1.5F, 0, 0f, "\u00A7b", "");

        TConstructRegistry.addBowMaterial(0, 384, 20, 1.0f); //Wood
        TConstructRegistry.addBowMaterial(1, 10, 80, 0.2f); //Stone
        TConstructRegistry.addBowMaterial(2, 576, 40, 1.2f); //Iron
        TConstructRegistry.addBowMaterial(3, 10, 80, 0.2f); //Flint
        TConstructRegistry.addBowMaterial(4, 384, 20, 1.0f); //Cactus
        TConstructRegistry.addBowMaterial(5, 192, 30, 1.0f); //Bone
        TConstructRegistry.addBowMaterial(6, 10, 80, 0.2f); //Obsidian
        TConstructRegistry.addBowMaterial(7, 10, 80, 0.2f); //Netherrack
        TConstructRegistry.addBowMaterial(8, 1536, 20, 1.2f); //Slime
        TConstructRegistry.addBowMaterial(9, 48, 25, 0.5f); //Paper
        TConstructRegistry.addBowMaterial(10, 1152, 40, 1.2f); //Cobalt
        TConstructRegistry.addBowMaterial(11, 960, 40, 1.2f); //Ardite
        TConstructRegistry.addBowMaterial(12, 1536, 40, 1.2f); //Manyullyn
        TConstructRegistry.addBowMaterial(13, 384, 40, 1.2f); //Copper
        TConstructRegistry.addBowMaterial(14, 576, 40, 1.2f); //Bronze
        TConstructRegistry.addBowMaterial(15, 768, 40, 1.2f); //Alumite
        TConstructRegistry.addBowMaterial(16, 768, 40, 1.2f); //Steel
        TConstructRegistry.addBowMaterial(17, 576, 20, 1.2f); //Blue Slime

        //Material ID, mass, fragility
        TConstructRegistry.addArrowMaterial(0, 0.69F, 1.0F, 100F); //Wood
        TConstructRegistry.addArrowMaterial(1, 2.5F, 5.0F, 100F); //Stone
        TConstructRegistry.addArrowMaterial(2, 7.2F, 0.5F, 100F); //Iron
        TConstructRegistry.addArrowMaterial(3, 2.65F, 1.0F, 100F); //Flint
        TConstructRegistry.addArrowMaterial(4, 0.76F, 1.0F, 100F); //Cactus
        TConstructRegistry.addArrowMaterial(5, 0.69F, 1.0F, 100); //Bone
        TConstructRegistry.addArrowMaterial(6, 2.4F, 1.0F, 100F); //Obsidian
        TConstructRegistry.addArrowMaterial(7, 3.5F, 1.0F, 100F); //Netherrack
        TConstructRegistry.addArrowMaterial(8, 0.42F, 0.0F, 100F); //Slime
        TConstructRegistry.addArrowMaterial(9, 1.1F, 3.0F, 90F); //Paper
        TConstructRegistry.addArrowMaterial(10, 8.9F, 0.25F, 100F); //Cobalt
        TConstructRegistry.addArrowMaterial(11, 7.2F, 0.25F, 100F); //Ardite
        TConstructRegistry.addArrowMaterial(12, 10.6F, 0.1F, 100F); //Manyullyn
        TConstructRegistry.addArrowMaterial(13, 8.96F, 0.5F, 100F); //Copper
        TConstructRegistry.addArrowMaterial(14, 7.9F, 0.25F, 100F); //Bronze
        TConstructRegistry.addArrowMaterial(15, 4.7F, 0.25F, 100F); //Alumite
        TConstructRegistry.addArrowMaterial(16, 7.6F, 0.25F, 100F); //Steel
        TConstructRegistry.addArrowMaterial(17, 0.42F, 0.0F, 100F); //Blue Slime

        TConstructRegistry.addBowstringMaterial(0, 2, new ItemStack(Item.silk), new ItemStack(bowstring, 1, 0), 1F, 1F, 1f); //String
        TConstructRegistry.addFletchingMaterial(0, 2, new ItemStack(Item.feather), new ItemStack(fletching, 1, 0), 100F, 0F, 0.05F); //Feather
        TConstructRegistry.addFletchingMaterial(1, 2, new ItemStack(Block.leaves), new ItemStack(fletching, 1, 1), 75F, 0F, 0.2F); //Leaves
        TConstructRegistry.addFletchingMaterial(2, 2, new ItemStack(materials, 1, 1), new ItemStack(fletching, 1, 2), 100F, 0F, 0.12F); //Slime
        TConstructRegistry.addFletchingMaterial(3, 2, new ItemStack(materials, 1, 17), new ItemStack(fletching, 1, 3), 100F, 0F, 0.12F); //BlueSlime

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
        else
            pb.registerMaterialSet("Stone", new ItemStack(TContent.toolShard, 1, 1), new ItemStack(TContent.toolRod, 1, 1), 0);
        pb.registerFullMaterial(Item.ingotIron, 2, "Iron", new ItemStack(TContent.toolShard, 1, 2), new ItemStack(TContent.toolRod, 1, 2), 2);
        if (PHConstruct.enableTFlint)
            pb.registerFullMaterial(Item.flint, 2, "Flint", new ItemStack(TContent.toolShard, 1, 3), new ItemStack(TContent.toolRod, 1, 3), 3);
        else
            pb.registerMaterialSet("Flint", new ItemStack(TContent.toolShard, 1, 3), new ItemStack(TContent.toolRod, 1, 3), 3);
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
        if (PHConstruct.enableTNetherrack)
            pb.registerFullMaterial(Block.netherrack, 2, "Netherrack", new ItemStack(TContent.toolShard, 1, 7), new ItemStack(TContent.toolRod, 1, 7), 7);
        else
            pb.registerMaterialSet("Netherrack", new ItemStack(TContent.toolShard, 1, 7), new ItemStack(TContent.toolRod, 1, 7), 7);
        if (PHConstruct.enableTSlime)
            pb.registerFullMaterial(new ItemStack(materials, 1, 1), 2, "Slime", new ItemStack(toolShard, 1, 8), new ItemStack(toolRod, 1, 8), 8);
        else
            pb.registerMaterialSet("Slime", new ItemStack(TContent.toolShard, 1, 8), new ItemStack(TContent.toolRod, 1, 17), 8);
        if (PHConstruct.enableTPaper)
            pb.registerFullMaterial(new ItemStack(materials, 1, 0), 2, "Paper", new ItemStack(Item.paper, 2), new ItemStack(toolRod, 1, 9), 9);
        else
            pb.registerMaterialSet("BlueSlime", new ItemStack(Item.paper, 2), new ItemStack(TContent.toolRod, 1, 9), 9);
        pb.registerMaterialSet("Cobalt", new ItemStack(toolShard, 1, 10), new ItemStack(toolRod, 1, 10), 10);
        pb.registerMaterialSet("Ardite", new ItemStack(toolShard, 1, 11), new ItemStack(toolRod, 1, 11), 11);
        pb.registerMaterialSet("Manyullyn", new ItemStack(toolShard, 1, 12), new ItemStack(toolRod, 1, 12), 12);
        pb.registerMaterialSet("Copper", new ItemStack(toolShard, 1, 13), new ItemStack(toolRod, 1, 13), 13);
        pb.registerMaterialSet("Bronze", new ItemStack(toolShard, 1, 14), new ItemStack(toolRod, 1, 14), 14);
        pb.registerMaterialSet("Alumite", new ItemStack(toolShard, 1, 15), new ItemStack(toolRod, 1, 15), 15);
        pb.registerMaterialSet("Steel", new ItemStack(toolShard, 1, 16), new ItemStack(toolRod, 1, 16), 16);
        if (PHConstruct.enableTBlueSlime)
            pb.registerFullMaterial(new ItemStack(materials, 1, 17), 2, "BlueSlime", new ItemStack(toolShard, 1, 17), new ItemStack(toolRod, 1, 17), 17);
        else
            pb.registerMaterialSet("BlueSlime", new ItemStack(TContent.toolShard, 1, 17), new ItemStack(TContent.toolRod, 1, 17), 17);

        pb.addToolPattern((IPattern) woodPattern);
    }

    public static Item[] patternOutputs;
    public static LiquidStack[] liquids;

    void addCraftingRecipes ()
    {
        /* Tools */
        patternOutputs = new Item[] { toolRod, pickaxeHead, shovelHead, hatchetHead, swordBlade, wideGuard, handGuard, crossbar, binding, frypanHead, signHead, knifeBlade, chiselHead, toughRod,
                toughBinding, largePlate, broadAxeHead, scytheBlade, excavatorHead, largeSwordBlade, hammerHead, fullGuard, null, null, arrowhead };

        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };

        if (PHConstruct.craftMetalTools)
        {
            for (int mat = 0; mat < 18; mat++)
            {
                for (int meta = 0; meta < patternOutputs.length; meta++)
                {
                    if (patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(woodPattern.itemID, meta + 1, mat, new ItemStack(patternOutputs[meta], 1, mat));
                }
            }
        }
        else
        {
            for (int mat = 0; mat < nonMetals.length; mat++)
            {
                for (int meta = 0; meta < patternOutputs.length; meta++)
                {
                    if (patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(woodPattern.itemID, meta + 1, nonMetals[mat], new ItemStack(patternOutputs[meta], 1, nonMetals[mat]));
                }
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
        tb.addNormalToolRecipe(lumberaxe, broadAxeHead, toughRod, largePlate, toughBinding);
        tb.addNormalToolRecipe(cleaver, largeSwordBlade, toughRod, largePlate, toughRod);
        tb.addNormalToolRecipe(excavator, excavatorHead, toughRod, largePlate, toughBinding);
        tb.addNormalToolRecipe(hammer, hammerHead, toughRod, largePlate, largePlate);
        tb.addNormalToolRecipe(battleaxe, broadAxeHead, toughRod, broadAxeHead, toughBinding);

        //tb.addNormalToolRecipe(shortbow, toolRod, bowstring, toolRod);
        BowRecipe recipe = new BowRecipe(toolRod, bowstring, toolRod, shortbow);
        tb.addCustomToolRecipe(recipe);
        tb.addNormalToolRecipe(arrow, arrowhead, toolRod, fletching);

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

        ItemStack obsidianPlate = new ItemStack(largePlate, 1, 6);
        tb.registerToolMod(new ModReinforced(new ItemStack[] { obsidianPlate }, 16, 1));

        TConstructRegistry.registerActiveToolMod(new TActiveOmniMod());

        /* Smeltery */
        ItemStack ingotcast = new ItemStack(metalPattern, 1, 0);
        ItemStack jewelCast = new ItemStack(metalPattern, 1, 23);

        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        //Blank
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 1), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), 80);
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 2), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1), 80);

        //Ingots
        tableCasting.addCastingRecipe(new ItemStack(Item.ingotIron), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0), ingotcast, 80); //iron
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
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 13), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 13), bucket, true, 10); //glass
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 14), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 14), bucket, true, 10); //seared stone
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 15), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 15), bucket, true, 10); //emerald

        tableCasting.addCastingRecipe(new ItemStack(glassPane), new LiquidStack(liquidMetalStill.blockID, 250, 13), null, 80);

        liquids = new LiquidStack[] { new LiquidStack(liquidMetalStill.blockID, 1, 0), new LiquidStack(liquidMetalStill.blockID, 1, 2), new LiquidStack(liquidMetalStill.blockID, 1, 5),
                new LiquidStack(liquidMetalStill.blockID, 1, 6), new LiquidStack(liquidMetalStill.blockID, 1, 9), new LiquidStack(liquidMetalStill.blockID, 1, 7),
                new LiquidStack(liquidMetalStill.blockID, 1, 10), new LiquidStack(liquidMetalStill.blockID, 1, 11), new LiquidStack(liquidMetalStill.blockID, 1, 12) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16 };

        for (int iter = 0; iter < patternOutputs.length; iter++)
        {
            if (patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
                {
                    ItemStack metalCast = new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new LiquidStack(liquids[iterTwo].itemID, ((IPattern) metalPattern).getPatternCost(metalCast) * TConstruct.ingotLiquidValue / 2,
                            liquids[iterTwo].itemMeta), cast, 50);
                }
            }
        }

        ItemStack[] ingotShapes = { new ItemStack(Item.ingotIron), new ItemStack(Item.ingotGold), new ItemStack(Item.brick), new ItemStack(Item.netherrackBrick), new ItemStack(materials, 1, 2) };
        for (int i = 0; i < ingotShapes.length; i++)
        {
            TConstruct.tableCasting.addCastingRecipe(new ItemStack(TContent.metalPattern, 1, 0), new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), ingotShapes[i],
                    false, 50);
            TConstruct.tableCasting.addCastingRecipe(new ItemStack(TContent.metalPattern, 1, 0), new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1),
                    ingotShapes[i], false, 50);
        }

        /*if (PHConstruct.convertWoodPatternsInSmeltery)
        {
            for (int i = 1; i < patternOutputs.length; i++)
            {
                if (patternOutputs[i] != null)
                {
                    tableCasting.addCastingRecipe(new ItemStack(metalPattern, 1, i), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(woodPattern, 1, i), true, 50);
                    tableCasting.addCastingRecipe(new ItemStack(metalPattern, 1, i), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1), new ItemStack(woodPattern, 1, i), true, 50);
                }
            }
        }*/

        ItemStack fullguardCast = new ItemStack(metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);

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
        basinCasting.addCastingRecipe(new ItemStack(glass, 1, 0), new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 13), null, true, 100); //glass
        basinCasting.addCastingRecipe(new ItemStack(smeltery, 1, 4), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 14), null, true, 100); //seared stone

        basinCasting.addCastingRecipe(new ItemStack(speedBlock, 1, 0), new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 3), new ItemStack(Block.gravel), true, 100); //brownstone

        //Ore
        Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        Smeltery.addMelting(Block.oreGold, 0, 400, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));
        Smeltery.addMelting(oreGravel, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        Smeltery.addMelting(oreGravel, 1, 400, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));

        //Items
        Smeltery.addMelting(new ItemStack(Item.ingotIron, 4), Block.blockIron.blockID, 0, 500, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
        Smeltery.addMelting(new ItemStack(Item.ingotGold, 4), Block.blockGold.blockID, 0, 300, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1));
        Smeltery.addMelting(new ItemStack(Item.goldNugget, 4), Block.blockGold.blockID, 0, 150, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 9, 1));

        Smeltery.addMelting(new ItemStack(materials, 1, 18), Block.obsidian.blockID, 0, 750, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 11)); //Obsidian ingot

        Smeltery.addMelting(new ItemStack(blankPattern, 4, 1), metalBlock.blockID, 7, 150, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 8));
        Smeltery.addMelting(new ItemStack(blankPattern, 4, 2), metalBlock.blockID, 7, 150, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));

        //Blocks
        Smeltery.addMelting(Block.blockIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 0));
        Smeltery.addMelting(Block.blockGold, 0, 400, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 1));
        Smeltery.addMelting(Block.obsidian, 0, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 11));
        Smeltery.addMelting(Block.ice, 0, 75, new LiquidStack(Block.waterStill.blockID, 1000, 0));
        Smeltery.addMelting(Block.sand, 0, 625, new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 13));
        Smeltery.addMelting(Block.glass, 0, 625, new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, 13));
        Smeltery.addMelting(Block.stone, 0, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 18, 14));
        Smeltery.addMelting(Block.cobblestone, 0, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue / 18, 14));

        Smeltery.addMelting(glass, 0, 500, new LiquidStack(liquidMetalStill.blockID, 1000, 13));
        Smeltery.addMelting(glassPane, 0, 350, new LiquidStack(liquidMetalStill.blockID, 250, 13));

        for (int i = 0; i < 16; i++)
        {
            Smeltery.addMelting(stainedGlassClear, i, 500, new LiquidStack(liquidMetalStill.blockID, 1000, 13));
            Smeltery.addMelting(stainedGlassClearPane, i, 350, new LiquidStack(liquidMetalStill.blockID, 250, 13));
        }

        //Alloys
        if (PHConstruct.harderBronze)
            Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 7), new LiquidStack(liquidMetalStill.blockID, 24, 2), new LiquidStack(liquidMetalStill.blockID, 8, 3)); //Bronze
        else
            Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 24, 7), new LiquidStack(liquidMetalStill.blockID, 24, 2), new LiquidStack(liquidMetalStill.blockID, 8, 3));
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 8), new LiquidStack(liquidMetalStill.blockID, 24, 4), new LiquidStack(liquidMetalStill.blockID, 8, 2)); //Aluminum Brass
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 9), new LiquidStack(liquidMetalStill.blockID, 32, 5), new LiquidStack(liquidMetalStill.blockID, 32, 6)); //Manyullyn
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 48, 10), new LiquidStack(liquidMetalStill.blockID, 80, 4), new LiquidStack(liquidMetalStill.blockID, 32, 0), new LiquidStack(
                liquidMetalStill.blockID, 32, 11)); //Alumite

        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 24, 21), new LiquidStack(liquidMetalStill.blockID, 16, 0), new LiquidStack(liquidMetalStill.blockID, 8, 17)); //Invar
        Smeltery.addAlloyMixing(new LiquidStack(liquidMetalStill.blockID, 16, 22), new LiquidStack(liquidMetalStill.blockID, 8, 1), new LiquidStack(liquidMetalStill.blockID, 8, 19)); //Electrum

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

        //Vanilla tools
        Smeltery.addMelting(new ItemStack(Item.hoeIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        Smeltery.addMelting(new ItemStack(Item.swordIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        Smeltery.addMelting(new ItemStack(Item.shovelIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 1, 0));
        Smeltery.addMelting(new ItemStack(Item.pickaxeIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 3, 0));
        Smeltery.addMelting(new ItemStack(Item.axeIron, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 3, 0));

        Smeltery.addMelting(new ItemStack(Item.hoeGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));
        Smeltery.addMelting(new ItemStack(Item.swordGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));
        Smeltery.addMelting(new ItemStack(Item.shovelGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 1, 1));
        Smeltery.addMelting(new ItemStack(Item.pickaxeGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 3, 1));
        Smeltery.addMelting(new ItemStack(Item.axeGold, 1, 0), Block.blockGold.blockID, 0, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 3, 1));
        //Vanilla items

        Smeltery.addMelting(new ItemStack(Item.flintAndSteel, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
        Smeltery.addMelting(new ItemStack(Item.compass, 1, 0), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 4, 0));

        //Vanilla blocks
        Smeltery.addMelting(new ItemStack(Item.bucketEmpty), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 3, 0));
        Smeltery.addMelting(new ItemStack(Item.minecartEmpty), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 5, 0));
        Smeltery.addMelting(new ItemStack(Item.doorIron), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 6, 0));
        Smeltery.addMelting(new ItemStack(Block.fenceIron), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 6 / 16, 0));
        Smeltery.addMelting(new ItemStack(Block.pressurePlateIron), Block.blockIron.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
        Smeltery.addMelting(new ItemStack(Block.pressurePlateGold, 4), Block.blockGold.blockID, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 1));
        Smeltery.addMelting(new ItemStack(Block.rail), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 6 / 16, 0));
        Smeltery.addMelting(new ItemStack(Block.railPowered), Block.blockGold.blockID, 8, 350, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 1));
        Smeltery.addMelting(new ItemStack(Block.railDetector), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
        Smeltery.addMelting(new ItemStack(Block.railActivator), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 0));
        Smeltery.addMelting(new ItemStack(Block.enchantmentTable), Block.obsidian.blockID, 0, 750, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 4, 11));
        Smeltery.addMelting(new ItemStack(Block.cauldron), Block.blockIron.blockID, 8, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 7, 0));
        Smeltery.addMelting(new ItemStack(Block.anvil, 1, 0), Block.blockIron.blockID, 8, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 31, 0));
        Smeltery.addMelting(new ItemStack(Block.anvil, 1, 1), Block.blockIron.blockID, 8, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 31, 0));
        Smeltery.addMelting(new ItemStack(Block.anvil, 1, 2), Block.blockIron.blockID, 8, 800, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 31, 0));

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

        chiseling.addDetailing(smeltery, 4, smeltery, 6, chisel);
        chiseling.addDetailing(smeltery, 6, smeltery, 11, chisel);
        chiseling.addDetailing(smeltery, 11, smeltery, 2, chisel);
        chiseling.addDetailing(smeltery, 2, smeltery, 8, chisel);
        chiseling.addDetailing(smeltery, 8, smeltery, 9, chisel);
        chiseling.addDetailing(smeltery, 9, smeltery, 10, chisel);

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

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(redstoneMachine, 1, 0), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(blankPattern, 1, 1),
                'r', new ItemStack(Item.redstone), 'd', new ItemStack(Block.dispenser))); //Drawbridge
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(redstoneMachine, 1, 0), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(blankPattern, 1, 2),
                'r', new ItemStack(Item.redstone), 'd', new ItemStack(Block.dispenser)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(redstoneMachine, 1, 1), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(largePlate, 1, 7), 'r',
                new ItemStack(Item.redstone), 'd', new ItemStack(Item.flintAndSteel))); //Igniter

        /* Crafting */
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.workbench);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "crafterWood"));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 2), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 3), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 4), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 5), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.chest);
        if (PHConstruct.stencilTableCrafting)
        {
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 0));
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 11), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 1));
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 12), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 2));
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 13), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 3));
        }
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "logWood"));
        if (PHConstruct.stencilTableCrafting)
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
        //FurnaceRecipes.smelting().addSmelting(oreBerries.itemID, 5, new ItemStack(materials, 1, 23), 0.2f);

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
        GameRegistry.addRecipe(new ItemStack(materials, 1, 18), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 27)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(materials, 1, 3), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 28)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(materials, 1, 4), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 29)); //Ardite
        GameRegistry.addRecipe(new ItemStack(materials, 1, 5), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 30)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(materials, 1, 13), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 31)); //Bronze
        GameRegistry.addRecipe(new ItemStack(materials, 1, 15), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 32)); //Alumite
        GameRegistry.addRecipe(new ItemStack(materials, 1, 16), "mmm", "mmm", "mmm", 'm', new ItemStack(materials, 1, 33)); //Steel    

        GameRegistry.addRecipe(new ItemStack(materials, 9, 19), "m", 'm', new ItemStack(Item.ingotIron)); //Iron
        GameRegistry.addRecipe(new ItemStack(materials, 9, 20), "m", 'm', new ItemStack(materials, 1, 9)); //Copper
        GameRegistry.addRecipe(new ItemStack(materials, 9, 21), "m", 'm', new ItemStack(materials, 1, 10)); //Tin
        GameRegistry.addRecipe(new ItemStack(materials, 9, 22), "m", 'm', new ItemStack(materials, 1, 12)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(materials, 9, 24), "m", 'm', new ItemStack(materials, 1, 14)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(materials, 9, 27), "m", 'm', new ItemStack(materials, 1, 18)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(materials, 9, 28), "m", 'm', new ItemStack(materials, 1, 3)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(materials, 9, 29), "m", 'm', new ItemStack(materials, 1, 4)); //Ardite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 30), "m", 'm', new ItemStack(materials, 1, 5)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(materials, 9, 31), "m", 'm', new ItemStack(materials, 1, 13)); //Bronze
        GameRegistry.addRecipe(new ItemStack(materials, 9, 32), "m", 'm', new ItemStack(materials, 1, 15)); //Alumite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 33), "m", 'm', new ItemStack(materials, 1, 16)); //Steel 

        //Dyes
        String[] pattern = { "###", "#m#", "###" };
        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue",
                "dyeMagenta", "dyeOrange", "dyeWhite" };
        for (int i = 0; i < 16; i++)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.cloth, 8, i), pattern, 'm', dyeTypes[15 - i], '#', new ItemStack(Block.cloth, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClear, 8, i), pattern, 'm', dyeTypes[15 - i], '#', glass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClear, 1, i), dyeTypes[15 - i], glass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClearPane, 8, i), pattern, 'm', dyeTypes[15 - i], '#', glassPane));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClearPane, 1, i), dyeTypes[15 - i], glassPane));
        }

        //Glass
        GameRegistry.addRecipe(new ItemStack(Item.glassBottle, 3), new Object[] { "# #", " # ", '#', glass });
        GameRegistry.addRecipe(new ItemStack(Block.daylightSensor), new Object[] { "GGG", "QQQ", "WWW", 'G', Block.glass, 'Q', Item.netherQuartz, 'W', Block.woodSingleSlab });
        GameRegistry.addRecipe(new ItemStack(Block.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', glass, 'S', Item.netherStar, 'O', Block.obsidian });

        //Smeltery
        ItemStack searedBrick = new ItemStack(materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks

        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 0), "bbb", "bgb", "bbb", 'b', searedBrick, 'g', Block.glass); //Tank
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', Block.glass); //Glass
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', Block.glass); //Window

        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 0), "bbb", "bgb", "bbb", 'b', searedBrick, 'g', glass); //Tank
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', glass); //Glass
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', glass); //Window

        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin

        GameRegistry.addRecipe(new ItemStack(Block.pumpkinLantern, 1, 0), "p", "s", 'p', new ItemStack(Block.pumpkin), 'w', new ItemStack(stoneTorch));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneTorch, 4), "p", "w", 'p', new ItemStack(Item.coal, 1, Short.MAX_VALUE), 'w', "stoneRod"));
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

        //Armor
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dryingRack, 1, 0), "bbb", 'b', "slabWood"));

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

        //Drying rack
        DryingRackRecipes.addDryingRecipe(Item.rottenFlesh, 20 * 60 * 5, Item.leather);

        //Slabs
        for (int i = 0; i < 7; i++)
        {
            GameRegistry.addRecipe(new ItemStack(speedSlab, 6, i), "bbb", 'b', new ItemStack(speedBlock, 1, i));
        }
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 0), "bbb", 'b', new ItemStack(smeltery, 1, 2));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 1), "bbb", 'b', new ItemStack(smeltery, 1, 4));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 2), "bbb", 'b', new ItemStack(smeltery, 1, 5));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 3), "bbb", 'b', new ItemStack(smeltery, 1, 6));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 4), "bbb", 'b', new ItemStack(smeltery, 1, 8));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 5), "bbb", 'b', new ItemStack(smeltery, 1, 9));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 6), "bbb", 'b', new ItemStack(smeltery, 1, 10));
        GameRegistry.addRecipe(new ItemStack(searedSlab, 6, 7), "bbb", 'b', new ItemStack(smeltery, 1, 11));
        
        //Traps
        GameRegistry.addRecipe(new ItemStack(punji, 5, 0), "b b", " b ", "b b", 'b', new ItemStack(Item.reed));
        
        GameRegistry.addRecipe(new ItemStack(barricadeOak, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(barricadeSpruce, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(barricadeBirch, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(barricadeJungle, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 3));
        
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(barricadeOak, 1, 0), "b", "b", 'b', "logWood"));

        GameRegistry.addRecipe(new ItemStack(craftingStationWood, 1, 0), "b", 'b', new ItemStack(Block.workbench));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(craftingStationWood, 1, 0), "b", 'b', "crafterWood"));
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
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(materials, 1, 24)); 
        OreDictionary.registerOre("nuggetObsidian", new ItemStack(materials, 1, 27));
        OreDictionary.registerOre("nuggetCobalt", new ItemStack(materials, 1, 28));
        OreDictionary.registerOre("nuggetArdite", new ItemStack(materials, 1, 29));
        OreDictionary.registerOre("nuggetManyullyn", new ItemStack(materials, 1, 30));
        OreDictionary.registerOre("nuggetBronze", new ItemStack(materials, 1, 31));
        OreDictionary.registerOre("nuggetAlumite", new ItemStack(materials, 1, 32));
        OreDictionary.registerOre("nuggetSteel", new ItemStack(materials, 1, 33));

        String[] matNames = { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel",
                "blueslime" };

        for (int i = 0; i < matNames.length; i++)
            OreDictionary.registerOre(matNames[i] + "Rod", new ItemStack(toolRod, 1, i));

        OreDictionary.registerOre("thaumiumRod", new ItemStack(toolRod, 1, 31));

        String[] names = new String[] { "Molten Iron", "Molten Gold", "Molten Copper", "Molten Tin", "Molten Aluminum", "Molten Cobalt", "Molten Ardite", "Molten Bronze", "Molten Aluminum Brass",
                "Molten Manyullyn", "Molten Alumite", "Molten Obsidian", "Molten Steel", "Molten Glass", "Seared Stone", "Molten Emerald", "Blood", "Liquid Nickel", "Liquid Lead", "Liquid Silver",
                "Liquid Shiny", "Liquid Invar", "Liquid Electrum" };
        liquidIcons = new LiquidStack[names.length];
        liquidNames = new String[names.length];
        for (int iter = 0; iter < names.length; iter++)
        {
            LiquidStack liquidstack = new LiquidStack(liquidMetalStill.blockID, LiquidContainerRegistry.BUCKET_VOLUME, iter);
            String name = names[iter];
            liquidIcons[iter] = liquidstack;
            liquidNames[iter] = name;
            LiquidDictionary.getOrCreateLiquid(name, liquidstack);
            LiquidContainerRegistry.registerLiquid(new LiquidContainerData(liquidstack, new ItemStack(buckets, 1, iter), new ItemStack(Item.bucketEmpty)));
        }

        String[] glassTypes = { "glassBlack", "glassRed", "glassGreen", "glassBrown", "glassBlue", "glassPurple", "glassCyan", "glassLightGray", "glassGray", "glassPink", "glassLime", "glassYellow",
                "glassLightBlue", "glassMagenta", "glassOrange", "glassWhite" };
        for (int i = 0; i < 16; i++)
        {
            OreDictionary.registerOre(glassTypes[15 - i], new ItemStack(stainedGlassClear, 1, i));
        }

        BlockDispenser.dispenseBehaviorRegistry.putObject(titleIcon, new TDispenserBehaviorSpawnEgg());

        //Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Item.slimeBall));
        OreDictionary.registerOre("slimeball", new ItemStack(strangeFood, 1, 0));
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.pistonStickyBase));
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.magmaCream));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.pistonStickyBase), "slimeball", Block.pistonBase));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.magmaCream), "slimeball", Item.blazePowder));
    }

    public static boolean thaumcraftAvailable;

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

        String builderItems = "builder@" + String.valueOf(stoneTorch.blockID) + ":*";

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
        Object obj = getStaticItem("itemResource", "thaumcraft.common.Config");
        if (obj != null)
        {
            System.out.println("[TConstruct] Thaumcraft detected. Adding thaumium tools.");
            thaumcraftAvailable = true;
            TConstructClientRegistry.addMaterialRenderMapping(31, "tinker", "thaumium", true);
            TConstructRegistry.addToolMaterial(31, "Thaumium", 3, 400, 700, 2, 1.3F, 0, 0f, "\u00A75", "Thaumic");
            PatternBuilder.instance.registerFullMaterial(new ItemStack((Item) obj, 1, 2), 2, "Thaumium", new ItemStack(toolShard, 1, 31), new ItemStack(toolRod, 1, 31), 31);
            for (int meta = 0; meta < patternOutputs.length; meta++)
            {
                if (patternOutputs[meta] != null)
                    TConstructRegistry.addPartMapping(woodPattern.itemID, meta + 1, 31, new ItemStack(patternOutputs[meta], 1, 31));
            }

            TConstructRegistry.addBowstringMaterial(1, 2, new ItemStack((Item) obj, 1, 7), new ItemStack(bowstring, 1, 1), 1F, 1F, 0.9f);
            TConstructRegistry.addBowMaterial(31, 576, 40, 1.2f);
            TConstructRegistry.addArrowMaterial(31, 1.8F, 0.5F, 100F);
        }
        else
        {
            System.out.println("[TConstruct] Thaumcraft not detected.");
        }

        /* Thermal Expansion */
        ItemStack ingotcast = new ItemStack(metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();

        ArrayList<ItemStack> ores = OreDictionary.getOres("ingotNickel");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 17), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotLead");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 18), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotSilver");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 19), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotPlatinum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 20), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotInvar");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 21), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotElectrum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue, 22), ingotcast, 80);
        }

        ores = OreDictionary.getOres("blockNickel");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 17), null, 100);
        }
        ores = OreDictionary.getOres("blockLead");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 18), null, 100);
        }
        ores = OreDictionary.getOres("blockSilver");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 19), null, 100);
        }
        ores = OreDictionary.getOres("blockPlatinum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 20), null, 100);
        }
        ores = OreDictionary.getOres("blockInvar");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 21), null, 100);
        }
        ores = OreDictionary.getOres("blockElectrum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 22), null, 100);
        }
    }

    private static boolean initRecipes;

    public static void modRecipes ()
    {
        if (!initRecipes)
        {
            initRecipes = true;
            if (PHConstruct.removeVanillaToolRecipes)
            {
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordWood));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordStone));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordIron));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordDiamond));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordGold));
            }

            if (!PHConstruct.vanillaMetalBlocks)
            {
                RecipeRemover.removeShapedRecipe(new ItemStack(Block.blockIron));
                RecipeRemover.removeShapedRecipe(new ItemStack(Block.blockGold));
            }
        }
    }

    public static void addShapedRecipeFirst (List recipeList, ItemStack itemstack, Object... objArray)
    {
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;

        if (objArray[var4] instanceof String[])
        {
            String[] var7 = (String[]) ((String[]) objArray[var4++]);

            for (int var8 = 0; var8 < var7.length; ++var8)
            {
                String var9 = var7[var8];
                ++var6;
                var5 = var9.length();
                var3 = var3 + var9;
            }
        }
        else
        {
            while (objArray[var4] instanceof String)
            {
                String var11 = (String) objArray[var4++];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        }

        HashMap var12;

        for (var12 = new HashMap(); var4 < objArray.length; var4 += 2)
        {
            Character var13 = (Character) objArray[var4];
            ItemStack var14 = null;

            if (objArray[var4 + 1] instanceof Item)
            {
                var14 = new ItemStack((Item) objArray[var4 + 1]);
            }
            else if (objArray[var4 + 1] instanceof Block)
            {
                var14 = new ItemStack((Block) objArray[var4 + 1], 1, Short.MAX_VALUE);
            }
            else if (objArray[var4 + 1] instanceof ItemStack)
            {
                var14 = (ItemStack) objArray[var4 + 1];
            }

            var12.put(var13, var14);
        }

        ItemStack[] var15 = new ItemStack[var5 * var6];

        for (int var16 = 0; var16 < var5 * var6; ++var16)
        {
            char var10 = var3.charAt(var16);

            if (var12.containsKey(Character.valueOf(var10)))
            {
                var15[var16] = ((ItemStack) var12.get(Character.valueOf(var10))).copy();
            }
            else
            {
                var15[var16] = null;
            }
        }

        ShapedRecipes var17 = new ShapedRecipes(var5, var6, var15, itemstack);
        recipeList.add(0, var17);
    }

    public static Object getStaticItem (String name, String classPackage)
    {
        try
        {
            Class clazz = Class.forName(classPackage);
            Field field = clazz.getDeclaredField(name);
            Object ret = field.get(null);
            if (ret != null && (ret instanceof ItemStack || ret instanceof Item))
                return ret;
            return null;
        }
        catch (Exception e)
        {
            //System.out.println("[TConstruct] Could not find " + name);
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