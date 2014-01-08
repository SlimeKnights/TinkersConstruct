package tconstruct.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.crafting.ToolBuilder;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.*;
import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.oredict.*;
import tconstruct.TConstruct;
import tconstruct.achievements.TAchievements;
import tconstruct.blocks.*;
import tconstruct.blocks.logic.*;
import tconstruct.blocks.slime.*;
import tconstruct.blocks.traps.*;
import tconstruct.client.StepSoundSlime;
import tconstruct.entity.*;
import tconstruct.entity.item.*;
import tconstruct.entity.projectile.*;
import tconstruct.items.*;
import tconstruct.items.armor.*;
import tconstruct.items.blocks.*;
import tconstruct.items.tools.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.*;
import tconstruct.library.client.FluidRenderProperties.Applications;
import tconstruct.library.crafting.*;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.IPattern;
import tconstruct.modifiers.tools.*;
import tconstruct.modifiers.armor.*;
import tconstruct.util.*;
import tconstruct.util.config.*;

public class TContent implements IFuelHandler
{
    // Supresses console spam when iguana's tweaks remove stuff
    public static boolean supressMissingToolLogs = PHConstruct.forceToolLogsOff;

    //Patterns and other materials
    public static Item blankPattern;
    public static Item materials;
    public static Item toolRod;
    public static Item toolShard;
    public static Item woodPattern;
    public static Item metalPattern;
    //public static Item armorPattern;

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
    public static Block furnaceSlab;

    public static Block heldItemBlock;
    public static Block craftedSoil;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block smelteryNether;
    public static Block lavaTankNether;
    public static Block searedBlockNether;
    public static Block metalBlock;
    public static Block tankAir;

    public static Block dryingRack;

    //Decoration
    public static Block stoneTorch;
    public static Block stoneLadder;
    public static Block multiBrick;
    public static Block multiBrickFancy;

    public static Block searedSlab;
    public static Block speedSlab;

    public static Block meatBlock;
    public static Block woolSlab1;
    public static Block woolSlab2;
    public static Block glueBlock;

    //Traps
    public static Block landmine;
    public static Block punji;
    public static Block barricadeOak;
    public static Block barricadeSpruce;
    public static Block barricadeBirch;
    public static Block barricadeJungle;
    public static Block slimeExplosive;

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
    public static Fluid pigIronFluid;

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
    public static Block bloodChannel;

    //Glue
    public static Fluid glueFluid;
    public static Block glueFluidBlock;

    //Ores
    public static Block oreSlag;
    public static Block oreGravel;
    public static OreberryBush oreBerry;
    public static OreberryBush oreBerrySecond;
    public static Item oreBerries;

    //Tool modifiers
    public static ModFlux modFlux;
    public static ModLapis modLapis;
    public static ModAttack modAttack;

    //Wearables
    public static Item glove;
    public static Item knapsack;

    public static Item heartCanister;
    public static Item goldHead;

    //Rail-related
    public static Block woodenRail;

    //Chest hooks
    public static ChestGenHooks tinkerHouseChest;
    public static ChestGenHooks tinkerHousePatterns;

    //Armor - basic
    public static Item helmetWood;
    public static Item chestplateWood;
    public static Item leggingsWood;
    public static Item bootsWood;
    public static EnumArmorMaterial materialWood;

    //Armor - exosuit
    public static Item exoGoggles;
    public static Item exoChest;
    public static Item exoPants;
    public static Item exoShoes;

    //Temporary items
    //public static Item armorTest = new ArmorStandard(2445, 4, EnumArmorPart.HELMET).setCreativeTab(CreativeTabs.tabAllSearch);

    public TContent()
    {
        registerItems();
        registerBlocks();
        registerMaterials();
        addCraftingRecipes();
        setupToolTabs();
        addLoot();
        if (PHConstruct.achievementsEnabled)
        {
            addAchievements();
        }
    }

    public void createEntities ()
    {
        EntityRegistry.registerModEntity(FancyEntityItem.class, "Fancy Item", 0, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(DaggerEntity.class, "Dagger", 1, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(Crystal.class, "Crystal", 2, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(LaunchedPotion.class, "Launched Potion", 3, TConstruct.instance, 32, 3, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 4, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(EntityLandmineFirework.class, "LandmineFirework", 5, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ExplosivePrimed.class, "SlimeExplosive", 6, TConstruct.instance, 32, 5, true);
        //EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1, TConstruct.instance, 32, 5, true);

        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        //EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13, TConstruct.instance, 64, 5, true);
    }

    public static Fluid[] fluids = new Fluid[27];
    public static Block[] fluidBlocks = new Block[26];

    void registerBlocks ()
    {
        //Tool Station
        toolStationWood = new ToolStationBlock(PHConstruct.woodStation, Material.wood).setUnlocalizedName("ToolStation");
        GameRegistry.registerBlock(toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartBuilderLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");

        toolForge = new ToolForgeBlock(PHConstruct.toolForge, Material.iron).setUnlocalizedName("ToolForge");
        GameRegistry.registerBlock(toolForge, MetadataItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");

        craftingStationWood = new CraftingStationBlock(PHConstruct.woodCrafter, Material.wood).setUnlocalizedName("CraftingStation");
        GameRegistry.registerBlock(craftingStationWood, "CraftingStation");
        GameRegistry.registerTileEntity(CraftingStationLogic.class, "CraftingStation");

        craftingSlabWood = new CraftingSlab(PHConstruct.woodCrafterSlab, Material.wood).setUnlocalizedName("CraftingSlab");
        GameRegistry.registerBlock(craftingSlabWood, CraftingSlabItemBlock.class, "CraftingSlab");

        furnaceSlab = new FurnaceSlab(PHConstruct.furnaceSlab, Material.rock).setUnlocalizedName("FurnaceSlab");
        GameRegistry.registerBlock(furnaceSlab, "FurnaceSlab");
        GameRegistry.registerTileEntity(FurnaceLogic.class, "TConstruct.Furnace");

        heldItemBlock = new EquipBlock(PHConstruct.heldItemBlock, Material.wood).setUnlocalizedName("Frypan");
        GameRegistry.registerBlock(heldItemBlock, "HeldItemBlock");
        GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");

        craftedSoil = new SoilBlock(PHConstruct.craftedSoil).setLightOpacity(0).setUnlocalizedName("TConstruct.Soil");
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
        GameRegistry.registerBlock(meatBlock, HamboneItemBlock.class, "MeatBlock");

        OreDictionary.registerOre("hambone", new ItemStack(meatBlock));
        LanguageRegistry.addName(meatBlock, "Hambone");
        GameRegistry.addRecipe(new ItemStack(meatBlock), "mmm", "mbm", "mmm", 'b', new ItemStack(Item.bone), 'm', new ItemStack(Item.porkRaw));

        glueBlock = new GlueBlock(PHConstruct.glueBlock).setUnlocalizedName("GlueBlock").setCreativeTab(TConstructRegistry.blockTab);
        GameRegistry.registerBlock(glueBlock, "GlueBlock");
        OreDictionary.registerOre("blockRubber", new ItemStack(glueBlock));

        woolSlab1 = new SlabBase(PHConstruct.woolSlab1, Material.cloth, Block.cloth, 0, 8).setUnlocalizedName("cloth");
        woolSlab1.setStepSound(Block.soundClothFootstep).setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(woolSlab1, WoolSlab1Item.class, "WoolSlab1");
        woolSlab2 = new SlabBase(PHConstruct.woolSlab2, Material.cloth, Block.cloth, 8, 8).setUnlocalizedName("cloth");
        woolSlab2.setStepSound(Block.soundClothFootstep).setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(woolSlab2, WoolSlab2Item.class, "WoolSlab2");

        //Smeltery
        smeltery = new SmelteryBlock(PHConstruct.smeltery).setUnlocalizedName("Smeltery");
        smelteryNether = new SmelteryBlock(PHConstruct.smelteryNether, "nether").setUnlocalizedName("Smeltery");
        GameRegistry.registerBlock(smeltery, SmelteryItemBlock.class, "Smeltery");
        GameRegistry.registerBlock(smelteryNether, SmelteryItemBlock.class, "SmelteryNether");
        if (PHConstruct.newSmeltery)
        {
            GameRegistry.registerTileEntity(AdaptiveSmelteryLogic.class, "TConstruct.Smeltery");
            GameRegistry.registerTileEntity(AdaptiveDrainLogic.class, "TConstruct.SmelteryDrain");
        }
        else
        {
            GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
            GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");
        }
        GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");

        lavaTank = new LavaTankBlock(PHConstruct.lavaTank).setStepSound(Block.soundGlassFootstep).setUnlocalizedName("LavaTank");
        lavaTankNether = new LavaTankBlock(PHConstruct.lavaTankNether, "nether").setStepSound(Block.soundGlassFootstep).setUnlocalizedName("LavaTank");
        GameRegistry.registerBlock(lavaTank, LavaTankItemBlock.class, "LavaTank");
        GameRegistry.registerBlock(lavaTankNether, LavaTankItemBlock.class, "LavaTankNether");
        GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

        searedBlock = new SearedBlock(PHConstruct.searedTable).setUnlocalizedName("SearedBlock");
        searedBlockNether = new SearedBlock(PHConstruct.searedTableNether, "nether").setUnlocalizedName("SearedBlock");
        GameRegistry.registerBlock(searedBlock, SearedTableItemBlock.class, "SearedBlock");
        GameRegistry.registerBlock(searedBlockNether, SearedTableItemBlock.class, "SearedBlockNether");
        GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "CastingBasin");

        castingChannel = (new CastingChannelBlock(PHConstruct.castingChannel)).setUnlocalizedName("CastingChannel");
        GameRegistry.registerBlock(castingChannel, CastingChannelItem.class, "CastingChannel");
        GameRegistry.registerTileEntity(CastingChannelLogic.class, "CastingChannel");

        tankAir = new TankAirBlock(PHConstruct.airTank, Material.leaves).setBlockUnbreakable().setUnlocalizedName("tconstruct.tank.air");
        GameRegistry.registerBlock(tankAir, "TankAir");
        GameRegistry.registerTileEntity(TankAirLogic.class, "tconstruct.tank.air");

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

        slimeExplosive = new SlimeExplosive(PHConstruct.slimeExplosive).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("explosive.slime");
        GameRegistry.registerBlock(slimeExplosive, MetadataItemBlock.class, "explosive.slime");

        dryingRack = new DryingRack(PHConstruct.dryingRack).setUnlocalizedName("Armor.DryingRack");
        GameRegistry.registerBlock(dryingRack, "Armor.DryingRack");
        GameRegistry.registerTileEntity(DryingRackLogic.class, "Armor.DryingRack");

        //Liquids
        liquidMetal = new MaterialLiquid(MapColor.tntColor);

        moltenIronFluid = new Fluid("iron.molten");
        if (!FluidRegistry.registerFluid(moltenIronFluid))
            moltenIronFluid = FluidRegistry.getFluid("iron.molten");
        moltenIron = new TConstructFluid(PHConstruct.moltenIron, moltenIronFluid, Material.lava, "liquid_iron").setUnlocalizedName("metal.molten.iron");
        GameRegistry.registerBlock(moltenIron, "metal.molten.iron");
        fluids[0] = moltenIronFluid;
        fluidBlocks[0] = moltenIron;
        moltenIronFluid.setBlockID(moltenIron).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenIronFluid, 1000), new ItemStack(buckets, 1, 0), new ItemStack(Item.bucketEmpty)));

        moltenGoldFluid = new Fluid("gold.molten");
        if (!FluidRegistry.registerFluid(moltenGoldFluid))
            moltenGoldFluid = FluidRegistry.getFluid("gold.molten");
        moltenGold = new TConstructFluid(PHConstruct.moltenGold, moltenGoldFluid, Material.lava, "liquid_gold").setUnlocalizedName("metal.molten.gold");
        GameRegistry.registerBlock(moltenGold, "metal.molten.gold");
        fluids[1] = moltenGoldFluid;
        fluidBlocks[1] = moltenGold;
        moltenGoldFluid.setBlockID(moltenGold).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenGoldFluid, 1000), new ItemStack(buckets, 1, 1), new ItemStack(Item.bucketEmpty)));

