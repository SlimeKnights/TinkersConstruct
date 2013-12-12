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
import tconstruct.util.*;
import tconstruct.util.config.*;

public class TContent implements IFuelHandler
{
    //Temporary items
    //public static Item armorTest = new ArmorStandard(2445, 4, EnumArmorPart.HELMET).setCreativeTab(CreativeTabs.tabAllSearch);

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
        //EntityRegistry.registerModEntity(CartEntity.class, "Small Wagon", 1, TConstruct.instance, 32, 5, true);

        EntityRegistry.registerModEntity(BlueSlime.class, "EdibleSlime", 12, TConstruct.instance, 64, 5, true);
        //EntityRegistry.registerModEntity(MetalSlime.class, "MetalSlime", 13, TConstruct.instance, 64, 5, true);
    }

    void registerBlocks ()
    {
        //Tool Station
        TRepo.toolStationWood = new ToolStationBlock(PHConstruct.woodStation, Material.wood).setUnlocalizedName("ToolStation");
        TRepo.toolForge = new ToolForgeBlock(PHConstruct.toolForge, Material.iron).setUnlocalizedName("ToolForge");
        TRepo.craftingStationWood = new CraftingStationBlock(PHConstruct.woodCrafter, Material.wood).setUnlocalizedName("CraftingStation");
        TRepo.craftingSlabWood = new CraftingSlab(PHConstruct.woodCrafterSlab, Material.wood).setUnlocalizedName("CraftingSlab");
        TRepo.furnaceSlab = new FurnaceSlab(PHConstruct.furnaceSlab, Material.rock).setUnlocalizedName("FurnaceSlab");

        TRepo.heldItemBlock = new EquipBlock(PHConstruct.heldItemBlock, Material.wood).setUnlocalizedName("Frypan");

        TRepo.craftedSoil = new SoilBlock(PHConstruct.craftedSoil).setLightOpacity(0).setUnlocalizedName("TConstruct.Soil");
        TRepo.craftedSoil.stepSound = Block.soundGravelFootstep;

        TRepo.searedSlab = new SearedSlab(PHConstruct.searedSlab).setUnlocalizedName("SearedSlab");
        TRepo.searedSlab.stepSound = Block.soundStoneFootstep;

        TRepo.speedSlab = new SpeedSlab(PHConstruct.speedSlab).setUnlocalizedName("SpeedSlab");
        TRepo.speedSlab.stepSound = Block.soundStoneFootstep;

        TRepo.metalBlock = new TMetalBlock(PHConstruct.metalBlock, Material.iron, 10.0F).setUnlocalizedName("tconstruct.metalblock");
        TRepo.metalBlock.stepSound = Block.soundMetalFootstep;

        TRepo.meatBlock = new MeatBlock(PHConstruct.meatBlock).setUnlocalizedName("tconstruct.meatblock");
        TRepo.glueBlock = new GlueBlock(PHConstruct.glueBlock).setUnlocalizedName("GlueBlock").setCreativeTab(TConstructRegistry.blockTab);

        TRepo.woolSlab1 = new SlabBase(PHConstruct.woolSlab1, Material.cloth, Block.cloth, 0, 8).setUnlocalizedName("cloth");
        TRepo.woolSlab1.setStepSound(Block.soundClothFootstep).setCreativeTab(CreativeTabs.tabDecorations);
        TRepo.woolSlab2 = new SlabBase(PHConstruct.woolSlab2, Material.cloth, Block.cloth, 8, 8).setUnlocalizedName("cloth");
        TRepo.woolSlab2.setStepSound(Block.soundClothFootstep).setCreativeTab(CreativeTabs.tabDecorations);

        //Smeltery
        TRepo.smeltery = new SmelteryBlock(PHConstruct.smeltery).setUnlocalizedName("Smeltery");
        TRepo.smelteryNether = new SmelteryBlock(PHConstruct.smelteryNether, "nether").setUnlocalizedName("Smeltery");
        TRepo.lavaTank = new LavaTankBlock(PHConstruct.lavaTank).setUnlocalizedName("LavaTank");
        TRepo.lavaTank.setStepSound(Block.soundGlassFootstep);

        TRepo.searedBlock = new SearedBlock(PHConstruct.searedTable).setUnlocalizedName("SearedBlock");

        TRepo.castingChannel = (new CastingChannelBlock(PHConstruct.castingChannel)).setUnlocalizedName("CastingChannel");

        TRepo.tankAir = new TankAirBlock(PHConstruct.airTank, Material.leaves).setBlockUnbreakable().setUnlocalizedName("tconstruct.tank.air");

        //Traps
        TRepo.landmine = new BlockLandmine(PHConstruct.landmine).setHardness(0.5F).setResistance(0F).setStepSound(Block.soundMetalFootstep).setCreativeTab(CreativeTabs.tabRedstone)
                .setUnlocalizedName("landmine");
        TRepo.punji = new Punji(PHConstruct.punji).setUnlocalizedName("trap.punji");
        TRepo.barricadeOak = new BarricadeBlock(PHConstruct.barricadeOak, Block.wood, 0).setUnlocalizedName("trap.barricade.oak");
        TRepo.barricadeSpruce = new BarricadeBlock(PHConstruct.barricadeSpruce, Block.wood, 1).setUnlocalizedName("trap.barricade.spruce");
        TRepo.barricadeBirch = new BarricadeBlock(PHConstruct.barricadeBirch, Block.wood, 2).setUnlocalizedName("trap.barricade.birch");
        TRepo.barricadeJungle = new BarricadeBlock(PHConstruct.barricadeJungle, Block.wood, 3).setUnlocalizedName("trap.barricade.jungle");
        TRepo.slimeExplosive = new SlimeExplosive(PHConstruct.slimeExplosive).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("explosive.slime");

        TRepo.dryingRack = new DryingRack(PHConstruct.dryingRack).setUnlocalizedName("Armor.DryingRack");

        //Liquids
        TRepo.liquidMetal = new MaterialLiquid(MapColor.tntColor);

        TRepo.moltenIronFluid = new Fluid("iron.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenIronFluid))
            TRepo.moltenIronFluid = FluidRegistry.getFluid("iron.molten");
        TRepo.moltenIron = new TConstructFluid(PHConstruct.moltenIron, TRepo.moltenIronFluid, Material.lava, "liquid_iron").setUnlocalizedName("fluid.molten.iron");
        GameRegistry.registerBlock(TRepo.moltenIron, "fluid.molten.iron");
        TRepo.moltenIronFluid.setBlockID(TRepo.moltenIron).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenIronFluid, 1000), new ItemStack(TRepo.buckets, 1, 0), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenGoldFluid = new Fluid("gold.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenGoldFluid))
            TRepo.moltenGoldFluid = FluidRegistry.getFluid("gold.molten");
        TRepo.moltenGold = new TConstructFluid(PHConstruct.moltenGold, TRepo.moltenGoldFluid, Material.lava, "liquid_gold").setUnlocalizedName("fluid.molten.gold");
        GameRegistry.registerBlock(TRepo.moltenGold, "fluid.molten.gold");
        TRepo.moltenGoldFluid.setBlockID(TRepo.moltenGold).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenGoldFluid, 1000), new ItemStack(TRepo.buckets, 1, 1), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenCopperFluid = new Fluid("copper.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenCopperFluid))
            TRepo.moltenCopperFluid = FluidRegistry.getFluid("copper.molten");
        TRepo.moltenCopper = new TConstructFluid(PHConstruct.moltenCopper, TRepo.moltenCopperFluid, Material.lava, "liquid_copper").setUnlocalizedName("fluid.molten.copper");
        GameRegistry.registerBlock(TRepo.moltenCopper, "fluid.molten.copper");
        TRepo.moltenCopperFluid.setBlockID(TRepo.moltenCopper).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenCopperFluid, 1000), new ItemStack(TRepo.buckets, 1, 2), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenTinFluid = new Fluid("tin.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenTinFluid))
            TRepo.moltenTinFluid = FluidRegistry.getFluid("tin.molten");
        TRepo.moltenTin = new TConstructFluid(PHConstruct.moltenTin, TRepo.moltenTinFluid, Material.lava, "liquid_tin").setUnlocalizedName("fluid.molten.tin");
        GameRegistry.registerBlock(TRepo.moltenTin, "fluid.molten.tin");
        TRepo.moltenTinFluid.setBlockID(TRepo.moltenTin).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenTinFluid, 1000), new ItemStack(TRepo.buckets, 1, 3), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenAluminumFluid = new Fluid("aluminum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAluminumFluid))
            TRepo.moltenAluminumFluid = FluidRegistry.getFluid("aluminum.molten");
        TRepo.moltenAluminum = new TConstructFluid(PHConstruct.moltenAluminum, TRepo.moltenAluminumFluid, Material.lava, "liquid_aluminum").setUnlocalizedName("fluid.molten.aluminum");
        GameRegistry.registerBlock(TRepo.moltenAluminum, "fluid.molten.aluminum");
        TRepo.moltenAluminumFluid.setBlockID(TRepo.moltenAluminum).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAluminumFluid, 1000), new ItemStack(TRepo.buckets, 1, 4), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenCobaltFluid = new Fluid("cobalt.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenCobaltFluid))
            TRepo.moltenCobaltFluid = FluidRegistry.getFluid("cobalt.molten");
        TRepo.moltenCobalt = new TConstructFluid(PHConstruct.moltenCobalt, TRepo.moltenCobaltFluid, Material.lava, "liquid_cobalt").setUnlocalizedName("fluid.molten.cobalt");
        GameRegistry.registerBlock(TRepo.moltenCobalt, "fluid.molten.cobalt");
        TRepo.moltenCobaltFluid.setBlockID(TRepo.moltenCobalt).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenCobaltFluid, 1000), new ItemStack(TRepo.buckets, 1, 5), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenArditeFluid = new Fluid("ardite.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenArditeFluid))
            TRepo.moltenArditeFluid = FluidRegistry.getFluid("ardite.molten");
        TRepo.moltenArdite = new TConstructFluid(PHConstruct.moltenArdite, TRepo.moltenArditeFluid, Material.lava, "liquid_ardite").setUnlocalizedName("fluid.molten.ardite");
        GameRegistry.registerBlock(TRepo.moltenArdite, "fluid.molten.ardite");
        TRepo.moltenArditeFluid.setBlockID(TRepo.moltenArdite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenArditeFluid, 1000), new ItemStack(TRepo.buckets, 1, 6), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenBronzeFluid = new Fluid("bronze.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenBronzeFluid))
            TRepo.moltenBronzeFluid = FluidRegistry.getFluid("bronze.molten");
        TRepo.moltenBronze = new TConstructFluid(PHConstruct.moltenBronze, TRepo.moltenBronzeFluid, Material.lava, "liquid_bronze").setUnlocalizedName("fluid.molten.bronze");
        GameRegistry.registerBlock(TRepo.moltenBronze, "fluid.molten.bronze");
        TRepo.moltenBronzeFluid.setBlockID(TRepo.moltenBronze).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenBronzeFluid, 1000), new ItemStack(TRepo.buckets, 1, 7), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenAlubrassFluid = new Fluid("aluminumbrass.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAlubrassFluid))
            TRepo.moltenAlubrassFluid = FluidRegistry.getFluid("aluminumbrass.molten");
        TRepo.moltenAlubrass = new TConstructFluid(PHConstruct.moltenAlubrass, TRepo.moltenAlubrassFluid, Material.lava, "liquid_alubrass").setUnlocalizedName("fluid.molten.alubrass");
        GameRegistry.registerBlock(TRepo.moltenAlubrass, "fluid.molten.alubrass");
        TRepo.moltenAlubrassFluid.setBlockID(TRepo.moltenAlubrass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAlubrassFluid, 1000), new ItemStack(TRepo.buckets, 1, 8), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenManyullynFluid = new Fluid("manyullyn.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenManyullynFluid))
            TRepo.moltenManyullynFluid = FluidRegistry.getFluid("manyullyn.molten");
        TRepo.moltenManyullyn = new TConstructFluid(PHConstruct.moltenManyullyn, TRepo.moltenManyullynFluid, Material.lava, "liquid_manyullyn").setUnlocalizedName("fluid.molten.manyullyn");
        GameRegistry.registerBlock(TRepo.moltenManyullyn, "fluid.molten.manyullyn");
        TRepo.moltenManyullynFluid.setBlockID(TRepo.moltenManyullyn).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenManyullynFluid, 1000), new ItemStack(TRepo.buckets, 1, 9), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenAlumiteFluid = new Fluid("alumite.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAlumiteFluid))
            TRepo.moltenAlumiteFluid = FluidRegistry.getFluid("alumite.molten");
        TRepo.moltenAlumite = new TConstructFluid(PHConstruct.moltenAlumite, TRepo.moltenAlumiteFluid, Material.lava, "liquid_alumite").setUnlocalizedName("fluid.molten.alumite");
        GameRegistry.registerBlock(TRepo.moltenAlumite, "fluid.molten.alumite");
        TRepo.moltenAlumiteFluid.setBlockID(TRepo.moltenAlumite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAlumiteFluid, 1000), new ItemStack(TRepo.buckets, 1, 10), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenObsidianFluid = new Fluid("obsidian.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenObsidianFluid))
            TRepo.moltenObsidianFluid = FluidRegistry.getFluid("obsidian.molten");
        TRepo.moltenObsidian = new TConstructFluid(PHConstruct.moltenObsidian, TRepo.moltenObsidianFluid, Material.lava, "liquid_obsidian").setUnlocalizedName("fluid.molten.obsidian");
        GameRegistry.registerBlock(TRepo.moltenObsidian, "fluid.molten.obsidian");
        TRepo.moltenObsidianFluid.setBlockID(TRepo.moltenObsidian).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenObsidianFluid, 1000), new ItemStack(TRepo.buckets, 1, 11), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenSteelFluid = new Fluid("steel.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenSteelFluid))
            TRepo.moltenSteelFluid = FluidRegistry.getFluid("steel.molten");
        TRepo.moltenSteel = new TConstructFluid(PHConstruct.moltenSteel, TRepo.moltenSteelFluid, Material.lava, "liquid_steel").setUnlocalizedName("fluid.molten.steel");
        GameRegistry.registerBlock(TRepo.moltenSteel, "fluid.molten.steel");
        TRepo.moltenSteelFluid.setBlockID(TRepo.moltenSteel).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenSteelFluid, 1000), new ItemStack(TRepo.buckets, 1, 12), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenGlassFluid = new Fluid("glass.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenGlassFluid))
            TRepo.moltenGlassFluid = FluidRegistry.getFluid("glass.molten");
        TRepo.moltenGlass = new TConstructFluid(PHConstruct.moltenGlass, TRepo.moltenGlassFluid, Material.lava, "liquid_glass", true).setUnlocalizedName("fluid.molten.glass");
        GameRegistry.registerBlock(TRepo.moltenGlass, "fluid.molten.glass");
        TRepo.moltenGlassFluid.setBlockID(TRepo.moltenGlass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenGlassFluid, 1000), new ItemStack(TRepo.buckets, 1, 13), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenStoneFluid = new Fluid("stone.seared");
        if (!FluidRegistry.registerFluid(TRepo.moltenStoneFluid))
            TRepo.moltenStoneFluid = FluidRegistry.getFluid("stone.seared");
        TRepo.moltenStone = new TConstructFluid(PHConstruct.moltenStone, TRepo.moltenStoneFluid, Material.lava, "liquid_stone").setUnlocalizedName("molten.stone");
        GameRegistry.registerBlock(TRepo.moltenStone, "molten.stone");
        TRepo.moltenStoneFluid.setBlockID(TRepo.moltenStone).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenStoneFluid, 1000), new ItemStack(TRepo.buckets, 1, 14), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenEmeraldFluid = new Fluid("emerald.liquid");
        if (!FluidRegistry.registerFluid(TRepo.moltenEmeraldFluid))
            TRepo.moltenEmeraldFluid = FluidRegistry.getFluid("emerald.liquid");
        TRepo.moltenEmerald = new TConstructFluid(PHConstruct.moltenEmerald, TRepo.moltenEmeraldFluid, Material.water, "liquid_villager").setUnlocalizedName("molten.emerald");
        GameRegistry.registerBlock(TRepo.moltenEmerald, "molten.emerald");
        TRepo.moltenEmeraldFluid.setBlockID(TRepo.moltenEmerald).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenEmeraldFluid, 1000), new ItemStack(TRepo.buckets, 1, 15), new ItemStack(Item.bucketEmpty)));

        TRepo.bloodFluid = new Fluid("blood");
        if (!FluidRegistry.registerFluid(TRepo.bloodFluid))
            TRepo.bloodFluid = FluidRegistry.getFluid("blood");
        TRepo.blood = new BloodBlock(PHConstruct.blood, TRepo.bloodFluid, Material.water, "liquid_cow").setUnlocalizedName("liquid.blood");
        GameRegistry.registerBlock(TRepo.blood, "liquid.blood");
        TRepo.bloodFluid.setBlockID(TRepo.blood).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.bloodFluid, 1000), new ItemStack(TRepo.buckets, 1, 16), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenNickelFluid = new Fluid("nickel.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenNickelFluid))
            TRepo.moltenNickelFluid = FluidRegistry.getFluid("nickel.molten");
        TRepo.moltenNickel = new TConstructFluid(PHConstruct.moltenNickel, TRepo.moltenNickelFluid, Material.lava, "liquid_ferrous").setUnlocalizedName("fluid.molten.nickel");
        GameRegistry.registerBlock(TRepo.moltenNickel, "fluid.molten.nickel");
        TRepo.moltenNickelFluid.setBlockID(TRepo.moltenNickel).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenNickelFluid, 1000), new ItemStack(TRepo.buckets, 1, 17), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenLeadFluid = new Fluid("lead.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenLeadFluid))
            TRepo.moltenLeadFluid = FluidRegistry.getFluid("lead.molten");
        TRepo.moltenLead = new TConstructFluid(PHConstruct.moltenLead, TRepo.moltenLeadFluid, Material.lava, "liquid_lead").setUnlocalizedName("fluid.molten.lead");
        GameRegistry.registerBlock(TRepo.moltenLead, "fluid.molten.lead");
        TRepo.moltenLeadFluid.setBlockID(TRepo.moltenLead).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenLeadFluid, 1000), new ItemStack(TRepo.buckets, 1, 18), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenSilverFluid = new Fluid("silver.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenSilverFluid))
            TRepo.moltenSilverFluid = FluidRegistry.getFluid("silver.molten");
        TRepo.moltenSilver = new TConstructFluid(PHConstruct.moltenSilver, TRepo.moltenSilverFluid, Material.lava, "liquid_silver").setUnlocalizedName("fluid.molten.silver");
        GameRegistry.registerBlock(TRepo.moltenSilver, "fluid.molten.silver");
        TRepo.moltenSilverFluid.setBlockID(TRepo.moltenSilver).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenSilverFluid, 1000), new ItemStack(TRepo.buckets, 1, 19), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenShinyFluid = new Fluid("platinum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenShinyFluid))
            TRepo.moltenShinyFluid = FluidRegistry.getFluid("platinum.molten");
        TRepo.moltenShiny = new TConstructFluid(PHConstruct.moltenShiny, TRepo.moltenShinyFluid, Material.lava, "liquid_shiny").setUnlocalizedName("fluid.molten.shiny");
        GameRegistry.registerBlock(TRepo.moltenShiny, "fluid.molten.shiny");
        TRepo.moltenShinyFluid.setBlockID(TRepo.moltenShiny).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenShinyFluid, 1000), new ItemStack(TRepo.buckets, 1, 20), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenInvarFluid = new Fluid("invar.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenInvarFluid))
            TRepo.moltenInvarFluid = FluidRegistry.getFluid("invar.molten");
        TRepo.moltenInvar = new TConstructFluid(PHConstruct.moltenInvar, TRepo.moltenInvarFluid, Material.lava, "liquid_invar").setUnlocalizedName("fluid.molten.invar");
        GameRegistry.registerBlock(TRepo.moltenInvar, "fluid.molten.invar");
        TRepo.moltenInvarFluid.setBlockID(TRepo.moltenInvar).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenInvarFluid, 1000), new ItemStack(TRepo.buckets, 1, 21), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenElectrumFluid = new Fluid("electrum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenElectrumFluid))
            TRepo.moltenElectrumFluid = FluidRegistry.getFluid("electrum.molten");
        TRepo.moltenElectrum = new TConstructFluid(PHConstruct.moltenElectrum, TRepo.moltenElectrumFluid, Material.lava, "liquid_electrum").setUnlocalizedName("fluid.molten.electrum");
        GameRegistry.registerBlock(TRepo.moltenElectrum, "fluid.molten.electrum");
        TRepo.moltenElectrumFluid.setBlockID(TRepo.moltenElectrum).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenElectrumFluid, 1000), new ItemStack(TRepo.buckets, 1, 22), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenEnderFluid = new Fluid("ender");
        if (!FluidRegistry.registerFluid(TRepo.moltenEnderFluid))
        {
            TRepo.moltenEnderFluid = FluidRegistry.getFluid("ender");
            TRepo.moltenEnder = Block.blocksList[TRepo.moltenEnderFluid.getBlockID()];
            if (TRepo.moltenEnder == null)
                TConstruct.logger.info("Molten ender block missing!");
        }
        else
        {
            TRepo.moltenEnder = new TConstructFluid(PHConstruct.moltenEnder, TRepo.moltenEnderFluid, Material.water, "liquid_ender").setUnlocalizedName("fluid.ender");
            GameRegistry.registerBlock(TRepo.moltenEnder, "fluid.ender");
            TRepo.moltenEnderFluid.setBlockID(TRepo.moltenEnder).setDensity(3000).setViscosity(6000);
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenEnderFluid, 1000), new ItemStack(TRepo.buckets, 1, 23), new ItemStack(Item.bucketEmpty)));
        }

        //Slime
        TRepo.slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        TRepo.blueSlimeFluid = new Fluid("slime.blue");
        if (!FluidRegistry.registerFluid(TRepo.blueSlimeFluid))
            TRepo.blueSlimeFluid = FluidRegistry.getFluid("slime.blue");
        TRepo.slimePool = new SlimeFluid(PHConstruct.slimePoolBlue, TRepo.blueSlimeFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TRepo.slimeStep)
                .setUnlocalizedName("liquid.slime");
        GameRegistry.registerBlock(TRepo.slimePool, "liquid.slime");
        TRepo.blueSlimeFluid.setBlockID(TRepo.slimePool);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.blueSlimeFluid, 1000), new ItemStack(TRepo.buckets, 1, 24), new ItemStack(Item.bucketEmpty)));

        //Glue
        TRepo.glueFluid = new Fluid("glue").setDensity(6000).setViscosity(6000).setTemperature(200);
        if (!FluidRegistry.registerFluid(TRepo.glueFluid))
            TRepo.glueFluid = FluidRegistry.getFluid("glue");
        TRepo.glueFluidBlock = new GlueFluid(PHConstruct.glueFluidBlock, TRepo.glueFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TRepo.slimeStep)
                .setUnlocalizedName("liquid.glue");
        GameRegistry.registerBlock(TRepo.glueFluidBlock, "liquid.glue");
        TRepo.glueFluid.setBlockID(TRepo.glueFluidBlock);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.glueFluid, 1000), new ItemStack(TRepo.buckets, 1, 26), new ItemStack(Item.bucketEmpty)));

        TRepo.pigIronFluid = new Fluid("pigiron.molten");
        if (!FluidRegistry.registerFluid(TRepo.pigIronFluid))
            TRepo.pigIronFluid = FluidRegistry.getFluid("pigiron.molten");
        else
            TRepo.pigIronFluid.setDensity(3000).setViscosity(6000).setTemperature(1300);
        TRepo.fluids = new Fluid[]{TRepo.moltenIronFluid, TRepo.moltenGoldFluid, TRepo.moltenCopperFluid, TRepo.moltenTinFluid, TRepo.moltenAluminumFluid, TRepo.moltenCobaltFluid, TRepo.moltenArditeFluid, TRepo.moltenBronzeFluid, TRepo.moltenAlubrassFluid, TRepo.moltenManyullynFluid, TRepo.moltenAlumiteFluid, TRepo.moltenObsidianFluid, TRepo.moltenSteelFluid, TRepo.moltenGlassFluid, TRepo.moltenStoneFluid, TRepo.moltenEmeraldFluid, TRepo.bloodFluid, TRepo.moltenNickelFluid, TRepo.moltenLeadFluid, TRepo.moltenSilverFluid, TRepo.moltenShinyFluid, TRepo.moltenInvarFluid, TRepo.moltenElectrumFluid, TRepo.moltenEnderFluid, TRepo.blueSlimeFluid, TRepo.glueFluid, TRepo.pigIronFluid};
        TRepo.fluidBlocks = new Block[]{TRepo.moltenIron, TRepo.moltenGold, TRepo.moltenCopper, TRepo.moltenTin, TRepo.moltenAluminum, TRepo.moltenCobalt, TRepo.moltenArdite, TRepo.moltenBronze, TRepo.moltenAlubrass, TRepo.moltenManyullyn, TRepo.moltenAlumite, TRepo.moltenObsidian, TRepo.moltenSteel, TRepo.moltenGlass, TRepo.moltenStone, TRepo.moltenEmerald, TRepo.blood, TRepo.moltenNickel, TRepo.moltenLead, TRepo.moltenSilver, TRepo.moltenShiny, TRepo.moltenInvar, TRepo.moltenElectrum, TRepo.moltenEnder, TRepo.slimePool, TRepo.glueFluidBlock};
        //Slime Islands
        TRepo.slimeGel = new SlimeGel(PHConstruct.slimeGel).setStepSound(TRepo.slimeStep).setLightOpacity(0).setUnlocalizedName("slime.gel");
        TRepo.slimeGrass = new SlimeGrass(PHConstruct.slimeGrass).setStepSound(Block.soundGrassFootstep).setLightOpacity(0).setUnlocalizedName("slime.grass");
        TRepo.slimeTallGrass = new SlimeTallGrass(PHConstruct.slimeTallGrass).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("slime.grass.tall");
        TRepo.slimeLeaves = (SlimeLeaves) new SlimeLeaves(PHConstruct.slimeLeaves).setStepSound(TRepo.slimeStep).setLightOpacity(0).setUnlocalizedName("slime.leaves");
        TRepo.slimeSapling = (SlimeSapling) new SlimeSapling(PHConstruct.slimeSapling).setStepSound(TRepo.slimeStep).setUnlocalizedName("slime.sapling");
        TRepo.slimeChannel = new ConveyorBase(PHConstruct.slimeChannel, Material.water, "greencurrent").setHardness(0.3f).setStepSound(TRepo.slimeStep).setUnlocalizedName("slime.channel");
        TRepo.bloodChannel = new ConveyorBase(PHConstruct.bloodChannel, Material.water, "liquid_cow").setHardness(0.3f).setStepSound(TRepo.slimeStep).setUnlocalizedName("blood.channel");
        TRepo.slimePad = new SlimePad(PHConstruct.slimePad, Material.cloth).setStepSound(TRepo.slimeStep).setHardness(0.3f).setUnlocalizedName("slime.pad");
        

        //Decoration
        TRepo.stoneTorch = new StoneTorch(PHConstruct.stoneTorch).setUnlocalizedName("decoration.stonetorch");
        TRepo.stoneLadder = new StoneLadder(PHConstruct.stoneLadder).setUnlocalizedName("decoration.stoneladder");
        TRepo.multiBrick = new MultiBrick(PHConstruct.multiBrick).setUnlocalizedName("Decoration.Brick");
        TRepo.multiBrickFancy = new MultiBrickFancy(PHConstruct.multiBrickFancy).setUnlocalizedName("Decoration.BrickFancy");

        //Ores
        String[] berryOres = new String[] { "berry_iron", "berry_gold", "berry_copper", "berry_tin", "berry_iron_ripe", "berry_gold_ripe", "berry_copper_ripe", "berry_tin_ripe" };
        TRepo.oreBerry = (OreberryBush) new OreberryBush(PHConstruct.oreBerry, berryOres, 0, 4, new String[] { "oreIron", "oreGold", "oreCopper", "oreTin" }).setUnlocalizedName("ore.berries.one");
        String[] berryOresTwo = new String[] { "berry_aluminum", "berry_essence", "", "", "berry_aluminum_ripe", "berry_essence_ripe", "", "" };
        TRepo.oreBerrySecond = (OreberryBush) new OreberryBushEssence(PHConstruct.oreBerrySecond, berryOresTwo, 4, 2, new String[] { "oreAluminum", "oreSilver" })
                .setUnlocalizedName("ore.berries.two");

        String[] oreTypes = new String[] { "nether_slag", "nether_cobalt", "nether_ardite", "ore_copper", "ore_tin", "ore_aluminum", "ore_slag" };
        TRepo.oreSlag = new MetalOre(PHConstruct.oreSlag, Material.rock, 10.0F, oreTypes).setUnlocalizedName("tconstruct.stoneore");
        MinecraftForge.setBlockHarvestLevel(TRepo.oreSlag, 1, "pickaxe", 4);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreSlag, 2, "pickaxe", 4);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreSlag, 3, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreSlag, 4, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreSlag, 5, "pickaxe", 1);

        TRepo.oreGravel = new GravelOre(PHConstruct.oreGravel).setUnlocalizedName("GravelOre").setUnlocalizedName("tconstruct.gravelore");
        MinecraftForge.setBlockHarvestLevel(TRepo.oreGravel, 0, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreGravel, 1, "shovel", 2);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreGravel, 2, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreGravel, 3, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreGravel, 4, "shovel", 1);
        MinecraftForge.setBlockHarvestLevel(TRepo.oreGravel, 5, "shovel", 4);

        TRepo.speedBlock = new SpeedBlock(PHConstruct.speedBlock).setUnlocalizedName("SpeedBlock");

        //Glass
        TRepo.clearGlass = new GlassBlockConnected(PHConstruct.glass, "clear", false).setUnlocalizedName("GlassBlock");
        TRepo.clearGlass.stepSound = Block.soundGlassFootstep;
        TRepo.glassPane = new GlassPaneConnected(PHConstruct.glassPane, "clear", false);
        TRepo.stainedGlassClear = new GlassBlockConnectedMeta(PHConstruct.stainedGlassClear, "stained", true, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").setUnlocalizedName("GlassBlock.StainedClear");
        TRepo.stainedGlassClear.stepSound = Block.soundGlassFootstep;
        TRepo.stainedGlassClearPane = new GlassPaneStained(PHConstruct.stainedGlassClearPane);

        //Rail
        TRepo.woodenRail = new WoodRail(PHConstruct.woodenRail).setStepSound(Block.soundWoodFootstep).setCreativeTab(TConstructRegistry.blockTab).setUnlocalizedName("rail.wood");

    }

    void registerItems ()
    {
        TRepo.titleIcon = new TitleIcon(PHConstruct.uselessItem).setUnlocalizedName("tconstruct.titleicon");
        GameRegistry.registerItem(TRepo.titleIcon, "titleIcon");
        String[] blanks = new String[] { "blank_pattern", "blank_cast", "blank_cast" };
        TRepo.blankPattern = new CraftingItem(PHConstruct.blankPattern, blanks, blanks, "materials/").setUnlocalizedName("tconstruct.Pattern");
        GameRegistry.registerItem(TRepo.blankPattern, "blankPattern");

        TRepo.materials = new MaterialItem(PHConstruct.materials).setUnlocalizedName("tconstruct.Materials");
        TRepo.toolRod = new ToolPart(PHConstruct.toolRod, "_rod", "ToolRod").setUnlocalizedName("tconstruct.ToolRod");
        TRepo.toolShard = new ToolShard(PHConstruct.toolShard, "_chunk").setUnlocalizedName("tconstruct.ToolShard");
        TRepo.woodPattern = new Pattern(PHConstruct.woodPattern, "pattern_", "materials/").setUnlocalizedName("tconstruct.Pattern");
        TRepo.metalPattern = new MetalPattern(PHConstruct.metalPattern, "cast_", "materials/").setUnlocalizedName("tconstruct.MetalPattern");
        //armorPattern = new ArmorPattern(PHConstruct.armorPattern, "armorcast_", "materials/").setUnlocalizedName("tconstruct.ArmorPattern");
        GameRegistry.registerItem(TRepo.materials, "materials");
        GameRegistry.registerItem(TRepo.woodPattern, "woodPattern");
        GameRegistry.registerItem(TRepo.metalPattern, "metalPattern");
        //GameRegistry.registerItem(TRepo.armorPattern, "armorPattern");

        TConstructRegistry.addItemToDirectory("blankPattern", TRepo.blankPattern);
        TConstructRegistry.addItemToDirectory("woodPattern", TRepo.woodPattern);
        TConstructRegistry.addItemToDirectory("metalPattern", TRepo.metalPattern);
        //TConstructRegistry.addItemToDirectory("armorPattern", armorPattern);

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
        /*String[] armorPartTypes = { "helmet", "chestplate", "leggings", "boots" };
        for (int i = 1; i < armorPartTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(armorPartTypes[i] + "Cast", new ItemStack(armorPattern, 1, i));
        }*/

        TRepo.manualBook = new Manual(PHConstruct.manual);
        GameRegistry.registerItem(TRepo.manualBook, "manualBook");
        TRepo.buckets = new FilledBucket(PHConstruct.buckets);
        GameRegistry.registerItem(TRepo.buckets, "buckets");

        TRepo.pickaxe = new Pickaxe(PHConstruct.pickaxe);
        TRepo.shovel = new Shovel(PHConstruct.shovel);
        TRepo.hatchet = new Hatchet(PHConstruct.axe);
        TRepo.broadsword = new Broadsword(PHConstruct.broadsword);
        TRepo.longsword = new Longsword(PHConstruct.longsword);
        TRepo.rapier = new Rapier(PHConstruct.rapier);
        TRepo.dagger = new Dagger(PHConstruct.dagger);
        TRepo.cutlass = new Cutlass(PHConstruct.cutlass);

        TRepo.frypan = new FryingPan(PHConstruct.frypan);
        TRepo.battlesign = new BattleSign(PHConstruct.battlesign);
        TRepo.mattock = new Mattock(PHConstruct.mattock);
        TRepo.chisel = new Chisel(PHConstruct.chisel);

        TRepo.lumberaxe = new LumberAxe(PHConstruct.lumberaxe);
        TRepo.cleaver = new Cleaver(PHConstruct.cleaver);
        TRepo.scythe = new Scythe(PHConstruct.scythe);
        TRepo.excavator = new Excavator(PHConstruct.excavator);
        TRepo.hammer = new Hammer(PHConstruct.hammer);
        TRepo.battleaxe = new Battleaxe(PHConstruct.battleaxe);

        TRepo.shortbow = new Shortbow(PHConstruct.shortbow);
        TRepo.arrow = new Arrow(PHConstruct.arrow);

        Item[] tools = { TRepo.pickaxe, TRepo.shovel, TRepo.hatchet, TRepo.broadsword, TRepo.longsword, TRepo.rapier, TRepo.dagger, TRepo.cutlass, TRepo.frypan, TRepo.battlesign, TRepo.mattock,
                TRepo.chisel, TRepo.lumberaxe, TRepo.cleaver, TRepo.scythe, TRepo.excavator, TRepo.hammer, TRepo.battleaxe, TRepo.shortbow, TRepo.arrow };
        String[] toolStrings = { "pickaxe", "shovel", "hatchet", "broadsword", "longsword", "rapier", "dagger", "cutlass", "frypan", "battlesign", "mattock", "chisel", "lumberaxe", "cleaver",
                "scythe", "excavator", "hammer", "battleaxe", "shortbow", "arrow" };

        for (int i = 0; i < tools.length; i++)
        {
            GameRegistry.registerItem(tools[i], toolStrings[i]); // 1.7 compat
            TConstructRegistry.addItemToDirectory(toolStrings[i], tools[i]);
        }

        TRepo.potionLauncher = new PotionLauncher(PHConstruct.potionLauncher).setUnlocalizedName("tconstruct.PotionLauncher");
        GameRegistry.registerItem(TRepo.potionLauncher, "potionLauncher");

        TRepo.pickaxeHead = new ToolPart(PHConstruct.pickaxeHead, "_pickaxe_head", "PickHead").setUnlocalizedName("tconstruct.PickaxeHead");
        TRepo.shovelHead = new ToolPart(PHConstruct.shovelHead, "_shovel_head", "ShovelHead").setUnlocalizedName("tconstruct.ShovelHead");
        TRepo.hatchetHead = new ToolPart(PHConstruct.axeHead, "_axe_head", "AxeHead").setUnlocalizedName("tconstruct.AxeHead");
        TRepo.binding = new ToolPart(PHConstruct.binding, "_binding", "Binding").setUnlocalizedName("tconstruct.Binding");
        TRepo.toughBinding = new ToolPart(PHConstruct.toughBinding, "_toughbind", "ToughBind").setUnlocalizedName("tconstruct.ThickBinding");
        TRepo.toughRod = new ToolPart(PHConstruct.toughRod, "_toughrod", "ToughRod").setUnlocalizedName("tconstruct.ThickRod");
        TRepo.largePlate = new ToolPart(PHConstruct.largePlate, "_largeplate", "LargePlate").setUnlocalizedName("tconstruct.LargePlate");

        TRepo.swordBlade = new ToolPart(PHConstruct.swordBlade, "_sword_blade", "SwordBlade").setUnlocalizedName("tconstruct.SwordBlade");
        TRepo.wideGuard = new ToolPart(PHConstruct.largeGuard, "_large_guard", "LargeGuard").setUnlocalizedName("tconstruct.LargeGuard");
        TRepo.handGuard = new ToolPart(PHConstruct.medGuard, "_medium_guard", "MediumGuard").setUnlocalizedName("tconstruct.MediumGuard");
        TRepo.crossbar = new ToolPart(PHConstruct.crossbar, "_crossbar", "Crossbar").setUnlocalizedName("tconstruct.Crossbar");
        TRepo.knifeBlade = new ToolPart(PHConstruct.knifeBlade, "_knife_blade", "KnifeBlade").setUnlocalizedName("tconstruct.KnifeBlade");
        TRepo.fullGuard = new ToolPartHidden(PHConstruct.fullGuard, "_full_guard", "FullGuard").setUnlocalizedName("tconstruct.FullGuard");

        TRepo.frypanHead = new ToolPart(PHConstruct.frypanHead, "_frypan_head", "FrypanHead").setUnlocalizedName("tconstruct.FrypanHead");
        TRepo.signHead = new ToolPart(PHConstruct.signHead, "_battlesign_head", "SignHead").setUnlocalizedName("tconstruct.SignHead");
        TRepo.chiselHead = new ToolPart(PHConstruct.chiselHead, "_chisel_head", "ChiselHead").setUnlocalizedName("tconstruct.ChiselHead");

        TRepo.scytheBlade = new ToolPart(PHConstruct.scytheBlade, "_scythe_head", "ScytheHead").setUnlocalizedName("tconstruct.ScytheBlade");
        TRepo.broadAxeHead = new ToolPart(PHConstruct.lumberHead, "_lumberaxe_head", "LumberHead").setUnlocalizedName("tconstruct.LumberHead");
        TRepo.excavatorHead = new ToolPart(PHConstruct.excavatorHead, "_excavator_head", "ExcavatorHead").setUnlocalizedName("tconstruct.ExcavatorHead");
        TRepo.largeSwordBlade = new ToolPart(PHConstruct.largeSwordBlade, "_large_sword_blade", "LargeSwordBlade").setUnlocalizedName("tconstruct.LargeSwordBlade");
        TRepo.hammerHead = new ToolPart(PHConstruct.hammerHead, "_hammer_head", "HammerHead").setUnlocalizedName("tconstruct.HammerHead");

        TRepo.bowstring = new Bowstring(PHConstruct.bowstring).setUnlocalizedName("tconstruct.Bowstring");
        TRepo.arrowhead = new ToolPart(PHConstruct.arrowhead, "_arrowhead", "ArrowHead").setUnlocalizedName("tconstruct.Arrowhead");
        TRepo.fletching = new Fletching(PHConstruct.fletching).setUnlocalizedName("tconstruct.Fletching");

        Item[] toolParts = { TRepo.toolRod, TRepo.toolShard, TRepo.pickaxeHead, TRepo.shovelHead, TRepo.hatchetHead, TRepo.binding, TRepo.toughBinding, TRepo.toughRod, TRepo.largePlate,
                TRepo.swordBlade, TRepo.wideGuard, TRepo.handGuard, TRepo.crossbar, TRepo.knifeBlade, TRepo.fullGuard, TRepo.frypanHead, TRepo.signHead, TRepo.chiselHead, TRepo.scytheBlade,
                TRepo.broadAxeHead, TRepo.excavatorHead, TRepo.largeSwordBlade, TRepo.hammerHead, TRepo.bowstring, TRepo.fletching, TRepo.arrowhead };
        String[] toolPartStrings = { "toolRod", "toolShard", "pickaxeHead", "shovelHead", "hatchetHead", "binding", "toughBinding", "toughRod", "heavyPlate", "swordBlade", "wideGuard", "handGuard",
                "crossbar", "knifeBlade", "fullGuard", "frypanHead", "signHead", "chiselHead", "scytheBlade", "broadAxeHead", "excavatorHead", "largeSwordBlade", "hammerHead", "bowstring",
                "fletching", "arrowhead" };

        for (int i = 0; i < toolParts.length; i++)
        {
            GameRegistry.registerItem(toolParts[i], toolPartStrings[i]); // 1.7 compat
            TConstructRegistry.addItemToDirectory(toolPartStrings[i], toolParts[i]);
        }

        TRepo.diamondApple = new DiamondApple(PHConstruct.diamondApple).setUnlocalizedName("tconstruct.apple.diamond");
        TRepo.strangeFood = new StrangeFood(PHConstruct.slimefood).setUnlocalizedName("tconstruct.strangefood");
        TRepo.oreBerries = new OreBerries(PHConstruct.oreChunks).setUnlocalizedName("oreberry");
        GameRegistry.registerItem(TRepo.diamondApple, "diamondApple");
        GameRegistry.registerItem(TRepo.strangeFood, "strangeFood");
        GameRegistry.registerItem(TRepo.oreBerries, "oreBerries");

        TRepo.jerky = new Jerky(PHConstruct.jerky, Loader.isModLoaded("HungerOverhaul")).setUnlocalizedName("tconstruct.jerky");
        GameRegistry.registerItem(TRepo.jerky, "jerky");

        //Wearables
        //heavyHelmet = new TArmorBase(PHConstruct.heavyHelmet, 0).setUnlocalizedName("tconstruct.HeavyHelmet");
        TRepo.heartCanister = new HeartCanister(PHConstruct.heartCanister).setUnlocalizedName("tconstruct.canister");
        //heavyBoots = new TArmorBase(PHConstruct.heavyBoots, 3).setUnlocalizedName("tconstruct.HeavyBoots");
        //glove = new Glove(PHConstruct.glove).setUnlocalizedName("tconstruct.Glove");
        TRepo.knapsack = new Knapsack(PHConstruct.knapsack).setUnlocalizedName("tconstruct.storage");
        TRepo.goldHead = new GoldenHead(PHConstruct.goldHead, 4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F).setUnlocalizedName("goldenhead");
        //GameRegistry.registerItem(TRepo.heavyHelmet, "heavyHelmet");
        GameRegistry.registerItem(TRepo.heartCanister, "heartCanister");
        //GameRegistry.registerItem(TRepo.heavyBoots, "heavyBoots");
        //GameRegistry.registerItem(TRepo.glove, "glove");
        GameRegistry.registerItem(TRepo.knapsack, "knapsack");
        GameRegistry.registerItem(TRepo.goldHead, "goldHead");

        LiquidCasting basinCasting = TConstruct.getBasinCasting();
        TRepo.materialWood = EnumHelper.addArmorMaterial("WOOD", 2, new int[] { 1, 2, 2, 1 }, 3);
        TRepo.helmetWood = new ArmorBasic(PHConstruct.woodHelmet, TRepo.materialWood, 0, "wood").setUnlocalizedName("tconstruct.helmetWood");
        TRepo.chestplateWood = new ArmorBasic(PHConstruct.woodChestplate, TRepo.materialWood, 1, "wood").setUnlocalizedName("tconstruct.chestplateWood");
        TRepo.leggingsWood = new ArmorBasic(PHConstruct.woodPants, TRepo.materialWood, 2, "wood").setUnlocalizedName("tconstruct.leggingsWood");
        TRepo.bootsWood = new ArmorBasic(PHConstruct.woodBoots, TRepo.materialWood, 3, "wood").setUnlocalizedName("tconstruct.bootsWood");
        GameRegistry.registerItem(TRepo.helmetWood, "helmetWood");
        GameRegistry.registerItem(TRepo.chestplateWood, "chestplateWood");
        GameRegistry.registerItem(TRepo.leggingsWood, "leggingsWood");
        GameRegistry.registerItem(TRepo.bootsWood, "bootsWood");

        TRepo.exoGoggles = new ExoArmor(PHConstruct.exoGoggles, EnumArmorPart.HELMET, "exosuit").setUnlocalizedName("tconstruct.exoGoggles");
        TRepo.exoChest = new ExoArmor(PHConstruct.exoChest, EnumArmorPart.CHESTPLATE, "exosuit").setUnlocalizedName("tconstruct.exoChest");
        TRepo.exoPants = new ExoArmor(PHConstruct.exoPants, EnumArmorPart.LEGGINGS, "exosuit").setUnlocalizedName("tconstruct.exoPants");
        TRepo.exoShoes = new ExoArmor(PHConstruct.exoShoes, EnumArmorPart.BOOTS, "exosuit").setUnlocalizedName("tconstruct.exoShoes");

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

        TConstructRegistry.addBowstringMaterial(0, 2, new ItemStack(Item.silk), new ItemStack(TRepo.bowstring, 1, 0), 1F, 1F, 1f); //String
        TConstructRegistry.addFletchingMaterial(0, 2, new ItemStack(Item.feather), new ItemStack(TRepo.fletching, 1, 0), 100F, 0F, 0.05F); //Feather
        for (int i = 0; i < 4; i++)
            TConstructRegistry.addFletchingMaterial(1, 2, new ItemStack(Block.leaves, 1, i), new ItemStack(TRepo.fletching, 1, 1), 75F, 0F, 0.2F); //All four vanialla Leaves
        TConstructRegistry.addFletchingMaterial(2, 2, new ItemStack(TRepo.materials, 1, 1), new ItemStack(TRepo.fletching, 1, 2), 100F, 0F, 0.12F); //Slime
        TConstructRegistry.addFletchingMaterial(3, 2, new ItemStack(TRepo.materials, 1, 17), new ItemStack(TRepo.fletching, 1, 3), 100F, 0F, 0.12F); //BlueSlime

        PatternBuilder pb = PatternBuilder.instance;
        if (PHConstruct.enableTWood)
            pb.registerFullMaterial(Block.planks, 2, "Wood", new ItemStack(Item.stick), new ItemStack(Item.stick), 0);
        else
            pb.registerMaterialSet("Wood", new ItemStack(Item.stick, 2), new ItemStack(Item.stick), 0);
        if (PHConstruct.enableTStone)
        {
            pb.registerFullMaterial(Block.stone, 2, "Stone", new ItemStack(TRepo.toolShard, 1, 1), new ItemStack(TRepo.toolRod, 1, 1), 1);
            pb.registerMaterial(Block.cobblestone, 2, "Stone");
        }
        else
            pb.registerMaterialSet("Stone", new ItemStack(TRepo.toolShard, 1, 1), new ItemStack(TRepo.toolRod, 1, 1), 0);
        pb.registerFullMaterial(Item.ingotIron, 2, "Iron", new ItemStack(TRepo.toolShard, 1, 2), new ItemStack(TRepo.toolRod, 1, 2), 2);
        if (PHConstruct.enableTFlint)
            pb.registerFullMaterial(Item.flint, 2, "Flint", new ItemStack(TRepo.toolShard, 1, 3), new ItemStack(TRepo.toolRod, 1, 3), 3);
        else
            pb.registerMaterialSet("Flint", new ItemStack(TRepo.toolShard, 1, 3), new ItemStack(TRepo.toolRod, 1, 3), 3);
        if (PHConstruct.enableTCactus)
            pb.registerFullMaterial(Block.cactus, 2, "Cactus", new ItemStack(TRepo.toolShard, 1, 4), new ItemStack(TRepo.toolRod, 1, 4), 4);
        else
            pb.registerMaterialSet("Cactus", new ItemStack(TRepo.toolShard, 1, 4), new ItemStack(TRepo.toolRod, 1, 4), 4);
        if (PHConstruct.enableTBone)
            pb.registerFullMaterial(Item.bone, 2, "Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
        else
            pb.registerMaterialSet("Bone", new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.bone), 5);
        pb.registerFullMaterial(Block.obsidian, 2, "Obsidian", new ItemStack(TRepo.toolShard, 1, 6), new ItemStack(TRepo.toolRod, 1, 6), 6);
        pb.registerMaterial(new ItemStack(TRepo.materials, 1, 18), 2, "Obsidian");
        if (PHConstruct.enableTNetherrack)
            pb.registerFullMaterial(Block.netherrack, 2, "Netherrack", new ItemStack(TRepo.toolShard, 1, 7), new ItemStack(TRepo.toolRod, 1, 7), 7);
        else
            pb.registerMaterialSet("Netherrack", new ItemStack(TRepo.toolShard, 1, 7), new ItemStack(TRepo.toolRod, 1, 7), 7);
        if (PHConstruct.enableTSlime)
            pb.registerFullMaterial(new ItemStack(TRepo.materials, 1, 1), 2, "Slime", new ItemStack(TRepo.toolShard, 1, 8), new ItemStack(TRepo.toolRod, 1, 8), 8);
        else
            pb.registerMaterialSet("Slime", new ItemStack(TRepo.toolShard, 1, 8), new ItemStack(TRepo.toolRod, 1, 17), 8);
        if (PHConstruct.enableTPaper)
            pb.registerFullMaterial(new ItemStack(TRepo.materials, 1, 0), 2, "Paper", new ItemStack(Item.paper, 2), new ItemStack(TRepo.toolRod, 1, 9), 9);
        else
            pb.registerMaterialSet("BlueSlime", new ItemStack(Item.paper, 2), new ItemStack(TRepo.toolRod, 1, 9), 9);
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
        TConstructRegistry.materialTab.init(new ItemStack(TRepo.titleIcon, 1, 255));
        TConstructRegistry.blockTab.init(new ItemStack(TRepo.toolStationWood));
        ItemStack tool = new ItemStack(TRepo.longsword, 1, 0);

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
        if (fuel.itemID == TRepo.materials.itemID && fuel.getItemDamage() == 7)
            return 26400;
        return 0;
    }

    public void addAchievements ()
    {
        HashMap<String, Achievement> achievements = TAchievements.achievements;

        achievements.put("tconstruct.beginner", new Achievement(2741, "tconstruct.beginner", 0, 0, TRepo.manualBook, null).setIndependent().registerAchievement());
        achievements.put("tconstruct.pattern", new Achievement(2742, "tconstruct.pattern", 2, 1, TRepo.blankPattern, achievements.get("tconstruct.beginner")).registerAchievement());
        achievements.put("tconstruct.tinkerer",
                new Achievement(2743, "tconstruct.tinkerer", 2, 2, new ItemStack(TRepo.titleIcon, 1, 4096), achievements.get("tconstruct.pattern")).registerAchievement());
        achievements.put("tconstruct.preparedFight",
                new Achievement(2744, "tconstruct.preparedFight", 1, 3, new ItemStack(TRepo.titleIcon, 1, 4097), achievements.get("tconstruct.tinkerer")).registerAchievement());
        achievements.put("tconstruct.proTinkerer", new Achievement(2745, "tconstruct.proTinkerer", 4, 4, new ItemStack(TRepo.titleIcon, 1, 4098), achievements.get("tconstruct.tinkerer")).setSpecial()
                .registerAchievement());
        achievements.put("tconstruct.smelteryMaker", new Achievement(2746, "tconstruct.smelteryMaker", -2, -1, TRepo.smeltery, achievements.get("tconstruct.beginner")).registerAchievement());
        achievements.put("tconstruct.enemySlayer",
                new Achievement(2747, "tconstruct.enemySlayer", 0, 5, new ItemStack(TRepo.titleIcon, 1, 4099), achievements.get("tconstruct.preparedFight")).registerAchievement());
        achievements.put("tconstruct.dualConvenience", new Achievement(2748, "tconstruct.dualConvenience", 0, 7, new ItemStack(TRepo.titleIcon, 1, 4100), achievements.get("tconstruct.enemySlayer"))
                .setSpecial().registerAchievement());
    }
}
