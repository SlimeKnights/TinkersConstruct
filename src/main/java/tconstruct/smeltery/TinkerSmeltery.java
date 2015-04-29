package tconstruct.smeltery;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.blocks.BlockUtils;
import mantle.blocks.abstracts.MultiServantLogic;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
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
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.util.IPattern;
import tconstruct.smeltery.blocks.*;
import tconstruct.smeltery.itemblocks.*;
import tconstruct.smeltery.items.FilledBucket;
import tconstruct.smeltery.items.MetalPattern;
import tconstruct.smeltery.logic.*;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;
import tconstruct.world.items.OreBerries;

import java.util.*;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers' Smeltery", description = "Liquid metals, casting, and the multiblock structure.")
public class TinkerSmeltery
{
    @SidedProxy(clientSide = "tconstruct.smeltery.SmelteryProxyClient", serverSide = "tconstruct.smeltery.SmelteryProxyCommon")
    public static SmelteryProxyCommon proxy;

    public static Item metalPattern;
    // public static Item armorPattern;
    public static Item buckets;
    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block smelteryNether;
    public static Block lavaTankNether;
    public static Block searedBlockNether;
    public static Block searedSlab;
    public static Block glueBlock;
    public static Block clearGlass;
    // public static Block stainedGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    // public static Block stainedGlassPane;
    public static Block stainedGlassClearPane;
    public static Block glassMagicSlab;
    public static Block stainedGlassMagicSlab;
    public static Block stainedGlassClearMagicSlab;
    // Liquids
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
    public static Fluid moltenNickelFluid;
    public static Fluid moltenLeadFluid;
    public static Fluid moltenSilverFluid;
    public static Fluid moltenShinyFluid;
    public static Fluid moltenInvarFluid;
    public static Fluid moltenElectrumFluid;
    public static Fluid moltenLumiumFluid;
    public static Fluid moltenSignalumFluid;
    public static Fluid moltenMithrilFluid;
    public static Fluid moltenEnderiumFluid;
    public static Fluid moltenEnderFluid;
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
    public static Block moltenNickel;
    public static Block moltenLead;
    public static Block moltenSilver;
    public static Block moltenShiny;
    public static Block moltenInvar;
    public static Block moltenElectrum;
    public static Block moltenLumium;
    public static Block moltenSignalum;
    public static Block moltenMithril;
    public static Block moltenEnderium;
    public static Block moltenEnder;
    // Glue
    public static Fluid glueFluid;
    public static Block glueFluidBlock;
    // Pigiron
    public static Fluid pigIronFluid;
    public static Block pigIronFluidBlock;
    public static Fluid[] fluids = new Fluid[26];
    public static Block[] fluidBlocks = new Block[25];
    public static FluidStack[] liquids;
    public static Block speedSlab;
    // InfiBlocks
    public static Block speedBlock;
    public static Fluid bloodFluid;
    public static Block blood;

