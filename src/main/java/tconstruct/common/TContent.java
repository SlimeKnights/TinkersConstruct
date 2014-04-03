package tconstruct.common;

import java.util.HashMap;

import mantle.blocks.BlockUtils;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.achievements.TAchievements;
import tconstruct.blocks.BlockLandmine;
import tconstruct.blocks.BloodBlock;
import tconstruct.blocks.CastingChannelBlock;
import tconstruct.blocks.ConveyorBase;
import tconstruct.blocks.CraftingSlab;
import tconstruct.blocks.CraftingStationBlock;
import tconstruct.blocks.DryingRack;
import tconstruct.blocks.EquipBlock;
import tconstruct.blocks.FurnaceSlab;
import tconstruct.blocks.GlassBlockConnected;
import tconstruct.blocks.GlassBlockConnectedMeta;
import tconstruct.blocks.GlassPaneConnected;
import tconstruct.blocks.GlassPaneStained;
import tconstruct.blocks.GlueBlock;
import tconstruct.blocks.GlueFluid;
import tconstruct.blocks.GravelOre;
import tconstruct.blocks.LavaTankBlock;
import tconstruct.blocks.MeatBlock;
import tconstruct.blocks.MetalOre;
import tconstruct.blocks.MultiBrick;
import tconstruct.blocks.MultiBrickFancy;
import tconstruct.blocks.OreberryBush;
import tconstruct.blocks.OreberryBushEssence;
import tconstruct.blocks.SearedBlock;
import tconstruct.blocks.SearedSlab;
import tconstruct.blocks.SlabBase;
import tconstruct.blocks.SlimeExplosive;
import tconstruct.blocks.SlimePad;
import tconstruct.blocks.SmelteryBlock;
import tconstruct.blocks.SoilBlock;
import tconstruct.blocks.SpeedBlock;
import tconstruct.blocks.SpeedSlab;
import tconstruct.blocks.StoneLadder;
import tconstruct.blocks.StoneTorch;
import tconstruct.blocks.TConstructFluid;
import tconstruct.blocks.TMetalBlock;
import tconstruct.blocks.TankAirBlock;
import tconstruct.blocks.ToolForgeBlock;
import tconstruct.blocks.ToolStationBlock;
import tconstruct.blocks.WoodRail;
import tconstruct.blocks.slime.SlimeFluid;
import tconstruct.blocks.slime.SlimeGel;
import tconstruct.blocks.slime.SlimeGrass;
import tconstruct.blocks.slime.SlimeLeaves;
import tconstruct.blocks.slime.SlimeSapling;
import tconstruct.blocks.slime.SlimeTallGrass;
import tconstruct.blocks.traps.BarricadeBlock;
import tconstruct.blocks.traps.Punji;
import tconstruct.client.StepSoundSlime;
import tconstruct.entity.BlueSlime;
import tconstruct.entity.Crystal;
import tconstruct.entity.FancyEntityItem;
import tconstruct.entity.item.EntityLandmineFirework;
import tconstruct.entity.item.ExplosivePrimed;
import tconstruct.entity.projectile.ArrowEntity;
import tconstruct.entity.projectile.DaggerEntity;
import tconstruct.entity.projectile.LaunchedPotion;
import tconstruct.items.Bowstring;
import tconstruct.items.CreativeModifier;
import tconstruct.items.DiamondApple;
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
import tconstruct.items.armor.ArmorBasic;
import tconstruct.items.armor.ExoArmor;
import tconstruct.items.armor.HeartCanister;
import tconstruct.items.armor.Knapsack;
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
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TContent implements IFuelHandler
{
    // Temporary items
    // public static Item armorTest = new ArmorStandard(2445, 4,
    // EnumArmorPart.HELMET).setCreativeTab(CreativeTabs.tabAllSearch);

    public TContent()
    {
        registerItems();
        TRecipes.registerItemRecipes();
        registerBlocks();
        TRecipes.registerBlockRecipes();
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
        // EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1,
        // TConstruct.instance, 32, 5, true);

        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        // EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13,
        // TConstruct.instance, 64, 5, true);
    }

    void registerBlocks ()
    {
        // Tool Station
        TRepo.toolStationWood = new ToolStationBlock(Material.wood).setBlockName("ToolStation");
        TRepo.toolForge = new ToolForgeBlock(Material.iron).setBlockName("ToolForge");
        TRepo.craftingStationWood = new CraftingStationBlock(Material.wood).setBlockName("CraftingStation");
        TRepo.craftingSlabWood = new CraftingSlab(Material.wood).setBlockName("CraftingSlab");
        TRepo.furnaceSlab = new FurnaceSlab(Material.rock).setBlockName("FurnaceSlab");

        TRepo.heldItemBlock = new EquipBlock(Material.wood).setBlockName("Frypan");
        
        /* battlesignBlock = new BattlesignBlock(PHConstruct.battlesignBlock).setUnlocalizedName("Battlesign");
        GameRegistry.registerBlock(battlesignBlock, "BattlesignBlock");
        ameRegistry.registerTileEntity(BattlesignLogic.class, "BattlesignLogic");*/

        TRepo.craftedSoil = new SoilBlock().setLightOpacity(0).setBlockName("TConstruct.Soil");
        TRepo.craftedSoil.stepSound = Block.soundTypeGravel;

        TRepo.searedSlab = new SearedSlab().setBlockName("SearedSlab");
        TRepo.searedSlab.stepSound = Block.soundTypeStone;

        TRepo.speedSlab = new SpeedSlab().setBlockName("SpeedSlab");
        TRepo.speedSlab.stepSound = Block.soundTypeStone;

        TRepo.metalBlock = new TMetalBlock(Material.iron, 10.0F).setBlockName("tconstruct.metalblock");
        TRepo.metalBlock.stepSound = Block.soundTypeMetal;

        TRepo.meatBlock = new MeatBlock().setBlockName("tconstruct.meatblock");
        TRepo.glueBlock = new GlueBlock().setBlockName("GlueBlock").setCreativeTab(TConstructRegistry.blockTab);

        TRepo.woolSlab1 = new SlabBase(Material.cloth, Blocks.wool, 0, 8).setBlockName("cloth");
        TRepo.woolSlab1.setStepSound(Block.soundTypeCloth).setCreativeTab(CreativeTabs.tabDecorations);
        TRepo.woolSlab2 = new SlabBase(Material.cloth, Blocks.wool, 8, 8).setBlockName("cloth");
        TRepo.woolSlab2.setStepSound(Block.soundTypeCloth).setCreativeTab(CreativeTabs.tabDecorations);

        // Smeltery
        TRepo.smeltery = new SmelteryBlock().setBlockName("Smeltery");
        TRepo.smelteryNether = new SmelteryBlock("nether").setBlockName("Smeltery");
        TRepo.lavaTank = new LavaTankBlock().setBlockName("LavaTank");
        TRepo.lavaTank.setStepSound(Block.soundTypeGlass);
        TRepo.lavaTankNether = new LavaTankBlock("nether").setStepSound(Block.soundTypeGlass).setBlockName("LavaTank");

        TRepo.searedBlock = new SearedBlock().setBlockName("SearedBlock");
        TRepo.searedBlockNether = new SearedBlock("nether").setBlockName("SearedBlock");

        TRepo.castingChannel = (new CastingChannelBlock()).setBlockName("CastingChannel");

        TRepo.tankAir = new TankAirBlock(Material.leaves).setBlockUnbreakable().setBlockName("tconstruct.tank.air");

        // Traps
        TRepo.landmine = new BlockLandmine().setHardness(0.5F).setResistance(0F).setStepSound(Block.soundTypeMetal).setCreativeTab(CreativeTabs.tabRedstone).setBlockName("landmine");
        TRepo.punji = new Punji().setBlockName("trap.punji");
        TRepo.barricadeOak = new BarricadeBlock(Blocks.log, 0).setBlockName("trap.barricade.oak");
        TRepo.barricadeSpruce = new BarricadeBlock(Blocks.log, 1).setBlockName("trap.barricade.spruce");
        TRepo.barricadeBirch = new BarricadeBlock(Blocks.log, 2).setBlockName("trap.barricade.birch");
        TRepo.barricadeJungle = new BarricadeBlock(Blocks.log, 3).setBlockName("trap.barricade.jungle");
        TRepo.slimeExplosive = new SlimeExplosive().setHardness(0.0F).setStepSound(Block.soundTypeGrass).setBlockName("explosive.slime");

        TRepo.dryingRack = new DryingRack().setBlockName("Armor.DryingRack");

        // Liquids
        TRepo.liquidMetal = new MaterialLiquid(MapColor.tntColor);

        TRepo.moltenIronFluid = new Fluid("iron.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenIronFluid))
            TRepo.moltenIronFluid = FluidRegistry.getFluid("iron.molten");
        TRepo.moltenIron = new TConstructFluid(TRepo.moltenIronFluid, Material.lava, "liquid_iron").setBlockName("fluid.molten.iron");
        GameRegistry.registerBlock(TRepo.moltenIron, "fluid.molten.iron");
        TRepo.moltenIronFluid.setBlock(TRepo.moltenIron).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenIronFluid, 1000), new ItemStack(TRepo.buckets, 1, 0), new ItemStack(Items.bucket)));

        TRepo.moltenGoldFluid = new Fluid("gold.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenGoldFluid))
            TRepo.moltenGoldFluid = FluidRegistry.getFluid("gold.molten");
        TRepo.moltenGold = new TConstructFluid(TRepo.moltenGoldFluid, Material.lava, "liquid_gold").setBlockName("fluid.molten.gold");
        GameRegistry.registerBlock(TRepo.moltenGold, "fluid.molten.gold");
        TRepo.moltenGoldFluid.setBlock(TRepo.moltenGold).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenGoldFluid, 1000), new ItemStack(TRepo.buckets, 1, 1), new ItemStack(Items.bucket)));

        TRepo.moltenCopperFluid = new Fluid("copper.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenCopperFluid))
            TRepo.moltenCopperFluid = FluidRegistry.getFluid("copper.molten");
        TRepo.moltenCopper = new TConstructFluid(TRepo.moltenCopperFluid, Material.lava, "liquid_copper").setBlockName("fluid.molten.copper");
        GameRegistry.registerBlock(TRepo.moltenCopper, "fluid.molten.copper");
        TRepo.moltenCopperFluid.setBlock(TRepo.moltenCopper).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenCopperFluid, 1000), new ItemStack(TRepo.buckets, 1, 2), new ItemStack(Items.bucket)));

        TRepo.moltenTinFluid = new Fluid("tin.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenTinFluid))
            TRepo.moltenTinFluid = FluidRegistry.getFluid("tin.molten");
        TRepo.moltenTin = new TConstructFluid(TRepo.moltenTinFluid, Material.lava, "liquid_tin").setBlockName("fluid.molten.tin");
        GameRegistry.registerBlock(TRepo.moltenTin, "fluid.molten.tin");
        TRepo.moltenTinFluid.setBlock(TRepo.moltenTin).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenTinFluid, 1000), new ItemStack(TRepo.buckets, 1, 3), new ItemStack(Items.bucket)));

        TRepo.moltenAluminumFluid = new Fluid("aluminum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAluminumFluid))
            TRepo.moltenAluminumFluid = FluidRegistry.getFluid("aluminum.molten");
        TRepo.moltenAluminum = new TConstructFluid(TRepo.moltenAluminumFluid, Material.lava, "liquid_aluminum").setBlockName("fluid.molten.aluminum");
        GameRegistry.registerBlock(TRepo.moltenAluminum, "fluid.molten.aluminum");
        TRepo.moltenAluminumFluid.setBlock(TRepo.moltenAluminum).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAluminumFluid, 1000), new ItemStack(TRepo.buckets, 1, 4), new ItemStack(Items.bucket)));

        TRepo.moltenCobaltFluid = new Fluid("cobalt.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenCobaltFluid))
            TRepo.moltenCobaltFluid = FluidRegistry.getFluid("cobalt.molten");
        TRepo.moltenCobalt = new TConstructFluid(TRepo.moltenCobaltFluid, Material.lava, "liquid_cobalt").setBlockName("fluid.molten.cobalt");
        GameRegistry.registerBlock(TRepo.moltenCobalt, "fluid.molten.cobalt");
        TRepo.moltenCobaltFluid.setBlock(TRepo.moltenCobalt).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenCobaltFluid, 1000), new ItemStack(TRepo.buckets, 1, 5), new ItemStack(Items.bucket)));

        TRepo.moltenArditeFluid = new Fluid("ardite.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenArditeFluid))
            TRepo.moltenArditeFluid = FluidRegistry.getFluid("ardite.molten");
        TRepo.moltenArdite = new TConstructFluid(TRepo.moltenArditeFluid, Material.lava, "liquid_ardite").setBlockName("fluid.molten.ardite");
        GameRegistry.registerBlock(TRepo.moltenArdite, "fluid.molten.ardite");
        TRepo.moltenArditeFluid.setBlock(TRepo.moltenArdite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenArditeFluid, 1000), new ItemStack(TRepo.buckets, 1, 6), new ItemStack(Items.bucket)));

        TRepo.moltenBronzeFluid = new Fluid("bronze.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenBronzeFluid))
            TRepo.moltenBronzeFluid = FluidRegistry.getFluid("bronze.molten");
        TRepo.moltenBronze = new TConstructFluid(TRepo.moltenBronzeFluid, Material.lava, "liquid_bronze").setBlockName("fluid.molten.bronze");
        GameRegistry.registerBlock(TRepo.moltenBronze, "fluid.molten.bronze");
        TRepo.moltenBronzeFluid.setBlock(TRepo.moltenBronze).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenBronzeFluid, 1000), new ItemStack(TRepo.buckets, 1, 7), new ItemStack(Items.bucket)));

        TRepo.moltenAlubrassFluid = new Fluid("aluminumbrass.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAlubrassFluid))
            TRepo.moltenAlubrassFluid = FluidRegistry.getFluid("aluminumbrass.molten");
        TRepo.moltenAlubrass = new TConstructFluid(TRepo.moltenAlubrassFluid, Material.lava, "liquid_alubrass").setBlockName("fluid.molten.alubrass");
        GameRegistry.registerBlock(TRepo.moltenAlubrass, "fluid.molten.alubrass");
        TRepo.moltenAlubrassFluid.setBlock(TRepo.moltenAlubrass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAlubrassFluid, 1000), new ItemStack(TRepo.buckets, 1, 8), new ItemStack(Items.bucket)));

        TRepo.moltenManyullynFluid = new Fluid("manyullyn.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenManyullynFluid))
            TRepo.moltenManyullynFluid = FluidRegistry.getFluid("manyullyn.molten");
        TRepo.moltenManyullyn = new TConstructFluid(TRepo.moltenManyullynFluid, Material.lava, "liquid_manyullyn").setBlockName("fluid.molten.manyullyn");
        GameRegistry.registerBlock(TRepo.moltenManyullyn, "fluid.molten.manyullyn");
        TRepo.moltenManyullynFluid.setBlock(TRepo.moltenManyullyn).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenManyullynFluid, 1000), new ItemStack(TRepo.buckets, 1, 9), new ItemStack(Items.bucket)));

        TRepo.moltenAlumiteFluid = new Fluid("alumite.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAlumiteFluid))
            TRepo.moltenAlumiteFluid = FluidRegistry.getFluid("alumite.molten");
        TRepo.moltenAlumite = new TConstructFluid(TRepo.moltenAlumiteFluid, Material.lava, "liquid_alumite").setBlockName("fluid.molten.alumite");
        GameRegistry.registerBlock(TRepo.moltenAlumite, "fluid.molten.alumite");
        TRepo.moltenAlumiteFluid.setBlock(TRepo.moltenAlumite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAlumiteFluid, 1000), new ItemStack(TRepo.buckets, 1, 10), new ItemStack(Items.bucket)));

        TRepo.moltenObsidianFluid = new Fluid("obsidian.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenObsidianFluid))
            TRepo.moltenObsidianFluid = FluidRegistry.getFluid("obsidian.molten");
        TRepo.moltenObsidian = new TConstructFluid(TRepo.moltenObsidianFluid, Material.lava, "liquid_obsidian").setBlockName("fluid.molten.obsidian");
        GameRegistry.registerBlock(TRepo.moltenObsidian, "fluid.molten.obsidian");
        TRepo.moltenObsidianFluid.setBlock(TRepo.moltenObsidian).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenObsidianFluid, 1000), new ItemStack(TRepo.buckets, 1, 11), new ItemStack(Items.bucket)));

        TRepo.moltenSteelFluid = new Fluid("steel.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenSteelFluid))
            TRepo.moltenSteelFluid = FluidRegistry.getFluid("steel.molten");
        TRepo.moltenSteel = new TConstructFluid(TRepo.moltenSteelFluid, Material.lava, "liquid_steel").setBlockName("fluid.molten.steel");
        GameRegistry.registerBlock(TRepo.moltenSteel, "fluid.molten.steel");
        TRepo.moltenSteelFluid.setBlock(TRepo.moltenSteel).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenSteelFluid, 1000), new ItemStack(TRepo.buckets, 1, 12), new ItemStack(Items.bucket)));

        TRepo.moltenGlassFluid = new Fluid("glass.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenGlassFluid))
            TRepo.moltenGlassFluid = FluidRegistry.getFluid("glass.molten");
        TRepo.moltenGlass = new TConstructFluid(TRepo.moltenGlassFluid, Material.lava, "liquid_glass", true).setBlockName("fluid.molten.glass");
        GameRegistry.registerBlock(TRepo.moltenGlass, "fluid.molten.glass");
        TRepo.moltenGlassFluid.setBlock(TRepo.moltenGlass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenGlassFluid, 1000), new ItemStack(TRepo.buckets, 1, 13), new ItemStack(Items.bucket)));

        TRepo.moltenStoneFluid = new Fluid("stone.seared");
        if (!FluidRegistry.registerFluid(TRepo.moltenStoneFluid))
            TRepo.moltenStoneFluid = FluidRegistry.getFluid("stone.seared");
        TRepo.moltenStone = new TConstructFluid(TRepo.moltenStoneFluid, Material.lava, "liquid_stone").setBlockName("molten.stone");
        GameRegistry.registerBlock(TRepo.moltenStone, "molten.stone");
        TRepo.moltenStoneFluid.setBlock(TRepo.moltenStone).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenStoneFluid, 1000), new ItemStack(TRepo.buckets, 1, 14), new ItemStack(Items.bucket)));

        TRepo.moltenEmeraldFluid = new Fluid("emerald.liquid");
        if (!FluidRegistry.registerFluid(TRepo.moltenEmeraldFluid))
            TRepo.moltenEmeraldFluid = FluidRegistry.getFluid("emerald.liquid");
        TRepo.moltenEmerald = new TConstructFluid(TRepo.moltenEmeraldFluid, Material.water, "liquid_villager").setBlockName("molten.emerald");
        GameRegistry.registerBlock(TRepo.moltenEmerald, "molten.emerald");
        TRepo.moltenEmeraldFluid.setBlock(TRepo.moltenEmerald).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenEmeraldFluid, 1000), new ItemStack(TRepo.buckets, 1, 15), new ItemStack(Items.bucket)));

        TRepo.bloodFluid = new Fluid("blood");
        if (!FluidRegistry.registerFluid(TRepo.bloodFluid))
            TRepo.bloodFluid = FluidRegistry.getFluid("blood");
        TRepo.blood = new BloodBlock(TRepo.bloodFluid, Material.water, "liquid_cow").setBlockName("liquid.blood");
        GameRegistry.registerBlock(TRepo.blood, "liquid.blood");
        TRepo.bloodFluid.setBlock(TRepo.blood).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.bloodFluid, 1000), new ItemStack(TRepo.buckets, 1, 16), new ItemStack(Items.bucket)));

        TRepo.moltenNickelFluid = new Fluid("nickel.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenNickelFluid))
            TRepo.moltenNickelFluid = FluidRegistry.getFluid("nickel.molten");
        TRepo.moltenNickel = new TConstructFluid(TRepo.moltenNickelFluid, Material.lava, "liquid_ferrous").setBlockName("fluid.molten.nickel");
        GameRegistry.registerBlock(TRepo.moltenNickel, "fluid.molten.nickel");
        TRepo.moltenNickelFluid.setBlock(TRepo.moltenNickel).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenNickelFluid, 1000), new ItemStack(TRepo.buckets, 1, 17), new ItemStack(Items.bucket)));

        TRepo.moltenLeadFluid = new Fluid("lead.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenLeadFluid))
            TRepo.moltenLeadFluid = FluidRegistry.getFluid("lead.molten");
        TRepo.moltenLead = new TConstructFluid(TRepo.moltenLeadFluid, Material.lava, "liquid_lead").setBlockName("fluid.molten.lead");
        GameRegistry.registerBlock(TRepo.moltenLead, "fluid.molten.lead");
        TRepo.moltenLeadFluid.setBlock(TRepo.moltenLead).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenLeadFluid, 1000), new ItemStack(TRepo.buckets, 1, 18), new ItemStack(Items.bucket)));

        TRepo.moltenSilverFluid = new Fluid("silver.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenSilverFluid))
            TRepo.moltenSilverFluid = FluidRegistry.getFluid("silver.molten");
        TRepo.moltenSilver = new TConstructFluid(TRepo.moltenSilverFluid, Material.lava, "liquid_silver").setBlockName("fluid.molten.silver");
        GameRegistry.registerBlock(TRepo.moltenSilver, "fluid.molten.silver");
        TRepo.moltenSilverFluid.setBlock(TRepo.moltenSilver).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenSilverFluid, 1000), new ItemStack(TRepo.buckets, 1, 19), new ItemStack(Items.bucket)));

        TRepo.moltenShinyFluid = new Fluid("platinum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenShinyFluid))
            TRepo.moltenShinyFluid = FluidRegistry.getFluid("platinum.molten");
        TRepo.moltenShiny = new TConstructFluid(TRepo.moltenShinyFluid, Material.lava, "liquid_shiny").setBlockName("fluid.molten.shiny");
        GameRegistry.registerBlock(TRepo.moltenShiny, "fluid.molten.shiny");
        TRepo.moltenShinyFluid.setBlock(TRepo.moltenShiny).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenShinyFluid, 1000), new ItemStack(TRepo.buckets, 1, 20), new ItemStack(Items.bucket)));

        TRepo.moltenInvarFluid = new Fluid("invar.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenInvarFluid))
            TRepo.moltenInvarFluid = FluidRegistry.getFluid("invar.molten");
        TRepo.moltenInvar = new TConstructFluid(TRepo.moltenInvarFluid, Material.lava, "liquid_invar").setBlockName("fluid.molten.invar");
        GameRegistry.registerBlock(TRepo.moltenInvar, "fluid.molten.invar");
        TRepo.moltenInvarFluid.setBlock(TRepo.moltenInvar).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenInvarFluid, 1000), new ItemStack(TRepo.buckets, 1, 21), new ItemStack(Items.bucket)));

        TRepo.moltenElectrumFluid = new Fluid("electrum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenElectrumFluid))
            TRepo.moltenElectrumFluid = FluidRegistry.getFluid("electrum.molten");
        TRepo.moltenElectrum = new TConstructFluid(TRepo.moltenElectrumFluid, Material.lava, "liquid_electrum").setBlockName("fluid.molten.electrum");
        GameRegistry.registerBlock(TRepo.moltenElectrum, "fluid.molten.electrum");
        TRepo.moltenElectrumFluid.setBlock(TRepo.moltenElectrum).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenElectrumFluid, 1000), new ItemStack(TRepo.buckets, 1, 22), new ItemStack(Items.bucket)));

        TRepo.moltenEnderFluid = new Fluid("ender");
        if (!FluidRegistry.registerFluid(TRepo.moltenEnderFluid))
        {
            TRepo.moltenEnderFluid = FluidRegistry.getFluid("ender");
            TRepo.moltenEnder = TRepo.moltenEnderFluid.getBlock();
            if (TRepo.moltenEnder == null)
                TConstruct.logger.info("Molten ender block missing!");
        }
        else
        {
            TRepo.moltenEnder = new TConstructFluid(TRepo.moltenEnderFluid, Material.water, "liquid_ender").setBlockName("fluid.ender");
            GameRegistry.registerBlock(TRepo.moltenEnder, "fluid.ender");
            TRepo.moltenEnderFluid.setBlock(TRepo.moltenEnder).setDensity(3000).setViscosity(6000);
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenEnderFluid, 1000), new ItemStack(TRepo.buckets, 1, 23), new ItemStack(Items.bucket)));
        }

        // Slime
        TRepo.slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        TRepo.blueSlimeFluid = new Fluid("slime.blue");
        if (!FluidRegistry.registerFluid(TRepo.blueSlimeFluid))
            TRepo.blueSlimeFluid = FluidRegistry.getFluid("slime.blue");
        TRepo.slimePool = new SlimeFluid(TRepo.blueSlimeFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TRepo.slimeStep).setBlockName("liquid.slime");
        GameRegistry.registerBlock(TRepo.slimePool, "liquid.slime");
        TRepo.blueSlimeFluid.setBlock(TRepo.slimePool);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.blueSlimeFluid, 1000), new ItemStack(TRepo.buckets, 1, 24), new ItemStack(Items.bucket)));

        // Glue
        TRepo.glueFluid = new Fluid("glue").setDensity(6000).setViscosity(6000).setTemperature(200);
        if (!FluidRegistry.registerFluid(TRepo.glueFluid))
            TRepo.glueFluid = FluidRegistry.getFluid("glue");
        TRepo.glueFluidBlock = new GlueFluid(TRepo.glueFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TRepo.slimeStep).setBlockName("liquid.glue");
        GameRegistry.registerBlock(TRepo.glueFluidBlock, "liquid.glue");
        TRepo.glueFluid.setBlock(TRepo.glueFluidBlock);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.glueFluid, 1000), new ItemStack(TRepo.buckets, 1, 26), new ItemStack(Items.bucket)));

        TRepo.pigIronFluid = new Fluid("pigiron.molten");
        if (!FluidRegistry.registerFluid(TRepo.pigIronFluid))
            TRepo.pigIronFluid = FluidRegistry.getFluid("pigiron.molten");
        else
            TRepo.pigIronFluid.setDensity(3000).setViscosity(6000).setTemperature(1300);
        TRepo.fluids = new Fluid[] { TRepo.moltenIronFluid, TRepo.moltenGoldFluid, TRepo.moltenCopperFluid, TRepo.moltenTinFluid, TRepo.moltenAluminumFluid, TRepo.moltenCobaltFluid,
                TRepo.moltenArditeFluid, TRepo.moltenBronzeFluid, TRepo.moltenAlubrassFluid, TRepo.moltenManyullynFluid, TRepo.moltenAlumiteFluid, TRepo.moltenObsidianFluid, TRepo.moltenSteelFluid,
                TRepo.moltenGlassFluid, TRepo.moltenStoneFluid, TRepo.moltenEmeraldFluid, TRepo.bloodFluid, TRepo.moltenNickelFluid, TRepo.moltenLeadFluid, TRepo.moltenSilverFluid,
                TRepo.moltenShinyFluid, TRepo.moltenInvarFluid, TRepo.moltenElectrumFluid, TRepo.moltenEnderFluid, TRepo.blueSlimeFluid, TRepo.glueFluid, TRepo.pigIronFluid };
        TRepo.fluidBlocks = new Block[] { TRepo.moltenIron, TRepo.moltenGold, TRepo.moltenCopper, TRepo.moltenTin, TRepo.moltenAluminum, TRepo.moltenCobalt, TRepo.moltenArdite, TRepo.moltenBronze,
                TRepo.moltenAlubrass, TRepo.moltenManyullyn, TRepo.moltenAlumite, TRepo.moltenObsidian, TRepo.moltenSteel, TRepo.moltenGlass, TRepo.moltenStone, TRepo.moltenEmerald, TRepo.blood,
                TRepo.moltenNickel, TRepo.moltenLead, TRepo.moltenSilver, TRepo.moltenShiny, TRepo.moltenInvar, TRepo.moltenElectrum, TRepo.moltenEnder, TRepo.slimePool, TRepo.glueFluidBlock };
        // Slime Islands
        TRepo.slimeGel = new SlimeGel().setStepSound(TRepo.slimeStep).setLightOpacity(0).setBlockName("slime.gel");
        TRepo.slimeGrass = new SlimeGrass().setStepSound(Block.soundTypeGrass).setLightOpacity(0).setBlockName("slime.grass");
        TRepo.slimeTallGrass = new SlimeTallGrass().setStepSound(Block.soundTypeGrass).setBlockName("slime.grass.tall");
        TRepo.slimeLeaves = (SlimeLeaves) new SlimeLeaves().setStepSound(TRepo.slimeStep).setLightOpacity(0).setBlockName("slime.leaves");
        TRepo.slimeSapling = (SlimeSapling) new SlimeSapling().setStepSound(TRepo.slimeStep).setBlockName("slime.sapling");
        TRepo.slimeChannel = new ConveyorBase(Material.water, "greencurrent").setHardness(0.3f).setStepSound(TRepo.slimeStep).setBlockName("slime.channel");
        TRepo.bloodChannel = new ConveyorBase(Material.water, "liquid_cow").setHardness(0.3f).setStepSound(TRepo.slimeStep).setBlockName("blood.channel");
        TRepo.slimePad = new SlimePad(Material.cloth).setStepSound(TRepo.slimeStep).setHardness(0.3f).setBlockName("slime.pad");

        // Decoration
        TRepo.stoneTorch = new StoneTorch().setBlockName("decoration.stonetorch");
        TRepo.stoneLadder = new StoneLadder().setBlockName("decoration.stoneladder");
        TRepo.multiBrick = new MultiBrick().setBlockName("Decoration.Brick");
        TRepo.multiBrickFancy = new MultiBrickFancy().setBlockName("Decoration.BrickFancy");

        // Ores
        String[] berryOres = new String[] { "berry_iron", "berry_gold", "berry_copper", "berry_tin", "berry_iron_ripe", "berry_gold_ripe", "berry_copper_ripe", "berry_tin_ripe" };
        TRepo.oreBerry = (OreberryBush) new OreberryBush(berryOres, 0, 4, new String[] { "oreIron", "oreGold", "oreCopper", "oreTin" }).setBlockName("ore.berries.one");
        String[] berryOresTwo = new String[] { "berry_aluminum", "berry_essence", "", "", "berry_aluminum_ripe", "berry_essence_ripe", "", "" };
        TRepo.oreBerrySecond = (OreberryBush) new OreberryBushEssence(berryOresTwo, 4, 2, new String[] { "oreAluminum", "oreSilver" }).setBlockName("ore.berries.two");

        String[] oreTypes = new String[] { "nether_slag", "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin", "ore_aluminum", "ore_slag" };
        TRepo.oreSlag = new MetalOre(Material.rock, 10.0F, oreTypes).setBlockName("tconstruct.stoneore");
        TRepo.oreSlag.setHarvestLevel("pickaxe", 4, 1);
        TRepo.oreSlag.setHarvestLevel("pickaxe", 4, 2);
        TRepo.oreSlag.setHarvestLevel("pickaxe", 1, 3);
        TRepo.oreSlag.setHarvestLevel("pickaxe", 1, 4);
        TRepo.oreSlag.setHarvestLevel("pickaxe", 1, 5);

        TRepo.oreGravel = new GravelOre().setBlockName("GravelOre").setBlockName("tconstruct.gravelore");
        TRepo.oreGravel.setHarvestLevel("shovel", 1, 0);
        TRepo.oreGravel.setHarvestLevel("shovel", 2, 1);
        TRepo.oreGravel.setHarvestLevel("shovel", 1, 2);
        TRepo.oreGravel.setHarvestLevel("shovel", 1, 3);
        TRepo.oreGravel.setHarvestLevel("shovel", 1, 4);
        TRepo.oreGravel.setHarvestLevel("shovel", 4, 5);

        TRepo.speedBlock = new SpeedBlock().setBlockName("SpeedBlock");

        // Glass
        TRepo.clearGlass = new GlassBlockConnected("clear", false).setBlockName("GlassBlock");
        TRepo.clearGlass.stepSound = Block.soundTypeGlass;
        TRepo.glassPane = new GlassPaneConnected("clear", false);
        TRepo.stainedGlassClear = new GlassBlockConnectedMeta("stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue",
                "brown", "green", "red", "black").setBlockName("GlassBlock.StainedClear");
        TRepo.stainedGlassClear.stepSound = Block.soundTypeGlass;
        TRepo.stainedGlassClearPane = new GlassPaneStained();

        // Rail
        TRepo.woodenRail = new WoodRail().setStepSound(Block.soundTypeWood).setCreativeTab(TConstructRegistry.blockTab).setBlockName("rail.wood");

    }

    void registerItems ()
    {
        TRepo.titleIcon = new TitleIcon().setUnlocalizedName("tconstruct.titleicon");
        GameRegistry.registerItem(TRepo.titleIcon, "titleIcon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast", "blank_cast" };
        TRepo.blankPattern = new CraftingItem(blanks, blanks, "materials/", "tinker", TConstructRegistry.materialTab).setUnlocalizedName("tconstruct.Pattern");
        GameRegistry.registerItem(TRepo.blankPattern, "blankPattern");

        TRepo.materials = new MaterialItem().setUnlocalizedName("tconstruct.Materials");
        TRepo.toolRod = new ToolPart("_rod", "ToolRod").setUnlocalizedName("tconstruct.ToolRod");
        TRepo.toolShard = new ToolShard("_chunk").setUnlocalizedName("tconstruct.ToolShard");
        TRepo.woodPattern = new Pattern("pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
        TRepo.metalPattern = new MetalPattern("cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
        // armorPattern = new ArmorPattern(PHConstruct.armorPattern,
        // "armorcast_",
        // "materials/").setUnlocalizedName("tconstruct.ArmorPattern");
        GameRegistry.registerItem(TRepo.materials, "materials");
        GameRegistry.registerItem(TRepo.woodPattern, "woodPattern");
        GameRegistry.registerItem(TRepo.metalPattern, "metalPattern");
        // GameRegistry.registerItem(TRepo.armorPattern, "armorPattern");

        TConstructRegistry.addItemToDirectory("blankPattern", TRepo.blankPattern);
        TConstructRegistry.addItemToDirectory("woodPattern", TRepo.woodPattern);
        TConstructRegistry.addItemToDirectory("metalPattern", TRepo.metalPattern);
        // TConstructRegistry.addItemToDirectory("armorPattern", armorPattern);

        String[] patternTypes = { "ingot", "toolRod", "pickaxeHead", "shovelHead", "hatchetHead", "swordBlade", "wideGuard", "handGuard", "crossbar", "binding", "frypanHead", "signHead",
                "knifeBlade", "chiselHead", "toughRod", "toughBinding", "largePlate", "broadAxeHead", "scytheHead", "excavatorHead", "largeBlade", "hammerHead", "fullGuard" };

        for (int i = 1; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Pattern", new ItemStack(TRepo.woodPattern, 1, i));
        }
        for (int i = 0; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Cast", new ItemStack(TRepo.metalPattern, 1, i));
        }
        /*
         * String[] armorPartTypes = { "helmet", "chestplate", "leggings",
         * "boots" }; for (int i = 1; i < armorPartTypes.length; i++) {
         * TConstructRegistry.addItemStackToDirectory(armorPartTypes[i] +
         * "Cast", new ItemStack(armorPattern, 1, i)); }
         */

        TRepo.manualBook = new Manual();
        GameRegistry.registerItem(TRepo.manualBook, "manualBook");
        TRepo.buckets = new FilledBucket(BlockUtils.getBlockFromItem(TRepo.buckets));
        GameRegistry.registerItem(TRepo.buckets, "buckets");

        TRepo.pickaxe = new Pickaxe();
        TRepo.shovel = new Shovel();
        TRepo.hatchet = new Hatchet();
        TRepo.broadsword = new Broadsword();
        TRepo.longsword = new Longsword();
        TRepo.rapier = new Rapier();
        TRepo.dagger = new Dagger();
        TRepo.cutlass = new Cutlass();

        TRepo.frypan = new FryingPan();
        TRepo.battlesign = new BattleSign();
        TRepo.mattock = new Mattock();
        TRepo.chisel = new Chisel();

        TRepo.lumberaxe = new LumberAxe();
        TRepo.cleaver = new Cleaver();
        TRepo.scythe = new Scythe();
        TRepo.excavator = new Excavator();
        TRepo.hammer = new Hammer();
        TRepo.battleaxe = new Battleaxe();

        TRepo.shortbow = new Shortbow();
        TRepo.arrow = new Arrow();

        Item[] tools = { TRepo.pickaxe, TRepo.shovel, TRepo.hatchet, TRepo.broadsword, TRepo.longsword, TRepo.rapier, TRepo.dagger, TRepo.cutlass, TRepo.frypan, TRepo.battlesign, TRepo.mattock,
                TRepo.chisel, TRepo.lumberaxe, TRepo.cleaver, TRepo.scythe, TRepo.excavator, TRepo.hammer, TRepo.battleaxe, TRepo.shortbow, TRepo.arrow };
        String[] toolStrings = { "pickaxe", "shovel", "hatchet", "broadsword", "longsword", "rapier", "dagger", "cutlass", "frypan", "battlesign", "mattock", "chisel", "lumberaxe", "cleaver",
                "scythe", "excavator", "hammer", "battleaxe", "shortbow", "arrow" };

        for (int i = 0; i < tools.length; i++)
        {
            GameRegistry.registerItem(tools[i], toolStrings[i]); // 1.7 compat
            TConstructRegistry.addItemToDirectory(toolStrings[i], tools[i]);
        }

        TRepo.potionLauncher = new PotionLauncher().setUnlocalizedName("tconstruct.PotionLauncher");
        GameRegistry.registerItem(TRepo.potionLauncher, "potionLauncher");

        TRepo.pickaxeHead = new ToolPart("_pickaxe_head", "PickHead").setUnlocalizedName("tconstruct.PickaxeHead");
        TRepo.shovelHead = new ToolPart("_shovel_head", "ShovelHead").setUnlocalizedName("tconstruct.ShovelHead");
        TRepo.hatchetHead = new ToolPart("_axe_head", "AxeHead").setUnlocalizedName("tconstruct.AxeHead");
        TRepo.binding = new ToolPart("_binding", "Binding").setUnlocalizedName("tconstruct.Binding");
        TRepo.toughBinding = new ToolPart("_toughbind", "ToughBind").setUnlocalizedName("tconstruct.ThickBinding");
        TRepo.toughRod = new ToolPart("_toughrod", "ToughRod").setUnlocalizedName("tconstruct.ThickRod");
        TRepo.largePlate = new ToolPart("_largeplate", "LargePlate").setUnlocalizedName("tconstruct.LargePlate");

        TRepo.swordBlade = new ToolPart("_sword_blade", "SwordBlade").setUnlocalizedName("tconstruct.SwordBlade");
        TRepo.wideGuard = new ToolPart("_large_guard", "LargeGuard").setUnlocalizedName("tconstruct.LargeGuard");
        TRepo.handGuard = new ToolPart("_medium_guard", "MediumGuard").setUnlocalizedName("tconstruct.MediumGuard");
        TRepo.crossbar = new ToolPart("_crossbar", "Crossbar").setUnlocalizedName("tconstruct.Crossbar");
        TRepo.knifeBlade = new ToolPart("_knife_blade", "KnifeBlade").setUnlocalizedName("tconstruct.KnifeBlade");
        TRepo.fullGuard = new ToolPartHidden("_full_guard", "FullGuard").setUnlocalizedName("tconstruct.FullGuard");

        TRepo.frypanHead = new ToolPart("_frypan_head", "FrypanHead").setUnlocalizedName("tconstruct.FrypanHead");
        TRepo.signHead = new ToolPart("_battlesign_head", "SignHead").setUnlocalizedName("tconstruct.SignHead");
        TRepo.chiselHead = new ToolPart("_chisel_head", "ChiselHead").setUnlocalizedName("tconstruct.ChiselHead");

        TRepo.scytheBlade = new ToolPart("_scythe_head", "ScytheHead").setUnlocalizedName("tconstruct.ScytheBlade");
        TRepo.broadAxeHead = new ToolPart("_lumberaxe_head", "LumberHead").setUnlocalizedName("tconstruct.LumberHead");
        TRepo.excavatorHead = new ToolPart("_excavator_head", "ExcavatorHead").setUnlocalizedName("tconstruct.ExcavatorHead");
        TRepo.largeSwordBlade = new ToolPart("_large_sword_blade", "LargeSwordBlade").setUnlocalizedName("tconstruct.LargeSwordBlade");
        TRepo.hammerHead = new ToolPart("_hammer_head", "HammerHead").setUnlocalizedName("tconstruct.HammerHead");

        TRepo.bowstring = new Bowstring().setUnlocalizedName("tconstruct.Bowstring");
        TRepo.arrowhead = new ToolPart("_arrowhead", "ArrowHead").setUnlocalizedName("tconstruct.Arrowhead");
        TRepo.fletching = new Fletching().setUnlocalizedName("tconstruct.Fletching");

        Item[] toolParts = { TRepo.toolRod, TRepo.toolShard, TRepo.pickaxeHead, TRepo.shovelHead, TRepo.hatchetHead, TRepo.binding, TRepo.toughBinding, TRepo.toughRod, TRepo.largePlate,
                TRepo.swordBlade, TRepo.wideGuard, TRepo.handGuard, TRepo.crossbar, TRepo.knifeBlade, TRepo.fullGuard, TRepo.frypanHead, TRepo.signHead, TRepo.chiselHead, TRepo.scytheBlade,
                TRepo.broadAxeHead, TRepo.excavatorHead, TRepo.largeSwordBlade, TRepo.hammerHead, TRepo.bowstring, TRepo.fletching, TRepo.arrowhead };
        String[] toolPartStrings = { "toolRod", "toolShard", "pickaxeHead", "shovelHead", "hatchetHead", "binding", "toughBinding", "toughRod", "heavyPlate", "swordBlade", "wideGuard", "handGuard",
                "crossbar", "knifeBlade", "fullGuard", "frypanHead", "signHead", "chiselHead", "scytheBlade", "broadAxeHead", "excavatorHead", "largeSwordBlade", "hammerHead", "bowstring",
                "fletching", "arrowhead" };

        for (int i = 0; i < toolParts.length; i++)
        {
            GameRegistry.registerItem(toolParts[i], toolPartStrings[i]); // 1.7
                                                                         // compat
            TConstructRegistry.addItemToDirectory(toolPartStrings[i], toolParts[i]);
        }

        TRepo.diamondApple = new DiamondApple().setUnlocalizedName("tconstruct.apple.diamond");
        TRepo.strangeFood = new StrangeFood().setUnlocalizedName("tconstruct.strangefood");
        TRepo.oreBerries = new OreBerries().setUnlocalizedName("oreberry");
        GameRegistry.registerItem(TRepo.diamondApple, "diamondApple");
        GameRegistry.registerItem(TRepo.strangeFood, "strangeFood");
        GameRegistry.registerItem(TRepo.oreBerries, "oreBerries");

        boolean foodOverhaul = false;
        if (Loader.isModLoaded("HungerOverhaul") || Loader.isModLoaded("fc_food"))
        {
            foodOverhaul = true;
        }

        TRepo.jerky = new Jerky(foodOverhaul).setUnlocalizedName("tconstruct.jerky");
        GameRegistry.registerItem(TRepo.jerky, "jerky");

        // Wearables
        // heavyHelmet = new TArmorBase(PHConstruct.heavyHelmet,
        // 0).setUnlocalizedName("tconstruct.HeavyHelmet");
        TRepo.heartCanister = new HeartCanister().setUnlocalizedName("tconstruct.canister");
        // heavyBoots = new TArmorBase(PHConstruct.heavyBoots,
        // 3).setUnlocalizedName("tconstruct.HeavyBoots");
        // glove = new
        // Glove(PHConstruct.glove).setUnlocalizedName("tconstruct.Glove");
        TRepo.knapsack = new Knapsack().setUnlocalizedName("tconstruct.storage");
        TRepo.goldHead = new GoldenHead(4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F).setUnlocalizedName("goldenhead");
        // GameRegistry.registerItem(TRepo.heavyHelmet, "heavyHelmet");
        GameRegistry.registerItem(TRepo.heartCanister, "heartCanister");
        // GameRegistry.registerItem(TRepo.heavyBoots, "heavyBoots");
        // GameRegistry.registerItem(TRepo.glove, "glove");
        GameRegistry.registerItem(TRepo.knapsack, "knapsack");
        GameRegistry.registerItem(TRepo.goldHead, "goldHead");
        
        TRepo.creativeModifier = new CreativeModifier().setUnlocalizedName("tconstruct.modifier.creative");
        GameRegistry.registerItem(TRepo.creativeModifier, "creativeModifier");

        LiquidCasting basinCasting = TConstruct.getBasinCasting();
        TRepo.materialWood = EnumHelper.addArmorMaterial("WOOD", 2, new int[] { 1, 2, 2, 1 }, 3);
        TRepo.helmetWood = new ArmorBasic(TRepo.materialWood, 0, "wood").setUnlocalizedName("tconstruct.helmetWood");
        TRepo.chestplateWood = new ArmorBasic(TRepo.materialWood, 1, "wood").setUnlocalizedName("tconstruct.chestplateWood");
        TRepo.leggingsWood = new ArmorBasic(TRepo.materialWood, 2, "wood").setUnlocalizedName("tconstruct.leggingsWood");
        TRepo.bootsWood = new ArmorBasic(TRepo.materialWood, 3, "wood").setUnlocalizedName("tconstruct.bootsWood");
        GameRegistry.registerItem(TRepo.helmetWood, "helmetWood");
        GameRegistry.registerItem(TRepo.chestplateWood, "chestplateWood");
        GameRegistry.registerItem(TRepo.leggingsWood, "leggingsWood");
        GameRegistry.registerItem(TRepo.bootsWood, "bootsWood");

        TRepo.exoGoggles = new ExoArmor(EnumArmorPart.HELMET, "exosuit").setUnlocalizedName("tconstruct.exoGoggles");
        TRepo.exoChest = new ExoArmor(EnumArmorPart.CHEST, "exosuit").setUnlocalizedName("tconstruct.exoChest");
        TRepo.exoPants = new ExoArmor(EnumArmorPart.PANTS, "exosuit").setUnlocalizedName("tconstruct.exoPants");
        TRepo.exoShoes = new ExoArmor(EnumArmorPart.SHOES, "exosuit").setUnlocalizedName("tconstruct.exoShoes");

        String[] materialStrings = { "paperStack", "greenSlimeCrystal", "searedBrick", "ingotCobalt", "ingotArdite", "ingotManyullyn", "mossBall", "lavaCrystal", "necroticBone", "ingotCopper",
                "ingotTin", "ingotAluminum", "rawAluminum", "ingotBronze", "ingotAluminumBrass", "ingotAlumite", "ingotSteel", "blueSlimeCrystal", "ingotObsidian", "nuggetIron", "nuggetCopper",
                "nuggetTin", "nuggetAluminum", "nuggetSilver", "nuggetAluminumBrass", "silkyCloth", "silkyJewel", "nuggetObsidian", "nuggetCobalt", "nuggetArdite", "nuggetManyullyn", "nuggetBronze",
                "nuggetAlumite", "nuggetSteel", "ingotPigIron", "nuggetPigIron", "glueball" };

        for (int i = 0; i < materialStrings.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(TRepo.materials, 1, i));
        }

        String[] oreberries = { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Essence" };

        for (int i = 0; i < oreberries.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory("oreberry" + oreberries[i], new ItemStack(TRepo.oreBerries, 1, i));
        }

        TConstructRegistry.addItemStackToDirectory("diamondApple", new ItemStack(TRepo.diamondApple, 1, 0));
        TConstructRegistry.addItemStackToDirectory("blueSlimeFood", new ItemStack(TRepo.strangeFood, 1, 0));

        TConstructRegistry.addItemStackToDirectory("canisterEmpty", new ItemStack(TRepo.heartCanister, 1, 0));
        TConstructRegistry.addItemStackToDirectory("miniRedHeart", new ItemStack(TRepo.heartCanister, 1, 1));
        TConstructRegistry.addItemStackToDirectory("canisterRedHeart", new ItemStack(TRepo.heartCanister, 1, 2));

        // Vanilla stack sizes
        Items.wooden_door.setMaxStackSize(16);
        Items.iron_door.setMaxStackSize(16);
        Items.boat.setMaxStackSize(16);
        Items.minecart.setMaxStackSize(3);
        // Items.minecartEmpty.setMaxStackSize(3);
        // Items.minecartCrate.setMaxStackSize(3);
        // Items.minecartPowered.setMaxStackSize(3);
        Items.cake.setMaxStackSize(16);
        // Block.torchWood.setTickRandomly(false);
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

        TConstructRegistry.addBowMaterial(0, 384, 20, 1.0f); // Wood
        TConstructRegistry.addBowMaterial(1, 10, 80, 0.2f); // Stone
        TConstructRegistry.addBowMaterial(2, 576, 40, 1.2f); // Iron
        TConstructRegistry.addBowMaterial(3, 10, 80, 0.2f); // Flint
        TConstructRegistry.addBowMaterial(4, 384, 20, 1.0f); // Cactus
        TConstructRegistry.addBowMaterial(5, 192, 30, 1.0f); // Bone
        TConstructRegistry.addBowMaterial(6, 10, 80, 0.2f); // Obsidian
        TConstructRegistry.addBowMaterial(7, 10, 80, 0.2f); // Netherrack
        TConstructRegistry.addBowMaterial(8, 1536, 20, 1.2f); // Slime
        TConstructRegistry.addBowMaterial(9, 48, 25, 0.5f); // Paper
        TConstructRegistry.addBowMaterial(10, 1152, 40, 1.2f); // Cobalt
        TConstructRegistry.addBowMaterial(11, 960, 40, 1.2f); // Ardite
        TConstructRegistry.addBowMaterial(12, 1536, 40, 1.2f); // Manyullyn
        TConstructRegistry.addBowMaterial(13, 384, 40, 1.2f); // Copper
        TConstructRegistry.addBowMaterial(14, 576, 40, 1.2f); // Bronze
        TConstructRegistry.addBowMaterial(15, 768, 40, 1.2f); // Alumite
        TConstructRegistry.addBowMaterial(16, 768, 40, 1.2f); // Steel
        TConstructRegistry.addBowMaterial(17, 576, 20, 1.2f); // Blue Slime
        TConstructRegistry.addBowMaterial(18, 384, 20, 1.2f); // Slime

        // Material ID, mass, fragility
        TConstructRegistry.addArrowMaterial(0, 0.69F, 1.0F, 100F); // Wood
        TConstructRegistry.addArrowMaterial(1, 2.5F, 5.0F, 100F); // Stone
        TConstructRegistry.addArrowMaterial(2, 7.2F, 0.5F, 100F); // Iron
        TConstructRegistry.addArrowMaterial(3, 2.65F, 1.0F, 100F); // Flint
        TConstructRegistry.addArrowMaterial(4, 0.76F, 1.0F, 100F); // Cactus
        TConstructRegistry.addArrowMaterial(5, 0.69F, 1.0F, 100); // Bone
        TConstructRegistry.addArrowMaterial(6, 2.4F, 1.0F, 100F); // Obsidian
        TConstructRegistry.addArrowMaterial(7, 3.5F, 1.0F, 100F); // Netherrack
        TConstructRegistry.addArrowMaterial(8, 0.42F, 0.0F, 100F); // Slime
        TConstructRegistry.addArrowMaterial(9, 1.1F, 3.0F, 90F); // Paper
        TConstructRegistry.addArrowMaterial(10, 8.9F, 0.25F, 100F); // Cobalt
        TConstructRegistry.addArrowMaterial(11, 7.2F, 0.25F, 100F); // Ardite
        TConstructRegistry.addArrowMaterial(12, 10.6F, 0.1F, 100F); // Manyullyn
        TConstructRegistry.addArrowMaterial(13, 8.96F, 0.5F, 100F); // Copper
        TConstructRegistry.addArrowMaterial(14, 7.9F, 0.25F, 100F); // Bronze
        TConstructRegistry.addArrowMaterial(15, 4.7F, 0.25F, 100F); // Alumite
        TConstructRegistry.addArrowMaterial(16, 7.6F, 0.25F, 100F); // Steel
        TConstructRegistry.addArrowMaterial(17, 0.42F, 0.0F, 100F); // Blue
                                                                    // Slime
        TConstructRegistry.addArrowMaterial(18, 6.8F, 0.5F, 100F); // Iron

        TConstructRegistry.addBowstringMaterial(0, 2, new ItemStack(Items.string), new ItemStack(TRepo.bowstring, 1, 0), 1F, 1F, 1f); // String
        TConstructRegistry.addFletchingMaterial(0, 2, new ItemStack(Items.feather), new ItemStack(TRepo.fletching, 1, 0), 100F, 0F, 0.05F); // Feather
        for (int i = 0; i < 4; i++)
            TConstructRegistry.addFletchingMaterial(1, 2, new ItemStack(Blocks.leaves, 1, i), new ItemStack(TRepo.fletching, 1, 1), 75F, 0F, 0.2F); // All four vanialla Leaves
        TConstructRegistry.addFletchingMaterial(2, 2, new ItemStack(TRepo.materials, 1, 1), new ItemStack(TRepo.fletching, 1, 2), 100F, 0F, 0.12F); // Slime
        TConstructRegistry.addFletchingMaterial(3, 2, new ItemStack(TRepo.materials, 1, 17), new ItemStack(TRepo.fletching, 1, 3), 100F, 0F, 0.12F); // BlueSlime

        PatternBuilder pb = PatternBuilder.instance;
        if (PHConstruct.enableTWood)
            pb.registerFullMaterial(Blocks.planks, 2, "Wood", new ItemStack(Items.stick), new ItemStack(Items.stick), 0);
        else
            pb.registerMaterialSet("Wood", new ItemStack(Items.stick, 2), new ItemStack(Items.stick), 0);
        if (PHConstruct.enableTStone)
        {
            pb.registerFullMaterial(Blocks.stone, 2, "Stone", new ItemStack(TRepo.toolShard, 1, 1), new ItemStack(TRepo.toolRod, 1, 1), 1);
            pb.registerMaterial(Blocks.cobblestone, 2, "Stone");
        }
        else
            pb.registerMaterialSet("Stone", new ItemStack(TRepo.toolShard, 1, 1), new ItemStack(TRepo.toolRod, 1, 1), 0);
        pb.registerFullMaterial(Items.iron_ingot, 2, "Iron", new ItemStack(TRepo.toolShard, 1, 2), new ItemStack(TRepo.toolRod, 1, 2), 2);
        if (PHConstruct.enableTFlint)
            pb.registerFullMaterial(Items.flint, 2, "Flint", new ItemStack(TRepo.toolShard, 1, 3), new ItemStack(TRepo.toolRod, 1, 3), 3);
        else
            pb.registerMaterialSet("Flint", new ItemStack(TRepo.toolShard, 1, 3), new ItemStack(TRepo.toolRod, 1, 3), 3);
        if (PHConstruct.enableTCactus)
            pb.registerFullMaterial(Blocks.cactus, 2, "Cactus", new ItemStack(TRepo.toolShard, 1, 4), new ItemStack(TRepo.toolRod, 1, 4), 4);
        else
            pb.registerMaterialSet("Cactus", new ItemStack(TRepo.toolShard, 1, 4), new ItemStack(TRepo.toolRod, 1, 4), 4);
        if (PHConstruct.enableTBone)
            pb.registerFullMaterial(Items.bone, 2, "Bone", new ItemStack(Items.dye, 1, 15), new ItemStack(Items.bone), 5);
        else
            pb.registerMaterialSet("Bone", new ItemStack(Items.dye, 1, 15), new ItemStack(Items.bone), 5);
        pb.registerFullMaterial(Blocks.obsidian, 2, "Obsidian", new ItemStack(TRepo.toolShard, 1, 6), new ItemStack(TRepo.toolRod, 1, 6), 6);
        pb.registerMaterial(new ItemStack(TRepo.materials, 1, 18), 2, "Obsidian");
        if (PHConstruct.enableTNetherrack)
            pb.registerFullMaterial(Blocks.netherrack, 2, "Netherrack", new ItemStack(TRepo.toolShard, 1, 7), new ItemStack(TRepo.toolRod, 1, 7), 7);
        else
            pb.registerMaterialSet("Netherrack", new ItemStack(TRepo.toolShard, 1, 7), new ItemStack(TRepo.toolRod, 1, 7), 7);
        if (PHConstruct.enableTSlime)
            pb.registerFullMaterial(new ItemStack(TRepo.materials, 1, 1), 2, "Slime", new ItemStack(TRepo.toolShard, 1, 8), new ItemStack(TRepo.toolRod, 1, 8), 8);
        else
            pb.registerMaterialSet("Slime", new ItemStack(TRepo.toolShard, 1, 8), new ItemStack(TRepo.toolRod, 1, 17), 8);
        if (PHConstruct.enableTPaper)
            pb.registerFullMaterial(new ItemStack(TRepo.materials, 1, 0), 2, "Paper", new ItemStack(Items.paper, 2), new ItemStack(TRepo.toolRod, 1, 9), 9);
        else
            pb.registerMaterialSet("BlueSlime", new ItemStack(Items.paper, 2), new ItemStack(TRepo.toolRod, 1, 9), 9);
        pb.registerMaterialSet("Cobalt", new ItemStack(TRepo.toolShard, 1, 10), new ItemStack(TRepo.toolRod, 1, 10), 10);
        pb.registerMaterialSet("Ardite", new ItemStack(TRepo.toolShard, 1, 11), new ItemStack(TRepo.toolRod, 1, 11), 11);
        pb.registerMaterialSet("Manyullyn", new ItemStack(TRepo.toolShard, 1, 12), new ItemStack(TRepo.toolRod, 1, 12), 12);
        pb.registerMaterialSet("Copper", new ItemStack(TRepo.toolShard, 1, 13), new ItemStack(TRepo.toolRod, 1, 13), 13);
        pb.registerMaterialSet("Bronze", new ItemStack(TRepo.toolShard, 1, 14), new ItemStack(TRepo.toolRod, 1, 14), 14);
        pb.registerMaterialSet("Alumite", new ItemStack(TRepo.toolShard, 1, 15), new ItemStack(TRepo.toolRod, 1, 15), 15);
        pb.registerMaterialSet("Steel", new ItemStack(TRepo.toolShard, 1, 16), new ItemStack(TRepo.toolRod, 1, 16), 16);
        if (PHConstruct.enableTBlueSlime)
            pb.registerFullMaterial(new ItemStack(TRepo.materials, 1, 17), 2, "BlueSlime", new ItemStack(TRepo.toolShard, 1, 17), new ItemStack(TRepo.toolRod, 1, 17), 17);
        else
            pb.registerMaterialSet("BlueSlime", new ItemStack(TRepo.toolShard, 1, 17), new ItemStack(TRepo.toolRod, 1, 17), 17);
        pb.registerFullMaterial(new ItemStack(TRepo.materials, 1, 34), 2, "PigIron", new ItemStack(TRepo.toolShard, 1, 18), new ItemStack(TRepo.toolRod, 1, 18), 18);

        pb.addToolPattern((IPattern) TRepo.woodPattern);
    }

    void addCraftingRecipes ()
    {
        TRecipes.addPartMapping();
        TRecipes.addRecipesForToolBuilder();
        TRecipes.addRecipesForTableCasting();
        TRecipes.addRecipesForBasinCasting();
        TRecipes.addRecipesForSmeltery();
        TRecipes.addRecipesForChisel();
        TRecipes.addRecipesForFurnace();
        TRecipes.addRecipesForCraftingTable();
        TRecipes.addRecipesForDryingRack();
    }

    void setupToolTabs ()
    {
        TConstructRegistry.materialTab.init(new ItemStack(TRepo.manualBook, 1, 0));
        TConstructRegistry.partTab.init(new ItemStack(TRepo.titleIcon, 1, 255));
        TConstructRegistry.blockTab.init(new ItemStack(TRepo.toolStationWood));
        ItemStack tool = new ItemStack(TRepo.longsword, 1, 0);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("InfiTool", new NBTTagCompound());
        compound.getCompoundTag("InfiTool").setInteger("RenderHead", 2);
        compound.getCompoundTag("InfiTool").setInteger("RenderHandle", 0);
        compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", 10);
        tool.setTagCompound(compound);

        // TConstruct.
        TConstructRegistry.toolTab.init(tool);
    }

    public void addLoot ()
    {
        // Item, min, max, weight
        ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(TRepo.heartCanister, 1, 1), 1, 1, 5));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(TRepo.heartCanister, 1, 1), 1, 1, 10));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(TRepo.heartCanister, 1, 1), 1, 1, 10));

        TRepo.tinkerHouseChest = new ChestGenHooks("TinkerHouse", new WeightedRandomChestContent[0], 3, 27);
        TRepo.tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(TRepo.heartCanister, 1, 1), 1, 1, 1));
        int[] validTypes = { 0, 1, 2, 3, 4, 5, 6, 8, 9, 13, 14, 17 };
        Item[] partTypes = { TRepo.pickaxeHead, TRepo.shovelHead, TRepo.hatchetHead, TRepo.binding, TRepo.swordBlade, TRepo.wideGuard, TRepo.handGuard, TRepo.crossbar, TRepo.knifeBlade,
                TRepo.frypanHead, TRepo.signHead, TRepo.chiselHead };

        for (int partIter = 0; partIter < partTypes.length; partIter++)
        {
            for (int typeIter = 0; typeIter < validTypes.length; typeIter++)
            {
                TRepo.tinkerHouseChest.addItem(new WeightedRandomChestContent(new ItemStack(partTypes[partIter], 1, validTypes[typeIter]), 1, 1, 15));
            }
        }

        TRepo.tinkerHousePatterns = new ChestGenHooks("TinkerPatterns", new WeightedRandomChestContent[0], 5, 30);
        for (int i = 0; i < 13; i++)
        {
            TRepo.tinkerHousePatterns.addItem(new WeightedRandomChestContent(new ItemStack(TRepo.woodPattern, 1, i + 1), 1, 3, 20));
        }
        TRepo.tinkerHousePatterns.addItem(new WeightedRandomChestContent(new ItemStack(TRepo.woodPattern, 1, 22), 1, 3, 40));
    }

    public static String[] liquidNames;

    @Override
    public int getBurnTime (ItemStack fuel)
    {
        if (fuel.getItem() == TRepo.materials && fuel.getItemDamage() == 7)
            return 26400;
        return 0;
    }

    public void addAchievements ()
    {
        HashMap<String, Achievement> achievements = TAchievements.achievements;

        achievements.put("tconstruct:beginner", new Achievement("tconstruct:beginner", "tconstruct.beginner", 0, 0, TRepo.manualBook, null));// .setIndependent());
        achievements.put("tconstruct:pattern", new Achievement("tconstruct:pattern", "tconstruct.pattern", 2, 1, TRepo.blankPattern, achievements.get("tconstruct:beginner")));
        achievements.put("tconstruct:tinkerer", new Achievement("tconstruct:tinkerer", "tconstruct.tinkerer", 2, 2, new ItemStack(TRepo.titleIcon, 1, 4096), achievements.get("tconstruct:pattern")));
        achievements.put("tconstruct:preparedFight", new Achievement("tconstruct:preparedFight", "tconstruct.preparedFight", 1, 3, new ItemStack(TRepo.titleIcon, 1, 4097), achievements.get("tconstruct:tinkerer")));
        achievements.put("tconstruct:proTinkerer", new Achievement("tconstruct:proTinkerer", "tconstruct.proTinkerer", 4, 4, new ItemStack(TRepo.titleIcon, 1, 4098), achievements.get("tconstruct:tinkerer")).setSpecial());
        achievements.put("tconstruct:smelteryMaker", new Achievement("tconstruct:smelteryMaker", "tconstruct.smelteryMaker", -2, -1, TRepo.smeltery, achievements.get("tconstruct:beginner")));
        achievements.put("tconstruct:enemySlayer", new Achievement("tconstruct:enemySlayer", "tconstruct.enemySlayer", 0, 5, new ItemStack(TRepo.titleIcon, 1, 4099), achievements.get("tconstruct:preparedFight")));
        achievements.put("tconstruct:dualConvenience", new Achievement("tconstruct:dualConvenience", "tconstruct.dualConvenience", 0, 7, new ItemStack(TRepo.titleIcon, 1, 4100), achievements.get("tconstruct:enemySlayer")).setSpecial());
        achievements.put("tconstruct:doingItWrong", new Achievement("tconstruct:doingItWrong", "tconstruct.doingItWrong", -2, -3, new ItemStack(TRepo.manualBook, 1, 2), achievements.get("tconstruct:smelteryMaker")));
        achievements.put("tconstruct:betterCrafting", new Achievement("tconstruct:betterCrafting", "tconstruct.betterCrafting", -2, 2, TRepo.craftingStationWood, achievements.get("tconstruct:beginner")));
    }
}
