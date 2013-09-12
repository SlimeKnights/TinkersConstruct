package tconstruct.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import tconstruct.TConstruct;
import tconstruct.blocks.BlockLandmine;
import tconstruct.blocks.CastingChannelBlock;
import tconstruct.blocks.ConveyorBase;
import tconstruct.blocks.CraftingSlab;
import tconstruct.blocks.CraftingStationBlock;
import tconstruct.blocks.DryingRack;
import tconstruct.blocks.EquipBlock;
import tconstruct.blocks.EssenceExtractor;
import tconstruct.blocks.GlassBlockConnected;
import tconstruct.blocks.GlassBlockConnectedMeta;
import tconstruct.blocks.GlassPane;
import tconstruct.blocks.GlassPaneStained;
import tconstruct.blocks.GravelOre;
import tconstruct.blocks.LavaTankBlock;
import tconstruct.blocks.LiquidMetalFinite;
import tconstruct.blocks.MeatBlock;
import tconstruct.blocks.MetalOre;
import tconstruct.blocks.MultiBrick;
import tconstruct.blocks.MultiBrickFancy;
import tconstruct.blocks.OreberryBush;
import tconstruct.blocks.OreberryBushEssence;
import tconstruct.blocks.RedstoneMachine;
import tconstruct.blocks.SearedBlock;
import tconstruct.blocks.SearedSlab;
import tconstruct.blocks.SlabBase;
import tconstruct.blocks.SlimePad;
import tconstruct.blocks.SmelteryBlock;
import tconstruct.blocks.SoilBlock;
import tconstruct.blocks.SpeedBlock;
import tconstruct.blocks.SpeedSlab;
import tconstruct.blocks.StoneTorch;
import tconstruct.blocks.TMetalBlock;
import tconstruct.blocks.ToolForgeBlock;
import tconstruct.blocks.ToolStationBlock;
import tconstruct.blocks.logic.CastingBasinLogic;
import tconstruct.blocks.logic.CastingChannelLogic;
import tconstruct.blocks.logic.CastingTableLogic;
import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.blocks.logic.DrawbridgeLogic;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.blocks.logic.EssenceExtractorLogic;
import tconstruct.blocks.logic.FaucetLogic;
import tconstruct.blocks.logic.FirestarterLogic;
import tconstruct.blocks.logic.FrypanLogic;
import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.blocks.logic.PartBuilderLogic;
import tconstruct.blocks.logic.PatternChestLogic;
import tconstruct.blocks.logic.SmelteryDrainLogic;
import tconstruct.blocks.logic.SmelteryLogic;
import tconstruct.blocks.logic.StencilTableLogic;
import tconstruct.blocks.logic.TileEntityLandmine;
import tconstruct.blocks.logic.ToolForgeLogic;
import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.blocks.slime.SlimeFluid;
import tconstruct.blocks.slime.SlimeGel;
import tconstruct.blocks.slime.SlimeGrass;
import tconstruct.blocks.slime.SlimeLeaves;
import tconstruct.blocks.slime.SlimeSapling;
import tconstruct.blocks.slime.SlimeTallGrass;
import tconstruct.blocks.traps.BarricadeBlock;
import tconstruct.blocks.traps.Punji;
import tconstruct.client.StepSoundSlime;
import tconstruct.compat.mystcraft.MystImcHandler;
import tconstruct.entity.Automaton;
import tconstruct.entity.BlueSlime;
import tconstruct.entity.Crystal;
import tconstruct.entity.FancyEntityItem;
import tconstruct.entity.MiniGardy;
import tconstruct.entity.SlimeClone;
import tconstruct.entity.projectile.ArrowEntity;
import tconstruct.entity.projectile.DaggerEntity;
import tconstruct.entity.projectile.LaunchedPotion;
import tconstruct.items.Bowstring;
import tconstruct.items.CraftingItem;
import tconstruct.items.DiamondApple;
import tconstruct.items.EssenceCrystal;
import tconstruct.items.FilledBucket;
import tconstruct.items.Fletching;
import tconstruct.items.GoldenHead;
import tconstruct.items.Jerky;
import tconstruct.items.Manual;
import tconstruct.items.MaterialItem;
import tconstruct.items.MetalPattern;
import tconstruct.items.OreBerries;
import tconstruct.items.Pattern;
import tconstruct.items.StrangeFood;
import tconstruct.items.TitleIcon;
import tconstruct.items.ToolPart;
import tconstruct.items.ToolPartHidden;
import tconstruct.items.ToolShard;
import tconstruct.items.armor.HeartCanister;
import tconstruct.items.armor.Knapsack;
import tconstruct.items.blocks.BarricadeItem;
import tconstruct.items.blocks.CastingChannelItem;
import tconstruct.items.blocks.CraftedSoilItemBlock;
import tconstruct.items.blocks.CraftingSlabItemBlock;
import tconstruct.items.blocks.GlassBlockItem;
import tconstruct.items.blocks.GlassPaneItem;
import tconstruct.items.blocks.GravelOreItem;
import tconstruct.items.blocks.ItemBlockLandmine;
import tconstruct.items.blocks.LavaTankItemBlock;
import tconstruct.items.blocks.MetalItemBlock;
import tconstruct.items.blocks.MetalOreItemBlock;
import tconstruct.items.blocks.MultiBrickFancyItem;
import tconstruct.items.blocks.MultiBrickItem;
import tconstruct.items.blocks.OreberryBushItem;
import tconstruct.items.blocks.OreberryBushSecondItem;
import tconstruct.items.blocks.RedstoneMachineItem;
import tconstruct.items.blocks.SearedSlabItem;
import tconstruct.items.blocks.SearedTableItemBlock;
import tconstruct.items.blocks.SlimeGelItemBlock;
import tconstruct.items.blocks.SlimeGrassItemBlock;
import tconstruct.items.blocks.SlimeLeavesItemBlock;
import tconstruct.items.blocks.SlimeSaplingItemBlock;
import tconstruct.items.blocks.SlimeTallGrassItem;
import tconstruct.items.blocks.SmelteryItemBlock;
import tconstruct.items.blocks.SpeedBlockItem;
import tconstruct.items.blocks.SpeedSlabItem;
import tconstruct.items.blocks.StainedGlassClearItem;
import tconstruct.items.blocks.StainedGlassClearPaneItem;
import tconstruct.items.blocks.ToolForgeItemBlock;
import tconstruct.items.blocks.ToolStationItemBlock;
import tconstruct.items.blocks.WoolSlab1Item;
import tconstruct.items.blocks.WoolSlab2Item;
import tconstruct.items.tools.Arrow;
import tconstruct.items.tools.BattleSign;
import tconstruct.items.tools.Battleaxe;
import tconstruct.items.tools.Broadsword;
import tconstruct.items.tools.Chisel;
import tconstruct.items.tools.Cleaver;
import tconstruct.items.tools.Cutlass;
import tconstruct.items.tools.Dagger;
import tconstruct.items.tools.Excavator;
import tconstruct.items.tools.FryingPan;
import tconstruct.items.tools.Hammer;
import tconstruct.items.tools.Hatchet;
import tconstruct.items.tools.Longsword;
import tconstruct.items.tools.LumberAxe;
import tconstruct.items.tools.Mattock;
import tconstruct.items.tools.Pickaxe;
import tconstruct.items.tools.PotionLauncher;
import tconstruct.items.tools.Rapier;
import tconstruct.items.tools.Scythe;
import tconstruct.items.tools.Shortbow;
import tconstruct.items.tools.Shovel;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.DryingRackRecipes;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.IPattern;
import tconstruct.modifiers.ModAntiSpider;
import tconstruct.modifiers.ModAttack;
import tconstruct.modifiers.ModAutoSmelt;
import tconstruct.modifiers.ModBlaze;
import tconstruct.modifiers.ModButtertouch;
import tconstruct.modifiers.ModDurability;
import tconstruct.modifiers.ModElectric;
import tconstruct.modifiers.ModExtraModifier;
import tconstruct.modifiers.ModInteger;
import tconstruct.modifiers.ModLapis;
import tconstruct.modifiers.ModPiston;
import tconstruct.modifiers.ModRedstone;
import tconstruct.modifiers.ModReinforced;
import tconstruct.modifiers.ModRepair;
import tconstruct.modifiers.ModSmite;
import tconstruct.modifiers.TActiveOmniMod;
import tconstruct.util.PHConstruct;
import tconstruct.util.RecipeRemover;
import tconstruct.util.TDispenserBehaviorSpawnEgg;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

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
    public static Item jerky;
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
    public static Block craftingSlabWood;

    public static Block heldItemBlock;
    public static Block craftedSoil;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block metalBlock;

    public static Block redstoneMachine;
    public static Block dryingRack;

    //Decoration
    public static Block stoneTorch;
    public static Block multiBrick;
    public static Block multiBrickFancy;

    public static Block searedSlab;
    public static Block speedSlab;

    public static Block meatBlock;
    public static Block woolSlab1;
    public static Block woolSlab2;

    //Traps
    public static Block landmine;
    public static Block punji;
    public static Block barricadeOak;
    public static Block barricadeSpruce;
    public static Block barricadeBirch;
    public static Block barricadeJungle;

    //InfiBlocks
    public static Block speedBlock;
    public static Block clearGlass;
    //public static Block stainedGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    //public static Block stainedGlassPane;
    public static Block stainedGlassClearPane;
    public static Block glassMagicSlab;
    public static Block stainedGlassMagicSlab;
    public static Block stainedGlassClearMagicSlab;

    //Crystalline
    public static Block essenceExtractor;
    public static Item essenceCrystal;

    //Liquids
    public static Material liquidMetal;

    public static Fluid moltenIronFluid;
    public static Fluid moltenGoldFluid;
    public static Fluid moltenCopperFluid;
    public static Fluid moltenTinFluid;
    public static Fluid moltenAluminumFluid;
    public static Fluid moltenCobaltFluid;
    public static Fluid moltenArditeFluid;
    public static Fluid moltenBronzeFluid;
    public static Fluid moltenAlubrassFluid;
    public static Fluid moltenManyullynFluid;
    public static Fluid moltenAlumiteFluid;
    public static Fluid moltenObsidianFluid;
    public static Fluid moltenSteelFluid;
    public static Fluid moltenGlassFluid;
    public static Fluid moltenStoneFluid;
    public static Fluid moltenEmeraldFluid;
    public static Fluid bloodFluid;
    public static Fluid moltenNickelFluid;
    public static Fluid moltenLeadFluid;
    public static Fluid moltenSilverFluid;
    public static Fluid moltenShinyFluid;
    public static Fluid moltenInvarFluid;
    public static Fluid moltenElectrumFluid;
    public static Fluid moltenEnderFluid;
    public static Fluid blueSlimeFluid;

    public static Block moltenIron;
    public static Block moltenGold;
    public static Block moltenCopper;
    public static Block moltenTin;
    public static Block moltenAluminum;
    public static Block moltenCobalt;
    public static Block moltenArdite;
    public static Block moltenBronze;
    public static Block moltenAlubrass;
    public static Block moltenManyullyn;
    public static Block moltenAlumite;
    public static Block moltenObsidian;
    public static Block moltenSteel;
    public static Block moltenGlass;
    public static Block moltenStone;
    public static Block moltenEmerald;
    public static Block blood;
    public static Block moltenNickel;
    public static Block moltenLead;
    public static Block moltenSilver;
    public static Block moltenShiny;
    public static Block moltenInvar;
    public static Block moltenElectrum;
    public static Block moltenEnder;

    //Slime
    public static StepSound slimeStep;
    public static Block slimePool;
    public static Block slimeGel;
    public static Block slimeGrass;
    public static Block slimeTallGrass;
    public static SlimeLeaves slimeLeaves;
    public static SlimeSapling slimeSapling;

    public static Block slimeChannel;
    public static Block slimePad;

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
        registerItems();
        registerBlocks();
        registerMaterials();
        addCraftingRecipes();
        setupToolTabs();
        addLoot();
    }

    public void createEntities()
    {
        EntityRegistry.registerModEntity(FancyEntityItem.class, "Fancy Item", 0, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(DaggerEntity.class, "Dagger", 1, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(Crystal.class, "Crystal", 2, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(LaunchedPotion.class, "Launched Potion", 3, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 4, TConstruct.instance, 32, 5, true);
        //EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1, TConstruct.instance, 32, 5, true);

        EntityRegistry.registerModEntity(SlimeClone.class, "SlimeClone", 10, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(Automaton.class, "Automaton", 11, TConstruct.instance, 64, 3, true);
        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        EntityRegistry.registerModEntity(MiniGardy.class, "MiniGardy", 13, TConstruct.instance, 64, 3, true);
        //EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13, TConstruct.instance, 64, 5, true);

        /*BiomeGenBase[] plains = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.PLAINS);
        BiomeGenBase[] mountain = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.MOUNTAIN);
        BiomeGenBase[] hills = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.HILLS);
        BiomeGenBase[] swamp = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.SWAMP);
        BiomeGenBase[] desert = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.DESERT);
        BiomeGenBase[] frozen = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.FROZEN);
        BiomeGenBase[] jungle = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.JUNGLE);
        BiomeGenBase[] wasteland = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.WASTELAND);

        BiomeGenBase[] nether = BiomeDictionary.getBiomesForType(BiomeDictionary.Type.NETHER);*/

        /*if (PHConstruct.blueSlime)
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

        }*/
    }

    public static Fluid[] fluids = new Fluid[25];
    public static Block[] fluidBlocks = new Block[25];

    void registerBlocks()
    {
        //Tool Station
        toolStationWood = new ToolStationBlock(PHConstruct.woodStation, Material.wood).setUnlocalizedName("ToolStation");
        GameRegistry.registerBlock(toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartBuilderLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");

        toolForge = new ToolForgeBlock(PHConstruct.toolForge, Material.iron).setUnlocalizedName("ToolForge");
        GameRegistry.registerBlock(toolForge, ToolForgeItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");

        craftingStationWood = new CraftingStationBlock(PHConstruct.woodCrafter, Material.wood).setUnlocalizedName("CraftingStation");
        GameRegistry.registerBlock(craftingStationWood, "CraftingStation");
        GameRegistry.registerTileEntity(CraftingStationLogic.class, "CraftingStation");

        craftingSlabWood = new CraftingSlab(PHConstruct.woodCrafterSlab, Material.wood).setUnlocalizedName("CraftingSlab");
        GameRegistry.registerBlock(craftingSlabWood, CraftingSlabItemBlock.class, "CraftingSlab");

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

        meatBlock = new MeatBlock(PHConstruct.meatBlock).setUnlocalizedName("tconstruct.meatblock");
        GameRegistry.registerBlock(meatBlock, "MeatBlock");

        OreDictionary.registerOre("hambone", new ItemStack(meatBlock));
        LanguageRegistry.addName(meatBlock, "Hambone");
        GameRegistry.addRecipe(new ItemStack(meatBlock), "mmm", "mbm", "mmm", 'b', new ItemStack(Item.bone), 'm', new ItemStack(Item.porkRaw));

        woolSlab1 = new SlabBase(PHConstruct.woolSlab1, Material.cloth, Block.cloth, 0, 8).setUnlocalizedName("cloth");
        woolSlab1.setStepSound(Block.soundClothFootstep).setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(woolSlab1, WoolSlab1Item.class, "WoolSlab1");
        woolSlab2 = new SlabBase(PHConstruct.woolSlab2, Material.cloth, Block.cloth, 8, 8).setUnlocalizedName("cloth");
        woolSlab2.setStepSound(Block.soundClothFootstep).setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(woolSlab2, WoolSlab2Item.class, "WoolSlab2");

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

        castingChannel = (new CastingChannelBlock(PHConstruct.castingChannel)).setUnlocalizedName("CastingChannel");
        GameRegistry.registerBlock(castingChannel, CastingChannelItem.class, "CastingChannel");
        GameRegistry.registerTileEntity(CastingChannelLogic.class, "CastingChannel");

        //Redstone machines
        redstoneMachine = new RedstoneMachine(PHConstruct.redstoneMachine).setUnlocalizedName("Redstone.Machine");
        GameRegistry.registerBlock(redstoneMachine, RedstoneMachineItem.class, "Redstone.Machine");
        GameRegistry.registerTileEntity(DrawbridgeLogic.class, "Drawbridge");
        GameRegistry.registerTileEntity(FirestarterLogic.class, "Firestarter");

        //Traps
        landmine = new BlockLandmine(PHConstruct.landmine).setHardness(0.5F).setResistance(0F).setStepSound(Block.soundMetalFootstep).setCreativeTab(CreativeTabs.tabRedstone)
                .setUnlocalizedName("landmine");
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

        moltenIronFluid = new Fluid("Molten Iron");
        FluidRegistry.registerFluid(moltenIronFluid);
        moltenIron = new LiquidMetalFinite(PHConstruct.moltenIron, moltenIronFluid, "liquid_iron").setUnlocalizedName("metal.molten.iron");
        GameRegistry.registerBlock(moltenIron, "metal.molten.iron");
        moltenIronFluid.setBlockID(moltenIron).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[0] = moltenIronFluid;
        fluidBlocks[0] = moltenIron;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenIronFluid, 1000), new ItemStack(buckets, 1, 0), new ItemStack(Item.bucketEmpty)));

        moltenGoldFluid = new Fluid("Molten Gold");
        FluidRegistry.registerFluid(moltenGoldFluid);
        moltenGold = new LiquidMetalFinite(PHConstruct.moltenGold, moltenGoldFluid, "liquid_gold").setUnlocalizedName("metal.molten.gold");
        GameRegistry.registerBlock(moltenGold, "metal.molten.gold");
        moltenGoldFluid.setBlockID(moltenGold).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[1] = moltenGoldFluid;
        fluidBlocks[1] = moltenGold;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenGoldFluid, 1000), new ItemStack(buckets, 1, 1), new ItemStack(Item.bucketEmpty)));

        moltenCopperFluid = new Fluid("Molten Copper");
        FluidRegistry.registerFluid(moltenCopperFluid);
        moltenCopper = new LiquidMetalFinite(PHConstruct.moltenCopper, moltenCopperFluid, "liquid_copper").setUnlocalizedName("metal.molten.copper");
        GameRegistry.registerBlock(moltenCopper, "metal.molten.copper");
        moltenCopperFluid.setBlockID(moltenCopper).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[2] = moltenCopperFluid;
        fluidBlocks[2] = moltenCopper;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenCopperFluid, 1000), new ItemStack(buckets, 1, 2), new ItemStack(Item.bucketEmpty)));

        moltenTinFluid = new Fluid("Molten Tin");
        FluidRegistry.registerFluid(moltenTinFluid);
        moltenTin = new LiquidMetalFinite(PHConstruct.moltenTin, moltenTinFluid, "liquid_tin").setUnlocalizedName("metal.molten.tin");
        GameRegistry.registerBlock(moltenTin, "metal.molten.tin");
        moltenTinFluid.setBlockID(moltenTin).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[3] = moltenTinFluid;
        fluidBlocks[3] = moltenTin;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenTinFluid, 1000), new ItemStack(buckets, 1, 3), new ItemStack(Item.bucketEmpty)));

        moltenAluminumFluid = new Fluid("Molten Aluminum");
        FluidRegistry.registerFluid(moltenAluminumFluid);
        moltenAluminum = new LiquidMetalFinite(PHConstruct.moltenAluminum, moltenAluminumFluid, "liquid_aluminum").setUnlocalizedName("metal.molten.aluminum");
        GameRegistry.registerBlock(moltenAluminum, "metal.molten.aluminum");
        moltenAluminumFluid.setBlockID(moltenAluminum).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[4] = moltenAluminumFluid;
        fluidBlocks[4] = moltenAluminum;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenAluminumFluid, 1000), new ItemStack(buckets, 1, 4), new ItemStack(Item.bucketEmpty)));

        moltenCobaltFluid = new Fluid("Molten Cobalt");
        FluidRegistry.registerFluid(moltenCobaltFluid);
        moltenCobalt = new LiquidMetalFinite(PHConstruct.moltenCobalt, moltenCobaltFluid, "liquid_cobalt").setUnlocalizedName("metal.molten.cobalt");
        GameRegistry.registerBlock(moltenCobalt, "metal.molten.cobalt");
        moltenCobaltFluid.setBlockID(moltenCobalt).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[5] = moltenCobaltFluid;
        fluidBlocks[5] = moltenCobalt;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenCobaltFluid, 1000), new ItemStack(buckets, 1, 5), new ItemStack(Item.bucketEmpty)));

        moltenArditeFluid = new Fluid("Molten Ardite");
        FluidRegistry.registerFluid(moltenArditeFluid);
        moltenArdite = new LiquidMetalFinite(PHConstruct.moltenArdite, moltenArditeFluid, "liquid_ardite").setUnlocalizedName("metal.molten.ardite");
        GameRegistry.registerBlock(moltenArdite, "metal.molten.ardite");
        moltenArditeFluid.setBlockID(moltenArdite).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[6] = moltenArditeFluid;
        fluidBlocks[6] = moltenArdite;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenArditeFluid, 1000), new ItemStack(buckets, 1, 6), new ItemStack(Item.bucketEmpty)));

        moltenBronzeFluid = new Fluid("Molten Bronze");
        FluidRegistry.registerFluid(moltenBronzeFluid);
        moltenBronze = new LiquidMetalFinite(PHConstruct.moltenBronze, moltenBronzeFluid, "liquid_bronze").setUnlocalizedName("metal.molten.bronze");
        GameRegistry.registerBlock(moltenBronze, "metal.molten.bronze");
        moltenBronzeFluid.setBlockID(moltenBronze).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[7] = moltenBronzeFluid;
        fluidBlocks[7] = moltenBronze;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenBronzeFluid, 1000), new ItemStack(buckets, 1, 7), new ItemStack(Item.bucketEmpty)));

        moltenAlubrassFluid = new Fluid("Molten Aluminum Brass");
        FluidRegistry.registerFluid(moltenAlubrassFluid);
        moltenAlubrass = new LiquidMetalFinite(PHConstruct.moltenAlubrass, moltenAlubrassFluid, "liquid_alubrass").setUnlocalizedName("metal.molten.alubrass");
        GameRegistry.registerBlock(moltenAlubrass, "metal.molten.alubrass");
        moltenAlubrassFluid.setBlockID(moltenAlubrass).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[8] = moltenAlubrassFluid;
        fluidBlocks[8] = moltenAlubrass;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenAlubrassFluid, 1000), new ItemStack(buckets, 1, 8), new ItemStack(Item.bucketEmpty)));

        moltenManyullynFluid = new Fluid("Molten Manyullyn");
        FluidRegistry.registerFluid(moltenManyullynFluid);
        moltenManyullyn = new LiquidMetalFinite(PHConstruct.moltenManyullyn, moltenManyullynFluid, "liquid_manyullyn").setUnlocalizedName("metal.molten.manyullyn");
        GameRegistry.registerBlock(moltenManyullyn, "metal.molten.manyullyn");
        moltenManyullynFluid.setBlockID(moltenManyullyn).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[9] = moltenManyullynFluid;
        fluidBlocks[9] = moltenManyullyn;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenManyullynFluid, 1000), new ItemStack(buckets, 1, 9), new ItemStack(Item.bucketEmpty)));

        moltenAlumiteFluid = new Fluid("Molten Alumite");
        FluidRegistry.registerFluid(moltenAlumiteFluid);
        moltenAlumite = new LiquidMetalFinite(PHConstruct.moltenAlumite, moltenAlumiteFluid, "liquid_alumite").setUnlocalizedName("metal.molten.alumite");
        GameRegistry.registerBlock(moltenAlumite, "metal.molten.alumite");
        moltenAlumiteFluid.setBlockID(moltenAlumite).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[10] = moltenAlumiteFluid;
        fluidBlocks[10] = moltenAlumite;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenAlumiteFluid, 1000), new ItemStack(buckets, 1, 10), new ItemStack(Item.bucketEmpty)));

        moltenObsidianFluid = new Fluid("Molten Obsidian");
        FluidRegistry.registerFluid(moltenObsidianFluid);
        moltenObsidian = new LiquidMetalFinite(PHConstruct.moltenObsidian, moltenObsidianFluid, "liquid_obsidian").setUnlocalizedName("metal.molten.obsidian");
        GameRegistry.registerBlock(moltenObsidian, "metal.molten.obsidian");
        moltenObsidianFluid.setBlockID(moltenObsidian).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[11] = moltenObsidianFluid;
        fluidBlocks[11] = moltenObsidian;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenObsidianFluid, 1000), new ItemStack(buckets, 1, 11), new ItemStack(Item.bucketEmpty)));

        moltenSteelFluid = new Fluid("Molten Steel");
        FluidRegistry.registerFluid(moltenSteelFluid);
        moltenSteel = new LiquidMetalFinite(PHConstruct.moltenSteel, moltenSteelFluid, "liquid_steel").setUnlocalizedName("metal.molten.steel");
        GameRegistry.registerBlock(moltenSteel, "metal.molten.steel");
        moltenSteelFluid.setBlockID(moltenSteel).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[12] = moltenSteelFluid;
        fluidBlocks[12] = moltenSteel;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenSteelFluid, 1000), new ItemStack(buckets, 1, 12), new ItemStack(Item.bucketEmpty)));

        moltenGlassFluid = new Fluid("Molten Glass");
        FluidRegistry.registerFluid(moltenGlassFluid);
        moltenGlass = new LiquidMetalFinite(PHConstruct.moltenGlass, moltenGlassFluid, "liquid_glass").setUnlocalizedName("metal.molten.glass");
        GameRegistry.registerBlock(moltenGlass, "metal.molten.glass");
        moltenGlassFluid.setBlockID(moltenGlass).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[13] = moltenGlassFluid;
        fluidBlocks[13] = moltenGlass;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenGlassFluid, 1000), new ItemStack(buckets, 1, 13), new ItemStack(Item.bucketEmpty)));

        moltenStoneFluid = new Fluid("Seared Stone");
        FluidRegistry.registerFluid(moltenStoneFluid);
        moltenStone = new LiquidMetalFinite(PHConstruct.moltenStone, moltenStoneFluid, "liquid_stone").setUnlocalizedName("molten.stone");
        GameRegistry.registerBlock(moltenStone, "molten.stone");
        moltenStoneFluid.setBlockID(moltenStone).setLuminosity(12).setDensity(3000).setViscosity(6000);
        fluids[14] = moltenStoneFluid;
        fluidBlocks[14] = moltenStone;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenStoneFluid, 1000), new ItemStack(buckets, 1, 14), new ItemStack(Item.bucketEmpty)));

        moltenEmeraldFluid = new Fluid("Liquified Emerald");
        FluidRegistry.registerFluid(moltenEmeraldFluid);
        moltenEmerald = new LiquidMetalFinite(PHConstruct.moltenEmerald, moltenEmeraldFluid, "liquid_villager").setUnlocalizedName("molten.emerald");
        GameRegistry.registerBlock(moltenEmerald, "molten.emerald");
        moltenEmeraldFluid.setBlockID(moltenEmerald).setDensity(3000).setViscosity(6000);
        fluids[15] = moltenEmeraldFluid;
        fluidBlocks[15] = moltenEmerald;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenEmeraldFluid, 1000), new ItemStack(buckets, 1, 15), new ItemStack(Item.bucketEmpty)));

        bloodFluid = new Fluid("Blood");
        FluidRegistry.registerFluid(bloodFluid);
        blood = new LiquidMetalFinite(PHConstruct.blood, bloodFluid, "liquid_cow").setUnlocalizedName("liquid.blood");
        GameRegistry.registerBlock(blood, "liquid.blood");
        bloodFluid.setBlockID(blood).setDensity(3000).setViscosity(6000);
        fluids[16] = bloodFluid;
        fluidBlocks[16] = blood;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(bloodFluid, 1000), new ItemStack(buckets, 1, 16), new ItemStack(Item.bucketEmpty)));

        moltenNickelFluid = new Fluid("nickel.molten");
        FluidRegistry.registerFluid(moltenNickelFluid);
        moltenNickel = new LiquidMetalFinite(PHConstruct.moltenNickel, moltenNickelFluid, "liquid_ferrous").setUnlocalizedName("metal.molten.nickel");
        GameRegistry.registerBlock(moltenNickel, "metal.molten.nickel");
        moltenNickelFluid.setBlockID(moltenNickel).setDensity(3000).setViscosity(6000);
        fluids[17] = moltenNickelFluid;
        fluidBlocks[17] = moltenNickel;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenNickelFluid, 1000), new ItemStack(buckets, 1, 17), new ItemStack(Item.bucketEmpty)));

        moltenLeadFluid = new Fluid("lead.molten");
        FluidRegistry.registerFluid(moltenLeadFluid);
        moltenLead = new LiquidMetalFinite(PHConstruct.moltenLead, moltenLeadFluid, "liquid_lead").setUnlocalizedName("metal.molten.lead");
        GameRegistry.registerBlock(moltenLead, "metal.molten.lead");
        moltenLeadFluid.setBlockID(moltenLead).setDensity(3000).setViscosity(6000);
        fluids[18] = moltenLeadFluid;
        fluidBlocks[18] = moltenLead;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenLeadFluid, 1000), new ItemStack(buckets, 1, 18), new ItemStack(Item.bucketEmpty)));

        moltenSilverFluid = new Fluid("silver.molten");
        FluidRegistry.registerFluid(moltenSilverFluid);
        moltenSilver = new LiquidMetalFinite(PHConstruct.moltenSilver, moltenSilverFluid, "liquid_silver").setUnlocalizedName("metal.molten.silver");
        GameRegistry.registerBlock(moltenSilver, "metal.molten.silver");
        moltenSilverFluid.setBlockID(moltenSilver).setDensity(3000).setViscosity(6000);
        fluids[19] = moltenSilverFluid;
        fluidBlocks[19] = moltenSilver;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenSilverFluid, 1000), new ItemStack(buckets, 1, 19), new ItemStack(Item.bucketEmpty)));

        moltenShinyFluid = new Fluid("platinum.molten");
        FluidRegistry.registerFluid(moltenShinyFluid);
        moltenShiny = new LiquidMetalFinite(PHConstruct.moltenShiny, moltenShinyFluid, "liquid_shiny").setUnlocalizedName("metal.molten.shiny");
        GameRegistry.registerBlock(moltenShiny, "metal.molten.shiny");
        moltenShinyFluid.setBlockID(moltenShiny).setDensity(3000).setViscosity(6000);
        fluids[20] = moltenLeadFluid;
        fluidBlocks[20] = moltenShiny;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenShinyFluid, 1000), new ItemStack(buckets, 1, 20), new ItemStack(Item.bucketEmpty)));

        moltenInvarFluid = new Fluid("invar.molten");
        FluidRegistry.registerFluid(moltenInvarFluid);
        moltenInvar = new LiquidMetalFinite(PHConstruct.moltenInvar, moltenInvarFluid, "liquid_invar").setUnlocalizedName("metal.molten.invar");
        GameRegistry.registerBlock(moltenInvar, "metal.molten.invar");
        moltenInvarFluid.setBlockID(moltenInvar).setDensity(3000).setViscosity(6000);
        fluids[21] = moltenInvarFluid;
        fluidBlocks[21] = moltenInvar;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenInvarFluid, 1000), new ItemStack(buckets, 1, 21), new ItemStack(Item.bucketEmpty)));

        moltenElectrumFluid = new Fluid("electrum.molten");
        FluidRegistry.registerFluid(moltenElectrumFluid);
        moltenElectrum = new LiquidMetalFinite(PHConstruct.moltenElectrum, moltenElectrumFluid, "liquid_electrum").setUnlocalizedName("metal.molten.electrum");
        GameRegistry.registerBlock(moltenElectrum, "metal.molten.electrum");
        moltenElectrumFluid.setBlockID(moltenElectrum).setDensity(3000).setViscosity(6000);
        fluids[22] = moltenElectrumFluid;
        fluidBlocks[22] = moltenElectrum;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenElectrumFluid, 1000), new ItemStack(buckets, 1, 22), new ItemStack(Item.bucketEmpty)));

        moltenEnderFluid = new Fluid("ender");
        FluidRegistry.registerFluid(moltenEnderFluid);
        moltenEnder = new LiquidMetalFinite(PHConstruct.moltenEnder, moltenEnderFluid, "liquid_ender").setUnlocalizedName("liquid.ender");
        GameRegistry.registerBlock(moltenEnder, "liquid.ender");
        moltenEnderFluid.setBlockID(moltenEnder).setDensity(3000).setViscosity(6000);
        fluids[23] = moltenEnderFluid;
        fluidBlocks[23] = moltenEnder;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenEnderFluid, 1000), new ItemStack(buckets, 1, 23), new ItemStack(Item.bucketEmpty)));

        //Slime
        slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        blueSlimeFluid = new Fluid("slime.blue");
        FluidRegistry.registerFluid(blueSlimeFluid);
        slimePool = new SlimeFluid(PHConstruct.slimePoolBlue, blueSlimeFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(slimeStep).setUnlocalizedName("liquid.slime");
        GameRegistry.registerBlock(slimePool, "liquid.slime");
        blueSlimeFluid.setBlockID(slimePool);
        fluids[24] = blueSlimeFluid;
        fluidBlocks[24] = slimePool;
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(blueSlimeFluid, 1000), new ItemStack(buckets, 1, 24), new ItemStack(Item.bucketEmpty)));

        slimeGel = new SlimeGel(PHConstruct.slimeGel).setStepSound(slimeStep).setUnlocalizedName("slime.gel");
        GameRegistry.registerBlock(slimeGel, SlimeGelItemBlock.class, "slime.gel");

        slimeGrass = new SlimeGrass(PHConstruct.slimeGrass).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("slime.grass");
        GameRegistry.registerBlock(slimeGrass, SlimeGrassItemBlock.class, "slime.grass");

        slimeTallGrass = new SlimeTallGrass(PHConstruct.slimeTallGrass).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("slime.grass.tall");
        GameRegistry.registerBlock(slimeTallGrass, SlimeTallGrassItem.class, "slime.grass.tall");

        slimeLeaves = (SlimeLeaves) new SlimeLeaves(PHConstruct.slimeLeaves).setStepSound(slimeStep).setUnlocalizedName("slime.leaves");
        GameRegistry.registerBlock(slimeLeaves, SlimeLeavesItemBlock.class, "slime.leaves");

        slimeSapling = (SlimeSapling) new SlimeSapling(PHConstruct.slimeSapling).setStepSound(slimeStep).setUnlocalizedName("slime.sapling");
        GameRegistry.registerBlock(slimeSapling, SlimeSaplingItemBlock.class, "slime.sapling");

        slimeChannel = new ConveyorBase(PHConstruct.slimeChannel, Material.water).setStepSound(slimeStep).setUnlocalizedName("slime.channel");
        GameRegistry.registerBlock(slimeChannel, "slime.channel");
        
        slimePad = new SlimePad(PHConstruct.slimePad, Material.cloth).setStepSound(slimeStep).setUnlocalizedName("slime.pad");
        GameRegistry.registerBlock(slimePad, "slime.pad");

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
        clearGlass = new GlassBlockConnected(PHConstruct.glass, "clear", false).setUnlocalizedName("GlassBlock");
        clearGlass.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(clearGlass, GlassBlockItem.class, "GlassBlock");

        glassPane = new GlassPane(PHConstruct.glassPane);
        GameRegistry.registerBlock(glassPane, GlassPaneItem.class, "GlassPane");

        stainedGlassClear = new GlassBlockConnectedMeta(PHConstruct.stainedGlassClear, "stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
                "cyan", "purple", "blue", "brown", "green", "red", "black").setUnlocalizedName("GlassBlock.StainedClear");
        stainedGlassClear.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");

        stainedGlassClearPane = new GlassPaneStained(PHConstruct.stainedGlassClearPane);
        GameRegistry.registerBlock(stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        //Crystalline
        essenceExtractor = new EssenceExtractor(PHConstruct.essenceExtractor).setHardness(12f).setUnlocalizedName("extractor.essence");
        GameRegistry.registerBlock(essenceExtractor, "extractor.essence");
        GameRegistry.registerTileEntity(EssenceExtractorLogic.class, "extractor.essence");
    }

    void registerItems()
    {
        titleIcon = new TitleIcon(PHConstruct.uselessItem).setUnlocalizedName("tconstruct.titleicon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast", "blank_cast" };
        blankPattern = new CraftingItem(PHConstruct.blankPattern, blanks, blanks, "materials/").setUnlocalizedName("tconstruct.Pattern");

        materials = new MaterialItem(PHConstruct.materials).setUnlocalizedName("tconstruct.Materials");
        toolRod = new ToolPart(PHConstruct.toolRod, "_rod", "ToolRod").setUnlocalizedName("tconstruct.ToolRod");
        toolShard = new ToolShard(PHConstruct.toolShard, "_chunk").setUnlocalizedName("tconstruct.ToolShard");
        woodPattern = new Pattern(PHConstruct.woodPattern, "WoodPattern", "pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
        metalPattern = new MetalPattern(PHConstruct.metalPattern, "MetalPattern", "cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");

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

        pickaxeHead = new ToolPart(PHConstruct.pickaxeHead, "_pickaxe_head", "PickHead").setUnlocalizedName("tconstruct.PickaxeHead");
        shovelHead = new ToolPart(PHConstruct.shovelHead, "_shovel_head", "ShovelHead").setUnlocalizedName("tconstruct.ShovelHead");
        hatchetHead = new ToolPart(PHConstruct.axeHead, "_axe_head", "AxeHead").setUnlocalizedName("tconstruct.AxeHead");
        binding = new ToolPart(PHConstruct.binding, "_binding", "Binding").setUnlocalizedName("tconstruct.Binding");
        toughBinding = new ToolPart(PHConstruct.toughBinding, "_toughbind", "ToughBind").setUnlocalizedName("tconstruct.ThickBinding");
        toughRod = new ToolPart(PHConstruct.toughRod, "_toughrod", "ToughRod").setUnlocalizedName("tconstruct.ThickRod");
        largePlate = new ToolPart(PHConstruct.largePlate, "_largeplate", "LargePlate").setUnlocalizedName("tconstruct.LargePlate");

        swordBlade = new ToolPart(PHConstruct.swordBlade, "_sword_blade", "SwordBlade").setUnlocalizedName("tconstruct.SwordBlade");
        wideGuard = new ToolPart(PHConstruct.largeGuard, "_large_guard", "LargeGuard").setUnlocalizedName("tconstruct.LargeGuard");
        handGuard = new ToolPart(PHConstruct.medGuard, "_medium_guard", "MediumGuard").setUnlocalizedName("tconstruct.MediumGuard");
        crossbar = new ToolPart(PHConstruct.crossbar, "_crossbar", "Crossbar").setUnlocalizedName("tconstruct.Crossbar");
        knifeBlade = new ToolPart(PHConstruct.knifeBlade, "_knife_blade", "KnifeBlade").setUnlocalizedName("tconstruct.KnifeBlade");
        fullGuard = new ToolPartHidden(PHConstruct.fullGuard, "_full_guard", "FullGuard").setUnlocalizedName("tconstruct.FullGuard");

        frypanHead = new ToolPart(PHConstruct.frypanHead, "_frypan_head", "FrypanHead").setUnlocalizedName("tconstruct.FrypanHead");
        signHead = new ToolPart(PHConstruct.signHead, "_battlesign_head", "SignHead").setUnlocalizedName("tconstruct.SignHead");
        chiselHead = new ToolPart(PHConstruct.chiselHead, "_chisel_head", "ChiselHead").setUnlocalizedName("tconstruct.ChiselHead");

        scytheBlade = new ToolPart(PHConstruct.scytheBlade, "_scythe_head", "ScytheHead").setUnlocalizedName("tconstruct.ScytheBlade");
        broadAxeHead = new ToolPart(PHConstruct.lumberHead, "_lumberaxe_head", "LumberHead").setUnlocalizedName("tconstruct.LumberHead");
        excavatorHead = new ToolPart(PHConstruct.excavatorHead, "_excavator_head", "ExcavatorHead").setUnlocalizedName("tconstruct.ExcavatorHead");
        largeSwordBlade = new ToolPart(PHConstruct.largeSwordBlade, "_large_sword_blade", "LargeSwordBlade").setUnlocalizedName("tconstruct.LargeSwordBlade");
        hammerHead = new ToolPart(PHConstruct.hammerHead, "_hammer_head", "HammerHead").setUnlocalizedName("tconstruct.HammerHead");

        bowstring = new Bowstring(PHConstruct.bowstring).setUnlocalizedName("tconstruct.Bowstring");
        arrowhead = new ToolPart(PHConstruct.arrowhead, "_arrowhead", "ArrowHead").setUnlocalizedName("tconstruct.Arrowhead");
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
        
        jerky = new Jerky(PHConstruct.jerky, Loader.isModLoaded("HungerOverhaul")).setUnlocalizedName("tconstruct.jerky");

        //Wearables
        //heavyHelmet = new TArmorBase(PHConstruct.heavyHelmet, 0).setUnlocalizedName("tconstruct.HeavyHelmet");
        heartCanister = new HeartCanister(PHConstruct.heartCanister).setUnlocalizedName("tconstruct.canister");
        //heavyBoots = new TArmorBase(PHConstruct.heavyBoots, 3).setUnlocalizedName("tconstruct.HeavyBoots");
        //glove = new Glove(PHConstruct.glove).setUnlocalizedName("tconstruct.Glove");
        knapsack = new Knapsack(PHConstruct.knapsack).setUnlocalizedName("tconstruct.storage");

        //Crystalline
        essenceCrystal = new EssenceCrystal(PHConstruct.essenceCrystal).setUnlocalizedName("tconstruct.crystal.essence");
        goldHead = new GoldenHead(PHConstruct.goldHead, 4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F).setUnlocalizedName("goldenhead");

        //        essenceCrystal = new EssenceCrystal(PHConstruct.essenceCrystal).setUnlocalizedName("tconstruct.crystal.essence");

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
        Item.itemsList[Block.cake.blockID].setMaxStackSize(16);
        //Block.torchWood.setTickRandomly(false);
    }

    void registerMaterials()
    {
        TConstructRegistry.addToolMaterial(0, "Wood", "Wooden ", 0, 59, 200, 0, 1.0F, 0, 0f, "\u00A7e", "");
        TConstructRegistry.addToolMaterial(1, "Stone", 1, 131, 400, 1, 0.5F, 0, 1f, "", "Stonebound");
        TConstructRegistry.addToolMaterial(2, "Iron", 2, 250, 600, 2, 1.3F, 1, 0f, "\u00A7f", "");
        TConstructRegistry.addToolMaterial(3, "Flint", 1, 171, 525, 2, 0.7F, 0, 0f, "\u00A78", "");
        TConstructRegistry.addToolMaterial(4, "Cactus", 1, 150, 500, 2, 1.0F, 0, -1f, "\u00A72", "Jagged");
        TConstructRegistry.addToolMaterial(5, "Bone", 1, 200, 400, 1, 1.0F, 0, 0f, "\u00A7e", "");
        TConstructRegistry.addToolMaterial(6, "Obsidian", 3, 89, 700, 2, 0.8F, 3, 0f, "\u00A7d", "");
        TConstructRegistry.addToolMaterial(7, "Netherrack", 2, 131, 400, 1, 1.2F, 0, 1f, "\u00A74", "Stonebound");
        TConstructRegistry.addToolMaterial(8, "Slime", 0, 500, 150, 0, 1.5F, 0, 0f, "\u00A7a", "");
        TConstructRegistry.addToolMaterial(9, "Paper", 0, 30, 200, 0, 0.3F, 0, 0f, "\u00A7f", "Writable");
        TConstructRegistry.addToolMaterial(10, "Cobalt", 4, 800, 1100, 3, 1.75F, 2, 0f, "\u00A73", "");
        TConstructRegistry.addToolMaterial(11, "Ardite", 4, 600, 800, 3, 2.0F, 0, 2f, "\u00A74", "Stonebound");
        TConstructRegistry.addToolMaterial(12, "Manyullyn", 5, 1200, 900, 4, 2.5F, 0, 0f, "\u00A75", "");
        TConstructRegistry.addToolMaterial(13, "Copper", 1, 180, 500, 2, 1.15F, 0, 0f, "\u00A7c", "");
        TConstructRegistry.addToolMaterial(14, "Bronze", 2, 350, 700, 2, 1.3F, 1, 0f, "\u00A76", "");
        TConstructRegistry.addToolMaterial(15, "Alumite", 4, 550, 800, 3, 1.3F, 2, 0f, "\u00A7d", "");
        TConstructRegistry.addToolMaterial(16, "Steel", 4, 750, 800, 3, 1.3F, 2, 0f, "", "");
        TConstructRegistry.addToolMaterial(17, "BlueSlime", "Slime ", 0, 1200, 150, 0, 2.0F, 0, 0f, "\u00A7b", "");

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
        for (int i = 0; i < 4; i++ )
            TConstructRegistry.addFletchingMaterial(1, 2, new ItemStack(Block.leaves, 1, i), new ItemStack(fletching, 1, 1), 75F, 0F, 0.2F); //All four vanialla Leaves
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
    public static FluidStack[] liquids;

    void addCraftingRecipes()
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
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 1), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), 80);
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 2), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), 80);

        //Ingots
        tableCasting.addCastingRecipe(new ItemStack(Item.ingotIron), new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //Iron
        tableCasting.addCastingRecipe(new ItemStack(Item.ingotGold), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //gold
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 9), new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //copper
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 10), new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //tin
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 11), new FluidStack(moltenAluminumFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //aluminum
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 3), new FluidStack(moltenCobaltFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //cobalt
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 4), new FluidStack(moltenArditeFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //ardite
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 13), new FluidStack(moltenBronzeFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //bronze
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 14), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //albrass
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 5), new FluidStack(moltenManyullynFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //manyullyn
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 15), new FluidStack(moltenAlumiteFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //alumite
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 18), new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //obsidian
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 16), new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue), ingotcast, 80); //steel

        //Buckets
        ItemStack bucket = new ItemStack(Item.bucketEmpty);
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 0), new FluidStack(moltenIronFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //Iron
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 1), new FluidStack(moltenGoldFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //gold
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 2), new FluidStack(moltenCopperFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //copper
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 3), new FluidStack(moltenTinFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //tin
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 4), new FluidStack(moltenAluminumFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //aluminum
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 5), new FluidStack(moltenCobaltFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //cobalt
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 6), new FluidStack(moltenArditeFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //ardite
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 7), new FluidStack(moltenBronzeFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //bronze
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 8), new FluidStack(moltenAlubrassFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //alubrass
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 9), new FluidStack(moltenManyullynFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //manyullyn
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 10), new FluidStack(moltenAlumiteFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //alumite
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 11), new FluidStack(moltenObsidianFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);// obsidian
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 12), new FluidStack(moltenSteelFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //steel
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 13), new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //glass
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 14), new FluidStack(moltenStoneFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //seared stone
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 15), new FluidStack(moltenEmeraldFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10); //emerald

        tableCasting.addCastingRecipe(new ItemStack(glassPane), new FluidStack(moltenGlassFluid, 250), null, 80);

        liquids = new FluidStack[] { new FluidStack(moltenIronFluid, 1), new FluidStack(moltenCopperFluid, 1), new FluidStack(moltenCobaltFluid, 1), new FluidStack(moltenArditeFluid, 1),
                new FluidStack(moltenManyullynFluid, 1), new FluidStack(moltenBronzeFluid, 1), new FluidStack(moltenAlumiteFluid, 1), new FluidStack(moltenObsidianFluid, 1),
                new FluidStack(moltenSteelFluid, 1) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16 };

        for (int iter = 0; iter < patternOutputs.length; iter++)
        {
            if (patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
                {
                    ItemStack metalCast = new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(liquids[iterTwo].getFluid(), ((IPattern) metalPattern).getPatternCost(metalCast) * TConstruct.ingotLiquidValue / 2), cast,
                            50);
                }
            }
        }

        ItemStack[] ingotShapes = { new ItemStack(Item.ingotIron), new ItemStack(Item.ingotGold), new ItemStack(Item.brick), new ItemStack(Item.netherrackBrick), new ItemStack(materials, 1, 2) };
        for (int i = 0; i < ingotShapes.length; i++)
        {
            TConstruct.tableCasting.addCastingRecipe(new ItemStack(TContent.metalPattern, 1, 0), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotShapes[i], false, 50);
            TConstruct.tableCasting.addCastingRecipe(new ItemStack(TContent.metalPattern, 1, 0), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), ingotShapes[i], false, 50);
        }

        ItemStack fullguardCast = new ItemStack(metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        basinCasting.addCastingRecipe(new ItemStack(Block.blockIron), new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //Iron
        basinCasting.addCastingRecipe(new ItemStack(Block.blockGold), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //gold
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 3), new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //copper
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 5), new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //tin
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 6), new FluidStack(moltenAluminumFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //aluminum
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 0), new FluidStack(moltenCobaltFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //cobalt
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 1), new FluidStack(moltenArditeFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //ardite
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 4), new FluidStack(moltenBronzeFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //bronze
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 7), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //albrass
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 2), new FluidStack(moltenManyullynFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //manyullyn
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 8), new FluidStack(moltenAlumiteFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //alumite
        basinCasting.addCastingRecipe(new ItemStack(Block.obsidian), new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 2), null, true, 100);// obsidian
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 9), new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue * 9), null, true, 100); //steel
        basinCasting.addCastingRecipe(new ItemStack(clearGlass, 1, 0), new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100); //glass
        basinCasting.addCastingRecipe(new ItemStack(smeltery, 1, 4), new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue), null, true, 100); //seared stone

        basinCasting.addCastingRecipe(new ItemStack(speedBlock, 1, 0), new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue / 9), new ItemStack(Block.gravel), true, 100); //brownstone
        basinCasting.addCastingRecipe(new ItemStack(Block.whiteStone), new FluidStack(moltenEnderFluid, 25), new ItemStack(Block.obsidian), true, 100); //endstone
        basinCasting.addCastingRecipe(new ItemStack(metalBlock.blockID, 1, 10), new FluidStack(moltenEnderFluid, 1000), null, true, 100); //ender

        //Ore
        Smeltery.addMelting(Block.oreIron, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.oreGold, 0, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(oreGravel, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(oreGravel, 1, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));

        //Items
        Smeltery.addMelting(new ItemStack(Item.ingotIron, 4), Block.blockIron.blockID, 0, 500, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Item.ingotGold, 4), Block.blockGold.blockID, 0, 300, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Item.goldNugget, 4), Block.blockGold.blockID, 0, 150, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue / 9));

        Smeltery.addMelting(new ItemStack(materials, 1, 18), Block.obsidian.blockID, 0, 750, new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue)); //Obsidian ingot

        Smeltery.addMelting(new ItemStack(blankPattern, 4, 1), metalBlock.blockID, 7, 150, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(blankPattern, 4, 2), metalBlock.blockID, 7, 150, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));

        Smeltery.addMelting(new ItemStack(Item.enderPearl, 4), metalBlock.blockID, 10, 500, new FluidStack(moltenEnderFluid, 250));
        Smeltery.addMelting(new ItemStack(metalBlock, 1, 10), metalBlock.blockID, 10, 500, new FluidStack(moltenEnderFluid, 1000));

        //Blocks
        Smeltery.addMelting(Block.blockIron, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.blockGold, 0, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.obsidian, 0, 800, new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.ice, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 1000));
        Smeltery.addMelting(Block.sand, 0, 625, new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.glass, 0, 625, new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.stone, 0, 800, new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.cobblestone, 0, 800, new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue / 18));

        Smeltery.addMelting(clearGlass, 0, 500, new FluidStack(moltenGlassFluid, 1000));
        Smeltery.addMelting(glassPane, 0, 350, new FluidStack(moltenGlassFluid, 250));

        for (int i = 0; i < 16; i++)
        {
            Smeltery.addMelting(stainedGlassClear, i, 500, new FluidStack(moltenGlassFluid, 1000));
            Smeltery.addMelting(stainedGlassClearPane, i, 350, new FluidStack(moltenGlassFluid, 250));
        }

        //Alloys
        if (PHConstruct.harderBronze)
            Smeltery.addAlloyMixing(new FluidStack(moltenBronzeFluid, 16), new FluidStack(moltenCopperFluid, 24), new FluidStack(moltenTinFluid, 8)); //Bronze
        else
            Smeltery.addAlloyMixing(new FluidStack(moltenBronzeFluid, 24), new FluidStack(moltenCopperFluid, 24), new FluidStack(moltenTinFluid, 8)); //Bronze
        Smeltery.addAlloyMixing(new FluidStack(moltenAlubrassFluid, 16), new FluidStack(moltenAluminumFluid, 24), new FluidStack(moltenCopperFluid, 8)); //Aluminum Brass
        Smeltery.addAlloyMixing(new FluidStack(moltenManyullynFluid, 16), new FluidStack(moltenCobaltFluid, 32), new FluidStack(moltenArditeFluid, 32)); //Manyullyn
        Smeltery.addAlloyMixing(new FluidStack(moltenAlumiteFluid, 48), new FluidStack(moltenAluminumFluid, 80), new FluidStack(moltenIronFluid, 32), new FluidStack(moltenObsidianFluid, 32)); //Alumite

        //Oreberries
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 0), Block.blockIron.blockID, 0, 100, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue / 9)); //Iron
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 1), Block.blockGold.blockID, 0, 100, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue / 9)); //Gold
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 2), metalBlock.blockID, 3, 100, new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue / 9)); //Copper
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 3), metalBlock.blockID, 5, 100, new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue / 9)); //Tin
        Smeltery.addMelting(new ItemStack(oreBerries, 4, 4), metalBlock.blockID, 6, 100, new FluidStack(moltenAluminumFluid, TConstruct.ingotLiquidValue / 9)); //Aluminum

        //Vanilla Armor
        Smeltery.addMelting(new ItemStack(Item.helmetIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 5));
        Smeltery.addMelting(new ItemStack(Item.plateIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 8));
        Smeltery.addMelting(new ItemStack(Item.legsIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 7));
        Smeltery.addMelting(new ItemStack(Item.bootsIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 4));

        Smeltery.addMelting(new ItemStack(Item.helmetGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 5));
        Smeltery.addMelting(new ItemStack(Item.plateGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 8));
        Smeltery.addMelting(new ItemStack(Item.legsGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 7));
        Smeltery.addMelting(new ItemStack(Item.bootsGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 4));

        Smeltery.addMelting(new ItemStack(Item.helmetChain, 1, 0), this.metalBlock.blockID, 9, 700, new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Item.plateChain, 1, 0), this.metalBlock.blockID, 9, 700, new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Item.legsChain, 1, 0), this.metalBlock.blockID, 9, 700, new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Item.bootsChain, 1, 0), this.metalBlock.blockID, 9, 700, new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue));

        //Vanilla tools
        Smeltery.addMelting(new ItemStack(Item.hoeIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Item.swordIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Item.shovelIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 1));
        Smeltery.addMelting(new ItemStack(Item.pickaxeIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 3));
        Smeltery.addMelting(new ItemStack(Item.axeIron, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 3));

        Smeltery.addMelting(new ItemStack(Item.hoeGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Item.swordGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Item.shovelGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 1));
        Smeltery.addMelting(new ItemStack(Item.pickaxeGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 3));
        Smeltery.addMelting(new ItemStack(Item.axeGold, 1, 0), Block.blockGold.blockID, 0, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 3));

        //Vanilla items
        Smeltery.addMelting(new ItemStack(Item.flintAndSteel, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Item.compass, 1, 0), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 4));

        //Vanilla blocks
        Smeltery.addMelting(new ItemStack(Item.bucketEmpty), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 3));
        Smeltery.addMelting(new ItemStack(Item.minecartEmpty), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 5));
        Smeltery.addMelting(new ItemStack(Item.doorIron), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 6));
        Smeltery.addMelting(new ItemStack(Block.fenceIron), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 6 / 16));
        Smeltery.addMelting(new ItemStack(Block.pressurePlateIron), Block.blockIron.blockID, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Block.pressurePlateGold, 4), Block.blockGold.blockID, 0, 600, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(new ItemStack(Block.rail), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 6 / 16));
        Smeltery.addMelting(new ItemStack(Block.railPowered), Block.blockGold.blockID, 8, 350, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Block.railDetector), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Block.railActivator), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(Block.enchantmentTable), Block.obsidian.blockID, 0, 750, new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 4));
        Smeltery.addMelting(new ItemStack(Block.cauldron), Block.blockIron.blockID, 8, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 7));
        Smeltery.addMelting(new ItemStack(Block.anvil, 1, 0), Block.blockIron.blockID, 8, 800, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 31));
        Smeltery.addMelting(new ItemStack(Block.anvil, 1, 1), Block.blockIron.blockID, 8, 800, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 31));
        Smeltery.addMelting(new ItemStack(Block.anvil, 1, 2), Block.blockIron.blockID, 8, 800, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 31));

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

        GameRegistry.addShapelessRecipe(new ItemStack(Item.book), Item.paper, Item.paper, Item.paper, Item.silk, blankPattern, blankPattern);//Vanilla books

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
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClear, 8, i), pattern, 'm', dyeTypes[15 - i], '#', clearGlass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClear, 1, i), dyeTypes[15 - i], clearGlass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClearPane, 8, i), pattern, 'm', dyeTypes[15 - i], '#', glassPane));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClearPane, 1, i), dyeTypes[15 - i], glassPane));
        }

        //Glass
        GameRegistry.addRecipe(new ItemStack(Item.glassBottle, 3), new Object[] { "# #", " # ", '#', clearGlass });
        GameRegistry.addRecipe(new ItemStack(Block.daylightSensor), new Object[] { "GGG", "QQQ", "WWW", 'G', Block.glass, 'Q', Item.netherQuartz, 'W', Block.woodSingleSlab });
        GameRegistry.addRecipe(new ItemStack(Block.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', clearGlass, 'S', Item.netherStar, 'O', Block.obsidian });

        //Smeltery
        ItemStack searedBrick = new ItemStack(materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks

        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 0), "bbb", "bgb", "bbb", 'b', searedBrick, 'g', Block.glass); //Tank
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', Block.glass); //Glass
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', Block.glass); //Window

        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 0), "bbb", "bgb", "bbb", 'b', searedBrick, 'g', clearGlass); //Tank
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', clearGlass); //Glass
        GameRegistry.addRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', clearGlass); //Window

        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin

        GameRegistry.addRecipe(new ItemStack(castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); //Channel

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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dryingRack, 1, 0), "bbb", 'b', "slabWood"));

        //Landmine
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 0), "mcm", "rpr", 'm', "plankWood", 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 1), "mcm", "rpr", 'm', Block.stone, 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 2), "mcm", "rpr", 'm', Block.obsidian, 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 3), "mcm", "rpr", 'm', Item.redstoneRepeater, 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
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
            tableCasting.addCastingRecipe(new ItemStack(Item.appleGold, 1), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 8), new ItemStack(Item.appleRed), true, 50);
        }
        else
        {
            GameRegistry.addRecipe(new ItemStack(goldHead), surround, '#', new ItemStack(Item.goldNugget), 'm', new ItemStack(Item.skull, 1, 3));
            GameRegistry.addRecipe(new ItemStack(Item.appleGold), surround, '#', new ItemStack(oreBerries, 1, 1), 'm', new ItemStack(Item.appleRed));
            GameRegistry.addRecipe(new ItemStack(Item.goldenCarrot), surround, '#', new ItemStack(oreBerries, 1, 1), 'm', new ItemStack(Item.appleRed));
            GameRegistry.addRecipe(new ItemStack(goldHead), surround, '#', new ItemStack(oreBerries, 1, 1), 'm', new ItemStack(Item.appleRed));
            tableCasting.addCastingRecipe(new ItemStack(Item.appleGold, 1), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue / 9 * 8), new ItemStack(Item.appleRed), true, 50);
        }
        tableCasting.addCastingRecipe(new ItemStack(Item.appleGold, 1, 1), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 8 * 9), new ItemStack(Item.appleRed), true, 50);

        //Drying rack
        DryingRackRecipes.addDryingRecipe(Item.beefRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 0));
        DryingRackRecipes.addDryingRecipe(Item.chickenRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 1));
        DryingRackRecipes.addDryingRecipe(Item.porkRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 2));
        //DryingRackRecipes.addDryingRecipe(Item.muttonRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 3));
        DryingRackRecipes.addDryingRecipe(Item.fishRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 4));
        DryingRackRecipes.addDryingRecipe(Item.rottenFlesh, 20 * 60 * 5, new ItemStack(jerky, 1, 5));
        
        //DryingRackRecipes.addDryingRecipe(new ItemStack(jerky, 1, 5), 20 * 60 * 10, Item.leather);

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

        //Slab crafters
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 6, 0), "bbb", 'b', new ItemStack(Block.workbench));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 1), "b", 'b', new ItemStack(toolStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 4));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 4), "b", 'b', new ItemStack(toolStationWood, 1, 5));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 10));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 11));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 12));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 13));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 5), "b", 'b', new ItemStack(toolForge, 1, Short.MAX_VALUE));

        GameRegistry.addRecipe(new ItemStack(essenceExtractor, 1, 0), " b ", "eme", "mmm", 'b', Item.book, 'e', Item.emerald, 'm', Block.whiteStone);

        //Slime
        GameRegistry.addRecipe(new ItemStack(slimeGel, 1, 0), "##", "##", '#', strangeFood);
        GameRegistry.addRecipe(new ItemStack(strangeFood, 4, 0), "#", '#', new ItemStack(slimeGel, 1, 0));
        GameRegistry.addRecipe(new ItemStack(slimeGel, 1, 1), "##", "##", '#', Item.slimeBall);
        GameRegistry.addRecipe(new ItemStack(Item.slimeBall, 4, 0), "#", '#', new ItemStack(slimeGel, 1, 1));
        
        GameRegistry.addShapelessRecipe(new ItemStack(slimeChannel, 1, 0), new ItemStack(slimeGel, 1, Short.MAX_VALUE), new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimePad, 1, 0), slimeChannel, new ItemStack(slimeGel, 1, Short.MAX_VALUE), "slimeBall"));

        //Slab crafters
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 6, 0), "bbb", 'b', new ItemStack(Block.workbench));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 1), "b", 'b', new ItemStack(toolStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 2), "b", 'b', new ItemStack(toolStationWood, 1, 4));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 4), "b", 'b', new ItemStack(toolStationWood, 1, 5));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 10));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 11));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 12));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 3), "b", 'b', new ItemStack(toolStationWood, 1, 13));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 5), "b", 'b', new ItemStack(toolForge, 1, Short.MAX_VALUE));
    }

    void setupToolTabs()
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

    public void addLoot()
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

    public static String[] liquidNames;

    public void oreRegistry()
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

    public void intermodCommunication()
    {
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 15));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerrySecond, 1, 13));
        MystImcHandler.blacklistFluids(); 
        /* FORESTRY
         * Edit these strings to change what items are added to the backpacks
         * Format info: "[backpack ID]@[item ID].[metadata or *]:[next item]" and so on
         * Avaliable backpack IDs: forester, miner, digger, hunter, adventurer, builder
         * May add more backpack items later - Spyboticsguy 
         */

        String builderItems = "builder@" + String.valueOf(stoneTorch.blockID) + ":*";

        FMLInterModComms.sendMessage("Forestry", "add-backpack-items", builderItems);
    }

    private static boolean initRecipes;

    public static void modRecipes()
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
        }
    }

    public static void addShapedRecipeFirst(List recipeList, ItemStack itemstack, Object... objArray)
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

    public void modIntegration()
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

        if (Loader.isModLoaded("Natura"))
        {
            try
            {
                Object plantItem = getStaticItem("plantItem", "mods.natura.common.NContent");
                TConstructRegistry.addBowstringMaterial(2, 2, new ItemStack((Item) plantItem, 1, 7), new ItemStack(bowstring, 1, 2), 1.2F, 0.8F, 1.3f);
            }
            catch (Exception e)
            {
            } //No need to handle
        }

        ItemStack ingotcast = new ItemStack(metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();

        /* Thermal Expansion */
        ArrayList<ItemStack> ores = OreDictionary.getOres("ingotNickel");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new FluidStack(moltenNickelFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotLead");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new FluidStack(moltenLeadFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotSilver");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new FluidStack(moltenSilverFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotPlatinum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new FluidStack(moltenShinyFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotInvar");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new FluidStack(moltenInvarFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(moltenInvarFluid, 24), new FluidStack(moltenIronFluid, 16), new FluidStack(moltenNickelFluid, 8)); //Invar
        }
        ores = OreDictionary.getOres("ingotElectrum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            tableCasting.addCastingRecipe(ingot, new FluidStack(moltenElectrumFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(moltenElectrumFluid, 16), new FluidStack(moltenGoldFluid, 8), new FluidStack(moltenSilverFluid, 8)); //Electrum
        }

        ores = OreDictionary.getOres("blockNickel");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new FluidStack(moltenNickelFluid, TConstruct.ingotLiquidValue * 9), null, 100);
        }
        ores = OreDictionary.getOres("blockLead");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new FluidStack(moltenLeadFluid, TConstruct.ingotLiquidValue * 9), null, 100);
        }
        ores = OreDictionary.getOres("blockSilver");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new FluidStack(moltenSilverFluid, TConstruct.ingotLiquidValue * 9), null, 100);
        }
        ores = OreDictionary.getOres("blockPlatinum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new FluidStack(moltenShinyFluid, TConstruct.ingotLiquidValue * 9), null, 100);
        }
        ores = OreDictionary.getOres("blockInvar");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new FluidStack(moltenInvarFluid, TConstruct.ingotLiquidValue * 9), null, 100);
        }
        ores = OreDictionary.getOres("blockElectrum");
        if (ores.size() > 0)
        {
            ItemStack ingot = ores.get(0);
            basinCasting.addCastingRecipe(ingot, new FluidStack(moltenElectrumFluid, TConstruct.ingotLiquidValue * 9), null, 100);
        }
    }

    public static Object getStaticItem(String name, String classPackage)
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
    public int getBurnTime(ItemStack fuel)
    {
        if (fuel.itemID == materials.itemID && fuel.getItemDamage() == 7)
            return 26400;
        return 0;
    }
}