    @Handler
    public void preInit (FMLPreInitializationEvent event)
    {
        TinkerSmelteryEvents smelteryEvents = new TinkerSmelteryEvents();
        MinecraftForge.EVENT_BUS.register(smelteryEvents);
        FMLCommonHandler.instance().bus().register(smelteryEvents);

        TinkerSmeltery.buckets = new FilledBucket(BlockUtils.getBlockFromItem(TinkerSmeltery.buckets));
        GameRegistry.registerItem(TinkerSmeltery.buckets, "buckets");

        TinkerSmeltery.searedSlab = new SearedSlab().setBlockName("SearedSlab");
        TinkerSmeltery.searedSlab.stepSound = Block.soundTypeStone;

        TinkerSmeltery.speedSlab = new SpeedSlab().setBlockName("SpeedSlab");
        TinkerSmeltery.speedSlab.stepSound = Block.soundTypeStone;

        TinkerSmeltery.glueBlock = new GlueBlock().setBlockName("GlueBlock").setCreativeTab(TConstructRegistry.blockTab);

        // Smeltery
        TinkerSmeltery.smeltery = new SmelteryBlock().setBlockName("Smeltery");
        TinkerSmeltery.smelteryNether = new SmelteryBlock("nether").setBlockName("Smeltery");
        TinkerSmeltery.lavaTank = new LavaTankBlock().setBlockName("LavaTank");
        TinkerSmeltery.lavaTank.setStepSound(Block.soundTypeGlass);
        TinkerSmeltery.lavaTankNether = new LavaTankBlock("nether").setStepSound(Block.soundTypeGlass).setBlockName("LavaTank");

        TinkerSmeltery.searedBlock = new SearedBlock().setBlockName("SearedBlock");
        TinkerSmeltery.searedBlockNether = new SearedBlock("nether").setBlockName("SearedBlock");

        TinkerSmeltery.castingChannel = (new CastingChannelBlock()).setBlockName("CastingChannel");

        // Liquids
        TinkerSmeltery.liquidMetal = new MaterialLiquid(MapColor.tntColor);

        TinkerSmeltery.moltenIronFluid = registerFluid("iron");
        TinkerSmeltery.moltenIron = TinkerSmeltery.moltenIronFluid.getBlock();

        TinkerSmeltery.moltenGoldFluid = registerFluid("gold");
        TinkerSmeltery.moltenGold = TinkerSmeltery.moltenGoldFluid.getBlock();


        TinkerSmeltery.moltenCopperFluid = registerFluid("copper");
        TinkerSmeltery.moltenCopper = TinkerSmeltery.moltenCopperFluid.getBlock();

        TinkerSmeltery.moltenTinFluid = registerFluid("tin");
        TinkerSmeltery.moltenTin = TinkerSmeltery.moltenTinFluid.getBlock();

        TinkerSmeltery.moltenAluminumFluid = registerFluid("aluminum");
        TinkerSmeltery.moltenAluminum = TinkerSmeltery.moltenAluminumFluid.getBlock();

        TinkerSmeltery.moltenCobaltFluid = registerFluid("cobalt");
        TinkerSmeltery.moltenCobalt = TinkerSmeltery.moltenCobaltFluid.getBlock();

        TinkerSmeltery.moltenArditeFluid = registerFluid("ardite");
        TinkerSmeltery.moltenArdite = TinkerSmeltery.moltenArditeFluid.getBlock();

        TinkerSmeltery.moltenBronzeFluid = registerFluid("bronze");
        TinkerSmeltery.moltenBronze = TinkerSmeltery.moltenBronzeFluid.getBlock();

        TinkerSmeltery.moltenAlubrassFluid = registerFluid("aluminiumbrass", "aluminumbrass.molten", "fluid.molten.alubrass", "liquid_alubrass", 3000, 6000, 1300, Material.lava);
        TinkerSmeltery.moltenAlubrass = TinkerSmeltery.moltenAlubrassFluid.getBlock();

        TinkerSmeltery.moltenManyullynFluid = registerFluid("manyullyn");
        TinkerSmeltery.moltenManyullyn = TinkerSmeltery.moltenManyullynFluid.getBlock();

        TinkerSmeltery.moltenAlumiteFluid = registerFluid("alumite");
        TinkerSmeltery.moltenAlumite = TinkerSmeltery.moltenAlumiteFluid.getBlock();

        TinkerSmeltery.moltenObsidianFluid = registerFluid("obsidian");
        TinkerSmeltery.moltenObsidian = TinkerSmeltery.moltenObsidianFluid.getBlock();

        TinkerSmeltery.moltenSteelFluid = registerFluid("steel");
        TinkerSmeltery.moltenSteel = TinkerSmeltery.moltenSteelFluid.getBlock();

        TinkerSmeltery.moltenGlassFluid = registerFluid("glass");
        TinkerSmeltery.moltenGlass = TinkerSmeltery.moltenGlassFluid.getBlock();

        TinkerSmeltery.moltenStoneFluid = registerFluid("stone", "stone.seared", "molten.stone", "liquid_stone", 3000, 6000, 1300, Material.lava);
        TinkerSmeltery.moltenStone = TinkerSmeltery.moltenStoneFluid.getBlock();

        TinkerSmeltery.moltenEmeraldFluid = registerFluid("emerald", "emerald.liquid", "molten.emerald", "liquid_villager", 3000, 6000, 1300, Material.lava);
        TinkerSmeltery.moltenEmerald = TinkerSmeltery.moltenEmeraldFluid.getBlock();

        TinkerSmeltery.moltenNickelFluid = registerFluid("nickel", "liquid_ferrous");
        TinkerSmeltery.moltenNickel = TinkerSmeltery.moltenNickelFluid.getBlock();

        TinkerSmeltery.moltenLeadFluid = registerFluid("lead");
        TinkerSmeltery.moltenLead = TinkerSmeltery.moltenLeadFluid.getBlock();

        TinkerSmeltery.moltenSilverFluid = registerFluid("silver");
        TinkerSmeltery.moltenSilver = TinkerSmeltery.moltenSilverFluid.getBlock();

        TinkerSmeltery.moltenShinyFluid = registerFluid("platinum", "platinum.molten", "fluid.molten.shiny", "liquid_shiny", 3000, 6000, 1300, Material.lava);
        TinkerSmeltery.moltenShiny = TinkerSmeltery.moltenShinyFluid.getBlock();

        TinkerSmeltery.moltenInvarFluid = registerFluid("invar");
        TinkerSmeltery.moltenInvar = TinkerSmeltery.moltenInvarFluid.getBlock();

        TinkerSmeltery.moltenElectrumFluid = registerFluid("electrum");
        TinkerSmeltery.moltenElectrum = TinkerSmeltery.moltenElectrumFluid.getBlock();

        TinkerSmeltery.moltenEnderFluid = registerFluid("ender", "ender", "fluid.ender", "liquid_ender", 3000, 6000, 295, Material.water);
        TinkerSmeltery.moltenEnder = TinkerSmeltery.moltenEnderFluid.getBlock();

        TinkerSmeltery.moltenLumiumFluid = registerFluid("lumium");
        TinkerSmeltery.moltenLumium = TinkerSmeltery.moltenLumiumFluid.getBlock();

        TinkerSmeltery.moltenSignalumFluid = registerFluid("signalum");
        TinkerSmeltery.moltenSignalum = TinkerSmeltery.moltenSignalumFluid.getBlock();

        TinkerSmeltery.moltenMithrilFluid = registerFluid("mithril");
        TinkerSmeltery.moltenMithril = TinkerSmeltery.moltenMithrilFluid.getBlock();

        TinkerSmeltery.moltenEnderiumFluid = registerFluid("enderium");
        TinkerSmeltery.moltenEnderium = TinkerSmeltery.moltenEnderiumFluid.getBlock();

        // Special liquids with different properties/blocks than the rest

        TinkerSmeltery.bloodFluid = new Fluid("blood").setDensity(3000).setViscosity(6000).setTemperature(1300);
        boolean isBloodPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.bloodFluid);
        TinkerSmeltery.blood = new BloodBlock(TinkerSmeltery.bloodFluid, Material.water, "liquid_cow").setBlockName("liquid.blood");
        GameRegistry.registerBlock(TinkerSmeltery.blood, "liquid.blood");
        if (isBloodPreReg)
        {
            TinkerSmeltery.bloodFluid = FluidRegistry.getFluid("blood");
            Block regBloodBlock = TinkerSmeltery.bloodFluid.getBlock();
            if (regBloodBlock != null)
            {
                ((TConstructFluid) TinkerSmeltery.blood).suppressOverwritingFluidIcons();
                TinkerSmeltery.blood = regBloodBlock;
            }
            else
                TinkerSmeltery.bloodFluid.setBlock(TinkerSmeltery.blood);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.bloodFluid, 1000), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.bloodFluid, 1000), new ItemStack(TinkerSmeltery.buckets, 1, 16), new ItemStack(Items.bucket)));


        // Glue
        TinkerSmeltery.glueFluid = new Fluid("glue").setDensity(6000).setViscosity(6000).setTemperature(200);
        boolean isGluePreReg = !FluidRegistry.registerFluid(TinkerSmeltery.glueFluid);
        TinkerSmeltery.glueFluidBlock = new GlueFluid(TinkerSmeltery.glueFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TinkerWorld.slimeStep).setBlockName("liquid.glue");
        GameRegistry.registerBlock(TinkerSmeltery.glueFluidBlock, "liquid.glue");
        if (isGluePreReg)
        {
            TinkerSmeltery.glueFluid = FluidRegistry.getFluid("glue");
            Block regGlueFluidBlock = TinkerSmeltery.glueFluid.getBlock();
            if (regGlueFluidBlock != null)
            {
                ((GlueFluid) TinkerSmeltery.glueFluidBlock).suppressOverwritingFluidIcons();
                TinkerSmeltery.glueFluidBlock = regGlueFluidBlock;
            }
            else
                TinkerSmeltery.glueFluid.setBlock(TinkerSmeltery.glueFluidBlock);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.glueFluid, 1000), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.glueFluid, 1000), new ItemStack(TinkerSmeltery.buckets, 1, 25), new ItemStack(Items.bucket)));

        // PigIron
        TinkerSmeltery.pigIronFluid = new Fluid("pigiron.molten").setDensity(3000).setViscosity(6000).setTemperature(1300);
        boolean isPigIronPreReg = !FluidRegistry.registerFluid(TinkerSmeltery.pigIronFluid);
        TinkerSmeltery.pigIronFluidBlock = new PigIronMoltenBlock(pigIronFluid, Material.water, "liquid_pigiron").setBlockName("fluid.molten.pigiron");
        GameRegistry.registerBlock(TinkerSmeltery.pigIronFluidBlock, "fluid.molten.pigiron");
        if (isPigIronPreReg)
        {
            TinkerSmeltery.pigIronFluid = FluidRegistry.getFluid("pigiron.molten");
            Block regPigIronFluid = TinkerSmeltery.pigIronFluid.getBlock();
            if (regPigIronFluid != null)
            {
                ((PigIronMoltenBlock) TinkerSmeltery.pigIronFluidBlock).suppressOverwritingFluidIcons();
                TinkerSmeltery.pigIronFluidBlock = regPigIronFluid;
            }
            else
                TinkerSmeltery.pigIronFluid.setBlock(TinkerSmeltery.pigIronFluidBlock);
        }
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerSmeltery.pigIronFluid, 1000), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TinkerSmeltery.pigIronFluid, 1000), new ItemStack(TinkerSmeltery.buckets, 1, 26), new ItemStack(Items.bucket)));

        // blueslime is registered here too because we have the bucket here
        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(TinkerWorld.blueSlimeFluid, 1000), new ItemStack(Items.bucket)) == null)
            FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(TinkerWorld.blueSlimeFluid, 1000), new ItemStack(TinkerSmeltery.buckets, 1, 24), new ItemStack(Items.bucket)));

        TinkerSmeltery.fluids = new Fluid[] { TinkerSmeltery.moltenIronFluid, TinkerSmeltery.moltenGoldFluid, TinkerSmeltery.moltenCopperFluid, TinkerSmeltery.moltenTinFluid, TinkerSmeltery.moltenAluminumFluid, TinkerSmeltery.moltenCobaltFluid, TinkerSmeltery.moltenArditeFluid, TinkerSmeltery.moltenBronzeFluid, TinkerSmeltery.moltenAlubrassFluid, TinkerSmeltery.moltenManyullynFluid, TinkerSmeltery.moltenAlumiteFluid, TinkerSmeltery.moltenObsidianFluid, TinkerSmeltery.moltenSteelFluid, TinkerSmeltery.moltenGlassFluid, TinkerSmeltery.moltenStoneFluid, TinkerSmeltery.moltenEmeraldFluid, TinkerSmeltery.bloodFluid, TinkerSmeltery.moltenNickelFluid, TinkerSmeltery.moltenLeadFluid, TinkerSmeltery.moltenSilverFluid, TinkerSmeltery.moltenShinyFluid, TinkerSmeltery.moltenInvarFluid, TinkerSmeltery.moltenElectrumFluid, TinkerSmeltery.moltenEnderFluid, TinkerWorld.blueSlimeFluid, TinkerSmeltery.glueFluid, TinkerSmeltery.pigIronFluid };
        TinkerSmeltery.fluidBlocks = new Block[] { TinkerSmeltery.moltenIron, TinkerSmeltery.moltenGold, TinkerSmeltery.moltenCopper, TinkerSmeltery.moltenTin, TinkerSmeltery.moltenAluminum, TinkerSmeltery.moltenCobalt, TinkerSmeltery.moltenArdite, TinkerSmeltery.moltenBronze, TinkerSmeltery.moltenAlubrass, TinkerSmeltery.moltenManyullyn, TinkerSmeltery.moltenAlumite, TinkerSmeltery.moltenObsidian, TinkerSmeltery.moltenSteel, TinkerSmeltery.moltenGlass, TinkerSmeltery.moltenStone, TinkerSmeltery.moltenEmerald, TinkerSmeltery.blood, TinkerSmeltery.moltenNickel, TinkerSmeltery.moltenLead, TinkerSmeltery.moltenSilver, TinkerSmeltery.moltenShiny, TinkerSmeltery.moltenInvar, TinkerSmeltery.moltenElectrum, TinkerSmeltery.moltenEnder, TinkerWorld.slimePool, TinkerSmeltery.glueFluidBlock, TinkerSmeltery.pigIronFluidBlock, TinkerSmeltery.moltenLumium, TinkerSmeltery.moltenSignalum, TinkerSmeltery.moltenMithril, TinkerSmeltery.moltenEnderium};

        FluidType.registerFluidType("Water", Blocks.snow, 0, 20, FluidRegistry.getFluid("water"), false);
        FluidType.registerFluidType("Iron", Blocks.iron_block, 0, 600, TinkerSmeltery.moltenIronFluid, true);
        FluidType.registerFluidType("Gold", Blocks.gold_block, 0, 400, TinkerSmeltery.moltenGoldFluid, false);
        FluidType.registerFluidType("Tin", TinkerWorld.metalBlock, 5, 400, TinkerSmeltery.moltenTinFluid, false);
        FluidType.registerFluidType("Copper", TinkerWorld.metalBlock, 3, 550, TinkerSmeltery.moltenCopperFluid, true);
        FluidType.registerFluidType("Aluminum", TinkerWorld.metalBlock, 6, 350, TinkerSmeltery.moltenAluminumFluid, false);
        FluidType.registerFluidType("NaturalAluminum", TinkerWorld.oreSlag, 6, 350, TinkerSmeltery.moltenAluminumFluid, false);
        FluidType.registerFluidType("Cobalt", TinkerWorld.metalBlock, 0, 650, TinkerSmeltery.moltenCobaltFluid, true);
        FluidType.registerFluidType("Ardite", TinkerWorld.metalBlock, 1, 650, TinkerSmeltery.moltenArditeFluid, true);
        FluidType.registerFluidType("AluminumBrass", TinkerWorld.metalBlock, 7, 350, TinkerSmeltery.moltenAlubrassFluid, false);
        FluidType.registerFluidType("Alumite", TinkerWorld.metalBlock, 8, 800, TinkerSmeltery.moltenAlumiteFluid, true);
        FluidType.registerFluidType("Manyullyn", TinkerWorld.metalBlock, 2, 750, TinkerSmeltery.moltenManyullynFluid, true);
        FluidType.registerFluidType("Bronze", TinkerWorld.metalBlock, 4, 500, TinkerSmeltery.moltenBronzeFluid, true);
        FluidType.registerFluidType("Steel", TinkerWorld.metalBlock, 9, 700, TinkerSmeltery.moltenSteelFluid, true);
        FluidType.registerFluidType("Obsidian", Blocks.obsidian, 0, 750, TinkerSmeltery.moltenObsidianFluid, true);
        FluidType.registerFluidType("Ender", TinkerWorld.metalBlock, 10, 500, TinkerSmeltery.moltenEnderFluid, false);
        FluidType.registerFluidType("Glass", Blocks.sand, 0, 625, TinkerSmeltery.moltenGlassFluid, false);
        FluidType.registerFluidType("Stone", Blocks.stone, 0, 800, TinkerSmeltery.moltenStoneFluid, true);
        FluidType.registerFluidType("Emerald", Blocks.emerald_block, 0, 575, TinkerSmeltery.moltenEmeraldFluid, false);
        FluidType.registerFluidType("PigIron", TinkerWorld.meatBlock, 0, 610, TinkerSmeltery.pigIronFluid, true);
        FluidType.registerFluidType("Glue", TinkerSmeltery.glueBlock, 0, 125, TinkerSmeltery.glueFluid, false);

        TinkerSmeltery.speedBlock = new SpeedBlock().setBlockName("SpeedBlock");

        // Glass
        TinkerSmeltery.clearGlass = new GlassBlockConnected("clear", false).setBlockName("GlassBlock");
        TinkerSmeltery.clearGlass.stepSound = Block.soundTypeGlass;
        TinkerSmeltery.glassPane = new GlassPaneConnected("clear", false);
        TinkerSmeltery.stainedGlassClear = new GlassBlockConnectedMeta("stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").setBlockName("GlassBlock.StainedClear");
        TinkerSmeltery.stainedGlassClear.stepSound = Block.soundTypeGlass;
        TinkerSmeltery.stainedGlassClearPane = new GlassPaneStained();

        GameRegistry.registerBlock(TinkerSmeltery.searedSlab, SearedSlabItem.class, "SearedSlab");
        GameRegistry.registerBlock(TinkerSmeltery.speedSlab, SpeedSlabItem.class, "SpeedSlab");

        GameRegistry.registerBlock(TinkerSmeltery.glueBlock, "GlueBlock");
        OreDictionary.registerOre("blockRubber", new ItemStack(TinkerSmeltery.glueBlock));

        // Smeltery stuff
        GameRegistry.registerBlock(TinkerSmeltery.smeltery, SmelteryItemBlock.class, "Smeltery");
        GameRegistry.registerBlock(TinkerSmeltery.smelteryNether, SmelteryItemBlock.class, "SmelteryNether");

        GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
        GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");

        GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");
        GameRegistry.registerBlock(TinkerSmeltery.lavaTank, LavaTankItemBlock.class, "LavaTank");
        GameRegistry.registerBlock(TinkerSmeltery.lavaTankNether, LavaTankItemBlock.class, "LavaTankNether");
        GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

        GameRegistry.registerBlock(TinkerSmeltery.searedBlock, SearedTableItemBlock.class, "SearedBlock");
        GameRegistry.registerBlock(TinkerSmeltery.searedBlockNether, SearedTableItemBlock.class, "SearedBlockNether");
        GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "CastingBasin");

        GameRegistry.registerBlock(TinkerSmeltery.castingChannel, CastingChannelItem.class, "CastingChannel");
        GameRegistry.registerTileEntity(CastingChannelLogic.class, "CastingChannel");

        GameRegistry.registerBlock(TinkerSmeltery.speedBlock, SpeedBlockItem.class, "SpeedBlock");

        // Glass
        GameRegistry.registerBlock(TinkerSmeltery.clearGlass, GlassBlockItem.class, "GlassBlock");
        GameRegistry.registerBlock(TinkerSmeltery.glassPane, GlassPaneItem.class, "GlassPane");
        GameRegistry.registerBlock(TinkerSmeltery.stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");
        GameRegistry.registerBlock(TinkerSmeltery.stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        //Items
        TinkerSmeltery.metalPattern = new MetalPattern("cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
        GameRegistry.registerItem(TinkerSmeltery.metalPattern, "metalPattern");
        TConstructRegistry.addItemToDirectory("metalPattern", TinkerSmeltery.metalPattern);
        String[] patternTypes = { "ingot", "toolRod", "pickaxeHead", "shovelHead", "hatchetHead", "swordBlade", "wideGuard", "handGuard", "crossbar", "binding", "frypanHead", "signHead", "knifeBlade", "chiselHead", "toughRod", "toughBinding", "largePlate", "broadAxeHead", "scytheHead", "excavatorHead", "largeBlade", "hammerHead", "fullGuard" };
        for (int i = 0; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Cast", new ItemStack(TinkerSmeltery.metalPattern, 1, i));
        }

        oreRegistry();
    }

    @Handler
    public void init (FMLInitializationEvent event)
    {
        proxy.initialize();
        craftingTableRecipes();
        addRecipesForSmeltery();
        addRecipesForTableCasting();
        addRecipesForBasinCasting();
        addRecipesForFurnace();
    }

    @Handler
    public void postInit (FMLPostInitializationEvent evt)
    {
        addOreDictionarySmelteryRecipes();
        modIntegration();
    }

    private void craftingTableRecipes ()
    {

        String[] patSurround = { "###", "#m#", "###" };

        // stained Glass Recipes
        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };
        String color = "";
        for (int i = 0; i < 16; i++)
        {
            color = dyeTypes[15 - i];
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, 8, i), patSurround, 'm', color, '#', TinkerSmeltery.clearGlass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, 1, i), color, TinkerSmeltery.clearGlass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, 8, i), patSurround, 'm', color, '#', new ItemStack(TinkerSmeltery.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClear, 1, i), color, new ItemStack(TinkerSmeltery.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', TinkerSmeltery.glassPane));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, i), color, TinkerSmeltery.glassPane));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, i), color, new ItemStack(TinkerSmeltery.stainedGlassClearPane, 1, Short.MAX_VALUE)));
        }

        // Glass Recipes
        GameRegistry.addRecipe(new ItemStack(Items.glass_bottle, 3), new Object[] { "# #", " # ", '#', TinkerSmeltery.clearGlass });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.daylight_detector), new Object[] { "GGG", "QQQ", "WWW", 'G', "blockGlass", 'Q', Items.quartz, 'W', "slabWood" }));
        GameRegistry.addRecipe(new ItemStack(Blocks.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', TinkerSmeltery.clearGlass, 'S', Items.nether_star, 'O', Blocks.obsidian });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.glassPane, 16, 0), "GGG", "GGG", 'G', TinkerSmeltery.clearGlass));

        // Smeltery Components Recipes
        ItemStack searedBrick = new ItemStack(TinkerTools.materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); // Controller
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); // Drain
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 2), "bb", "bb", 'b', searedBrick); // Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTank, 1, 0), patSurround, '#', searedBrick, 'm', "blockGlass")); // Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "blockGlass")); // Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "blockGlass")); // Window
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); // Table
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); // Faucet
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); // Basin
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); // Channel

        searedBrick = new ItemStack(TinkerTools.materials, 1, 37);
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smelteryNether, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); // Controller
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smelteryNether, 1, 1), "b b", "b b", "b b", 'b', searedBrick); // Drain
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.smelteryNether, 1, 2), "bb", "bb", 'b', searedBrick); // Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTankNether, 1, 0), patSurround, '#', searedBrick, 'm', "blockGlass")); // Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTankNether, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "blockGlass")); // Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerSmeltery.lavaTankNether, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "blockGlass")); // Window
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlockNether, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); // Table
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlockNether, 1, 1), "b b", " b ", 'b', searedBrick); // Faucet
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedBlockNether, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); // Basin
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); // Channel

        // Slab Smeltery Components Recipes
        for (int i = 0; i < 7; i++)
        {
            GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.speedSlab, 6, i), "bbb", 'b', new ItemStack(TinkerSmeltery.speedBlock, 1, i));
        }
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 0), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 1), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 2), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 3), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 6));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 4), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 8));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 5), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 9));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 6), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TinkerSmeltery.searedSlab, 6, 7), "bbb", 'b', new ItemStack(TinkerSmeltery.smeltery, 1, 11));
    }

    public void addOreDictionarySmelteryRecipes ()
    {
        List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.getFluidType("Water"), FluidType.getFluidType("Stone"), FluidType.getFluidType("Emerald"), FluidType.getFluidType("Ender"), FluidType.getFluidType("Glass"), FluidType.getFluidType("Slime"), FluidType.getFluidType("Obsidian") });
        Iterator iter = FluidType.fluidTypes.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            FluidType ft = (FluidType) pairs.getValue();
            if (exceptions.contains(ft))
                continue;
            String fluidTypeName = (String) pairs.getKey();

            // Nuggets
            Smeltery.addDictionaryMelting("nugget" + fluidTypeName, ft, -100, TConstruct.nuggetLiquidValue);
            registerNuggetCasting(ft, "nugget" + fluidTypeName);

            // Ingots, Dust
            registerIngotCasting(ft, "ingot" + fluidTypeName);
            Smeltery.addDictionaryMelting("ingot" + fluidTypeName, ft, -50, TConstruct.ingotLiquidValue);
            Smeltery.addDictionaryMelting("dust" + fluidTypeName, ft, -75, TConstruct.ingotLiquidValue);

            // Factorization support
            Smeltery.addDictionaryMelting("crystalline" + fluidTypeName, ft, -50, TConstruct.ingotLiquidValue);

            // Ores
            Smeltery.addDictionaryMelting("ore" + fluidTypeName, ft, 0, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre));

            // NetherOres support
            Smeltery.addDictionaryMelting("oreNether" + fluidTypeName, ft, 75, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 2));

            // DenseOres support
            Smeltery.addDictionaryMelting("denseore" + fluidTypeName, ft, 75, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 3));

            // DenseOres support
            Smeltery.addDictionaryMelting("orePoor" + fluidTypeName, ft, 75, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsPerOre));

            // Blocks
            registerBlockCasting(ft, "block" + fluidTypeName);
            Smeltery.addDictionaryMelting("block" + fluidTypeName, ft, 100, TConstruct.blockLiquidValue);

            if (ft.isToolpart)
            {
                TinkerTools.registerPatternMaterial("ingot" + fluidTypeName, 2, fluidTypeName);
                TinkerTools.registerPatternMaterial("block" + fluidTypeName, 18, fluidTypeName);
            }
        }
        // Obsidian, different dust amount
        {
            FluidType ft = FluidType.getFluidType("Obsidian");
            String fluidTypeName = "Obsidian";
            Smeltery.addDictionaryMelting("nugget" + fluidTypeName, ft, -100, TConstruct.nuggetLiquidValue);
            registerNuggetCasting(ft, "nugget" + fluidTypeName);

            // Ingots, Dust
            registerIngotCasting(ft, "ingot" + fluidTypeName);
            Smeltery.addDictionaryMelting("ingot" + fluidTypeName, ft, -50, TConstruct.ingotLiquidValue);
            Smeltery.addDictionaryMelting("dust" + fluidTypeName, ft, -75, TConstruct.ingotLiquidValue / 4);

            // Factorization support
            Smeltery.addDictionaryMelting("crystalline" + fluidTypeName, ft, -50, TConstruct.ingotLiquidValue);

            // Ores
            Smeltery.addDictionaryMelting("ore" + fluidTypeName, ft, 0, ((int) TConstruct.ingotLiquidValue * (int) PHConstruct.ingotsPerOre));

            // Poor ores
            Smeltery.addDictionaryMelting("orePoor" + fluidTypeName, ft, 0, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsPerOre * 1.5f));

            // NetherOres support
            Smeltery.addDictionaryMelting("oreNether" + fluidTypeName, ft, 75, ((int) TConstruct.ingotLiquidValue * (int) PHConstruct.ingotsPerOre * 2));

            // Blocks
            Smeltery.addDictionaryMelting("block" + fluidTypeName, ft, 100, TConstruct.ingotLiquidValue*2);

            if (ft.isToolpart)
            {
                TinkerTools.registerPatternMaterial("ingot" + fluidTypeName, 2, fluidTypeName);
                TinkerTools.registerPatternMaterial("block" + fluidTypeName, 18, fluidTypeName);
            }
        }

        // Compressed materials. max 4x because it's too much otherwise.
        for (int i = 1; i <= 4; i++)
        {
            Smeltery.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.getFluidType("Stone"), 0, TConstruct.stoneLiquidValue * (int) Math.pow(9, i));
        }
        Smeltery.addDictionaryMelting("compressedSand1x", FluidType.getFluidType("Glass"), 175, FluidContainerRegistry.BUCKET_VOLUME * 9);
        Smeltery.addDictionaryMelting("compressedSand2x", FluidType.getFluidType("Glass"), 175, FluidContainerRegistry.BUCKET_VOLUME * 9 * 9);
    }

    private void addRecipesForTableCasting ()
    {
        /* Smeltery */
        ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
        ItemStack gemcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 26);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        // Blank
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.blankPattern, 1, 1), new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), 80);
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.blankPattern, 1, 2), new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(Items.emerald), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(Items.emerald), 80);

        // Ingots
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 2), new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.ingotLiquidValue / 4), ingotcast, 80); // stone

        // Misc
        tableCasting.addCastingRecipe(new ItemStack(Items.emerald), new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640), gemcast, 80);
        tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 36), new FluidStack(TinkerSmeltery.glueFluid, TConstruct.ingotLiquidValue), null, 50);
        tableCasting.addCastingRecipe(new ItemStack(TinkerWorld.strangeFood, 1, 1), new FluidStack(TinkerSmeltery.bloodFluid, 160), null, 50);

        // Buckets
        ItemStack bucket = new ItemStack(Items.bucket);

        Item thermalBucket = GameRegistry.findItem("ThermalFoundation", "bucket");

        for (int sc = 0; sc < 26; sc++)
        {
            if (TinkerSmeltery.fluids[sc] != null) {
                // TE support
                if(fluids[sc] == TinkerSmeltery.moltenEnderFluid && thermalBucket != null)
                    // bucket of resonant ender instead of liquified ender
                    tableCasting.addCastingRecipe(new ItemStack(thermalBucket, 1, 2), new FluidStack(TinkerSmeltery.fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
                else
                    tableCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.buckets, 1, sc), new FluidStack(TinkerSmeltery.fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
            }
        }

        // water and lava bucket
        tableCasting.addCastingRecipe(new ItemStack(Items.water_bucket), new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
        tableCasting.addCastingRecipe(new ItemStack(Items.lava_bucket), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

        // Clear glass pane casting
        tableCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.glassPane), new FluidStack(TinkerSmeltery.moltenGlassFluid, 250), null, 80);

        // Metal toolpart casting
        TinkerSmeltery.liquids = new FluidStack[] { new FluidStack(TinkerSmeltery.moltenIronFluid, 1), new FluidStack(TinkerSmeltery.moltenCopperFluid, 1), new FluidStack(TinkerSmeltery.moltenCobaltFluid, 1), new FluidStack(TinkerSmeltery.moltenArditeFluid, 1), new FluidStack(TinkerSmeltery.moltenManyullynFluid, 1), new FluidStack(TinkerSmeltery.moltenBronzeFluid, 1), new FluidStack(TinkerSmeltery.moltenAlumiteFluid, 1), new FluidStack(TinkerSmeltery.moltenObsidianFluid, 1), new FluidStack(TinkerSmeltery.moltenSteelFluid, 1), new FluidStack(TinkerSmeltery.pigIronFluid, 1) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16, 18 }; // ItemStack
                                                                                 // damage
                                                                                 // value
        int fluidAmount = 0;
        Fluid fs = null;

        for (int iter = 0; iter < TinkerTools.patternOutputs.length; iter++)
        {
            if (TinkerTools.patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(TinkerSmeltery.metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(TinkerTools.patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(TinkerTools.patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < TinkerSmeltery.liquids.length; iterTwo++)
                {
                    fs = TinkerSmeltery.liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    ItemStack metalCast = new ItemStack(TinkerTools.patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }
            }
        }

        tableCasting.addCastingRecipe(new ItemStack(Items.ender_pearl), new FluidStack(TinkerSmeltery.moltenEnderFluid, 250), new ItemStack(TinkerSmeltery.metalPattern, 1, 10), 50);
        tableCasting.addCastingRecipe(new ItemStack(Items.ender_pearl), new FluidStack(TinkerSmeltery.moltenEnderFluid, 250), new ItemStack(TinkerSmeltery.metalPattern, 1, 26), 50);

        ItemStack[] ingotShapes = { new ItemStack(Items.brick), new ItemStack(Items.netherbrick), new ItemStack(TinkerTools.materials, 1, 2), new ItemStack(TinkerTools.materials, 1, 37) };
        for (int i = 0; i < ingotShapes.length; i++)
        {
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotShapes[i], false, 50);
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), ingotShapes[i], false, 50);
        }

        ItemStack fullguardCast = new ItemStack(TinkerSmeltery.metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(TinkerTools.fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(TinkerTools.fullGuard, 1, Short.MAX_VALUE), false, 50);

        // Golden Food Stuff
        // 9 gold nuggets
        FluidStack goldAmount = new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.nuggetLiquidValue * 8);
        tableCasting.addCastingRecipe(new ItemStack(Items.golden_carrot, 1), goldAmount, new ItemStack(Items.carrot), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Items.speckled_melon, 1), goldAmount, new ItemStack(Items.melon), true, 50);
        // 8 gold ingots
        goldAmount = new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 8);
        tableCasting.addCastingRecipe(new ItemStack(Items.golden_apple, 1), goldAmount, new ItemStack(Items.apple), true, 50);
        if (TinkerWorld.goldHead != null)
            tableCasting.addCastingRecipe(new ItemStack(TinkerWorld.goldHead), goldAmount, new ItemStack(Items.skull, 1, 3), true, 50);


        // Ensure TConstruct ingots are always first. Otherwise you might get ingots from other mods from casting
        if (PHConstruct.tconComesFirst && TinkerTools.materials != null)
        {
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 9), new FluidStack(moltenCopperFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Copper
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 10), new FluidStack(moltenTinFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Tin
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 11), new FluidStack(moltenAluminumFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Aluminum
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 3), new FluidStack(moltenCobaltFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Cobalt
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 4), new FluidStack(moltenArditeFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Ardite
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 5), new FluidStack(moltenManyullynFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Manyullyn
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 13), new FluidStack(moltenBronzeFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Bronze
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 14), new FluidStack(moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Alubrass
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 15), new FluidStack(moltenAlumiteFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Alumite
            tableCasting.addCastingRecipe(new ItemStack(TinkerTools.materials, 1, 16), new FluidStack(moltenSteelFluid, TConstruct.ingotLiquidValue), ingotcast, false, 50); //Steel
        }
    }

    protected void addRecipesForBasinCasting ()
    {
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        // Block Casting
        basinCasting.addCastingRecipe(new ItemStack(Blocks.iron_block), new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.blockLiquidValue), null, true, 100); // Iron
        basinCasting.addCastingRecipe(new ItemStack(Blocks.gold_block), new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.blockLiquidValue), null, true, 100); // gold
        if (PHConstruct.tconComesFirst)
        {
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 3), new FluidStack(TinkerSmeltery.moltenCopperFluid, TConstruct.blockLiquidValue), null, true, 100); // copper
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 5), new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.blockLiquidValue), null, true, 100); // tin
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 6), new FluidStack(TinkerSmeltery.moltenAluminumFluid, TConstruct.blockLiquidValue), null, true, 100); // aluminum
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 0), new FluidStack(TinkerSmeltery.moltenCobaltFluid, TConstruct.blockLiquidValue), null, true, 100); // cobalt
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 1), new FluidStack(TinkerSmeltery.moltenArditeFluid, TConstruct.blockLiquidValue), null, true, 100); // ardite
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 4), new FluidStack(TinkerSmeltery.moltenBronzeFluid, TConstruct.blockLiquidValue), null, true, 100); // bronze
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 7), new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.blockLiquidValue), null, true, 100); // albrass
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 2), new FluidStack(TinkerSmeltery.moltenManyullynFluid, TConstruct.blockLiquidValue), null, true, 100); // manyullyn
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 8), new FluidStack(TinkerSmeltery.moltenAlumiteFluid, TConstruct.blockLiquidValue), null, true, 100); // alumite
            basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 9), new FluidStack(TinkerSmeltery.moltenSteelFluid, TConstruct.blockLiquidValue), null, true, 100); // steel
        }
        basinCasting.addCastingRecipe(new ItemStack(Blocks.obsidian), new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.ingotLiquidValue*2), null, true, 100);// obsidian
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.clearGlass, 1, 0), new FluidStack(TinkerSmeltery.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100); // glass
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 4), new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.ingotLiquidValue), null, true, 100); // seared
        // stone
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.smeltery, 1, 5), new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.chunkLiquidValue), new ItemStack(Blocks.cobblestone), true, 100);
        basinCasting.addCastingRecipe(new ItemStack(Blocks.emerald_block), new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640 * 9), null, true, 100); // emerald
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 1, 0), new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.nuggetLiquidValue), new ItemStack(Blocks.gravel), true, 100); // brownstone
        if (PHConstruct.craftEndstone)
        {
            basinCasting.addCastingRecipe(new ItemStack(Blocks.end_stone), new FluidStack(TinkerSmeltery.moltenEnderFluid, 50), new ItemStack(Blocks.obsidian), true, 100);
            basinCasting.addCastingRecipe(new ItemStack(Blocks.end_stone), new FluidStack(TinkerSmeltery.moltenEnderFluid, 250), new ItemStack(Blocks.sandstone), true, 100);
        }
        basinCasting.addCastingRecipe(new ItemStack(TinkerWorld.metalBlock, 1, 10), new FluidStack(TinkerSmeltery.moltenEnderFluid, 1000), null, true, 100); // ender
        basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.glueBlock), new FluidStack(TinkerSmeltery.glueFluid, TConstruct.blockLiquidValue), null, true, 100); // glue

        // basinCasting.addCastingRecipe(new ItemStack(slimeGel, 1, 0), new
        // FluidStack(blueSlimeFluid, FluidContainerRegistry.BUCKET_VOLUME),
        // null, true, 100);

        // Armor casts
        /*
         * FluidRenderProperties frp = new
         * FluidRenderProperties(Applications.BASIN.minHeight, 0.65F,
         * Applications.BASIN); FluidStack aluFlu = new
         * FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue *
         * 10); FluidStack gloFlu = new FluidStack(TRepo.moltenGoldFluid,
         * TConstruct.ingotLiquidValue * 10); ItemStack[] armor = { new
         * ItemStack(helmetWood), new ItemStack(chestplateWood), new
         * ItemStack(leggingsWood), new ItemStack(bootsWood) }; for (int sc = 0;
         * sc < armor.length; sc++) { basinCasting.addCastingRecipe(new
         * ItemStack(armorPattern, 1, sc), aluFlu, armor[sc], 50, frp);
         * basinCasting.addCastingRecipe(new ItemStack(armorPattern, 1, sc),
         * gloFlu, armor[sc], 50, frp); }
         */
    }

    protected static void addRecipesForSmeltery ()
    {
        // Smeltery fuels
        Smeltery.addSmelteryFuel(FluidRegistry.LAVA, 1300, 80); // lava lasts 4 seconds per 15 mb

        // BLOOD FOR THE BLOOD GOD
        if (TinkerWorld.meatBlock != null)
        {
            Smeltery.addMelting(new ItemStack(Items.rotten_flesh), TinkerWorld.meatBlock, 0, 200, new FluidStack(bloodFluid, 5));
            Smeltery.addMelting(new ItemStack(TinkerWorld.strangeFood, 1, 1), TinkerWorld.meatBlock, 0, 80, new FluidStack(bloodFluid, 160));
        }

        // Alloy Smelting
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenBronzeFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsBronzeAlloy)), new FluidStack(TinkerSmeltery.moltenCopperFluid, TConstruct.nuggetLiquidValue * 3), new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.nuggetLiquidValue)); // Bronze
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenAlubrassFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsAluminumBrassAlloy)), new FluidStack(TinkerSmeltery.moltenAluminumFluid, TConstruct.nuggetLiquidValue * 3), new FluidStack(TinkerSmeltery.moltenCopperFluid, TConstruct.nuggetLiquidValue * 1)); // Aluminum Brass
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenAlumiteFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsAlumiteAlloy)), new FluidStack(TinkerSmeltery.moltenAluminumFluid, TConstruct.nuggetLiquidValue * 5), new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.nuggetLiquidValue * 2), new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.nuggetLiquidValue * 2)); // Alumite
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenManyullynFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsManyullynAlloy)), new FluidStack(TinkerSmeltery.moltenCobaltFluid, TConstruct.nuggetLiquidValue), new FluidStack(TinkerSmeltery.moltenArditeFluid, TConstruct.nuggetLiquidValue)); // Manyullyn
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.pigIronFluid, (int) (TConstruct.nuggetLiquidValue * PHConstruct.ingotsPigironAlloy)), new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.nuggetLiquidValue), new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640), new FluidStack(TinkerSmeltery.bloodFluid, 80)); // Pigiron
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.oreLiquidValue), new FluidStack(FluidRegistry.LAVA, 1000), new FluidStack(FluidRegistry.WATER, 1000)); //Obsidian
        // Stone parts
        FluidType stone = FluidType.getFluidType("Stone");
        for (int sc = 0; sc < TinkerTools.patternOutputs.length; sc++)
        {
            if (TinkerTools.patternOutputs[sc] != null)
            {
                Smeltery.addMelting(stone, new ItemStack(TinkerTools.patternOutputs[sc], 1, 1), 1, (8 * ((IPattern) TinkerTools.woodPattern).getPatternCost(new ItemStack(TinkerTools.woodPattern, 1, sc + 1))) / 2);
            }
        }

        FluidType iron = FluidType.getFluidType("Iron");
        FluidType gold = FluidType.getFluidType("Gold");
        FluidType steel = FluidType.getFluidType("Steel");

        // Chunks
        Smeltery.addMelting(FluidType.getFluidType("Stone"), new ItemStack(TinkerTools.toolShard, 1, 1), 0, 4);
        Smeltery.addMelting(iron, new ItemStack(TinkerTools.toolShard, 1, 2), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Obsidian"), new ItemStack(TinkerTools.toolShard, 1, 6), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Cobalt"), new ItemStack(TinkerTools.toolShard, 1, 10), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Ardite"), new ItemStack(TinkerTools.toolShard, 1, 11), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Manyullyn"), new ItemStack(TinkerTools.toolShard, 1, 12), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Copper"), new ItemStack(TinkerTools.toolShard, 1, 13), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Bronze"), new ItemStack(TinkerTools.toolShard, 1, 14), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Alumite"), new ItemStack(TinkerTools.toolShard, 1, 15), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(TinkerTools.toolShard, 1, 16), 0, TConstruct.chunkLiquidValue);

        // Items
        Smeltery.addMelting(FluidType.getFluidType("AluminumBrass"), new ItemStack(TinkerTools.blankPattern, 4, 1), -50, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(TinkerTools.blankPattern, 4, 2), -50, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.getFluidType("Glue"), new ItemStack(TinkerTools.materials, 1, 36), 0, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.getFluidType("Ender"), new ItemStack(Items.ender_pearl, 4), 0, 250);
        Smeltery.addMelting(TinkerWorld.metalBlock, 10, 50, new FluidStack(moltenEnderFluid, 1000));
        Smeltery.addMelting(FluidType.getFluidType("Water"), new ItemStack(Items.snowball, 1, 0), 0, 125);
        Smeltery.addMelting(iron, new ItemStack(Items.flint_and_steel, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.compass, 1, 0), 0, TConstruct.ingotLiquidValue * 4);
        Smeltery.addMelting(iron, new ItemStack(Items.bucket), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(iron, new ItemStack(Items.minecart), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.chest_minecart), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.furnace_minecart), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.hopper_minecart), 50, TConstruct.ingotLiquidValue * 10);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_door), 0, TConstruct.ingotLiquidValue * 6);
        Smeltery.addMelting(iron, new ItemStack(Items.cauldron), 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(iron, new ItemStack(Items.shears), 0, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.getFluidType("Emerald"), new ItemStack(Items.emerald), -50, 640);

        Smeltery.addMelting(FluidType.getFluidType("Ardite"), new ItemStack(TinkerTools.materials, 1, 38), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Cobalt"), new ItemStack(TinkerTools.materials, 1, 39), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Aluminum"), new ItemStack(TinkerTools.materials, 1, 40), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Manyullyn"), new ItemStack(TinkerTools.materials, 1, 41), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("AluminumBrass"), new ItemStack(TinkerTools.materials, 1, 42), 0, TConstruct.ingotLiquidValue);

        // Blocks melt as themselves!
        // Ore
        Smeltery.addMelting(Blocks.iron_ore, 0, 600, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Blocks.gold_ore, 0, 400, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TinkerWorld.oreGravel, 0, 600, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TinkerWorld.oreGravel, 1, 400, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));

        // Blocks
        Smeltery.addMelting(Blocks.iron_block, 0, 600, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Blocks.gold_block, 0, 400, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Blocks.obsidian, 0, 800, new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Blocks.ice, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 1000));
        Smeltery.addMelting(Blocks.snow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 500));
        Smeltery.addMelting(Blocks.snow_layer, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 250));
        Smeltery.addMelting(Blocks.sand, 0, 625, new FluidStack(TinkerSmeltery.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Blocks.glass, 0, 625, new FluidStack(TinkerSmeltery.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Blocks.glass_pane, 0, 625, new FluidStack(TinkerSmeltery.moltenGlassFluid, 250));
        Smeltery.addMelting(Blocks.stone, 0, 800, new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.stoneLiquidValue));
        Smeltery.addMelting(Blocks.cobblestone, 0, 800, new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.stoneLiquidValue));
        Smeltery.addMelting(Blocks.emerald_block, 0, 800, new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640 * 9));
        Smeltery.addMelting(Blocks.emerald_ore, 0, 800, new FluidStack(TinkerSmeltery.moltenEmeraldFluid, 640 * 2)); // the ore also is done here
        Smeltery.addMelting(TinkerSmeltery.glueBlock, 0, 250, new FluidStack(TinkerSmeltery.glueFluid, TConstruct.blockLiquidValue));
        Smeltery.addMelting(TinkerTools.craftedSoil, 1, 600, new FluidStack(TinkerSmeltery.moltenStoneFluid, TConstruct.ingotLiquidValue / 4));

        Smeltery.addMelting(TinkerSmeltery.clearGlass, 0, 500, new FluidStack(TinkerSmeltery.moltenGlassFluid, 1000));
        Smeltery.addMelting(TinkerSmeltery.glassPane, 0, 350, new FluidStack(TinkerSmeltery.moltenGlassFluid, 250));

        for (int i = 0; i < 16; i++)
        {
            Smeltery.addMelting(TinkerSmeltery.stainedGlassClear, i, 500, new FluidStack(TinkerSmeltery.moltenGlassFluid, 1000));
            Smeltery.addMelting(TinkerSmeltery.stainedGlassClearPane, i, 350, new FluidStack(TinkerSmeltery.moltenGlassFluid, 250));
        }

        // Bricks
        Smeltery.addMelting(TinkerTools.multiBrick, 4, 600, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrickFancy, 4, 600, new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrick, 5, 400, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrickFancy, 5, 400, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TinkerTools.multiBrick, 0, 800, new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TinkerTools.multiBrickFancy, 0, 800, new FluidStack(TinkerSmeltery.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));

        // Vanilla blocks
        Smeltery.addMelting(iron, new ItemStack(Blocks.iron_bars), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(iron, new ItemStack(Blocks.heavy_weighted_pressure_plate), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Blocks.light_weighted_pressure_plate, 4), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Blocks.rail), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(gold, new ItemStack(Blocks.golden_rail), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Blocks.detector_rail), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Blocks.activator_rail), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.getFluidType("Obsidian"), new ItemStack(Blocks.enchanting_table), 0, TConstruct.ingotLiquidValue * 4);
        // Smeltery.addMelting(iron, new ItemStack(Blocks.cauldron),
        // 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(iron, new ItemStack(Blocks.anvil, 1, 0), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(iron, new ItemStack(Blocks.anvil, 1, 1), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(iron, new ItemStack(Blocks.anvil, 1, 2), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(iron, new ItemStack(Blocks.hopper), 0, TConstruct.ingotLiquidValue * 5);

        // Vanilla Armor
        Smeltery.addMelting(iron, new ItemStack(Items.iron_helmet, 1, 0), 50, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_chestplate, 1, 0), 50, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_leggings, 1, 0), 50, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_boots, 1, 0), 50, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(gold, new ItemStack(Items.golden_helmet, 1, 0), 50, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_chestplate, 1, 0), 50, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_leggings, 1, 0), 50, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_boots, 1, 0), 50, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_helmet, 1, 0), 25, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_chestplate, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_leggings, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(steel, new ItemStack(Items.chainmail_boots, 1, 0), 25, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(iron, new ItemStack(Items.iron_horse_armor, 1), 100, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_horse_armor, 1), 100, TConstruct.ingotLiquidValue * 8);

        // Vanilla tools
        Smeltery.addMelting(iron, new ItemStack(Items.iron_hoe, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_sword, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_shovel, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_pickaxe, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(iron, new ItemStack(Items.iron_axe, 1, 0), 0, TConstruct.ingotLiquidValue * 3);

        Smeltery.addMelting(gold, new ItemStack(Items.golden_hoe, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_sword, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_shovel, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_pickaxe, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_axe, 1, 0), 0, TConstruct.ingotLiquidValue * 3);

        Smeltery.addMelting(gold, new ItemStack(Items.golden_apple, 1, 0), 250, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_apple, 1, 1), 600, TConstruct.ingotLiquidValue * 72);
        Smeltery.addMelting(gold, new ItemStack(Items.speckled_melon, 1, 0), -20, TConstruct.ingotLiquidValue * 8 / 9);
        Smeltery.addMelting(gold, new ItemStack(Items.golden_carrot, 1, 0), -20, TConstruct.ingotLiquidValue * 8 / 9);
    }

    private void registerIngotCasting (FluidType ft, String name)
    {
        ItemStack pattern = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        for (ItemStack ore : OreDictionary.getOres(name))
        {
            tableCasting.addCastingRecipe(pattern, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(ore.getItem(), 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(pattern, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(ore.getItem(), 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(new ItemStack(ore.getItem(), 1, ore.getItemDamage()), new FluidStack(ft.fluid, TConstruct.ingotLiquidValue), pattern, 80);
        }
    }

    private void registerNuggetCasting (FluidType ft, String name)
    {
        ItemStack pattern = new ItemStack(TinkerSmeltery.metalPattern, 1, 27);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        for (ItemStack ore : OreDictionary.getOres(name))
        {
            // don't do oreberries. That'd be silly.
            if(ore.getItem() != null && ore.getItem() instanceof OreBerries) {
                boolean isOreberry = false;
                for(int id : OreDictionary.getOreIDs(ore))
                    if(OreDictionary.getOreName(id).startsWith("oreberry"))
                        isOreberry = true;

                if(isOreberry)
                    continue;
            }
            tableCasting.addCastingRecipe(pattern, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(ore.getItem(), 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(pattern, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(ore.getItem(), 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(new ItemStack(ore.getItem(), 1, ore.getItemDamage()), new FluidStack(ft.fluid, TConstruct.nuggetLiquidValue), pattern, 40);
        }
    }

    private void registerBlockCasting (FluidType ft, String name)
    {
        for (ItemStack ore : OreDictionary.getOres(name))
        {
            TConstructRegistry.getBasinCasting().addCastingRecipe(new ItemStack(ore.getItem(), 1, ore.getItemDamage()), new FluidStack(ft.fluid, TConstruct.blockLiquidValue), 100);
        }
    }

    public void addRecipesForFurnace ()
    {
        FurnaceRecipes.smelting().func_151394_a(new ItemStack(TinkerSmeltery.speedBlock, 1, 0), new ItemStack(TinkerSmeltery.speedBlock, 1, 2), 0.2f);
    }

    public void oreRegistry ()
    {
        String[] glassTypes = { "glassBlack", "glassRed", "glassGreen", "glassBrown", "glassBlue", "glassPurple", "glassCyan", "glassLightGray", "glassGray", "glassPink", "glassLime", "glassYellow", "glassLightBlue", "glassMagenta", "glassOrange", "glassWhite" };
        for (int i = 0; i < 16; i++)
        {
            OreDictionary.registerOre(glassTypes[15 - i], new ItemStack(TinkerSmeltery.stainedGlassClear, 1, i));
        }

        OreDictionary.registerOre("blockGlass", new ItemStack(TinkerSmeltery.clearGlass));
    }

    public void modIntegration ()
    {
        /* Natura */
        Block taintedSoil = GameRegistry.findBlock("Natura", "soil.tainted");
        Block heatSand = GameRegistry.findBlock("Natura", "heatsand");
        if (taintedSoil != null && heatSand != null)
            GameRegistry.addShapelessRecipe(new ItemStack(TinkerTools.craftedSoil, 2, 6), Items.nether_wart, taintedSoil, heatSand);

        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        ArrayList<ItemStack> ores;

        /* Extra Utilities */
        ores = OreDictionary.getOres("compressedGravel1x");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 9), new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.ingotLiquidValue), ores.get(0), true, 100);
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 9), new FluidStack(TinkerSmeltery.moltenElectrumFluid, TConstruct.ingotLiquidValue / 3), ores.get(0), true, 100);
        }
        ores = OreDictionary.getOres("compressedGravel2x"); // Higher won't save
                                                            // properly
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 81), new FluidStack(TinkerSmeltery.moltenTinFluid, TConstruct.blockLiquidValue), ores.get(0), true, 100);
            basinCasting.addCastingRecipe(new ItemStack(TinkerSmeltery.speedBlock, 81), new FluidStack(TinkerSmeltery.moltenElectrumFluid, TConstruct.blockLiquidValue / 3), ores.get(0), true, 100);
        }

        /* Rubber */
        ores = OreDictionary.getOres("itemRubber");
        if (ores.size() > 0)
        {
            FurnaceRecipes.smelting().func_151394_a(new ItemStack(TinkerTools.materials, 1, 36), ores.get(0), 0.2f);
        }
    }



    public static Fluid registerFluid(String name) {
        return registerFluid(name, "liquid_" + name);
    }

    public static Fluid registerFluid(String name, String texture) {
        return registerFluid(name, name + ".molten", "fluid.molten." + name, texture, 3000, 6000, 1300, Material.lava);
    }

    public static Fluid registerFluid(String name, String fluidName, String blockName, String texture, int density, int viscosity, int temperature, Material material) {
        // create the new fluid
        Fluid fluid = new Fluid(fluidName).setDensity(density).setViscosity(viscosity).setTemperature(temperature);

        if(material == Material.lava)
            fluid.setLuminosity(12);

        // register it if it's not already existing
        boolean isFluidPreRegistered = !FluidRegistry.registerFluid(fluid);

        // register our fluid block for the fluid
        // this constructor implicitly does fluid.setBlock to it, that's why it's not called separately
        TConstructFluid block = new TConstructFluid(fluid, material, texture);
        block.setBlockName(blockName);
        GameRegistry.registerBlock(block, blockName);

        fluid.setBlock(block);
        block.setFluid(fluid);

        // if the fluid was already registered we use that one instead
        if (isFluidPreRegistered)
        {
            fluid = FluidRegistry.getFluid(fluidName);

            // don't change the fluid icons of already existing fluids
            if(fluid.getBlock() != null)
                block.suppressOverwritingFluidIcons();
            // if no block is registered with an existing liquid, we set our own
            else
                fluid.setBlock(block);
        }


        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), new ItemStack(Items.bucket)) == null) {
            // custom hacks for teh lookup. hoooray for inconsintency.
            if(name.equals("aluminiumbrass"))
                name = "alubrass";
            if(name.equals("platinum"))
                name = "shiny";
            boolean reg = false;
            for(int i = 0; i < FilledBucket.textureNames.length; i++)
                if(FilledBucket.textureNames[i].equals(name)) {
                    FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(fluid, 1000), new ItemStack(TinkerSmeltery.buckets, 1, i), new ItemStack(Items.bucket)));
                    reg = true;
                }

            if(!reg)
                TConstruct.logger.error("Couldn't register fluid container for " + name);
        }

        return fluid;
    }
}