        moltenCopperFluid = new Fluid("copper.molten");
        if (!FluidRegistry.registerFluid(moltenCopperFluid))
            moltenCopperFluid = FluidRegistry.getFluid("copper.molten");
        moltenCopper = new TConstructFluid(PHConstruct.moltenCopper, moltenCopperFluid, Material.lava, "liquid_copper").setUnlocalizedName("metal.molten.copper");
        GameRegistry.registerBlock(moltenCopper, "metal.molten.copper");
        fluids[2] = moltenCopperFluid;
        fluidBlocks[2] = moltenCopper;
        moltenCopperFluid.setBlockID(moltenCopper).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenCopperFluid, 1000), new ItemStack(buckets, 1, 2), new ItemStack(Item.bucketEmpty)));

        moltenTinFluid = new Fluid("tin.molten");
        if (!FluidRegistry.registerFluid(moltenTinFluid))
            moltenTinFluid = FluidRegistry.getFluid("tin.molten");
        moltenTin = new TConstructFluid(PHConstruct.moltenTin, moltenTinFluid, Material.lava, "liquid_tin").setUnlocalizedName("metal.molten.tin");
        GameRegistry.registerBlock(moltenTin, "metal.molten.tin");
        fluids[3] = moltenTinFluid;
        fluidBlocks[3] = moltenTin;
        moltenTinFluid.setBlockID(moltenTin).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenTinFluid, 1000), new ItemStack(buckets, 1, 3), new ItemStack(Item.bucketEmpty)));

        moltenAluminumFluid = new Fluid("aluminum.molten");
        if (!FluidRegistry.registerFluid(moltenAluminumFluid))
            moltenAluminumFluid = FluidRegistry.getFluid("aluminum.molten");
        moltenAluminum = new TConstructFluid(PHConstruct.moltenAluminum, moltenAluminumFluid, Material.lava, "liquid_aluminum").setUnlocalizedName("metal.molten.aluminum");
        GameRegistry.registerBlock(moltenAluminum, "metal.molten.aluminum");
        fluids[4] = moltenAluminumFluid;
        fluidBlocks[4] = moltenAluminum;
        moltenAluminumFluid.setBlockID(moltenAluminum).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenAluminumFluid, 1000), new ItemStack(buckets, 1, 4), new ItemStack(Item.bucketEmpty)));

        moltenCobaltFluid = new Fluid("cobalt.molten");
        if (!FluidRegistry.registerFluid(moltenCobaltFluid))
            moltenCobaltFluid = FluidRegistry.getFluid("cobalt.molten");
        moltenCobalt = new TConstructFluid(PHConstruct.moltenCobalt, moltenCobaltFluid, Material.lava, "liquid_cobalt").setUnlocalizedName("metal.molten.cobalt");
        GameRegistry.registerBlock(moltenCobalt, "metal.molten.cobalt");
        fluids[5] = moltenCobaltFluid;
        fluidBlocks[5] = moltenCobalt;
        moltenCobaltFluid.setBlockID(moltenCobalt).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenCobaltFluid, 1000), new ItemStack(buckets, 1, 5), new ItemStack(Item.bucketEmpty)));

        moltenArditeFluid = new Fluid("ardite.molten");
        if (!FluidRegistry.registerFluid(moltenArditeFluid))
            moltenArditeFluid = FluidRegistry.getFluid("ardite.molten");
        moltenArdite = new TConstructFluid(PHConstruct.moltenArdite, moltenArditeFluid, Material.lava, "liquid_ardite").setUnlocalizedName("metal.molten.ardite");
        GameRegistry.registerBlock(moltenArdite, "metal.molten.ardite");
        fluids[6] = moltenArditeFluid;
        fluidBlocks[6] = moltenArdite;
        moltenArditeFluid.setBlockID(moltenArdite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenArditeFluid, 1000), new ItemStack(buckets, 1, 6), new ItemStack(Item.bucketEmpty)));

        moltenBronzeFluid = new Fluid("bronze.molten");
        if (!FluidRegistry.registerFluid(moltenBronzeFluid))
            moltenBronzeFluid = FluidRegistry.getFluid("bronze.molten");
        moltenBronze = new TConstructFluid(PHConstruct.moltenBronze, moltenBronzeFluid, Material.lava, "liquid_bronze").setUnlocalizedName("metal.molten.bronze");
        GameRegistry.registerBlock(moltenBronze, "metal.molten.bronze");
        fluids[7] = moltenBronzeFluid;
        fluidBlocks[7] = moltenBronze;
        moltenBronzeFluid.setBlockID(moltenBronze).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenBronzeFluid, 1000), new ItemStack(buckets, 1, 7), new ItemStack(Item.bucketEmpty)));

        moltenAlubrassFluid = new Fluid("aluminumbrass.molten");
        if (!FluidRegistry.registerFluid(moltenAlubrassFluid))
            moltenAlubrassFluid = FluidRegistry.getFluid("aluminumbrass.molten");
        moltenAlubrass = new TConstructFluid(PHConstruct.moltenAlubrass, moltenAlubrassFluid, Material.lava, "liquid_alubrass").setUnlocalizedName("metal.molten.alubrass");
        GameRegistry.registerBlock(moltenAlubrass, "metal.molten.alubrass");
        fluids[8] = moltenAlubrassFluid;
        fluidBlocks[8] = moltenAlubrass;
        moltenAlubrassFluid.setBlockID(moltenAlubrass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenAlubrassFluid, 1000), new ItemStack(buckets, 1, 8), new ItemStack(Item.bucketEmpty)));

        moltenManyullynFluid = new Fluid("manyullyn.molten");
        if (!FluidRegistry.registerFluid(moltenManyullynFluid))
            moltenManyullynFluid = FluidRegistry.getFluid("manyullyn.molten");
        moltenManyullyn = new TConstructFluid(PHConstruct.moltenManyullyn, moltenManyullynFluid, Material.lava, "liquid_manyullyn").setUnlocalizedName("metal.molten.manyullyn");
        GameRegistry.registerBlock(moltenManyullyn, "metal.molten.manyullyn");
        fluids[9] = moltenManyullynFluid;
        fluidBlocks[9] = moltenManyullyn;
        moltenManyullynFluid.setBlockID(moltenManyullyn).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenManyullynFluid, 1000), new ItemStack(buckets, 1, 9), new ItemStack(Item.bucketEmpty)));

        moltenAlumiteFluid = new Fluid("alumite.molten");
        if (!FluidRegistry.registerFluid(moltenAlumiteFluid))
            moltenAlumiteFluid = FluidRegistry.getFluid("alumite.molten");
        moltenAlumite = new TConstructFluid(PHConstruct.moltenAlumite, moltenAlumiteFluid, Material.lava, "liquid_alumite").setUnlocalizedName("metal.molten.alumite");
        GameRegistry.registerBlock(moltenAlumite, "metal.molten.alumite");
        fluids[10] = moltenAlumiteFluid;
        fluidBlocks[10] = moltenAlumite;
        moltenAlumiteFluid.setBlockID(moltenAlumite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenAlumiteFluid, 1000), new ItemStack(buckets, 1, 10), new ItemStack(Item.bucketEmpty)));

        moltenObsidianFluid = new Fluid("obsidian.molten");
        if (!FluidRegistry.registerFluid(moltenObsidianFluid))
            moltenObsidianFluid = FluidRegistry.getFluid("obsidian.molten");
        moltenObsidian = new TConstructFluid(PHConstruct.moltenObsidian, moltenObsidianFluid, Material.lava, "liquid_obsidian").setUnlocalizedName("metal.molten.obsidian");
        GameRegistry.registerBlock(moltenObsidian, "metal.molten.obsidian");
        fluids[11] = moltenObsidianFluid;
        fluidBlocks[11] = moltenObsidian;
        moltenObsidianFluid.setBlockID(moltenObsidian).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenObsidianFluid, 1000), new ItemStack(buckets, 1, 11), new ItemStack(Item.bucketEmpty)));

        moltenSteelFluid = new Fluid("steel.molten");
        if (!FluidRegistry.registerFluid(moltenSteelFluid))
            moltenSteelFluid = FluidRegistry.getFluid("steel.molten");
        moltenSteel = new TConstructFluid(PHConstruct.moltenSteel, moltenSteelFluid, Material.lava, "liquid_steel").setUnlocalizedName("metal.molten.steel");
        GameRegistry.registerBlock(moltenSteel, "metal.molten.steel");
        fluids[12] = moltenSteelFluid;
        fluidBlocks[12] = moltenSteel;
        moltenSteelFluid.setBlockID(moltenSteel).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenSteelFluid, 1000), new ItemStack(buckets, 1, 12), new ItemStack(Item.bucketEmpty)));

        moltenGlassFluid = new Fluid("glass.molten");
        if (!FluidRegistry.registerFluid(moltenGlassFluid))
            moltenGlassFluid = FluidRegistry.getFluid("glass.molten");
        moltenGlass = new TConstructFluid(PHConstruct.moltenGlass, moltenGlassFluid, Material.lava, "liquid_glass", true).setUnlocalizedName("metal.molten.glass");
        GameRegistry.registerBlock(moltenGlass, "metal.molten.glass");
        fluids[13] = moltenGlassFluid;
        fluidBlocks[13] = moltenGlass;
        moltenGlassFluid.setBlockID(moltenGlass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenGlassFluid, 1000), new ItemStack(buckets, 1, 13), new ItemStack(Item.bucketEmpty)));

        moltenStoneFluid = new Fluid("stone.seared");
        if (!FluidRegistry.registerFluid(moltenStoneFluid))
            moltenStoneFluid = FluidRegistry.getFluid("stone.seared");
        moltenStone = new TConstructFluid(PHConstruct.moltenStone, moltenStoneFluid, Material.lava, "liquid_stone").setUnlocalizedName("molten.stone");
        GameRegistry.registerBlock(moltenStone, "molten.stone");
        fluids[14] = moltenStoneFluid;
        fluidBlocks[14] = moltenStone;
        moltenStoneFluid.setBlockID(moltenStone).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenStoneFluid, 1000), new ItemStack(buckets, 1, 14), new ItemStack(Item.bucketEmpty)));

        moltenEmeraldFluid = new Fluid("emerald.liquid");
        if (!FluidRegistry.registerFluid(moltenEmeraldFluid))
            moltenEmeraldFluid = FluidRegistry.getFluid("emerald.liquid");
        moltenEmerald = new TConstructFluid(PHConstruct.moltenEmerald, moltenEmeraldFluid, Material.water, "liquid_villager").setUnlocalizedName("molten.emerald");
        GameRegistry.registerBlock(moltenEmerald, "molten.emerald");
        fluids[15] = moltenEmeraldFluid;
        fluidBlocks[15] = moltenEmerald;
        moltenEmeraldFluid.setBlockID(moltenEmerald).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenEmeraldFluid, 1000), new ItemStack(buckets, 1, 15), new ItemStack(Item.bucketEmpty)));

        bloodFluid = new Fluid("blood");
        if (!FluidRegistry.registerFluid(bloodFluid))
            bloodFluid = FluidRegistry.getFluid("blood");
        blood = new BloodBlock(PHConstruct.blood, bloodFluid, Material.water, "liquid_cow").setUnlocalizedName("liquid.blood");
        GameRegistry.registerBlock(blood, "liquid.blood");
        fluids[16] = bloodFluid;
        fluidBlocks[16] = blood;
        bloodFluid.setBlockID(blood).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(bloodFluid, 1000), new ItemStack(buckets, 1, 16), new ItemStack(Item.bucketEmpty)));

        moltenNickelFluid = new Fluid("nickel.molten");
        if (!FluidRegistry.registerFluid(moltenNickelFluid))
            moltenNickelFluid = FluidRegistry.getFluid("nickel.molten");
        moltenNickel = new TConstructFluid(PHConstruct.moltenNickel, moltenNickelFluid, Material.lava, "liquid_ferrous").setUnlocalizedName("metal.molten.nickel");
        GameRegistry.registerBlock(moltenNickel, "metal.molten.nickel");
        fluids[17] = moltenNickelFluid;
        fluidBlocks[17] = moltenNickel;
        moltenNickelFluid.setBlockID(moltenNickel).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenNickelFluid, 1000), new ItemStack(buckets, 1, 17), new ItemStack(Item.bucketEmpty)));

        moltenLeadFluid = new Fluid("lead.molten");
        if (!FluidRegistry.registerFluid(moltenLeadFluid))
            moltenLeadFluid = FluidRegistry.getFluid("lead.molten");
        moltenLead = new TConstructFluid(PHConstruct.moltenLead, moltenLeadFluid, Material.lava, "liquid_lead").setUnlocalizedName("metal.molten.lead");
        GameRegistry.registerBlock(moltenLead, "metal.molten.lead");
        fluids[18] = moltenLeadFluid;
        fluidBlocks[18] = moltenLead;
        moltenLeadFluid.setBlockID(moltenLead).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenLeadFluid, 1000), new ItemStack(buckets, 1, 18), new ItemStack(Item.bucketEmpty)));

        moltenSilverFluid = new Fluid("silver.molten");
        if (!FluidRegistry.registerFluid(moltenSilverFluid))
            moltenSilverFluid = FluidRegistry.getFluid("silver.molten");
        moltenSilver = new TConstructFluid(PHConstruct.moltenSilver, moltenSilverFluid, Material.lava, "liquid_silver").setUnlocalizedName("metal.molten.silver");
        GameRegistry.registerBlock(moltenSilver, "metal.molten.silver");
        fluids[19] = moltenSilverFluid;
        fluidBlocks[19] = moltenSilver;
        moltenSilverFluid.setBlockID(moltenSilver).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenSilverFluid, 1000), new ItemStack(buckets, 1, 19), new ItemStack(Item.bucketEmpty)));

        moltenShinyFluid = new Fluid("platinum.molten");
        if (!FluidRegistry.registerFluid(moltenShinyFluid))
            moltenShinyFluid = FluidRegistry.getFluid("platinum.molten");
        moltenShiny = new TConstructFluid(PHConstruct.moltenShiny, moltenShinyFluid, Material.lava, "liquid_shiny").setUnlocalizedName("metal.molten.shiny");
        GameRegistry.registerBlock(moltenShiny, "metal.molten.shiny");
        fluids[20] = moltenShinyFluid;
        fluidBlocks[20] = moltenShiny;
        moltenShinyFluid.setBlockID(moltenShiny).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenShinyFluid, 1000), new ItemStack(buckets, 1, 20), new ItemStack(Item.bucketEmpty)));

        moltenInvarFluid = new Fluid("invar.molten");
        if (!FluidRegistry.registerFluid(moltenInvarFluid))
            moltenInvarFluid = FluidRegistry.getFluid("invar.molten");
        moltenInvar = new TConstructFluid(PHConstruct.moltenInvar, moltenInvarFluid, Material.lava, "liquid_invar").setUnlocalizedName("metal.molten.invar");
        GameRegistry.registerBlock(moltenInvar, "metal.molten.invar");
        fluids[21] = moltenInvarFluid;
        fluidBlocks[21] = moltenInvar;
        moltenInvarFluid.setBlockID(moltenInvar).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenInvarFluid, 1000), new ItemStack(buckets, 1, 21), new ItemStack(Item.bucketEmpty)));

        moltenElectrumFluid = new Fluid("electrum.molten");
        if (!FluidRegistry.registerFluid(moltenElectrumFluid))
            moltenElectrumFluid = FluidRegistry.getFluid("electrum.molten");
        moltenElectrum = new TConstructFluid(PHConstruct.moltenElectrum, moltenElectrumFluid, Material.lava, "liquid_electrum").setUnlocalizedName("metal.molten.electrum");
        GameRegistry.registerBlock(moltenElectrum, "metal.molten.electrum");
        fluids[22] = moltenElectrumFluid;
        fluidBlocks[22] = moltenElectrum;
        moltenElectrumFluid.setBlockID(moltenElectrum).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenElectrumFluid, 1000), new ItemStack(buckets, 1, 22), new ItemStack(Item.bucketEmpty)));

        moltenEnderFluid = new Fluid("ender");
        if (!FluidRegistry.registerFluid(moltenEnderFluid))
        {
            moltenEnderFluid = FluidRegistry.getFluid("ender");
            moltenEnder = Block.blocksList[moltenEnderFluid.getBlockID()];
            if (moltenEnder == null)
                TConstruct.logger.info("Molten ender block missing!");
        }
        else
        {
            moltenEnder = new TConstructFluid(PHConstruct.moltenEnder, moltenEnderFluid, Material.water, "liquid_ender").setUnlocalizedName("fluid.ender");
            GameRegistry.registerBlock(moltenEnder, "fluid.ender");
            moltenEnderFluid.setBlockID(moltenEnder).setDensity(3000).setViscosity(6000);
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenEnderFluid, 1000), new ItemStack(buckets, 1, 23), new ItemStack(Item.bucketEmpty)));
        }
        fluids[23] = moltenEnderFluid;
        fluidBlocks[23] = moltenEnder;

        //Slime
        slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        blueSlimeFluid = new Fluid("slime.blue");
        if (!FluidRegistry.registerFluid(blueSlimeFluid))
            blueSlimeFluid = FluidRegistry.getFluid("slime.blue");
        slimePool = new SlimeFluid(PHConstruct.slimePoolBlue, blueSlimeFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(slimeStep).setUnlocalizedName("liquid.slime");
        GameRegistry.registerBlock(slimePool, "liquid.slime");
        fluids[24] = blueSlimeFluid;
        fluidBlocks[24] = slimePool;
        blueSlimeFluid.setBlockID(slimePool);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(blueSlimeFluid, 1000), new ItemStack(buckets, 1, 24), new ItemStack(Item.bucketEmpty)));

        //Glue
        glueFluid = new Fluid("glue").setDensity(6000).setViscosity(6000).setTemperature(200);
        if (!FluidRegistry.registerFluid(glueFluid))
            glueFluid = FluidRegistry.getFluid("glue");
        glueFluidBlock = new GlueFluid(PHConstruct.glueFluidBlock, glueFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(slimeStep).setUnlocalizedName("liquid.glue");
        GameRegistry.registerBlock(glueFluidBlock, "liquid.glue");
        fluids[25] = glueFluid;
        fluidBlocks[25] = glueFluidBlock;
        glueFluid.setBlockID(glueFluidBlock);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(glueFluid, 1000), new ItemStack(buckets, 1, 26), new ItemStack(Item.bucketEmpty)));

        pigIronFluid = new Fluid("pigiron.molten");
        if (!FluidRegistry.registerFluid(pigIronFluid))
            pigIronFluid = FluidRegistry.getFluid("pigiron.molten");
        else
            pigIronFluid.setDensity(3000).setViscosity(6000).setTemperature(1300);
        fluids[26] = pigIronFluid;

        slimeGel = new SlimeGel(PHConstruct.slimeGel).setStepSound(slimeStep).setLightOpacity(0).setUnlocalizedName("slime.gel");
        GameRegistry.registerBlock(slimeGel, SlimeGelItemBlock.class, "slime.gel");

        slimeGrass = new SlimeGrass(PHConstruct.slimeGrass).setStepSound(Block.soundGrassFootstep).setLightOpacity(0).setUnlocalizedName("slime.grass");
        GameRegistry.registerBlock(slimeGrass, SlimeGrassItemBlock.class, "slime.grass");

        slimeTallGrass = new SlimeTallGrass(PHConstruct.slimeTallGrass).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("slime.grass.tall");
        GameRegistry.registerBlock(slimeTallGrass, SlimeTallGrassItem.class, "slime.grass.tall");

        slimeLeaves = (SlimeLeaves) new SlimeLeaves(PHConstruct.slimeLeaves).setStepSound(slimeStep).setLightOpacity(0).setUnlocalizedName("slime.leaves");
        GameRegistry.registerBlock(slimeLeaves, SlimeLeavesItemBlock.class, "slime.leaves");

        slimeSapling = (SlimeSapling) new SlimeSapling(PHConstruct.slimeSapling).setStepSound(slimeStep).setUnlocalizedName("slime.sapling");
        GameRegistry.registerBlock(slimeSapling, SlimeSaplingItemBlock.class, "slime.sapling");

        slimeChannel = new ConveyorBase(PHConstruct.slimeChannel, Material.water, "greencurrent").setHardness(0.3f).setStepSound(slimeStep).setUnlocalizedName("slime.channel");
        GameRegistry.registerBlock(slimeChannel, "slime.channel");
        TConstructRegistry.drawbridgeState[slimeChannel.blockID] = 1;

        bloodChannel = new ConveyorBase(PHConstruct.bloodChannel, Material.water, "liquid_cow").setHardness(0.3f).setStepSound(slimeStep).setUnlocalizedName("blood.channel");
        GameRegistry.registerBlock(bloodChannel, "blood.channel");
        TConstructRegistry.drawbridgeState[bloodChannel.blockID] = 1;

        slimePad = new SlimePad(PHConstruct.slimePad, Material.cloth).setStepSound(slimeStep).setHardness(0.3f).setUnlocalizedName("slime.pad");
        GameRegistry.registerBlock(slimePad, "slime.pad");
        TConstructRegistry.drawbridgeState[slimePad.blockID] = 1;

        //Decoration
        stoneTorch = new StoneTorch(PHConstruct.stoneTorch).setUnlocalizedName("decoration.stonetorch");
        GameRegistry.registerBlock(stoneTorch, "decoration.stonetorch");
        stoneLadder = new StoneLadder(PHConstruct.stoneLadder).setUnlocalizedName("decoration.stoneladder");
        GameRegistry.registerBlock(stoneLadder, "decoration.stoneladder");

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
        oreSlag = new MetalOre(PHConstruct.oreSlag, Material.rock, 10.0F, oreTypes).setUnlocalizedName("tconstruct.stoneore");
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

        glassPane = new GlassPaneConnected(PHConstruct.glassPane, "clear", false);
        GameRegistry.registerBlock(glassPane, GlassPaneItem.class, "GlassPane");

        stainedGlassClear = new GlassBlockConnectedMeta(PHConstruct.stainedGlassClear, "stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
                "cyan", "purple", "blue", "brown", "green", "red", "black").setUnlocalizedName("GlassBlock.StainedClear");
        stainedGlassClear.stepSound = Block.soundGlassFootstep;
        GameRegistry.registerBlock(stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");

        stainedGlassClearPane = new GlassPaneStained(PHConstruct.stainedGlassClearPane);
        GameRegistry.registerBlock(stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        //Rail
        woodenRail = new WoodRail(PHConstruct.woodenRail).setStepSound(Block.soundWoodFootstep).setCreativeTab(TConstructRegistry.blockTab).setUnlocalizedName("rail.wood");
        GameRegistry.registerBlock(woodenRail, "rail.wood");

    }

    void registerItems ()
    {
        titleIcon = new TitleIcon(PHConstruct.uselessItem).setUnlocalizedName("tconstruct.titleicon");
        GameRegistry.registerItem(titleIcon, "titleIcon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast", "blank_cast" };
        blankPattern = new CraftingItem(PHConstruct.blankPattern, blanks, blanks, "materials/").setUnlocalizedName("tconstruct.Pattern");
        GameRegistry.registerItem(blankPattern, "blankPattern");

        materials = new MaterialItem(PHConstruct.materials).setUnlocalizedName("tconstruct.Materials");
        toolRod = new ToolPart(PHConstruct.toolRod, "_rod", "ToolRod").setUnlocalizedName("tconstruct.ToolRod");
        toolShard = new ToolShard(PHConstruct.toolShard, "_chunk").setUnlocalizedName("tconstruct.ToolShard");
        woodPattern = new Pattern(PHConstruct.woodPattern, "pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
        metalPattern = new MetalPattern(PHConstruct.metalPattern, "cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
        //armorPattern = new ArmorPattern(PHConstruct.armorPattern, "armorcast_", "materials/").setUnlocalizedName("tconstruct.ArmorPattern");
        GameRegistry.registerItem(materials, "materials");
        GameRegistry.registerItem(woodPattern, "woodPattern");
        GameRegistry.registerItem(metalPattern, "metalPattern");
        //GameRegistry.registerItem(armorPattern, "armorPattern");

        TConstructRegistry.addItemToDirectory("blankPattern", blankPattern);
        TConstructRegistry.addItemToDirectory("woodPattern", woodPattern);
        TConstructRegistry.addItemToDirectory("metalPattern", metalPattern);
        //TConstructRegistry.addItemToDirectory("armorPattern", armorPattern);

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
        /*String[] armorPartTypes = { "helmet", "chestplate", "leggings", "boots" };
        for (int i = 1; i < armorPartTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(armorPartTypes[i] + "Cast", new ItemStack(armorPattern, 1, i));
        }*/

        manualBook = new Manual(PHConstruct.manual);
        GameRegistry.registerItem(manualBook, "manualBook");
        buckets = new FilledBucket(PHConstruct.buckets);
        GameRegistry.registerItem(buckets, "buckets");

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

        Item[] tools = { pickaxe, shovel, hatchet, broadsword, longsword, rapier, dagger, cutlass, frypan, battlesign, mattock, chisel, lumberaxe, cleaver, scythe, excavator, hammer, battleaxe,
                shortbow, arrow };
        String[] toolStrings = { "pickaxe", "shovel", "hatchet", "broadsword", "longsword", "rapier", "dagger", "cutlass", "frypan", "battlesign", "mattock", "chisel", "lumberaxe", "cleaver",
                "scythe", "excavator", "hammer", "battleaxe", "shortbow", "arrow" };

        for (int i = 0; i < tools.length; i++)
        {
            GameRegistry.registerItem(tools[i], toolStrings[i]); // 1.7 compat
            TConstructRegistry.addItemToDirectory(toolStrings[i], tools[i]);
        }

        potionLauncher = new PotionLauncher(PHConstruct.potionLauncher).setUnlocalizedName("tconstruct.PotionLauncher");
        GameRegistry.registerItem(potionLauncher, "potionLauncher");

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
            GameRegistry.registerItem(toolParts[i], toolPartStrings[i]); // 1.7 compat
            TConstructRegistry.addItemToDirectory(toolPartStrings[i], toolParts[i]);
        }

        diamondApple = new DiamondApple(PHConstruct.diamondApple).setUnlocalizedName("tconstruct.apple.diamond");
        strangeFood = new StrangeFood(PHConstruct.slimefood).setUnlocalizedName("tconstruct.strangefood");
        oreBerries = new OreBerries(PHConstruct.oreChunks).setUnlocalizedName("oreberry");
        GameRegistry.registerItem(diamondApple, "diamondApple");
        GameRegistry.registerItem(strangeFood, "strangeFood");
        GameRegistry.registerItem(oreBerries, "oreBerries");

        jerky = new Jerky(PHConstruct.jerky, Loader.isModLoaded("HungerOverhaul")).setUnlocalizedName("tconstruct.jerky");
        GameRegistry.registerItem(jerky, "jerky");

        //Wearables
        heartCanister = new HeartCanister(PHConstruct.heartCanister).setUnlocalizedName("tconstruct.canister");
        knapsack = new Knapsack(PHConstruct.knapsack).setUnlocalizedName("tconstruct.storage");
        goldHead = new GoldenHead(PHConstruct.goldHead, 4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F).setUnlocalizedName("goldenhead");
        GameRegistry.registerItem(heartCanister, "heartCanister");
        GameRegistry.registerItem(knapsack, "knapsack");
        GameRegistry.registerItem(goldHead, "goldHead");

        LiquidCasting basinCasting = TConstruct.getBasinCasting();
        materialWood = EnumHelper.addArmorMaterial("WOOD", 2, new int[] { 1, 2, 2, 1 }, 3);
        helmetWood = new ArmorBasic(PHConstruct.woodHelmet, materialWood, 0, "wood").setUnlocalizedName("tconstruct.helmetWood");
        chestplateWood = new ArmorBasic(PHConstruct.woodChestplate, materialWood, 1, "wood").setUnlocalizedName("tconstruct.chestplateWood");
        leggingsWood = new ArmorBasic(PHConstruct.woodPants, materialWood, 2, "wood").setUnlocalizedName("tconstruct.leggingsWood");
        bootsWood = new ArmorBasic(PHConstruct.woodBoots, materialWood, 3, "wood").setUnlocalizedName("tconstruct.bootsWood");
        GameRegistry.registerItem(helmetWood, "helmetWood");
        GameRegistry.registerItem(chestplateWood, "chestplateWood");
        GameRegistry.registerItem(leggingsWood, "leggingsWood");
        GameRegistry.registerItem(bootsWood, "bootsWood");

        exoGoggles = new ExoArmor(PHConstruct.exoGoggles, EnumArmorPart.HELMET, "exosuit").setUnlocalizedName("tconstruct.exoGoggles");
        exoChest = new ExoArmor(PHConstruct.exoChest, EnumArmorPart.CHEST, "exosuit").setUnlocalizedName("tconstruct.exoChest");
        exoPants = new ExoArmor(PHConstruct.exoPants, EnumArmorPart.PANTS, "exosuit").setUnlocalizedName("tconstruct.exoPants");
        exoShoes = new ExoArmor(PHConstruct.exoShoes, EnumArmorPart.SHOES, "exosuit").setUnlocalizedName("tconstruct.exoShoes");

        String[] materialStrings = { "paperStack", "greenSlimeCrystal", "searedBrick", "ingotCobalt", "ingotArdite", "ingotManyullyn", "mossBall", "lavaCrystal", "necroticBone", "ingotCopper",
                "ingotTin", "ingotAluminum", "rawAluminum", "ingotBronze", "ingotAluminumBrass", "ingotAlumite", "ingotSteel", "blueSlimeCrystal", "ingotObsidian", "nuggetIron", "nuggetCopper",
                "nuggetTin", "nuggetAluminum", "nuggetSilver", "nuggetAluminumBrass", "silkyCloth", "silkyJewel", "nuggetObsidian", "nuggetCobalt", "nuggetArdite", "nuggetManyullyn", "nuggetBronze",
                "nuggetAlumite", "nuggetSteel", "ingotPigIron", "nuggetPigIron", "glueball" };

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
        TConstructRegistry.addToolMaterial(18, "PigIron", "Pig Iron ", 3, 250, 600, 2, 1.3F, 1, 0f, "\u00A7c", "Tasty");

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
        TConstructRegistry.addBowMaterial(18, 384, 20, 1.2f); //Slime

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
        TConstructRegistry.addArrowMaterial(18, 6.8F, 0.5F, 100F); //Iron

        TConstructRegistry.addBowstringMaterial(0, 2, new ItemStack(Item.silk), new ItemStack(bowstring, 1, 0), 1F, 1F, 1f); //String
        TConstructRegistry.addFletchingMaterial(0, 2, new ItemStack(Item.feather), new ItemStack(fletching, 1, 0), 100F, 0F, 0.05F); //Feather
        for (int i = 0; i < 4; i++)
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
        pb.registerFullMaterial(new ItemStack(materials, 1, 34), 2, "PigIron", new ItemStack(toolShard, 1, 18), new ItemStack(toolRod, 1, 18), 18);

        pb.addToolPattern((IPattern) woodPattern);
    }

    public static Item[] patternOutputs;
    public static FluidStack[] liquids;

    void addCraftingRecipes ()
    {
        addPartMapping();

        addRecipesForToolBuilder();
        addRecipesForTableCasting();
        addRecipesForBasinCasting();
        addRecipesForSmeltery();
        addRecipesForChisel();
        addRecipesForFurnace();
        addRecipesForCraftingTable();
        addRecipesForDryingRack();
    }

    private void addRecipesForCraftingTable ()
    {
        String[] patBlock = { "###", "###", "###" };
        String[] patSurround = { "###", "#m#", "###" };

        Object[] toolForgeBlocks = { "blockIron", "blockGold", Block.blockDiamond, Block.blockEmerald, "blockCobalt", "blockArdite", "blockManyullyn", "blockCopper", "blockBronze", "blockTin",
                "blockAluminum", "blockAluminumBrass", "blockAlumite", "blockSteel" };

        // ToolForge Recipes (Metal Version)
        for (int sc = 0; sc < toolForgeBlocks.length; sc++)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolForge, 1, sc), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(toolStationWood, 1, 0), 'm',
                    toolForgeBlocks[sc]));
            // adding slab version recipe
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(craftingSlabWood, 1, 5), "bbb", "msm", "m m", 'b', new ItemStack(smeltery, 1, 2), 's', new ItemStack(craftingSlabWood, 1, 1), 'm',
                    toolForgeBlocks[sc]));
        }

        // ToolStation Recipes (Wooden Version)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(craftingSlabWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 2), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 3), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 4), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 5), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', Block.chest);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "logWood"));
        if (PHConstruct.stencilTableCrafting)
        {
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 0));
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 11), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 1));
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 12), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 2));
            GameRegistry.addRecipe(new ItemStack(toolStationWood, 1, 13), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 3));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(blankPattern, 1, 0), 'w', "plankWood"));
        }
        GameRegistry.addRecipe(new ItemStack(furnaceSlab, 1, 0), "###", "# #", "###", '#', new ItemStack(Block.stoneSingleSlab, 1, 3));

        // Blank Pattern Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blankPattern, 1, 0), "ps", "sp", 'p', "plankWood", 's', "stickWood"));
        // Manual Book Recipes
        GameRegistry.addRecipe(new ItemStack(manualBook), "wp", 'w', new ItemStack(blankPattern, 1, 0), 'p', Item.paper);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 2, 0), new ItemStack(manualBook, 1, 0), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 1, 1), new ItemStack(manualBook, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 2, 1), new ItemStack(manualBook, 1, 1), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 1, 2), new ItemStack(manualBook, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 2, 2), new ItemStack(manualBook, 1, 2), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(manualBook, 1, 3), new ItemStack(manualBook, 1, 2));
        // alternative Vanilla Book Recipe
        GameRegistry.addShapelessRecipe(new ItemStack(Item.book), Item.paper, Item.paper, Item.paper, Item.silk, blankPattern, blankPattern);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.nameTag), "P~ ", "~O ", "  ~", '~', Item.silk, 'P', Item.paper, 'O', "slimeball"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimeExplosive, 1, 0), "slimeball", Block.tnt));
        // Paperstack Recipe
        GameRegistry.addRecipe(new ItemStack(materials, 1, 0), "pp", "pp", 'p', Item.paper);
        // Mossball Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(materials, 1, 6), patBlock, '#', "stoneMossy"));
        // LavaCrystal Recipes -Auto-smelt
        GameRegistry.addRecipe(new ItemStack(materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'c', Item.fireballCharge, 'x', Item.blazeRod);
        GameRegistry.addRecipe(new ItemStack(materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'x', Item.fireballCharge, 'c', Item.blazeRod);
        // Slimy sand Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 0), Item.slimeBall, Item.slimeBall, Item.slimeBall, Item.slimeBall, Block.sand, Block.dirt);
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 2), strangeFood, strangeFood, strangeFood, strangeFood, Block.sand, Block.dirt);
        // Grout Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 2, 1), Item.clay, Block.sand, Block.gravel);
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 8, 1), new ItemStack(Block.blockClay, 1, Short.MAX_VALUE), Block.sand, Block.sand, Block.sand, Block.sand, Block.gravel,
                Block.gravel, Block.gravel, Block.gravel);
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 2, 6), Item.netherStalkSeeds, Block.slowSand, Block.gravel);
        // Graveyard Soil Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 1, 3), Block.dirt, Item.rottenFlesh, new ItemStack(Item.dyePowder, 1, 15));
        // Silky Cloth Recipes
        GameRegistry.addRecipe(new ItemStack(materials, 1, 25), patSurround, 'm', new ItemStack(materials, 1, 24), '#', new ItemStack(Item.silk));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(materials, 1, 25), patSurround, 'm', "nuggetGold", '#', new ItemStack(Item.silk)));
        // Silky Jewel Recipes
        GameRegistry.addRecipe(new ItemStack(materials, 1, 26), " c ", "cec", " c ", 'c', new ItemStack(materials, 1, 25), 'e', new ItemStack(Item.emerald));
        // Armor Recipes
        Object[] helm = new String[] { "www", "w w" };
        Object[] chest = new String[] { "w w", "www", "www" };
        Object[] pants = new String[] { "www", "w w", "w w" };
        Object[] shoes = new String[] { "w w", "w w" };
        GameRegistry.addRecipe(new ShapedOreRecipe(helmetWood, helm, 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(chestplateWood, chest, 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(leggingsWood, pants, 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(bootsWood, shoes, 'w', "logWood"));

        ItemStack exoGoggleStack = new ItemStack(exoGoggles);
        ItemStack exoChestStack = new ItemStack(exoChest);
        ItemStack exoPantsStack = new ItemStack(exoPants);
        ItemStack exoShoesStack = new ItemStack(exoShoes);
        ToolBuilder.instance.addArmorTag(exoGoggleStack);
        ToolBuilder.instance.addArmorTag(exoChestStack);
        ToolBuilder.instance.addArmorTag(exoPantsStack);
        ToolBuilder.instance.addArmorTag(exoShoesStack);
        GameRegistry.addShapedRecipe(exoGoggleStack, helm, 'w', new ItemStack(largePlate, 1, 14));
        GameRegistry.addShapedRecipe(exoChestStack, chest, 'w', new ItemStack(largePlate, 1, 14));
        GameRegistry.addShapedRecipe(exoPantsStack, pants, 'w', new ItemStack(largePlate, 1, 14));
        GameRegistry.addShapedRecipe(exoShoesStack, shoes, 'w', new ItemStack(largePlate, 1, 14));

        // Metal conversion Recipes
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 3), patBlock, '#', new ItemStack(materials, 1, 9)); // Copper
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 5), patBlock, '#', new ItemStack(materials, 1, 10)); // Tin
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 6), patBlock, '#', new ItemStack(materials, 1, 11)); // Aluminum
        //GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 6), patBlock, '#', new ItemStack(materials, 1, 12)); // Aluminum
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 4), patBlock, '#', new ItemStack(materials, 1, 13)); // Bronze
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 7), patBlock, '#', new ItemStack(materials, 1, 14)); // AluBrass
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 0), patBlock, '#', new ItemStack(materials, 1, 3)); // Cobalt
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 1), patBlock, '#', new ItemStack(materials, 1, 4)); // Ardite
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 2), patBlock, '#', new ItemStack(materials, 1, 5)); // Manyullyn
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 8), patBlock, '#', new ItemStack(materials, 1, 15)); // Alumite
        GameRegistry.addRecipe(new ItemStack(metalBlock, 1, 9), patBlock, '#', new ItemStack(materials, 1, 16)); // Steel
        GameRegistry.addRecipe(new ItemStack(materials, 1, 11), patBlock, '#', new ItemStack(materials, 1, 12)); //Aluminum raw -> ingot

        GameRegistry.addRecipe(new ItemStack(materials, 9, 9), "m", 'm', new ItemStack(metalBlock, 1, 3)); //Copper
        GameRegistry.addRecipe(new ItemStack(materials, 9, 10), "m", 'm', new ItemStack(metalBlock, 1, 5)); //Tin
        GameRegistry.addRecipe(new ItemStack(materials, 9, 11), "m", 'm', new ItemStack(metalBlock, 1, 6)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(materials, 9, 13), "m", 'm', new ItemStack(metalBlock, 1, 4)); //Bronze
        GameRegistry.addRecipe(new ItemStack(materials, 9, 14), "m", 'm', new ItemStack(metalBlock, 1, 7)); //AluBrass
        GameRegistry.addRecipe(new ItemStack(materials, 9, 3), "m", 'm', new ItemStack(metalBlock, 1, 0)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(materials, 9, 4), "m", 'm', new ItemStack(metalBlock, 1, 1)); //Ardite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 5), "m", 'm', new ItemStack(metalBlock, 1, 2)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(materials, 9, 15), "m", 'm', new ItemStack(metalBlock, 1, 8)); //Alumite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 16), "m", 'm', new ItemStack(metalBlock, 1, 9)); //Steel

        GameRegistry.addRecipe(new ItemStack(Item.ingotIron), patBlock, '#', new ItemStack(materials, 1, 19)); //Iron
        GameRegistry.addRecipe(new ItemStack(materials, 1, 9), patBlock, '#', new ItemStack(materials, 1, 20)); //Copper
        GameRegistry.addRecipe(new ItemStack(materials, 1, 10), patBlock, '#', new ItemStack(materials, 1, 21)); //Tin
        GameRegistry.addRecipe(new ItemStack(materials, 1, 11), patBlock, '#', new ItemStack(materials, 1, 22)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(materials, 1, 14), patBlock, '#', new ItemStack(materials, 1, 24)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(materials, 1, 18), patBlock, '#', new ItemStack(materials, 1, 27)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(materials, 1, 3), patBlock, '#', new ItemStack(materials, 1, 28)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(materials, 1, 4), patBlock, '#', new ItemStack(materials, 1, 29)); //Ardite
        GameRegistry.addRecipe(new ItemStack(materials, 1, 5), patBlock, '#', new ItemStack(materials, 1, 30)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(materials, 1, 13), patBlock, '#', new ItemStack(materials, 1, 31)); //Bronze
        GameRegistry.addRecipe(new ItemStack(materials, 1, 15), patBlock, '#', new ItemStack(materials, 1, 32)); //Alumite
        GameRegistry.addRecipe(new ItemStack(materials, 1, 16), patBlock, '#', new ItemStack(materials, 1, 33)); //Steel    

        GameRegistry.addRecipe(new ItemStack(materials, 9, 19), "m", 'm', new ItemStack(Item.ingotIron)); //Iron
        GameRegistry.addRecipe(new ItemStack(materials, 9, 20), "m", 'm', new ItemStack(materials, 1, 9)); //Copper
        GameRegistry.addRecipe(new ItemStack(materials, 9, 21), "m", 'm', new ItemStack(materials, 1, 10)); //Tin
        GameRegistry.addRecipe(new ItemStack(materials, 9, 22), "m", 'm', new ItemStack(materials, 1, 11)); //Aluminum
        //GameRegistry.addRecipe(new ItemStack(materials, 9, 22), "m", 'm', new ItemStack(materials, 1, 12)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(materials, 9, 24), "m", 'm', new ItemStack(materials, 1, 14)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(materials, 9, 27), "m", 'm', new ItemStack(materials, 1, 18)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(materials, 9, 28), "m", 'm', new ItemStack(materials, 1, 3)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(materials, 9, 29), "m", 'm', new ItemStack(materials, 1, 4)); //Ardite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 30), "m", 'm', new ItemStack(materials, 1, 5)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(materials, 9, 31), "m", 'm', new ItemStack(materials, 1, 13)); //Bronze
        GameRegistry.addRecipe(new ItemStack(materials, 9, 32), "m", 'm', new ItemStack(materials, 1, 15)); //Alumite
        GameRegistry.addRecipe(new ItemStack(materials, 9, 33), "m", 'm', new ItemStack(materials, 1, 16)); //Steel 

        // stained Glass Recipes
        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue",
                "dyeMagenta", "dyeOrange", "dyeWhite" };
        String color = "";
        for (int i = 0; i < 16; i++)
        {
            color = dyeTypes[15 - i];
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.cloth, 8, i), patSurround, 'm', color, '#', new ItemStack(Block.cloth, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClear, 8, i), patSurround, 'm', color, '#', clearGlass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClear, 1, i), color, clearGlass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClear, 8, i), patSurround, 'm', color, '#', new ItemStack(stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClear, 1, i), color, new ItemStack(stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', glassPane));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClearPane, 1, i), color, glassPane));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', new ItemStack(stainedGlassClearPane, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(stainedGlassClearPane, 1, i), color, new ItemStack(stainedGlassClearPane, 1, Short.MAX_VALUE)));
        }

        // Glass Recipes
        GameRegistry.addRecipe(new ItemStack(Item.glassBottle, 3), new Object[] { "# #", " # ", '#', clearGlass });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.daylightSensor), new Object[] { "GGG", "QQQ", "WWW", 'G', "glass", 'Q', Item.netherQuartz, 'W', "slabWood" }));
        GameRegistry.addRecipe(new ItemStack(Block.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', clearGlass, 'S', Item.netherStar, 'O', Block.obsidian });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(glassPane, 16, 0), "GGG", "GGG", 'G', clearGlass));

        // Smeltery Components Recipes
        ItemStack searedBrick = new ItemStack(materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(smeltery, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(lavaTank, 1, 0), patSurround, '#', searedBrick, 'm', "glass")); //Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "glass")); //Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "glass")); //Window
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin
        GameRegistry.addRecipe(new ItemStack(castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); //Channel

        searedBrick = new ItemStack(materials, 1, 37);
        GameRegistry.addRecipe(new ItemStack(smelteryNether, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(smelteryNether, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(smelteryNether, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(lavaTankNether, 1, 0), patSurround, '#', searedBrick, 'm', "glass")); //Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(lavaTankNether, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "glass")); //Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(lavaTankNether, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "glass")); //Window
        GameRegistry.addRecipe(new ItemStack(searedBlockNether, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(searedBlockNether, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(searedBlockNether, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin
        GameRegistry.addRecipe(new ItemStack(castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); //Channel

        // Jack o'Latern Recipe - Stone Torch
        GameRegistry.addRecipe(new ItemStack(Block.pumpkinLantern, 1, 0), "p", "s", 'p', new ItemStack(Block.pumpkin), 's', new ItemStack(stoneTorch));
        // Stone Torch Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneTorch, 4), "p", "w", 'p', new ItemStack(Item.coal, 1, Short.MAX_VALUE), 'w', "stoneRod"));
        // Stone Ladder Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(stoneLadder, 3), "w w", "www", "w w", 'w', "stoneRod"));
        // Wooden Rail Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(woodenRail, 4, 0), "b b", "bxb", "b b", 'b', "plankWood", 'x', "stickWood"));
        // Stonesticks Recipes
        GameRegistry.addRecipe(new ItemStack(toolRod, 4, 1), "c", "c", 'c', new ItemStack(Block.stone));
        GameRegistry.addRecipe(new ItemStack(toolRod, 2, 1), "c", "c", 'c', new ItemStack(Block.cobblestone));
        // 
        ItemStack aluBrass = new ItemStack(materials, 1, 14);
        // Clock Recipe - Vanilla alternativ
        GameRegistry.addRecipe(new ItemStack(Item.pocketSundial), " i ", "iri", " i ", 'i', aluBrass, 'r', new ItemStack(Item.redstone));
        // Gold Pressure Plate -  Vanilla alternativ
        GameRegistry.addRecipe(new ItemStack(Block.pressurePlateGold), "ii", 'i', aluBrass);
        //Accessories
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(heartCanister, 1, 0), "##", "##", '#', "ingotAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(heartCanister, 1, 0), "##", "##", '#', "ingotAluminium"));
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(heartCanister, 1, 0), "##", "##", '#', "ingotNaturalAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(heartCanister, 1, 0), " # ", "#B#", " # ", '#', "ingotTin", 'B', Item.bone));
        GameRegistry.addRecipe(new ItemStack(diamondApple), " d ", "d#d", " d ", 'd', new ItemStack(Item.diamond), '#', new ItemStack(Item.appleRed));
        GameRegistry.addShapelessRecipe(new ItemStack(heartCanister, 1, 2), new ItemStack(diamondApple), new ItemStack(materials, 1, 8), new ItemStack(heartCanister, 1, 0), new ItemStack(
                heartCanister, 1, 1));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(toughRod, 1, 2), 'm', "ingotGold"));
        GameRegistry.addRecipe(new ItemStack(knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(toughRod, 1, 2), 'm', aluBrass);
        // Drying Rack Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dryingRack, 1, 0), "bbb", 'b', "slabWood"));
        //Landmine Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 0), "mcm", "rpr", 'm', "plankWood", 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 1), "mcm", "rpr", 'm', Block.stone, 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 2), "mcm", "rpr", 'm', Block.obsidian, 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landmine, 1, 3), "mcm", "rpr", 'm', Item.redstoneRepeater, 'c', new ItemStack(blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));

        //Ultra hardcore recipes
        GameRegistry.addRecipe(new ItemStack(goldHead), patSurround, '#', new ItemStack(Item.ingotGold), 'm', new ItemStack(Item.skull, 1, 3));

        // Slab Smeltery Components Recipes
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

        // Wool Slab Recipes
        for (int sc = 0; sc <= 7; sc++)
        {
            GameRegistry.addRecipe(new ItemStack(woolSlab1, 6, sc), "www", 'w', new ItemStack(Block.cloth, 1, sc));
            GameRegistry.addRecipe(new ItemStack(woolSlab2, 6, sc), "www", 'w', new ItemStack(Block.cloth, 1, sc + 8));

            GameRegistry.addShapelessRecipe(new ItemStack(Block.cloth, 1, sc), new ItemStack(woolSlab1, 1, sc), new ItemStack(woolSlab1, 1, sc));
            GameRegistry.addShapelessRecipe(new ItemStack(Block.cloth, 1, sc + 8), new ItemStack(woolSlab2, 1, sc), new ItemStack(woolSlab2, 1, sc));
        }
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.cloth, 1, 0), "slabCloth", "slabCloth"));
        //Trap Recipes
        GameRegistry.addRecipe(new ItemStack(punji, 5, 0), "b b", " b ", "b b", 'b', new ItemStack(Item.reed));
        GameRegistry.addRecipe(new ItemStack(barricadeSpruce, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(barricadeBirch, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(barricadeJungle, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(barricadeOak, 1, 0), "b", "b", 'b', "logWood"));
        // Advanced WorkBench Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(craftingStationWood, 1, 0), "b", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(craftingStationWood, 1, 0), "b", 'b', "craftingTableWood"));
        //Slab crafters
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(craftingSlabWood, 6, 0), "bbb", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(craftingSlabWood, 6, 0), "bbb", 'b', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(craftingSlabWood, 1, 0), "b", 'b', new ItemStack(craftingStationWood, 1, 0));
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
        // EssenceExtractor Recipe
        //Slime Recipes
        GameRegistry.addRecipe(new ItemStack(slimeGel, 1, 0), "##", "##", '#', strangeFood);
        GameRegistry.addRecipe(new ItemStack(strangeFood, 4, 0), "#", '#', new ItemStack(slimeGel, 1, 0));
        GameRegistry.addRecipe(new ItemStack(slimeGel, 1, 1), "##", "##", '#', Item.slimeBall);
        GameRegistry.addRecipe(new ItemStack(Item.slimeBall, 4, 0), "#", '#', new ItemStack(slimeGel, 1, 1));
        //slimeExplosive
        GameRegistry.addShapelessRecipe(new ItemStack(slimeExplosive, 1, 0), Item.slimeBall, Block.tnt);
        GameRegistry.addShapelessRecipe(new ItemStack(slimeExplosive, 1, 2), strangeFood, Block.tnt);
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimeExplosive, 1, 0), "slimeball", Block.tnt));

        GameRegistry.addShapelessRecipe(new ItemStack(slimeChannel, 1, 0), new ItemStack(slimeGel, 1, Short.MAX_VALUE), new ItemStack(Item.redstone));
        GameRegistry.addShapelessRecipe(new ItemStack(bloodChannel, 1, 0), new ItemStack(strangeFood, 1, 1), new ItemStack(strangeFood, 1, 1), new ItemStack(strangeFood, 1, 1), new ItemStack(
                strangeFood, 1, 1), new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimeChannel, 1, 0), "slimeball", "slimeball", "slimeball", "slimeball", new ItemStack(Item.redstone)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(slimePad, 1, 0), slimeChannel, "slimeball"));
    }

    private void addRecipesForFurnace ()
    {
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 3, new ItemStack(craftedSoil, 1, 4), 0.2f); //Concecrated Soil

        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 0, new ItemStack(materials, 1, 1), 2f); //Slime
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 1, new ItemStack(materials, 1, 2), 2f); //Seared brick item
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 2, new ItemStack(materials, 1, 17), 2f); //Blue Slime
        FurnaceRecipes.smelting().addSmelting(craftedSoil.blockID, 6, new ItemStack(materials, 1, 37), 2f); //Nether seared brick
        //FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 1, new ItemStack(materials, 1, 3), 3f);
        //FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 2, new ItemStack(materials, 1, 4), 3f);
        FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 3, new ItemStack(materials, 1, 9), 0.5f);
        FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 4, new ItemStack(materials, 1, 10), 0.5f);
        FurnaceRecipes.smelting().addSmelting(oreSlag.blockID, 5, new ItemStack(materials, 1, 11), 0.5f);

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
        FurnaceRecipes.smelting().addSmelting(oreGravel.blockID, 4, new ItemStack(materials, 1, 11), 0.2f);

        FurnaceRecipes.smelting().addSmelting(speedBlock.blockID, 0, new ItemStack(speedBlock, 1, 2), 0.2f);
    }

    private void addPartMapping ()
    {
        /* Tools */
        patternOutputs = new Item[] { toolRod, pickaxeHead, shovelHead, hatchetHead, swordBlade, wideGuard, handGuard, crossbar, binding, frypanHead, signHead, knifeBlade, chiselHead, toughRod,
                toughBinding, largePlate, broadAxeHead, scytheBlade, excavatorHead, largeSwordBlade, hammerHead, fullGuard, null, null, arrowhead, null };

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
    }

    private void addRecipesForToolBuilder ()
    {
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

        modFlux = new ModFlux();
        tb.registerToolMod(modFlux);

        ItemStack redstoneItem = new ItemStack(Item.redstone);
        ItemStack redstoneBlock = new ItemStack(Block.blockRedstone);
        tb.registerToolMod(new ModRedstone(2, new ItemStack[] { redstoneItem, redstoneBlock }, new int[] { 1, 9 }));

        ItemStack lapisItem = new ItemStack(Item.dyePowder, 1, 4);
        ItemStack lapisBlock = new ItemStack(Block.blockLapis);
        this.modLapis = new ModLapis(10, new ItemStack[] { lapisItem, lapisBlock }, new int[] { 1, 9 });
        tb.registerToolMod(this.modLapis);

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(this.materials, 1, 6) }, 4, "Moss", 3, "\u00a72", "Auto-Repair"));
        ItemStack blazePowder = new ItemStack(Item.blazePowder);
        tb.registerToolMod(new ModBlaze(7, new ItemStack[] { blazePowder }, new int[] { 1 }));
        tb.registerToolMod(new ModAutoSmelt(new ItemStack[] { new ItemStack(this.materials, 1, 7) }, 6, "Lava", "\u00a74", "Auto-Smelt"));
        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(this.materials, 1, 8) }, 8, "Necrotic", 1, "\u00a78", "Life Steal"));

        this.modAttack = new ModAttack("Quartz", 11, new ItemStack[] { new ItemStack(Item.netherQuartz), new ItemStack(Block.blockNetherQuartz, 1, Short.MAX_VALUE) }, new int[] { 1, 4 });
        tb.registerToolMod(this.modAttack);

        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { diamond, new ItemStack(Block.blockGold) }, "Tier1Free"));
        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { new ItemStack(Block.blockDiamond), new ItemStack(Item.appleGold, 1, 1) }, "Tier1.5Free"));
        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { new ItemStack(Item.netherStar) }, "Tier2Free"));

        ItemStack silkyJewel = new ItemStack(this.materials, 1, 26);
        tb.registerToolMod(new ModButtertouch(new ItemStack[] { silkyJewel }, 12));

        ItemStack piston = new ItemStack(Block.pistonBase);
        tb.registerToolMod(new ModPiston(3, new ItemStack[] { piston }, new int[] { 1 }));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl) }, 13, "Beheading", 1, "\u00a7d", "Beheading"));

        ItemStack holySoil = new ItemStack(this.craftedSoil, 1, 4);
        tb.registerToolMod(new ModSmite("Smite", 14, new ItemStack[] { holySoil }, new int[] { 1 }));

        ItemStack spidereyeball = new ItemStack(Item.fermentedSpiderEye);
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", 15, new ItemStack[] { spidereyeball }, new int[] { 1 }));

        ItemStack obsidianPlate = new ItemStack(this.largePlate, 1, 6);
        tb.registerToolMod(new ModReinforced(new ItemStack[] { obsidianPlate }, 16, 1));

        EnumSet<EnumArmorPart> allArmors = EnumSet.of(EnumArmorPart.HELMET, EnumArmorPart.CHEST, EnumArmorPart.PANTS, EnumArmorPart.SHOES);
        EnumSet<EnumArmorPart> chest = EnumSet.of(EnumArmorPart.CHEST);
        tb.registerArmorMod(new AModMoveSpeed(0, allArmors, new ItemStack[] { redstoneItem, redstoneBlock }, new int[] { 1, 9 }, false));
        tb.registerArmorMod(new AModKnockbackResistance(1, allArmors, new ItemStack[] { new ItemStack(Item.ingotGold), new ItemStack(Block.blockGold) }, new int[] { 1, 9 }, false));
        tb.registerArmorMod(new AModHealthBoost(2, allArmors, new ItemStack[] { new ItemStack(heartCanister, 1, 2) }, new int[] { 2 }, true));
        tb.registerArmorMod(new AModDamageBoost(3, allArmors, new ItemStack[] { new ItemStack(Item.diamond), new ItemStack(Block.blockDiamond) }, new int[] { 1, 9 }, false, 3, 0.05));
        tb.registerArmorMod(new AModDamageBoost(4, chest, new ItemStack[] { new ItemStack(Block.blockNetherQuartz, 1, Short.MAX_VALUE) }, new int[] { 1 }, true, 5, 1));
        tb.registerArmorMod(new AModProtection(5, allArmors, new ItemStack[] { new ItemStack(largePlate, 1, 2) }, new int[] { 2 }));

        tb.registerArmorMod(new AModDoubleJump(new ItemStack[] { new ItemStack(Item.ghastTear), new ItemStack(slimeGel, 1, 0), new ItemStack(slimeGel, 1, 1) }));

        TConstructRegistry.registerActiveToolMod(new TActiveOmniMod());
    }

    private void addRecipesForTableCasting ()
    {
        /* Smeltery */
        ItemStack ingotcast = new ItemStack(metalPattern, 1, 0);
        ItemStack gemcast = new ItemStack(metalPattern, 1, 26);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        //Blank
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 1), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), 80);
        tableCasting.addCastingRecipe(new ItemStack(blankPattern, 1, 2), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(Item.emerald), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(Item.emerald), 80);

        //Ingots
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 2), new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue / 4), ingotcast, 80); //stone

        //Misc
        tableCasting.addCastingRecipe(new ItemStack(Item.emerald), new FluidStack(moltenEmeraldFluid, 640), gemcast, 80);
        tableCasting.addCastingRecipe(new ItemStack(materials, 1, 36), new FluidStack(glueFluid, TConstruct.ingotLiquidValue), null, 50);
        tableCasting.addCastingRecipe(new ItemStack(strangeFood, 1, 1), new FluidStack(bloodFluid, 160), null, 50);

        //Buckets
        ItemStack bucket = new ItemStack(Item.bucketEmpty);

        for (int sc = 0; sc < 24; sc++)
        {
            tableCasting.addCastingRecipe(new ItemStack(buckets, 1, sc), new FluidStack(fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
        }
        tableCasting.addCastingRecipe(new ItemStack(buckets, 1, 26), new FluidStack(fluids[26], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

        // Clear glass pane casting
        tableCasting.addCastingRecipe(new ItemStack(glassPane), new FluidStack(moltenGlassFluid, 250), null, 80);

        // Metal toolpart casting
        liquids = new FluidStack[] { new FluidStack(moltenIronFluid, 1), new FluidStack(moltenCopperFluid, 1), new FluidStack(moltenCobaltFluid, 1), new FluidStack(moltenArditeFluid, 1),
                new FluidStack(moltenManyullynFluid, 1), new FluidStack(moltenBronzeFluid, 1), new FluidStack(moltenAlumiteFluid, 1), new FluidStack(moltenObsidianFluid, 1),
                new FluidStack(moltenSteelFluid, 1), new FluidStack(pigIronFluid, 1) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16, 18 }; //ItemStack damage value
        int fluidAmount = 0;
        Fluid fs = null;

        for (int iter = 0; iter < patternOutputs.length; iter++)
        {
            if (patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
                {
                    fs = liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    ItemStack metalCast = new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }
            }
        }

        ItemStack[] ingotShapes = { new ItemStack(Item.brick), new ItemStack(Item.netherrackBrick), new ItemStack(materials, 1, 2), new ItemStack(materials, 1, 37) };
        for (int i = 0; i < ingotShapes.length; i++)
        {
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotShapes[i], false, 50);
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), ingotShapes[i], false, 50);
        }

        ItemStack fullguardCast = new ItemStack(metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(fullGuard, 1, Short.MAX_VALUE), false, 50);

        // Golden Food Stuff
        FluidStack goldAmount = null;
        if (PHConstruct.goldAppleRecipe)
        {
            goldAmount = new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 8);
        }
        else
        {
            goldAmount = new FluidStack(moltenGoldFluid, TConstruct.nuggetLiquidValue * 8);
        }
        tableCasting.addCastingRecipe(new ItemStack(Item.appleGold, 1), goldAmount, new ItemStack(Item.appleRed), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Item.goldenCarrot, 1), goldAmount, new ItemStack(Item.carrot), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Item.speckledMelon, 1), goldAmount, new ItemStack(Item.melon), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(goldHead), goldAmount, new ItemStack(Item.skull, 1, 3), true, 50);
    }

    private void addRecipesForBasinCasting ()
    {
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        // Block Casting
        basinCasting.addCastingRecipe(new ItemStack(Block.blockIron), new FluidStack(moltenIronFluid, TConstruct.blockLiquidValue), null, true, 100); //Iron
        basinCasting.addCastingRecipe(new ItemStack(Block.blockGold), new FluidStack(moltenGoldFluid, TConstruct.blockLiquidValue), null, true, 100); //gold
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 3), new FluidStack(moltenCopperFluid, TConstruct.blockLiquidValue), null, true, 100); //copper
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 5), new FluidStack(moltenTinFluid, TConstruct.blockLiquidValue), null, true, 100); //tin
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 6), new FluidStack(moltenAluminumFluid, TConstruct.blockLiquidValue), null, true, 100); //aluminum
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 0), new FluidStack(moltenCobaltFluid, TConstruct.blockLiquidValue), null, true, 100); //cobalt
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 1), new FluidStack(moltenArditeFluid, TConstruct.blockLiquidValue), null, true, 100); //ardite
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 4), new FluidStack(moltenBronzeFluid, TConstruct.blockLiquidValue), null, true, 100); //bronze
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 7), new FluidStack(moltenAlubrassFluid, TConstruct.blockLiquidValue), null, true, 100); //albrass
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 2), new FluidStack(moltenManyullynFluid, TConstruct.blockLiquidValue), null, true, 100); //manyullyn
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 8), new FluidStack(moltenAlumiteFluid, TConstruct.blockLiquidValue), null, true, 100); //alumite
        basinCasting.addCastingRecipe(new ItemStack(Block.obsidian), new FluidStack(moltenObsidianFluid, TConstruct.oreLiquidValue), null, true, 100);// obsidian
        basinCasting.addCastingRecipe(new ItemStack(metalBlock, 1, 9), new FluidStack(moltenSteelFluid, TConstruct.blockLiquidValue), null, true, 100); //steel
        basinCasting.addCastingRecipe(new ItemStack(clearGlass, 1, 0), new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100); //glass
        basinCasting.addCastingRecipe(new ItemStack(smeltery, 1, 4), new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue), null, true, 100); //seared stone
        basinCasting.addCastingRecipe(new ItemStack(smeltery, 1, 5), new FluidStack(moltenStoneFluid, TConstruct.chunkLiquidValue), new ItemStack(Block.cobblestone), true, 100);
        basinCasting.addCastingRecipe(new ItemStack(Block.blockEmerald), new FluidStack(moltenEmeraldFluid, 640 * 9), null, true, 100); //emerald
        basinCasting.addCastingRecipe(new ItemStack(speedBlock, 1, 0), new FluidStack(moltenTinFluid, TConstruct.nuggetLiquidValue), new ItemStack(Block.gravel), true, 100); //brownstone
        if (PHConstruct.craftEndstone)//endstone
        {
            basinCasting.addCastingRecipe(new ItemStack(Block.whiteStone), new FluidStack(moltenEnderFluid, 50), new ItemStack(Block.obsidian), true, 100);
            basinCasting.addCastingRecipe(new ItemStack(Block.whiteStone), new FluidStack(moltenEnderFluid, 250), new ItemStack(Block.sandStone), true, 100);
        }
        basinCasting.addCastingRecipe(new ItemStack(metalBlock.blockID, 1, 10), new FluidStack(moltenEnderFluid, 1000), null, true, 100); //ender
        basinCasting.addCastingRecipe(new ItemStack(glueBlock), new FluidStack(glueFluid, TConstruct.blockLiquidValue), null, true, 100); //glue

        // basinCasting.addCastingRecipe(new ItemStack(slimeGel, 1, 0), new FluidStack(blueSlimeFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100);

        //Armor casts
        /*FluidRenderProperties frp = new FluidRenderProperties(Applications.BASIN.minHeight, 0.65F, Applications.BASIN);
        FluidStack aluFlu = new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue * 10);
        FluidStack gloFlu = new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 10);
        ItemStack[] armor = { new ItemStack(helmetWood), new ItemStack(chestplateWood), new ItemStack(leggingsWood), new ItemStack(bootsWood) };
        for (int sc = 0; sc < armor.length; sc++)
        {
            basinCasting.addCastingRecipe(new ItemStack(armorPattern, 1, sc), aluFlu, armor[sc], 50, frp);
            basinCasting.addCastingRecipe(new ItemStack(armorPattern, 1, sc), gloFlu, armor[sc], 50, frp);
        }*/
    }

    private void addRecipesForSmeltery ()
    {
        //Alloy Smelting
        Smeltery.addAlloyMixing(new FluidStack(moltenBronzeFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsBronzeAlloy), new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue * 3),
                new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue)); //Bronze			
        Smeltery.addAlloyMixing(new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsAluminumBrassAlloy), new FluidStack(moltenAluminumFluid,
                TConstruct.ingotLiquidValue * 3), new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue * 1)); //Aluminum Brass
        Smeltery.addAlloyMixing(new FluidStack(moltenAlumiteFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsAlumiteAlloy), new FluidStack(moltenAluminumFluid, TConstruct.ingotLiquidValue * 5),
                new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2), new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 2)); //Alumite
        Smeltery.addAlloyMixing(new FluidStack(moltenManyullynFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsManyullynAlloy), new FluidStack(moltenCobaltFluid, TConstruct.ingotLiquidValue),
                new FluidStack(moltenArditeFluid, TConstruct.ingotLiquidValue)); //Manyullyn
        Smeltery.addAlloyMixing(new FluidStack(pigIronFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsPigironAlloy), new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue),
                new FluidStack(moltenEmeraldFluid, 640), new FluidStack(bloodFluid, 80)); //Pigiron 

        // Stone parts
        for (int sc = 0; sc < patternOutputs.length; sc++)
        {
            if (patternOutputs[sc] != null)
            {
                Smeltery.addMelting(FluidType.Stone, new ItemStack(patternOutputs[sc], 1, 1), 1, (8 * ((IPattern) woodPattern).getPatternCost(new ItemStack(woodPattern, 1, sc + 1))) / 2);
            }
        }

        // Chunks
        Smeltery.addMelting(FluidType.Stone, new ItemStack(toolShard, 1, 1), 0, 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(toolShard, 1, 2), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Obsidian, new ItemStack(toolShard, 1, 6), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Cobalt, new ItemStack(toolShard, 1, 10), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Ardite, new ItemStack(toolShard, 1, 11), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Manyullyn, new ItemStack(toolShard, 1, 12), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Copper, new ItemStack(toolShard, 1, 13), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Bronze, new ItemStack(toolShard, 1, 14), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Alumite, new ItemStack(toolShard, 1, 15), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(toolShard, 1, 16), 0, TConstruct.chunkLiquidValue);

        // Items

        Smeltery.addMelting(FluidType.AluminumBrass, new ItemStack(blankPattern, 4, 1), -50, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(blankPattern, 4, 2), -50, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.Glue, new ItemStack(materials, 1, 36), 0, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.Ender, new ItemStack(Item.enderPearl, 4), 0, 250);
        Smeltery.addMelting(metalBlock, 10, 50, new FluidStack(moltenEnderFluid, 1000));
        Smeltery.addMelting(FluidType.Water, new ItemStack(Item.snowball, 1, 0), 0, 125);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.flintAndSteel, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.compass, 1, 0), 0, TConstruct.ingotLiquidValue * 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.bucketEmpty), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartEmpty), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartCrate), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartPowered), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartHopper), 50, TConstruct.ingotLiquidValue * 10);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.doorIron), 0, TConstruct.ingotLiquidValue * 6);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.cauldron), 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.shears), 0, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.Emerald, new ItemStack(Item.emerald), -50, 640);

        //Blocks melt as themselves!
        //Ore
        Smeltery.addMelting(Block.oreIron, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.oreGold, 0, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(oreGravel, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(oreGravel, 1, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 2));

        //Blocks
        Smeltery.addMelting(Block.blockIron, 0, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.blockGold, 0, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.obsidian, 0, 800, new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.ice, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 1000));
        Smeltery.addMelting(Block.blockSnow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 500));
        Smeltery.addMelting(Block.snow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 250));
        Smeltery.addMelting(Block.sand, 0, 625, new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.glass, 0, 625, new FluidStack(moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.thinGlass, 0, 625, new FluidStack(moltenGlassFluid, 250));
        Smeltery.addMelting(Block.stone, 0, 800, new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.cobblestone, 0, 800, new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.blockEmerald, 0, 800, new FluidStack(moltenEmeraldFluid, 640 * 9));
        Smeltery.addMelting(glueBlock, 0, 250, new FluidStack(glueFluid, TConstruct.blockLiquidValue));
        Smeltery.addMelting(craftedSoil, 1, 600, new FluidStack(moltenStoneFluid, TConstruct.ingotLiquidValue / 4));

        Smeltery.addMelting(clearGlass, 0, 500, new FluidStack(moltenGlassFluid, 1000));
        Smeltery.addMelting(glassPane, 0, 350, new FluidStack(moltenGlassFluid, 250));

        for (int i = 0; i < 16; i++)
        {
            Smeltery.addMelting(stainedGlassClear, i, 500, new FluidStack(moltenGlassFluid, 1000));
            Smeltery.addMelting(stainedGlassClearPane, i, 350, new FluidStack(moltenGlassFluid, 250));
        }

        //Bricks
        Smeltery.addMelting(multiBrick, 4, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(multiBrickFancy, 4, 600, new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(multiBrick, 5, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(multiBrickFancy, 5, 400, new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(multiBrick, 0, 800, new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(multiBrickFancy, 0, 800, new FluidStack(moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));

        //Vanilla blocks
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.fenceIron), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.pressurePlateIron), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Block.pressurePlateGold, 4), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.rail), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Block.railPowered), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.railDetector), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.railActivator), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Obsidian, new ItemStack(Block.enchantmentTable), 0, TConstruct.ingotLiquidValue * 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.cauldron), 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.anvil, 1, 0), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.anvil, 1, 1), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.anvil, 1, 2), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.hopperBlock), 0, TConstruct.ingotLiquidValue * 5);

        //Vanilla Armor
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.helmetIron, 1, 0), 50, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.plateIron, 1, 0), 50, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.legsIron, 1, 0), 50, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.bootsIron, 1, 0), 50, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.helmetGold, 1, 0), 50, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.plateGold, 1, 0), 50, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.legsGold, 1, 0), 50, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.bootsGold, 1, 0), 50, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.helmetChain, 1, 0), 25, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.plateChain, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.legsChain, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.bootsChain, 1, 0), 25, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.horseArmorIron, 1), 100, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.horseArmorGold, 1), 100, TConstruct.ingotLiquidValue * 8);

        //Vanilla tools
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.hoeIron, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.swordIron, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.shovelIron, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.pickaxeIron, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.axeIron, 1, 0), 0, TConstruct.ingotLiquidValue * 3);

        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.hoeGold, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.swordGold, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.shovelGold, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.pickaxeGold, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.axeGold, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
    }

    private void addRecipesForDryingRack ()
    {
        //Drying rack
        DryingRackRecipes.addDryingRecipe(Item.beefRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 0));
        DryingRackRecipes.addDryingRecipe(Item.chickenRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 1));
        DryingRackRecipes.addDryingRecipe(Item.porkRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 2));
        //DryingRackRecipes.addDryingRecipe(Item.muttonRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 3));
        DryingRackRecipes.addDryingRecipe(Item.fishRaw, 20 * 60 * 5, new ItemStack(jerky, 1, 4));
        DryingRackRecipes.addDryingRecipe(Item.rottenFlesh, 20 * 60 * 5, new ItemStack(jerky, 1, 5));
        DryingRackRecipes.addDryingRecipe(new ItemStack(strangeFood, 1, 0), 20 * 60 * 5, new ItemStack(jerky, 1, 6));
        DryingRackRecipes.addDryingRecipe(new ItemStack(strangeFood, 1, 1), 20 * 60 * 5, new ItemStack(jerky, 1, 7));

        //DryingRackRecipes.addDryingRecipe(new ItemStack(jerky, 1, 5), 20 * 60 * 10, Item.leather);
    }

    private void addRecipesForChisel ()
    {
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

        // adding multiBrick / multiBrickFanxy meta 0-13 to list
        for (int sc = 0; sc < 14; sc++)
        {
            chiseling.addDetailing(multiBrick, sc, multiBrickFancy, sc, chisel);
        }

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

    public static String[] liquidNames;

    public void oreRegistry ()
    {
        OreDictionary.registerOre("oreCobalt", new ItemStack(oreSlag, 1, 1));
        OreDictionary.registerOre("oreArdite", new ItemStack(oreSlag, 1, 2));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreSlag, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(oreSlag, 1, 4));
        OreDictionary.registerOre("oreAluminum", new ItemStack(oreSlag, 1, 5));
        OreDictionary.registerOre("oreAluminium", new ItemStack(oreSlag, 1, 5));

        OreDictionary.registerOre("oreIron", new ItemStack(oreGravel, 1, 0));
        OreDictionary.registerOre("oreGold", new ItemStack(oreGravel, 1, 1));
        OreDictionary.registerOre("oreCobalt", new ItemStack(oreGravel, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(oreGravel, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(oreGravel, 1, 3));
        OreDictionary.registerOre("oreAluminum", new ItemStack(oreGravel, 1, 4));
        OreDictionary.registerOre("oreAluminium", new ItemStack(oreGravel, 1, 4));

        OreDictionary.registerOre("ingotCobalt", new ItemStack(materials, 1, 3));
        OreDictionary.registerOre("ingotArdite", new ItemStack(materials, 1, 4));
        OreDictionary.registerOre("ingotManyullyn", new ItemStack(materials, 1, 5));
        OreDictionary.registerOre("ingotCopper", new ItemStack(materials, 1, 9));
        OreDictionary.registerOre("ingotTin", new ItemStack(materials, 1, 10));
        OreDictionary.registerOre("ingotAluminum", new ItemStack(materials, 1, 11));
        OreDictionary.registerOre("ingotAluminium", new ItemStack(materials, 1, 11));
        OreDictionary.registerOre("ingotBronze", new ItemStack(materials, 1, 13));
        OreDictionary.registerOre("ingotAluminumBrass", new ItemStack(materials, 1, 14));
        OreDictionary.registerOre("ingotAluminiumBrass", new ItemStack(materials, 1, 14));
        OreDictionary.registerOre("ingotAlumite", new ItemStack(materials, 1, 15));
        OreDictionary.registerOre("ingotSteel", new ItemStack(materials, 1, 16));
        ensureOreIsRegistered("ingotIron", new ItemStack(Item.ingotIron));
        ensureOreIsRegistered("ingotGold", new ItemStack(Item.ingotGold));
        OreDictionary.registerOre("ingotObsidian", new ItemStack(materials, 1, 18));
        OreDictionary.registerOre("ingotPigIron", new ItemStack(materials, 1, 34));
        OreDictionary.registerOre("itemRawRubber", new ItemStack(materials, 1, 36));

        OreDictionary.registerOre("blockCobalt", new ItemStack(metalBlock, 1, 0));
        OreDictionary.registerOre("blockArdite", new ItemStack(metalBlock, 1, 1));
        OreDictionary.registerOre("blockManyullyn", new ItemStack(metalBlock, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(metalBlock, 1, 3));
        OreDictionary.registerOre("blockBronze", new ItemStack(metalBlock, 1, 4));
        OreDictionary.registerOre("blockTin", new ItemStack(metalBlock, 1, 5));
        OreDictionary.registerOre("blockAluminum", new ItemStack(metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminium", new ItemStack(metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminumBrass", new ItemStack(metalBlock, 1, 7));
        OreDictionary.registerOre("blockAluminiumBrass", new ItemStack(metalBlock, 1, 7));
        OreDictionary.registerOre("blockAlumite", new ItemStack(metalBlock, 1, 8));
        OreDictionary.registerOre("blockSteel", new ItemStack(metalBlock, 1, 9));
        ensureOreIsRegistered("blockIron", new ItemStack(Block.blockIron));
        ensureOreIsRegistered("blockGold", new ItemStack(Block.blockGold));

        OreDictionary.registerOre("nuggetIron", new ItemStack(materials, 1, 19));
        OreDictionary.registerOre("nuggetIron", new ItemStack(oreBerries, 1, 0));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(materials, 1, 20));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(oreBerries, 1, 2));
        OreDictionary.registerOre("nuggetTin", new ItemStack(materials, 1, 21));
        OreDictionary.registerOre("nuggetTin", new ItemStack(oreBerries, 1, 3));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(materials, 1, 22));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(materials, 1, 22));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(materials, 1, 24));
        OreDictionary.registerOre("nuggetAluminiumBrass", new ItemStack(materials, 1, 24));
        OreDictionary.registerOre("nuggetObsidian", new ItemStack(materials, 1, 27));
        OreDictionary.registerOre("nuggetCobalt", new ItemStack(materials, 1, 28));
        OreDictionary.registerOre("nuggetArdite", new ItemStack(materials, 1, 29));
        OreDictionary.registerOre("nuggetManyullyn", new ItemStack(materials, 1, 30));
        OreDictionary.registerOre("nuggetBronze", new ItemStack(materials, 1, 31));
        OreDictionary.registerOre("nuggetAlumite", new ItemStack(materials, 1, 32));
        OreDictionary.registerOre("nuggetSteel", new ItemStack(materials, 1, 33));
        OreDictionary.registerOre("nuggetGold", new ItemStack(oreBerries, 1, 1));
        ensureOreIsRegistered("nuggetGold", new ItemStack(Item.goldNugget));
        OreDictionary.registerOre("nuggetPigIron", new ItemStack(materials, 1, 35));

        OreDictionary.registerOre("slabCloth", new ItemStack(woolSlab1, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("slabCloth", new ItemStack(woolSlab2, 1, Short.MAX_VALUE));

        ensureOreIsRegistered("stoneMossy", new ItemStack(Block.stoneBrick, 1, 1));
        ensureOreIsRegistered("stoneMossy", new ItemStack(Block.cobblestoneMossy));

        OreDictionary.registerOre("crafterWood", new ItemStack(Block.workbench, 1));
        OreDictionary.registerOre("craftingTableWood", new ItemStack(Block.workbench, 1));

        OreDictionary.registerOre("torchStone", new ItemStack(stoneTorch));

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

        BlockDispenser.dispenseBehaviorRegistry.putObject(arrow, new TDispenserBehaviorArrow());

        //Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Item.slimeBall));
        OreDictionary.registerOre("slimeball", new ItemStack(strangeFood, 1, 0));
        OreDictionary.registerOre("slimeball", new ItemStack(strangeFood, 1, 1));
        OreDictionary.registerOre("slimeball", new ItemStack(materials, 1, 36));
        OreDictionary.registerOre("glass", new ItemStack(clearGlass));
        OreDictionary.registerOre("glass", new ItemStack(Block.glass));
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.pistonStickyBase));
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.magmaCream));
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.leash));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.pistonStickyBase), "slimeball", Block.pistonBase));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.magmaCream), "slimeball", Item.blazePowder));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.leash, 2), "ss ", "sS ", "  s", 's', Item.silk, 'S', "slimeball"));
    }

    private void ensureOreIsRegistered (String oreName, ItemStack is)
    {
        int oreId = OreDictionary.getOreID(is);
        if (oreId == -1)
        {
            OreDictionary.registerOre(oreName, is);
        }
    }

    public static boolean thaumcraftAvailable;

    public void intermodCommunication ()
    {
        if (Loader.isModLoaded("Thaumcraft"))
        {
            FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 12));
            FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 13));
            FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 14));
            FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerry, 1, 15));
            FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerrySecond, 1, 12));
            FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(oreBerrySecond, 1, 13));
        }
        if (Loader.isModLoaded("Mystcraft"))
        {
            MystImcHandler.blacklistFluids();
        }
        if (Loader.isModLoaded("BuildCraft|Transport"))
        {
            BCImcHandler.registerFacades();
        }
        /* FORESTRY
         * Edit these strings to change what items are added to the backpacks
         * Format info: "[backpack ID]@[item ID].[metadata or *]:[next item]" and so on
         * Avaliable backpack IDs: forester, miner, digger, hunter, adventurer, builder
         * May add more backpack items later - Spyboticsguy 
         */
        if (Loader.isModLoaded("Forestry"))
        {
            String builderItems = "builder@" + String.valueOf(stoneTorch.blockID) + ":*";
            FMLInterModComms.sendMessage("Forestry", "add-backpack-items", builderItems);
        }
        if (!Loader.isModLoaded("AppliedEnergistics"))
        {
            AEImcHandler.registerForSpatialIO();
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
            String[] var7 = ((String[]) objArray[var4++]);

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

    public void modIntegration ()
    {
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TContent.pickaxeHead, 1, 6), new ItemStack(TContent.toolRod, 1, 2), new ItemStack(TContent.binding, 1, 6), "");
        /* Natura */
        Block taintedSoil = GameRegistry.findBlock("Natura", "soil.tainted");
        Block heatSand = GameRegistry.findBlock("Natura", "heatsand");
        if (taintedSoil != null && heatSand != null)
            GameRegistry.addShapelessRecipe(new ItemStack(craftedSoil, 2, 6), Item.netherStalkSeeds, taintedSoil, heatSand);

        /*TE3 Flux*/
        ItemStack batHardened = GameRegistry.findItemStack("ThermalExpansion", "capacitorHardened", 1);
        if (batHardened != null)
        {
            modFlux.batteries.add(batHardened);
        }
        ItemStack basicCell = GameRegistry.findItemStack("ThermalExpansion", "cellBasic", 1);
        if (basicCell != null)
        {
            modFlux.batteries.add(basicCell);
        }
        if (batHardened != null)
            TConstructClientRegistry.registerManualModifier("fluxmod", ironpick.copy(), (ItemStack) batHardened);
        if (basicCell != null)
            TConstructClientRegistry.registerManualModifier("fluxmod2", ironpick.copy(), (ItemStack) basicCell);

        /* Thaumcraft */
        Object obj = getStaticItem("itemResource", "thaumcraft.common.config.ConfigItems");
        if (obj != null)
        {
            TConstruct.logger.info("Thaumcraft detected. Adding thaumium tools.");
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
            TConstruct.logger.warning("Thaumcraft not detected.");
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

        /* Thermal Expansion 3 Metals */
        ArrayList<ItemStack> ores = OreDictionary.getOres("ingotNickel");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenNickelFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotLead");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenLeadFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotSilver");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenSilverFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotPlatinum");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenShinyFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotInvar");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenInvarFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(moltenInvarFluid, TConstruct.ingotLiquidValue * 3), new FluidStack(moltenIronFluid, TConstruct.ingotLiquidValue * 2), new FluidStack(
                    moltenNickelFluid, TConstruct.ingotLiquidValue * 1)); //Invar
        }
        ores = OreDictionary.getOres("ingotElectrum");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenElectrumFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(moltenElectrumFluid, TConstruct.ingotLiquidValue * 2), new FluidStack(moltenGoldFluid, TConstruct.ingotLiquidValue), new FluidStack(
                    moltenSilverFluid, TConstruct.ingotLiquidValue)); //Electrum
        }

        ores = OreDictionary.getOres("blockNickel");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenNickelFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockLead");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenLeadFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockSilver");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenSilverFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockPlatinum");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenShinyFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockInvar");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenInvarFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockElectrum");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(moltenElectrumFluid, TConstruct.blockLiquidValue), null, 100);
        }

        /* Extra Utilities */
        ores = OreDictionary.getOres("compressedGravel1x");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(speedBlock, 9), new FluidStack(moltenElectrumFluid, TConstruct.blockLiquidValue), ores.get(0), 100);
        }
        ores = OreDictionary.getOres("compressedGravel2x"); //Higher won't save properly
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(speedBlock, 81), new FluidStack(moltenElectrumFluid, TConstruct.blockLiquidValue * 9), ores.get(0), 100);
        }

        /* Rubber */
        ores = OreDictionary.getOres("itemRubber");
        if (ores.size() > 0)
        {
            FurnaceRecipes.smelting().addSmelting(materials.itemID, 36, ores.get(0), 0.2f);
        }
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
            TConstruct.logger.warning("Could not find " + name);
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

    public void addOreDictionarySmelteryRecipes ()
    {
        List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.Water, FluidType.Stone, FluidType.Ender, FluidType.Glass, FluidType.Slime, FluidType.Obsidian });
        for (FluidType ft : FluidType.values())
        {
            if (exceptions.contains(ft))
                continue;

            // Nuggets
            Smeltery.addDictionaryMelting("nugget" + ft.toString(), ft, -100, TConstruct.nuggetLiquidValue);

            // Ingots, Dust
            registerIngotCasting(ft);
            Smeltery.addDictionaryMelting("ingot" + ft.toString(), ft, -50, TConstruct.ingotLiquidValue);
            Smeltery.addDictionaryMelting("dust" + ft.toString(), ft, -75, TConstruct.ingotLiquidValue);

            // Factorization support
            Smeltery.addDictionaryMelting("crystalline" + ft.toString(), ft, -50, TConstruct.ingotLiquidValue);

            // Ores
            Smeltery.addDictionaryMelting("ore" + ft.toString(), ft, 0, TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre);

            // NetherOres support
            Smeltery.addDictionaryMelting("oreNether" + ft.toString(), ft, 75, TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 2);

            // Blocks
            Smeltery.addDictionaryMelting("block" + ft.toString(), ft, 100, TConstruct.blockLiquidValue);

            if (ft.isToolpart)
            {
                registerPatternMaterial("ingot" + ft.toString(), 2, ft.toString());
                registerPatternMaterial("block" + ft.toString(), 18, ft.toString());
            }
        }

        //Obsidian, different dust amount
        {
            FluidType ft = FluidType.Obsidian;
            Smeltery.addDictionaryMelting("nugget" + ft.toString(), ft, -100, TConstruct.nuggetLiquidValue);

            // Ingots, Dust
            registerIngotCasting(ft);
            Smeltery.addDictionaryMelting("ingot" + ft.toString(), ft, -50, TConstruct.ingotLiquidValue);
            Smeltery.addDictionaryMelting("dust" + ft.toString(), ft, -75, TConstruct.ingotLiquidValue / 4);

            // Factorization support
            Smeltery.addDictionaryMelting("crystalline" + ft.toString(), ft, -50, TConstruct.ingotLiquidValue);

            // Ores
            Smeltery.addDictionaryMelting("ore" + ft.toString(), ft, 0, TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre);

            // NetherOres support
            Smeltery.addDictionaryMelting("oreNether" + ft.toString(), ft, 75, TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 2);

            // Blocks
            Smeltery.addDictionaryMelting("block" + ft.toString(), ft, 100, TConstruct.blockLiquidValue);

            if (ft.isToolpart)
            {
                registerPatternMaterial("ingot" + ft.toString(), 2, ft.toString());
                registerPatternMaterial("block" + ft.toString(), 18, ft.toString());
            }
        }

        //Compressed materials
        for (int i = 1; i <= 8; i++)
        {
            Smeltery.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.Stone, 0, TConstruct.ingotLiquidValue / 18 * (9 ^ i));
        }
        Smeltery.addDictionaryMelting("compressedSand1x", FluidType.Glass, 175, FluidContainerRegistry.BUCKET_VOLUME * 9);

        registerPatternMaterial("plankWood", 2, "Wood");
        registerPatternMaterial("stickWood", 1, "Wood");
        registerPatternMaterial("slabWood", 1, "Wood");
        registerPatternMaterial("compressedCobblestone1x", 18, "Stone");
    }

    private void registerPatternMaterial (String oreName, int value, String materialName)
    {
        for (ItemStack ore : OreDictionary.getOres(oreName))
        {
            PatternBuilder.instance.registerMaterial(ore, value, materialName);
        }
    }

    private void registerIngotCasting (FluidType ft)
    {
        ItemStack pattern = new ItemStack(TContent.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        for (ItemStack ore : OreDictionary.getOres("ingot" + ft.toString()))
        {
            tableCasting.addCastingRecipe(pattern, new FluidStack(TContent.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(ore.itemID, 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(pattern, new FluidStack(TContent.moltenGoldFluid, TConstruct.oreLiquidValue), new ItemStack(ore.itemID, 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(new ItemStack(ore.itemID, 1, ore.getItemDamage()), new FluidStack(ft.fluid, TConstruct.ingotLiquidValue), pattern, 80);
        }

    }

    public void addAchievements ()
    {
        HashMap<String, Achievement> achievements = TAchievements.achievements;

        achievements.put("tconstruct.beginner", new Achievement(2741, "tconstruct.beginner", 0, 0, manualBook, null).setIndependent().registerAchievement());
        achievements.put("tconstruct.pattern", new Achievement(2742, "tconstruct.pattern", 2, 1, blankPattern, achievements.get("tconstruct.beginner")).registerAchievement());
        achievements.put("tconstruct.tinkerer", new Achievement(2743, "tconstruct.tinkerer", 2, 2, new ItemStack(titleIcon, 1, 4096), achievements.get("tconstruct.pattern")).registerAchievement());
        achievements.put("tconstruct.preparedFight",
                new Achievement(2744, "tconstruct.preparedFight", 1, 3, new ItemStack(titleIcon, 1, 4097), achievements.get("tconstruct.tinkerer")).registerAchievement());
        achievements.put("tconstruct.proTinkerer", new Achievement(2745, "tconstruct.proTinkerer", 4, 4, new ItemStack(titleIcon, 1, 4098), achievements.get("tconstruct.tinkerer")).setSpecial()
                .registerAchievement());
        achievements.put("tconstruct.smelteryMaker", new Achievement(2746, "tconstruct.smelteryMaker", -2, -1, smeltery, achievements.get("tconstruct.beginner")).registerAchievement());
        achievements.put("tconstruct.enemySlayer",
                new Achievement(2747, "tconstruct.enemySlayer", 0, 5, new ItemStack(titleIcon, 1, 4099), achievements.get("tconstruct.preparedFight")).registerAchievement());
        achievements.put("tconstruct.dualConvenience", new Achievement(2748, "tconstruct.dualConvenience", 0, 7, new ItemStack(titleIcon, 1, 4100), achievements.get("tconstruct.enemySlayer"))
                .setSpecial().registerAchievement());
    }
}
