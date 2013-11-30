package tconstruct.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import tconstruct.modifiers.*;
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
        TRepo.moltenIron = new TConstructFluid(PHConstruct.moltenIron, TRepo.moltenIronFluid, Material.lava, "liquid_iron").setUnlocalizedName("metal.molten.iron");
        GameRegistry.registerBlock(TRepo.moltenIron, "metal.molten.iron");
        TRepo.fluids[0] = TRepo.moltenIronFluid;
        TRepo.fluidBlocks[0] = TRepo.moltenIron;
        TRepo.moltenIronFluid.setBlockID(TRepo.moltenIron).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenIronFluid, 1000), new ItemStack(TRepo.buckets, 1, 0), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenGoldFluid = new Fluid("gold.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenGoldFluid))
            TRepo.moltenGoldFluid = FluidRegistry.getFluid("gold.molten");
        TRepo.moltenGold = new TConstructFluid(PHConstruct.moltenGold, TRepo.moltenGoldFluid, Material.lava, "liquid_gold").setUnlocalizedName("metal.molten.gold");
        GameRegistry.registerBlock(TRepo.moltenGold, "metal.molten.gold");
        TRepo.fluids[1] = TRepo.moltenGoldFluid;
        TRepo.fluidBlocks[1] = TRepo.moltenGold;
        TRepo.moltenGoldFluid.setBlockID(TRepo.moltenGold).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenGoldFluid, 1000), new ItemStack(TRepo.buckets, 1, 1), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenCopperFluid = new Fluid("copper.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenCopperFluid))
            TRepo.moltenCopperFluid = FluidRegistry.getFluid("copper.molten");
        TRepo.moltenCopper = new TConstructFluid(PHConstruct.moltenCopper, TRepo.moltenCopperFluid, Material.lava, "liquid_copper").setUnlocalizedName("metal.molten.copper");
        GameRegistry.registerBlock(TRepo.moltenCopper, "metal.molten.copper");
        TRepo.fluids[2] = TRepo.moltenCopperFluid;
        TRepo.fluidBlocks[2] = TRepo.moltenCopper;
        TRepo.moltenCopperFluid.setBlockID(TRepo.moltenCopper).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenCopperFluid, 1000), new ItemStack(TRepo.buckets, 1, 2), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenTinFluid = new Fluid("tin.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenTinFluid))
            TRepo.moltenTinFluid = FluidRegistry.getFluid("tin.molten");
        TRepo.moltenTin = new TConstructFluid(PHConstruct.moltenTin, TRepo.moltenTinFluid, Material.lava, "liquid_tin").setUnlocalizedName("metal.molten.tin");
        GameRegistry.registerBlock(TRepo.moltenTin, "metal.molten.tin");
        TRepo.fluids[3] = TRepo.moltenTinFluid;
        TRepo.fluidBlocks[3] = TRepo.moltenTin;
        TRepo.moltenTinFluid.setBlockID(TRepo.moltenTin).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenTinFluid, 1000), new ItemStack(TRepo.buckets, 1, 3), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenAluminumFluid = new Fluid("aluminum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAluminumFluid))
            TRepo.moltenAluminumFluid = FluidRegistry.getFluid("aluminum.molten");
        TRepo.moltenAluminum = new TConstructFluid(PHConstruct.moltenAluminum, TRepo.moltenAluminumFluid, Material.lava, "liquid_aluminum").setUnlocalizedName("metal.molten.aluminum");
        GameRegistry.registerBlock(TRepo.moltenAluminum, "metal.molten.aluminum");
        TRepo.fluids[4] = TRepo.moltenAluminumFluid;
        TRepo.fluidBlocks[4] = TRepo.moltenAluminum;
        TRepo.moltenAluminumFluid.setBlockID(TRepo.moltenAluminum).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAluminumFluid, 1000), new ItemStack(TRepo.buckets, 1, 4), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenCobaltFluid = new Fluid("cobalt.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenCobaltFluid))
            TRepo.moltenCobaltFluid = FluidRegistry.getFluid("cobalt.molten");
        TRepo.moltenCobalt = new TConstructFluid(PHConstruct.moltenCobalt, TRepo.moltenCobaltFluid, Material.lava, "liquid_cobalt").setUnlocalizedName("metal.molten.cobalt");
        GameRegistry.registerBlock(TRepo.moltenCobalt, "metal.molten.cobalt");
        TRepo.fluids[5] = TRepo.moltenCobaltFluid;
        TRepo.fluidBlocks[5] = TRepo.moltenCobalt;
        TRepo.moltenCobaltFluid.setBlockID(TRepo.moltenCobalt).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenCobaltFluid, 1000), new ItemStack(TRepo.buckets, 1, 5), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenArditeFluid = new Fluid("ardite.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenArditeFluid))
            TRepo.moltenArditeFluid = FluidRegistry.getFluid("ardite.molten");
        TRepo.moltenArdite = new TConstructFluid(PHConstruct.moltenArdite, TRepo.moltenArditeFluid, Material.lava, "liquid_ardite").setUnlocalizedName("metal.molten.ardite");
        GameRegistry.registerBlock(TRepo.moltenArdite, "metal.molten.ardite");
        TRepo.fluids[6] = TRepo.moltenArditeFluid;
        TRepo.fluidBlocks[6] = TRepo.moltenArdite;
        TRepo.moltenArditeFluid.setBlockID(TRepo.moltenArdite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenArditeFluid, 1000), new ItemStack(TRepo.buckets, 1, 6), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenBronzeFluid = new Fluid("bronze.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenBronzeFluid))
            TRepo.moltenBronzeFluid = FluidRegistry.getFluid("bronze.molten");
        TRepo.moltenBronze = new TConstructFluid(PHConstruct.moltenBronze, TRepo.moltenBronzeFluid, Material.lava, "liquid_bronze").setUnlocalizedName("metal.molten.bronze");
        GameRegistry.registerBlock(TRepo.moltenBronze, "metal.molten.bronze");
        TRepo.fluids[7] = TRepo.moltenBronzeFluid;
        TRepo.fluidBlocks[7] = TRepo.moltenBronze;
        TRepo.moltenBronzeFluid.setBlockID(TRepo.moltenBronze).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenBronzeFluid, 1000), new ItemStack(TRepo.buckets, 1, 7), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenAlubrassFluid = new Fluid("aluminumbrass.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAlubrassFluid))
            TRepo.moltenAlubrassFluid = FluidRegistry.getFluid("aluminumbrass.molten");
        TRepo.moltenAlubrass = new TConstructFluid(PHConstruct.moltenAlubrass, TRepo.moltenAlubrassFluid, Material.lava, "liquid_alubrass").setUnlocalizedName("metal.molten.alubrass");
        GameRegistry.registerBlock(TRepo.moltenAlubrass, "metal.molten.alubrass");
        TRepo.fluids[8] = TRepo.moltenAlubrassFluid;
        TRepo.fluidBlocks[8] = TRepo.moltenAlubrass;
        TRepo.moltenAlubrassFluid.setBlockID(TRepo.moltenAlubrass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAlubrassFluid, 1000), new ItemStack(TRepo.buckets, 1, 8), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenManyullynFluid = new Fluid("manyullyn.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenManyullynFluid))
            TRepo.moltenManyullynFluid = FluidRegistry.getFluid("manyullyn.molten");
        TRepo.moltenManyullyn = new TConstructFluid(PHConstruct.moltenManyullyn, TRepo.moltenManyullynFluid, Material.lava, "liquid_manyullyn").setUnlocalizedName("metal.molten.manyullyn");
        GameRegistry.registerBlock(TRepo.moltenManyullyn, "metal.molten.manyullyn");
        TRepo.fluids[9] = TRepo.moltenManyullynFluid;
        TRepo.fluidBlocks[9] = TRepo.moltenManyullyn;
        TRepo.moltenManyullynFluid.setBlockID(TRepo.moltenManyullyn).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenManyullynFluid, 1000), new ItemStack(TRepo.buckets, 1, 9), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenAlumiteFluid = new Fluid("alumite.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenAlumiteFluid))
            TRepo.moltenAlumiteFluid = FluidRegistry.getFluid("alumite.molten");
        TRepo.moltenAlumite = new TConstructFluid(PHConstruct.moltenAlumite, TRepo.moltenAlumiteFluid, Material.lava, "liquid_alumite").setUnlocalizedName("metal.molten.alumite");
        GameRegistry.registerBlock(TRepo.moltenAlumite, "metal.molten.alumite");
        TRepo.fluids[10] = TRepo.moltenAlumiteFluid;
        TRepo.fluidBlocks[10] = TRepo.moltenAlumite;
        TRepo.moltenAlumiteFluid.setBlockID(TRepo.moltenAlumite).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenAlumiteFluid, 1000), new ItemStack(TRepo.buckets, 1, 10), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenObsidianFluid = new Fluid("obsidian.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenObsidianFluid))
            TRepo.moltenObsidianFluid = FluidRegistry.getFluid("obsidian.molten");
        TRepo.moltenObsidian = new TConstructFluid(PHConstruct.moltenObsidian, TRepo.moltenObsidianFluid, Material.lava, "liquid_obsidian").setUnlocalizedName("metal.molten.obsidian");
        GameRegistry.registerBlock(TRepo.moltenObsidian, "metal.molten.obsidian");
        TRepo.fluids[11] = TRepo.moltenObsidianFluid;
        TRepo.fluidBlocks[11] = TRepo.moltenObsidian;
        TRepo.moltenObsidianFluid.setBlockID(TRepo.moltenObsidian).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenObsidianFluid, 1000), new ItemStack(TRepo.buckets, 1, 11), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenSteelFluid = new Fluid("steel.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenSteelFluid))
            TRepo.moltenSteelFluid = FluidRegistry.getFluid("steel.molten");
        TRepo.moltenSteel = new TConstructFluid(PHConstruct.moltenSteel, TRepo.moltenSteelFluid, Material.lava, "liquid_steel").setUnlocalizedName("metal.molten.steel");
        GameRegistry.registerBlock(TRepo.moltenSteel, "metal.molten.steel");
        TRepo.fluids[12] = TRepo.moltenSteelFluid;
        TRepo.fluidBlocks[12] = TRepo.moltenSteel;
        TRepo.moltenSteelFluid.setBlockID(TRepo.moltenSteel).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenSteelFluid, 1000), new ItemStack(TRepo.buckets, 1, 12), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenGlassFluid = new Fluid("glass.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenGlassFluid))
            TRepo.moltenGlassFluid = FluidRegistry.getFluid("glass.molten");
        TRepo.moltenGlass = new TConstructFluid(PHConstruct.moltenGlass, TRepo.moltenGlassFluid, Material.lava, "liquid_glass", true).setUnlocalizedName("metal.molten.glass");
        GameRegistry.registerBlock(TRepo.moltenGlass, "metal.molten.glass");
        TRepo.fluids[13] = TRepo.moltenGlassFluid;
        TRepo.fluidBlocks[13] = TRepo.moltenGlass;
        TRepo.moltenGlassFluid.setBlockID(TRepo.moltenGlass).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenGlassFluid, 1000), new ItemStack(TRepo.buckets, 1, 13), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenStoneFluid = new Fluid("stone.seared");
        if (!FluidRegistry.registerFluid(TRepo.moltenStoneFluid))
            TRepo.moltenStoneFluid = FluidRegistry.getFluid("stone.seared");
        TRepo.moltenStone = new TConstructFluid(PHConstruct.moltenStone, TRepo.moltenStoneFluid, Material.lava, "liquid_stone").setUnlocalizedName("molten.stone");
        GameRegistry.registerBlock(TRepo.moltenStone, "molten.stone");
        TRepo.fluids[14] = TRepo.moltenStoneFluid;
        TRepo.fluidBlocks[14] = TRepo.moltenStone;
        TRepo.moltenStoneFluid.setBlockID(TRepo.moltenStone).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenStoneFluid, 1000), new ItemStack(TRepo.buckets, 1, 14), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenEmeraldFluid = new Fluid("emerald.liquid");
        if (!FluidRegistry.registerFluid(TRepo.moltenEmeraldFluid))
            TRepo.moltenEmeraldFluid = FluidRegistry.getFluid("emerald.liquid");
        TRepo.moltenEmerald = new TConstructFluid(PHConstruct.moltenEmerald, TRepo.moltenEmeraldFluid, Material.water, "liquid_villager").setUnlocalizedName("molten.emerald");
        GameRegistry.registerBlock(TRepo.moltenEmerald, "molten.emerald");
        TRepo.fluids[15] = TRepo.moltenEmeraldFluid;
        TRepo.fluidBlocks[15] = TRepo.moltenEmerald;
        TRepo.moltenEmeraldFluid.setBlockID(TRepo.moltenEmerald).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenEmeraldFluid, 1000), new ItemStack(TRepo.buckets, 1, 15), new ItemStack(Item.bucketEmpty)));

        TRepo.bloodFluid = new Fluid("blood");
        if (!FluidRegistry.registerFluid(TRepo.bloodFluid))
            TRepo.bloodFluid = FluidRegistry.getFluid("blood");
        TRepo.blood = new BloodBlock(PHConstruct.blood, TRepo.bloodFluid, Material.water, "liquid_cow").setUnlocalizedName("liquid.blood");
        GameRegistry.registerBlock(TRepo.blood, "liquid.blood");
        TRepo.fluids[16] = TRepo.bloodFluid;
        TRepo.fluidBlocks[16] = TRepo.blood;
        TRepo.bloodFluid.setBlockID(TRepo.blood).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.bloodFluid, 1000), new ItemStack(TRepo.buckets, 1, 16), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenNickelFluid = new Fluid("nickel.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenNickelFluid))
            TRepo.moltenNickelFluid = FluidRegistry.getFluid("nickel.molten");
        TRepo.moltenNickel = new TConstructFluid(PHConstruct.moltenNickel, TRepo.moltenNickelFluid, Material.lava, "liquid_ferrous").setUnlocalizedName("metal.molten.nickel");
        GameRegistry.registerBlock(TRepo.moltenNickel, "metal.molten.nickel");
        TRepo.fluids[17] = TRepo.moltenNickelFluid;
        TRepo.fluidBlocks[17] = TRepo.moltenNickel;
        TRepo.moltenNickelFluid.setBlockID(TRepo.moltenNickel).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenNickelFluid, 1000), new ItemStack(TRepo.buckets, 1, 17), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenLeadFluid = new Fluid("lead.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenLeadFluid))
            TRepo.moltenLeadFluid = FluidRegistry.getFluid("lead.molten");
        TRepo.moltenLead = new TConstructFluid(PHConstruct.moltenLead, TRepo.moltenLeadFluid, Material.lava, "liquid_lead").setUnlocalizedName("metal.molten.lead");
        GameRegistry.registerBlock(TRepo.moltenLead, "metal.molten.lead");
        TRepo.fluids[18] = TRepo.moltenLeadFluid;
        TRepo.fluidBlocks[18] = TRepo.moltenLead;
        TRepo.moltenLeadFluid.setBlockID(TRepo.moltenLead).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenLeadFluid, 1000), new ItemStack(TRepo.buckets, 1, 18), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenSilverFluid = new Fluid("silver.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenSilverFluid))
            TRepo.moltenSilverFluid = FluidRegistry.getFluid("silver.molten");
        TRepo.moltenSilver = new TConstructFluid(PHConstruct.moltenSilver, TRepo.moltenSilverFluid, Material.lava, "liquid_silver").setUnlocalizedName("metal.molten.silver");
        GameRegistry.registerBlock(TRepo.moltenSilver, "metal.molten.silver");
        TRepo.fluids[19] = TRepo.moltenSilverFluid;
        TRepo.fluidBlocks[19] = TRepo.moltenSilver;
        TRepo.moltenSilverFluid.setBlockID(TRepo.moltenSilver).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenSilverFluid, 1000), new ItemStack(TRepo.buckets, 1, 19), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenShinyFluid = new Fluid("platinum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenShinyFluid))
            TRepo.moltenShinyFluid = FluidRegistry.getFluid("platinum.molten");
        TRepo.moltenShiny = new TConstructFluid(PHConstruct.moltenShiny, TRepo.moltenShinyFluid, Material.lava, "liquid_shiny").setUnlocalizedName("metal.molten.shiny");
        GameRegistry.registerBlock(TRepo.moltenShiny, "metal.molten.shiny");
        TRepo.fluids[20] = TRepo.moltenShinyFluid;
        TRepo.fluidBlocks[20] = TRepo.moltenShiny;
        TRepo.moltenShinyFluid.setBlockID(TRepo.moltenShiny).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenShinyFluid, 1000), new ItemStack(TRepo.buckets, 1, 20), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenInvarFluid = new Fluid("invar.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenInvarFluid))
            TRepo.moltenInvarFluid = FluidRegistry.getFluid("invar.molten");
        TRepo.moltenInvar = new TConstructFluid(PHConstruct.moltenInvar, TRepo.moltenInvarFluid, Material.lava, "liquid_invar").setUnlocalizedName("metal.molten.invar");
        GameRegistry.registerBlock(TRepo.moltenInvar, "metal.molten.invar");
        TRepo.fluids[21] = TRepo.moltenInvarFluid;
        TRepo.fluidBlocks[21] = TRepo.moltenInvar;
        TRepo.moltenInvarFluid.setBlockID(TRepo.moltenInvar).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.moltenInvarFluid, 1000), new ItemStack(TRepo.buckets, 1, 21), new ItemStack(Item.bucketEmpty)));

        TRepo.moltenElectrumFluid = new Fluid("electrum.molten");
        if (!FluidRegistry.registerFluid(TRepo.moltenElectrumFluid))
            TRepo.moltenElectrumFluid = FluidRegistry.getFluid("electrum.molten");
        TRepo.moltenElectrum = new TConstructFluid(PHConstruct.moltenElectrum, TRepo.moltenElectrumFluid, Material.lava, "liquid_electrum").setUnlocalizedName("metal.molten.electrum");
        GameRegistry.registerBlock(TRepo.moltenElectrum, "metal.molten.electrum");
        TRepo.fluids[22] = TRepo.moltenElectrumFluid;
        TRepo.fluidBlocks[22] = TRepo.moltenElectrum;
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
        TRepo.fluids[23] = TRepo.moltenEnderFluid;
        TRepo.fluidBlocks[23] = TRepo.moltenEnder;

        //Slime
        TRepo.slimeStep = new StepSoundSlime("mob.slime", 1.0f, 1.0f);

        TRepo.blueSlimeFluid = new Fluid("slime.blue");
        if (!FluidRegistry.registerFluid(TRepo.blueSlimeFluid))
            TRepo.blueSlimeFluid = FluidRegistry.getFluid("slime.blue");
        TRepo.slimePool = new SlimeFluid(PHConstruct.slimePoolBlue, TRepo.blueSlimeFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TRepo.slimeStep)
                .setUnlocalizedName("liquid.slime");
        GameRegistry.registerBlock(TRepo.slimePool, "liquid.slime");
        TRepo.fluids[24] = TRepo.blueSlimeFluid;
        TRepo.fluidBlocks[24] = TRepo.slimePool;
        TRepo.blueSlimeFluid.setBlockID(TRepo.slimePool);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.blueSlimeFluid, 1000), new ItemStack(TRepo.buckets, 1, 24), new ItemStack(Item.bucketEmpty)));

        //Glue
        TRepo.glueFluid = new Fluid("glue").setDensity(6000).setViscosity(6000).setTemperature(200);
        if (!FluidRegistry.registerFluid(TRepo.glueFluid))
            TRepo.glueFluid = FluidRegistry.getFluid("glue");
        TRepo.glueFluidBlock = new GlueFluid(PHConstruct.glueFluidBlock, TRepo.glueFluid, Material.water).setCreativeTab(TConstructRegistry.blockTab).setStepSound(TRepo.slimeStep)
                .setUnlocalizedName("liquid.glue");
        GameRegistry.registerBlock(TRepo.glueFluidBlock, "liquid.glue");
        TRepo.fluids[25] = TRepo.glueFluid;
        TRepo.fluidBlocks[25] = TRepo.glueFluidBlock;
        TRepo.glueFluid.setBlockID(TRepo.glueFluidBlock);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(TRepo.glueFluid, 1000), new ItemStack(TRepo.buckets, 1, 26), new ItemStack(Item.bucketEmpty)));

        TRepo.pigIronFluid = new Fluid("pigiron.molten");
        if (!FluidRegistry.registerFluid(TRepo.pigIronFluid))
            TRepo.pigIronFluid = FluidRegistry.getFluid("pigiron.molten");
        else
            TRepo.pigIronFluid.setDensity(3000).setViscosity(6000).setTemperature(1300);
        TRepo.fluids[26] = TRepo.pigIronFluid;
        //Slime Islands
        TRepo.slimeGel = new SlimeGel(PHConstruct.slimeGel).setStepSound(TRepo.slimeStep).setLightOpacity(0).setUnlocalizedName("slime.gel");
        TRepo.slimeGrass = new SlimeGrass(PHConstruct.slimeGrass).setStepSound(Block.soundGrassFootstep).setLightOpacity(0).setUnlocalizedName("slime.grass");
        TRepo.slimeTallGrass = new SlimeTallGrass(PHConstruct.slimeTallGrass).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("slime.grass.tall");
        TRepo.slimeLeaves = (SlimeLeaves) new SlimeLeaves(PHConstruct.slimeLeaves).setStepSound(TRepo.slimeStep).setLightOpacity(0).setUnlocalizedName("slime.leaves");
        TRepo.slimeSapling = (SlimeSapling) new SlimeSapling(PHConstruct.slimeSapling).setStepSound(TRepo.slimeStep).setUnlocalizedName("slime.sapling");
        TRepo.slimeChannel = new ConveyorBase(PHConstruct.slimeChannel, Material.water).setStepSound(TRepo.slimeStep).setUnlocalizedName("slime.channel");
        TRepo.slimePad = new SlimePad(PHConstruct.slimePad, Material.cloth).setStepSound(TRepo.slimeStep).setUnlocalizedName("slime.pad");

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

        //        essenceCrystal = new EssenceCrystal(PHConstruct.essenceCrystal).setUnlocalizedName("tconstruct.crystal.essence");

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
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolForge, 1, sc), "bbb", "msm", "m m", 'b', new ItemStack(TRepo.smeltery, 1, 2), 's', new ItemStack(TRepo.toolStationWood,
                    1, 0), 'm', toolForgeBlocks[sc]));
            // adding slab version recipe
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 5), "bbb", "msm", "m m", 'b', new ItemStack(TRepo.smeltery, 1, 2), 's', new ItemStack(
                    TRepo.craftingSlabWood, 1, 1), 'm', toolForgeBlocks[sc]));
        }

        // ToolStation Recipes (Wooden Version)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(TRepo.craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(TRepo.craftingSlabWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 2), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 3), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 4), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 5), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', Block.chest);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "logWood"));
        if (PHConstruct.stencilTableCrafting)
        {
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 0));
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 11), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 1));
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 12), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 2));
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 13), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 3));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "plankWood"));
        }
        GameRegistry.addRecipe(new ItemStack(TRepo.furnaceSlab, 1, 0), "###", "# #", "###", '#', new ItemStack(Block.stoneSingleSlab, 1, 3));

        // Blank Pattern Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.blankPattern, 1, 0), "ps", "sp", 'p', "plankWood", 's', "stickWood"));
        // Manual Book Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.manualBook), "wp", 'w', new ItemStack(TRepo.blankPattern, 1, 0), 'p', Item.paper);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 2, 0), new ItemStack(TRepo.manualBook, 1, 0), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 1, 1), new ItemStack(TRepo.manualBook, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 2, 1), new ItemStack(TRepo.manualBook, 1, 1), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 1, 2), new ItemStack(TRepo.manualBook, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 2, 2), new ItemStack(TRepo.manualBook, 1, 2), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 1, 3), new ItemStack(TRepo.manualBook, 1, 2));
        // alternative Vanilla Book Recipe
        GameRegistry.addShapelessRecipe(new ItemStack(Item.book), Item.paper, Item.paper, Item.paper, Item.silk, TRepo.blankPattern, TRepo.blankPattern);
        // Paperstack Recipe
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 0), "pp", "pp", 'p', Item.paper);
        // Mossball Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.materials, 1, 6), patBlock, '#', "stoneMossy"));
        // LavaCrystal Recipes -Auto-smelt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'c', Item.fireballCharge, 'x', Item.blazeRod);
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'x', Item.fireballCharge, 'c', Item.blazeRod);
        // Slimy sand Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 1, 0), Item.slimeBall, Item.slimeBall, Item.slimeBall, Item.slimeBall, Block.sand, Block.dirt);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 1, 2), TRepo.strangeFood, TRepo.strangeFood, TRepo.strangeFood, TRepo.strangeFood, Block.sand, Block.dirt);
        // Grout Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 2, 1), Item.clay, Block.sand, Block.gravel);
        GameRegistry.addRecipe(new ItemStack(TRepo.craftedSoil, 8, 1), "sgs", "gcg", "sgs", 'c', new ItemStack(Block.stainedClay, 1, Short.MAX_VALUE), 's', Block.sand, 'g', Block.gravel);
        GameRegistry.addRecipe(new ItemStack(TRepo.craftedSoil, 8, 1), "sgs", "gcg", "sgs", 'c', new ItemStack(Block.blockClay, 1, Short.MAX_VALUE), 's', Block.sand, 'g', Block.gravel);
        // Graveyard Soil Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 1, 3), Block.dirt, Item.rottenFlesh, new ItemStack(Item.dyePowder, 1, 15));
        // Silky Cloth Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 25), patSurround, 'm', new ItemStack(TRepo.materials, 1, 24), '#', new ItemStack(Item.silk));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.materials, 1, 25), patSurround, 'm', "nuggetGold", '#', new ItemStack(Item.silk)));
        // Silky Jewel Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 26), " c ", "cec", " c ", 'c', new ItemStack(TRepo.materials, 1, 25), 'e', new ItemStack(Item.emerald));
        // Wooden Armor Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.helmetWood, new Object[] { "www", "w w", 'w', "logWood" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.chestplateWood, new Object[] { "w w", "www", "www", 'w', "logWood" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.leggingsWood, new Object[] { "www", "w w", "w w", 'w', "logWood" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.bootsWood, new Object[] { "w w", "w w", 'w', "logWood" }));
        // Metal conversion Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 3), patBlock, '#', new ItemStack(TRepo.materials, 1, 9)); // Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 5), patBlock, '#', new ItemStack(TRepo.materials, 1, 10)); // Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 6), patBlock, '#', new ItemStack(TRepo.materials, 1, 11)); // Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 6), patBlock, '#', new ItemStack(TRepo.materials, 1, 12)); // Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 4), patBlock, '#', new ItemStack(TRepo.materials, 1, 13)); // Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 7), patBlock, '#', new ItemStack(TRepo.materials, 1, 14)); // AluBrass
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 0), patBlock, '#', new ItemStack(TRepo.materials, 1, 3)); // Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 1), patBlock, '#', new ItemStack(TRepo.materials, 1, 4)); // Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 2), patBlock, '#', new ItemStack(TRepo.materials, 1, 5)); // Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 8), patBlock, '#', new ItemStack(TRepo.materials, 1, 15)); // Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 9), patBlock, '#', new ItemStack(TRepo.materials, 1, 16)); // Steel
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 11), patBlock, '#', new ItemStack(TRepo.materials, 1, 12)); //Aluminum raw -> ingot

        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 9), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 3)); //Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 10), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 5)); //Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 12), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 6)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 13), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 4)); //Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 14), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 7)); //AluBrass
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 3), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 0)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 4), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 1)); //Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 5), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 2)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 15), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 8)); //Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 16), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 9)); //Steel

        GameRegistry.addRecipe(new ItemStack(Item.ingotIron), patBlock, '#', new ItemStack(TRepo.materials, 1, 19)); //Iron
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 9), patBlock, '#', new ItemStack(TRepo.materials, 1, 20)); //Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 10), patBlock, '#', new ItemStack(TRepo.materials, 1, 21)); //Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 11), patBlock, '#', new ItemStack(TRepo.materials, 1, 22)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 14), patBlock, '#', new ItemStack(TRepo.materials, 1, 24)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 18), patBlock, '#', new ItemStack(TRepo.materials, 1, 27)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 3), patBlock, '#', new ItemStack(TRepo.materials, 1, 28)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 4), patBlock, '#', new ItemStack(TRepo.materials, 1, 29)); //Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 5), patBlock, '#', new ItemStack(TRepo.materials, 1, 30)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 13), patBlock, '#', new ItemStack(TRepo.materials, 1, 31)); //Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 15), patBlock, '#', new ItemStack(TRepo.materials, 1, 32)); //Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 16), patBlock, '#', new ItemStack(TRepo.materials, 1, 33)); //Steel    

        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 19), "m", 'm', new ItemStack(Item.ingotIron)); //Iron
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 20), "m", 'm', new ItemStack(TRepo.materials, 1, 9)); //Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 21), "m", 'm', new ItemStack(TRepo.materials, 1, 10)); //Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 22), "m", 'm', new ItemStack(TRepo.materials, 1, 11)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 22), "m", 'm', new ItemStack(TRepo.materials, 1, 12)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 24), "m", 'm', new ItemStack(TRepo.materials, 1, 14)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 27), "m", 'm', new ItemStack(TRepo.materials, 1, 18)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 28), "m", 'm', new ItemStack(TRepo.materials, 1, 3)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 29), "m", 'm', new ItemStack(TRepo.materials, 1, 4)); //Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 30), "m", 'm', new ItemStack(TRepo.materials, 1, 5)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 31), "m", 'm', new ItemStack(TRepo.materials, 1, 13)); //Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 32), "m", 'm', new ItemStack(TRepo.materials, 1, 15)); //Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 33), "m", 'm', new ItemStack(TRepo.materials, 1, 16)); //Steel 

        // stained Glass Recipes
        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue",
                "dyeMagenta", "dyeOrange", "dyeWhite" };
        String color = "";
        for (int i = 0; i < 16; i++)
        {
            color = dyeTypes[15 - i];
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.cloth, 8, i), patSurround, 'm', color, '#', new ItemStack(Block.cloth, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClear, 8, i), patSurround, 'm', color, '#', TRepo.clearGlass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClear, 1, i), color, TRepo.clearGlass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClear, 8, i), patSurround, 'm', color, '#', new ItemStack(TRepo.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClear, 1, i), color, new ItemStack(TRepo.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', TRepo.glassPane));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 1, i), color, TRepo.glassPane));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', new ItemStack(TRepo.stainedGlassClearPane, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 1, i), color, new ItemStack(TRepo.stainedGlassClearPane, 1, Short.MAX_VALUE)));
        }

        // Glass Recipes
        GameRegistry.addRecipe(new ItemStack(Item.glassBottle, 3), new Object[] { "# #", " # ", '#', TRepo.clearGlass });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.daylightSensor), new Object[] { "GGG", "QQQ", "WWW", 'G', "glass", 'Q', Item.netherQuartz, 'W', "slabWood" }));
        GameRegistry.addRecipe(new ItemStack(Block.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', TRepo.clearGlass, 'S', Item.netherStar, 'O', Block.obsidian });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.glassPane, 16, 0), "GGG", "GGG", 'G', TRepo.clearGlass));

        // Smeltery Components Recipes
        ItemStack searedBrick = new ItemStack(TRepo.materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(TRepo.smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(TRepo.smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(TRepo.smeltery, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTank, 1, 0), patSurround, '#', searedBrick, 'm', "glass")); //Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "glass")); //Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "glass")); //Window
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin
        GameRegistry.addRecipe(new ItemStack(TRepo.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); //Channel

        // Jack o'Latern Recipe - Stone Torch
        GameRegistry.addRecipe(new ItemStack(Block.pumpkinLantern, 1, 0), "p", "s", 'p', new ItemStack(Block.pumpkin), 's', new ItemStack(TRepo.stoneTorch));
        // Stone Torch Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stoneTorch, 4), "p", "w", 'p', new ItemStack(Item.coal, 1, Short.MAX_VALUE), 'w', "stoneRod"));
        // Stone Ladder Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stoneLadder, 3), "w w", "www", "w w", 'w', "stoneRod"));
        // Wooden Rail Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.woodenRail, 4, 0), "b b", "bxb", "b b", 'b', "plankWood", 'x', "stickWood"));
        // Stonesticks Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.toolRod, 4, 1), "c", "c", 'c', new ItemStack(Block.stone));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolRod, 2, 1), "c", "c", 'c', new ItemStack(Block.cobblestone));
        // 
        ItemStack aluBrass = new ItemStack(TRepo.materials, 1, 14);
        // Clock Recipe - Vanilla alternativ
        GameRegistry.addRecipe(new ItemStack(Item.pocketSundial), " i ", "iri", " i ", 'i', aluBrass, 'r', new ItemStack(Item.redstone));
        // Gold Pressure Plate -  Vanilla alternativ
        GameRegistry.addRecipe(new ItemStack(Block.pressurePlateGold), "ii", 'i', aluBrass);
        //Accessories
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#', "ingotAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#', "ingotAluminium"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#', "ingotNaturalAluminum"));
        GameRegistry.addRecipe(new ItemStack(TRepo.diamondApple), " d ", "d#d", " d ", 'd', new ItemStack(Item.diamond), '#', new ItemStack(Item.appleRed));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.heartCanister, 1, 2), new ItemStack(TRepo.diamondApple), new ItemStack(TRepo.materials, 1, 8), new ItemStack(TRepo.heartCanister, 1, 0),
                new ItemStack(TRepo.heartCanister, 1, 1));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(TRepo.toughRod, 1, 2), 'm',
                "ingotGold"));
        GameRegistry.addRecipe(new ItemStack(TRepo.knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(TRepo.toughRod, 1, 2), 'm', aluBrass);
        // Drying Rack Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.dryingRack, 1, 0), "bbb", 'b', "slabWood"));
        //Landmine Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 0), "mcm", "rpr", 'm', "plankWood", 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 1), "mcm", "rpr", 'm', Block.stone, 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 2), "mcm", "rpr", 'm', Block.obsidian, 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 3), "mcm", "rpr", 'm', Item.redstoneRepeater, 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone,
                'p', Block.pressurePlateStone));

        //Ultra hardcore recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.goldHead), patSurround, '#', new ItemStack(Item.ingotGold), 'm', new ItemStack(Item.skull, 1, 3));

        // Slab Smeltery Components Recipes
        for (int i = 0; i < 7; i++)
        {
            GameRegistry.addRecipe(new ItemStack(TRepo.speedSlab, 6, i), "bbb", 'b', new ItemStack(TRepo.speedBlock, 1, i));
        }
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 0), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 1), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 2), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 3), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 6));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 4), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 8));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 5), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 9));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 6), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 7), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 11));

        // Wool Slab Recipes
        for (int sc = 0; sc <= 7; sc++)
        {
            GameRegistry.addRecipe(new ItemStack(TRepo.woolSlab1, 6, sc), "www", 'w', new ItemStack(Block.cloth, 1, sc));
            GameRegistry.addRecipe(new ItemStack(TRepo.woolSlab2, 6, sc), "www", 'w', new ItemStack(Block.cloth, 1, sc + 8));

            GameRegistry.addShapelessRecipe(new ItemStack(Block.cloth, 1, sc), new ItemStack(TRepo.woolSlab1, 1, sc), new ItemStack(TRepo.woolSlab1, 1, sc));
            GameRegistry.addShapelessRecipe(new ItemStack(Block.cloth, 1, sc + 8), new ItemStack(TRepo.woolSlab2, 1, sc), new ItemStack(TRepo.woolSlab2, 1, sc));
        }
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.cloth, 1, 0), "slabCloth", "slabCloth"));
        //Trap Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.punji, 5, 0), "b b", " b ", "b b", 'b', new ItemStack(Item.reed));
        GameRegistry.addRecipe(new ItemStack(TRepo.barricadeSpruce, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TRepo.barricadeBirch, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.barricadeJungle, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.barricadeOak, 1, 0), "b", "b", 'b', "logWood"));
        // Advanced WorkBench Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingStationWood, 1, 0), "b", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingStationWood, 1, 0), "b", 'b', "craftingTableWood"));
        //Slab crafters
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingSlabWood, 6, 0), "bbb", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingSlabWood, 6, 0), "bbb", 'b', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 0), "b", 'b', new ItemStack(TRepo.craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 1), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 4), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 11));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 12));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 13));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 5), "b", 'b', new ItemStack(TRepo.toolForge, 1, Short.MAX_VALUE));
        // EssenceExtractor Recipe
        //Slime Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.slimeGel, 1, 0), "##", "##", '#', TRepo.strangeFood);
        GameRegistry.addRecipe(new ItemStack(TRepo.strangeFood, 4, 0), "#", '#', new ItemStack(TRepo.slimeGel, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.slimeGel, 1, 1), "##", "##", '#', Item.slimeBall);
        GameRegistry.addRecipe(new ItemStack(Item.slimeBall, 4, 0), "#", '#', new ItemStack(TRepo.slimeGel, 1, 1));
        //slimeExplosive
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.slimeExplosive, 1, 0), Item.slimeBall, Block.tnt);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.slimeExplosive, 1, 2), TRepo.strangeFood, Block.tnt);
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.slimeExplosive, 1, 0), "slimeball", Block.tnt));

        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.slimeChannel, 1, 0), new ItemStack(TRepo.slimeGel, 1, Short.MAX_VALUE), new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.slimePad, 1, 0), TRepo.slimeChannel, "slimeBall"));
    }

    private void addRecipesForFurnace ()
    {
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 3, new ItemStack(TRepo.craftedSoil, 1, 4), 0.2f); //Concecrated Soil

        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 0, new ItemStack(TRepo.materials, 1, 1), 2f); //Slime
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 1, new ItemStack(TRepo.materials, 1, 2), 2f); //Seared brick item
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 2, new ItemStack(TRepo.materials, 1, 17), 2f); //Blue Slime
        //FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 1, new ItemStack(TRepo.materials, 1, 3), 3f);
        //FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 2, new ItemStack(TRepo.materials, 1, 4), 3f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 3, new ItemStack(TRepo.materials, 1, 9), 0.5f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 4, new ItemStack(TRepo.materials, 1, 10), 0.5f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 5, new ItemStack(TRepo.materials, 1, 11), 0.5f);

        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 0, new ItemStack(TRepo.materials, 1, 19), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 1, new ItemStack(Item.goldNugget), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 2, new ItemStack(TRepo.materials, 1, 20), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 3, new ItemStack(TRepo.materials, 1, 21), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 4, new ItemStack(TRepo.materials, 1, 22), 0.2f);
        //FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 5, new ItemStack(TRepo.materials, 1, 23), 0.2f);

        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 0, new ItemStack(Item.ingotIron), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 1, new ItemStack(Item.ingotGold), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 2, new ItemStack(TRepo.materials, 1, 9), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 3, new ItemStack(TRepo.materials, 1, 10), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 4, new ItemStack(TRepo.materials, 1, 12), 0.2f);

        FurnaceRecipes.smelting().addSmelting(TRepo.speedBlock.blockID, 0, new ItemStack(TRepo.speedBlock, 1, 2), 0.2f);
    }

    private void addPartMapping ()
    {
        /* Tools */
        patternOutputs = new Item[] { TRepo.toolRod, TRepo.pickaxeHead, TRepo.shovelHead, TRepo.hatchetHead, TRepo.swordBlade, TRepo.wideGuard, TRepo.handGuard, TRepo.crossbar, TRepo.binding,
                TRepo.frypanHead, TRepo.signHead, TRepo.knifeBlade, TRepo.chiselHead, TRepo.toughRod, TRepo.toughBinding, TRepo.largePlate, TRepo.broadAxeHead, TRepo.scytheBlade, TRepo.excavatorHead,
                TRepo.largeSwordBlade, TRepo.hammerHead, TRepo.fullGuard, null, null, TRepo.arrowhead, null };

        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };

        if (PHConstruct.craftMetalTools)
        {
            for (int mat = 0; mat < 18; mat++)
            {
                for (int meta = 0; meta < patternOutputs.length; meta++)
                {
                    if (patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(TRepo.woodPattern.itemID, meta + 1, mat, new ItemStack(patternOutputs[meta], 1, mat));
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
                        TConstructRegistry.addPartMapping(TRepo.woodPattern.itemID, meta + 1, nonMetals[mat], new ItemStack(patternOutputs[meta], 1, nonMetals[mat]));
                }
            }
        }
    }

    private void addRecipesForToolBuilder ()
    {
        ToolBuilder tb = ToolBuilder.instance;
        tb.addNormalToolRecipe(TRepo.pickaxe, TRepo.pickaxeHead, TRepo.toolRod, TRepo.binding);
        tb.addNormalToolRecipe(TRepo.broadsword, TRepo.swordBlade, TRepo.toolRod, TRepo.wideGuard);
        tb.addNormalToolRecipe(TRepo.hatchet, TRepo.hatchetHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.shovel, TRepo.shovelHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.longsword, TRepo.swordBlade, TRepo.toolRod, TRepo.handGuard);
        tb.addNormalToolRecipe(TRepo.rapier, TRepo.swordBlade, TRepo.toolRod, TRepo.crossbar);
        tb.addNormalToolRecipe(TRepo.frypan, TRepo.frypanHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.battlesign, TRepo.signHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.mattock, TRepo.hatchetHead, TRepo.toolRod, TRepo.shovelHead);
        tb.addNormalToolRecipe(TRepo.dagger, TRepo.knifeBlade, TRepo.toolRod, TRepo.crossbar);
        tb.addNormalToolRecipe(TRepo.cutlass, TRepo.swordBlade, TRepo.toolRod, TRepo.fullGuard);
        tb.addNormalToolRecipe(TRepo.chisel, TRepo.chiselHead, TRepo.toolRod);

        tb.addNormalToolRecipe(TRepo.scythe, TRepo.scytheBlade, TRepo.toughRod, TRepo.toughBinding, TRepo.toughRod);
        tb.addNormalToolRecipe(TRepo.lumberaxe, TRepo.broadAxeHead, TRepo.toughRod, TRepo.largePlate, TRepo.toughBinding);
        tb.addNormalToolRecipe(TRepo.cleaver, TRepo.largeSwordBlade, TRepo.toughRod, TRepo.largePlate, TRepo.toughRod);
        tb.addNormalToolRecipe(TRepo.excavator, TRepo.excavatorHead, TRepo.toughRod, TRepo.largePlate, TRepo.toughBinding);
        tb.addNormalToolRecipe(TRepo.hammer, TRepo.hammerHead, TRepo.toughRod, TRepo.largePlate, TRepo.largePlate);
        tb.addNormalToolRecipe(TRepo.battleaxe, TRepo.broadAxeHead, TRepo.toughRod, TRepo.broadAxeHead, TRepo.toughBinding);

        //tb.addNormalToolRecipe(TRepo.shortbow, TRepo.toolRod, TRepo.bowstring, TRepo.toolRod);
        BowRecipe recipe = new BowRecipe(TRepo.toolRod, TRepo.bowstring, TRepo.toolRod, TRepo.shortbow);
        tb.addCustomToolRecipe(recipe);
        tb.addNormalToolRecipe(TRepo.arrow, TRepo.arrowhead, TRepo.toolRod, TRepo.fletching);

        ItemStack diamond = new ItemStack(Item.diamond);
        tb.registerToolMod(new ModRepair());
        tb.registerToolMod(new ModDurability(new ItemStack[] { diamond }, 0, 500, 0f, 3, "Diamond", "\u00a7bDurability +500", "\u00a7b"));
        tb.registerToolMod(new ModDurability(new ItemStack[] { new ItemStack(Item.emerald) }, 1, 0, 0.5f, 2, "Emerald", "\u00a72Durability +50%", "\u00a72"));

        TRepo.modF = new ModFlux();
        tb.registerToolMod(TRepo.modF);

        ItemStack redstoneItem = new ItemStack(Item.redstone);
        ItemStack redstoneBlock = new ItemStack(Block.blockRedstone);
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem }, 2, 1));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem, redstoneItem }, 2, 2));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneBlock }, 2, 9));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem, redstoneBlock }, 2, 10));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneBlock, redstoneBlock }, 2, 18));

        ItemStack lapisItem = new ItemStack(Item.dyePowder, 1, 4);
        ItemStack lapisBlock = new ItemStack(Block.blockLapis);
        TRepo.modL = new ModLapis(new ItemStack[] { lapisItem }, 10, 1);
        tb.registerToolMod(TRepo.modL);
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisItem, lapisItem }, 10, 2));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisBlock }, 10, 9));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisItem, lapisBlock }, 10, 10));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisBlock, lapisBlock }, 10, 18));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(TRepo.materials, 1, 6) }, 4, "Moss", 3, "\u00a72", "Auto-Repair"));
        ItemStack blazePowder = new ItemStack(Item.blazePowder);
        tb.registerToolMod(new ModBlaze(new ItemStack[] { blazePowder }, 7, 1));
        tb.registerToolMod(new ModBlaze(new ItemStack[] { blazePowder, blazePowder }, 7, 2));
        tb.registerToolMod(new ModAutoSmelt(new ItemStack[] { new ItemStack(TRepo.materials, 1, 7) }, 6, "Lava", "\u00a74", "Auto-Smelt"));
        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(TRepo.materials, 1, 8) }, 8, "Necrotic", 1, "\u00a78", "Life Steal"));

        ItemStack quartzItem = new ItemStack(Item.netherQuartz);
        ItemStack quartzBlock = new ItemStack(Block.blockNetherQuartz, 1, Short.MAX_VALUE);
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem }, 11, 1));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem, quartzItem }, 11, 2));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzBlock }, 11, 4));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem, quartzBlock }, 11, 5));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzBlock, quartzBlock }, 11, 8));

        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { diamond, new ItemStack(Block.blockGold) }, "Tier1Free"));
        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { new ItemStack(Item.netherStar) }, "Tier2Free"));

        ItemStack silkyJewel = new ItemStack(TRepo.materials, 1, 26);
        tb.registerToolMod(new ModButtertouch(new ItemStack[] { silkyJewel }, 12));

        ItemStack piston = new ItemStack(Block.pistonBase);
        tb.registerToolMod(new ModPiston(new ItemStack[] { piston }, 3, 1));
        tb.registerToolMod(new ModPiston(new ItemStack[] { piston, piston }, 3, 2));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl) }, 13, "Beheading", 1, "\u00a7d", "Beheading"));

        ItemStack holySoil = new ItemStack(TRepo.craftedSoil, 1, 4);
        tb.registerToolMod(new ModSmite("Smite", new ItemStack[] { holySoil }, 14, 1));
        tb.registerToolMod(new ModSmite("Smite", new ItemStack[] { holySoil, holySoil }, 14, 2));

        ItemStack spidereyeball = new ItemStack(Item.fermentedSpiderEye);
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", new ItemStack[] { spidereyeball }, 15, 1));
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", new ItemStack[] { spidereyeball, spidereyeball }, 15, 2));

        ItemStack obsidianPlate = new ItemStack(TRepo.largePlate, 1, 6);
        tb.registerToolMod(new ModReinforced(new ItemStack[] { obsidianPlate }, 16, 1));

        TConstructRegistry.registerActiveToolMod(new TActiveOmniMod());
    }

    private void addRecipesForTableCasting ()
    {
        /* Smeltery */
        ItemStack ingotcast = new ItemStack(TRepo.metalPattern, 1, 0);
        ItemStack gemcast = new ItemStack(TRepo.metalPattern, 1, 26);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        //Blank
        tableCasting.addCastingRecipe(new ItemStack(TRepo.blankPattern, 1, 1), new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), 80);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.blankPattern, 1, 2), new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(Item.emerald), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(Item.emerald), 80);

        //Ingots
        tableCasting.addCastingRecipe(new ItemStack(TRepo.materials, 1, 2), new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 4), ingotcast, 80); //stone

        //Misc
        tableCasting.addCastingRecipe(new ItemStack(Item.emerald), new FluidStack(TRepo.moltenEmeraldFluid, 640), gemcast, 80);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.materials, 1, 36), new FluidStack(TRepo.glueFluid, TConstruct.ingotLiquidValue), null, 50);

        //Buckets
        ItemStack bucket = new ItemStack(Item.bucketEmpty);

        for (int sc = 0; sc < 24; sc++)
        {
            tableCasting.addCastingRecipe(new ItemStack(TRepo.buckets, 1, sc), new FluidStack(TRepo.fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
        }
        tableCasting.addCastingRecipe(new ItemStack(TRepo.buckets, 1, 26), new FluidStack(TRepo.fluids[26], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

        // Clear glass pane casting
        tableCasting.addCastingRecipe(new ItemStack(TRepo.glassPane), new FluidStack(TRepo.moltenGlassFluid, 250), null, 80);

        // Metal toolpart casting
        liquids = new FluidStack[] { new FluidStack(TRepo.moltenIronFluid, 1), new FluidStack(TRepo.moltenCopperFluid, 1), new FluidStack(TRepo.moltenCobaltFluid, 1),
                new FluidStack(TRepo.moltenArditeFluid, 1), new FluidStack(TRepo.moltenManyullynFluid, 1), new FluidStack(TRepo.moltenBronzeFluid, 1), new FluidStack(TRepo.moltenAlumiteFluid, 1),
                new FluidStack(TRepo.moltenObsidianFluid, 1), new FluidStack(TRepo.moltenSteelFluid, 1), new FluidStack(TRepo.pigIronFluid, 1) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16, 18 }; //ItemStack damage value
        int fluidAmount = 0;
        Fluid fs = null;

        for (int iter = 0; iter < patternOutputs.length; iter++)
        {
            if (patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(TRepo.metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
                {
                    fs = liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) TRepo.metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    ItemStack metalCast = new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }
            }
        }

        ItemStack[] ingotShapes = { new ItemStack(Item.brick), new ItemStack(Item.netherrackBrick), new ItemStack(TRepo.materials, 1, 2) };
        for (int i = 0; i < ingotShapes.length; i++)
        {
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotShapes[i], false, 50);
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), ingotShapes[i], false, 50);
        }

        ItemStack fullguardCast = new ItemStack(TRepo.metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(TRepo.fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(TRepo.fullGuard, 1, Short.MAX_VALUE), false, 50);

        // Golden Food Stuff
        FluidStack goldAmount = null;
        if (PHConstruct.goldAppleRecipe)
        {
            goldAmount = new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 8);
        }
        else
        {
            goldAmount = new FluidStack(TRepo.moltenGoldFluid, TConstruct.nuggetLiquidValue * 8);
        }
        tableCasting.addCastingRecipe(new ItemStack(Item.appleGold, 1), goldAmount, new ItemStack(Item.appleRed), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Item.goldenCarrot, 1), goldAmount, new ItemStack(Item.carrot), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Item.speckledMelon, 1), goldAmount, new ItemStack(Item.melon), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.goldHead), goldAmount, new ItemStack(Item.skull, 1, 3), true, 50);
    }

    private void addRecipesForBasinCasting ()
    {
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        // Block Casting
        basinCasting.addCastingRecipe(new ItemStack(Block.blockIron), new FluidStack(TRepo.moltenIronFluid, TConstruct.blockLiquidValue), null, true, 100); //Iron
        basinCasting.addCastingRecipe(new ItemStack(Block.blockGold), new FluidStack(TRepo.moltenGoldFluid, TConstruct.blockLiquidValue), null, true, 100); //gold
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 3), new FluidStack(TRepo.moltenCopperFluid, TConstruct.blockLiquidValue), null, true, 100); //copper
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 5), new FluidStack(TRepo.moltenTinFluid, TConstruct.blockLiquidValue), null, true, 100); //tin
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 6), new FluidStack(TRepo.moltenAluminumFluid, TConstruct.blockLiquidValue), null, true, 100); //aluminum
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 0), new FluidStack(TRepo.moltenCobaltFluid, TConstruct.blockLiquidValue), null, true, 100); //cobalt
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 1), new FluidStack(TRepo.moltenArditeFluid, TConstruct.blockLiquidValue), null, true, 100); //ardite
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 4), new FluidStack(TRepo.moltenBronzeFluid, TConstruct.blockLiquidValue), null, true, 100); //bronze
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 7), new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.blockLiquidValue), null, true, 100); //albrass
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 2), new FluidStack(TRepo.moltenManyullynFluid, TConstruct.blockLiquidValue), null, true, 100); //manyullyn
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 8), new FluidStack(TRepo.moltenAlumiteFluid, TConstruct.blockLiquidValue), null, true, 100); //alumite
        basinCasting.addCastingRecipe(new ItemStack(Block.obsidian), new FluidStack(TRepo.moltenObsidianFluid, TConstruct.oreLiquidValue), null, true, 100);// obsidian
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 9), new FluidStack(TRepo.moltenSteelFluid, TConstruct.blockLiquidValue), null, true, 100); //steel
        basinCasting.addCastingRecipe(new ItemStack(TRepo.clearGlass, 1, 0), new FluidStack(TRepo.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100); //glass
        basinCasting.addCastingRecipe(new ItemStack(TRepo.smeltery, 1, 4), new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue), null, true, 100); //seared stone
        basinCasting.addCastingRecipe(new ItemStack(TRepo.smeltery, 1, 5), new FluidStack(TRepo.moltenStoneFluid, TConstruct.chunkLiquidValue), new ItemStack(Block.cobblestone), true, 100);
        basinCasting.addCastingRecipe(new ItemStack(Block.blockEmerald), new FluidStack(TRepo.moltenEmeraldFluid, 640 * 9), null, true, 100); //emerald
        basinCasting.addCastingRecipe(new ItemStack(TRepo.speedBlock, 1, 0), new FluidStack(TRepo.moltenTinFluid, TConstruct.nuggetLiquidValue), new ItemStack(Block.gravel), true, 100); //brownstone
        basinCasting.addCastingRecipe(new ItemStack(Block.whiteStone), new FluidStack(TRepo.moltenEnderFluid, TConstruct.chunkLiquidValue), new ItemStack(Block.obsidian), true, 100); //endstone
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock.blockID, 1, 10), new FluidStack(TRepo.moltenEnderFluid, 1000), null, true, 100); //ender
        basinCasting.addCastingRecipe(new ItemStack(TRepo.glueBlock), new FluidStack(TRepo.glueFluid, TConstruct.blockLiquidValue), null, true, 100); //glue

        // basinCasting.addCastingRecipe(new ItemStack(slimeGel, 1, 0), new FluidStack(blueSlimeFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100);

        //Armor casts
        /*FluidRenderProperties frp = new FluidRenderProperties(Applications.BASIN.minHeight, 0.65F, Applications.BASIN);
        FluidStack aluFlu = new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue * 10);
        FluidStack gloFlu = new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 10);
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
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenBronzeFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsBronzeAlloy), new FluidStack(TRepo.moltenCopperFluid,
                TConstruct.ingotLiquidValue * 3), new FluidStack(TRepo.moltenTinFluid, TConstruct.ingotLiquidValue)); //Bronze			
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsAluminumBrassAlloy), new FluidStack(TRepo.moltenAluminumFluid,
                TConstruct.ingotLiquidValue * 3), new FluidStack(TRepo.moltenCopperFluid, TConstruct.ingotLiquidValue * 1)); //Aluminum Brass
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenAlumiteFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsAlumiteAlloy), new FluidStack(TRepo.moltenAluminumFluid,
                TConstruct.ingotLiquidValue * 5), new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 2), new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2)); //Alumite
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenManyullynFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsManyullynAlloy), new FluidStack(TRepo.moltenCobaltFluid,
                TConstruct.ingotLiquidValue), new FluidStack(TRepo.moltenArditeFluid, TConstruct.ingotLiquidValue)); //Manyullyn
        Smeltery.addAlloyMixing(new FluidStack(TRepo.pigIronFluid, TConstruct.ingotLiquidValue * PHConstruct.ingotsPigironAlloy), new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue),
                new FluidStack(TRepo.moltenEmeraldFluid, 640), new FluidStack(TRepo.bloodFluid, 80)); //Pigiron 

        // Stone parts
        for (int sc = 0; sc < patternOutputs.length; sc++)
        {
            if (patternOutputs[sc] != null)
            {
                Smeltery.addMelting(FluidType.Stone, new ItemStack(patternOutputs[sc], 1, 1), 1, (8 * ((IPattern) TRepo.woodPattern).getPatternCost(new ItemStack(TRepo.woodPattern, 1, sc + 1))) / 2);
            }
        }

        // Chunks
        Smeltery.addMelting(FluidType.Stone, new ItemStack(TRepo.toolShard, 1, 1), 0, 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(TRepo.toolShard, 1, 2), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Obsidian, new ItemStack(TRepo.toolShard, 1, 6), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Cobalt, new ItemStack(TRepo.toolShard, 1, 10), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Ardite, new ItemStack(TRepo.toolShard, 1, 11), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Manyullyn, new ItemStack(TRepo.toolShard, 1, 12), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Copper, new ItemStack(TRepo.toolShard, 1, 13), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Bronze, new ItemStack(TRepo.toolShard, 1, 14), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Alumite, new ItemStack(TRepo.toolShard, 1, 15), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(TRepo.toolShard, 1, 16), 0, TConstruct.chunkLiquidValue);

        // Items

        Smeltery.addMelting(FluidType.AluminumBrass, new ItemStack(TRepo.blankPattern, 4, 1), -50, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(TRepo.blankPattern, 4, 2), -50, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.Glue, new ItemStack(TRepo.materials, 1, 36), 0, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.Ender, new ItemStack(Item.enderPearl, 4), 0, 250);
        Smeltery.addMelting(TRepo.metalBlock, 10, 50, new FluidStack(TRepo.moltenEnderFluid, 1000));
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
        Smeltery.addMelting(Block.oreIron, 0, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.oreGold, 0, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TRepo.oreGravel, 0, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TRepo.oreGravel, 1, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));

        //Blocks
        Smeltery.addMelting(Block.blockIron, 0, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.blockGold, 0, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.obsidian, 0, 800, new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.ice, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 1000));
        Smeltery.addMelting(Block.blockSnow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 500));
        Smeltery.addMelting(Block.snow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 250));
        Smeltery.addMelting(Block.sand, 0, 625, new FluidStack(TRepo.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.glass, 0, 625, new FluidStack(TRepo.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.thinGlass, 0, 625, new FluidStack(TRepo.moltenGlassFluid, 250));
        Smeltery.addMelting(Block.stone, 0, 800, new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.cobblestone, 0, 800, new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.blockEmerald, 0, 800, new FluidStack(TRepo.moltenEmeraldFluid, 640 * 9));
        Smeltery.addMelting(TRepo.glueBlock, 0, 250, new FluidStack(TRepo.glueFluid, TConstruct.blockLiquidValue));
        Smeltery.addMelting(TRepo.craftedSoil, 1, 600, new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 4));

        Smeltery.addMelting(TRepo.clearGlass, 0, 500, new FluidStack(TRepo.moltenGlassFluid, 1000));
        Smeltery.addMelting(TRepo.glassPane, 0, 350, new FluidStack(TRepo.moltenGlassFluid, 250));

        for (int i = 0; i < 16; i++)
        {
            Smeltery.addMelting(TRepo.stainedGlassClear, i, 500, new FluidStack(TRepo.moltenGlassFluid, 1000));
            Smeltery.addMelting(TRepo.stainedGlassClearPane, i, 350, new FluidStack(TRepo.moltenGlassFluid, 250));
        }

        //Bricks
        Smeltery.addMelting(TRepo.multiBrick, 4, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrickFancy, 4, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrick, 5, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrickFancy, 5, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrick, 0, 800, new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TRepo.multiBrickFancy, 0, 800, new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));

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
        DryingRackRecipes.addDryingRecipe(Item.beefRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 0));
        DryingRackRecipes.addDryingRecipe(Item.chickenRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 1));
        DryingRackRecipes.addDryingRecipe(Item.porkRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 2));
        //DryingRackRecipes.addDryingRecipe(Item.muttonRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 3));
        DryingRackRecipes.addDryingRecipe(Item.fishRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 4));
        DryingRackRecipes.addDryingRecipe(Item.rottenFlesh, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 5));

        //DryingRackRecipes.addDryingRecipe(new ItemStack(TRepo.jerky, 1, 5), 20 * 60 * 10, Item.leather);
    }

    private void addRecipesForChisel ()
    {
        /* Detailing */
        Detailing chiseling = TConstructRegistry.getChiselDetailing();
        chiseling.addDetailing(Block.stone, 0, Block.stoneBrick, 0, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 0, TRepo.speedBlock, 1, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 2, TRepo.speedBlock, 3, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 3, TRepo.speedBlock, 4, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 4, TRepo.speedBlock, 5, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 5, TRepo.speedBlock, 6, TRepo.chisel);

        chiseling.addDetailing(Block.obsidian, 0, TRepo.multiBrick, 0, TRepo.chisel);
        chiseling.addDetailing(Block.sandStone, 0, Block.sandStone, 2, TRepo.chisel);
        chiseling.addDetailing(Block.sandStone, 2, Block.sandStone, 1, TRepo.chisel);
        chiseling.addDetailing(Block.sandStone, 1, TRepo.multiBrick, 1, TRepo.chisel);
        //chiseling.addDetailing(Block.netherrack, 0, TRepo.multiBrick, 2, TRepo.chisel);
        //chiseling.addDetailing(Block.stone_refined, 0, TRepo.multiBrick, 3, TRepo.chisel);
        chiseling.addDetailing(Item.ingotIron, 0, TRepo.multiBrick, 4, TRepo.chisel);
        chiseling.addDetailing(Item.ingotGold, 0, TRepo.multiBrick, 5, TRepo.chisel);
        chiseling.addDetailing(Item.dyePowder, 4, TRepo.multiBrick, 6, TRepo.chisel);
        chiseling.addDetailing(Item.diamond, 0, TRepo.multiBrick, 7, TRepo.chisel);
        chiseling.addDetailing(Item.redstone, 0, TRepo.multiBrick, 8, TRepo.chisel);
        chiseling.addDetailing(Item.bone, 0, TRepo.multiBrick, 9, TRepo.chisel);
        chiseling.addDetailing(Item.slimeBall, 0, TRepo.multiBrick, 10, TRepo.chisel);
        chiseling.addDetailing(TRepo.strangeFood, 0, TRepo.multiBrick, 11, TRepo.chisel);
        chiseling.addDetailing(Block.whiteStone, 0, TRepo.multiBrick, 12, TRepo.chisel);
        chiseling.addDetailing(TRepo.materials, 18, TRepo.multiBrick, 13, TRepo.chisel);

        // adding multiBrick / multiBrickFanxy meta 0-13 to list
        for (int sc = 0; sc < 14; sc++)
        {
            chiseling.addDetailing(TRepo.multiBrick, sc, TRepo.multiBrickFancy, sc, TRepo.chisel);
        }

        chiseling.addDetailing(Block.stoneBrick, 0, TRepo.multiBrickFancy, 15, TRepo.chisel);
        chiseling.addDetailing(TRepo.multiBrickFancy, 15, TRepo.multiBrickFancy, 14, TRepo.chisel);
        chiseling.addDetailing(TRepo.multiBrickFancy, 14, Block.stoneBrick, 3, TRepo.chisel);
        /*chiseling.addDetailing(TRepo.multiBrick, 14, TRepo.multiBrickFancy, 14, TRepo.chisel);
        chiseling.addDetailing(TRepo.multiBrick, 15, TRepo.multiBrickFancy, 15, TRepo.chisel);*/

        chiseling.addDetailing(TRepo.smeltery, 4, TRepo.smeltery, 6, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 6, TRepo.smeltery, 11, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 11, TRepo.smeltery, 2, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 2, TRepo.smeltery, 8, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 8, TRepo.smeltery, 9, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 9, TRepo.smeltery, 10, TRepo.chisel);
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

    public void oreRegistry ()
    {
        OreDictionary.registerOre("oreCobalt", new ItemStack(TRepo.oreSlag, 1, 1));
        OreDictionary.registerOre("oreArdite", new ItemStack(TRepo.oreSlag, 1, 2));
        OreDictionary.registerOre("oreCopper", new ItemStack(TRepo.oreSlag, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(TRepo.oreSlag, 1, 4));
        OreDictionary.registerOre("oreAluminum", new ItemStack(TRepo.oreSlag, 1, 5));
        OreDictionary.registerOre("oreAluminium", new ItemStack(TRepo.oreSlag, 1, 5));

        OreDictionary.registerOre("oreIron", new ItemStack(TRepo.oreGravel, 1, 0));
        OreDictionary.registerOre("oreGold", new ItemStack(TRepo.oreGravel, 1, 1));
        OreDictionary.registerOre("oreCobalt", new ItemStack(TRepo.oreGravel, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(TRepo.oreGravel, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(TRepo.oreGravel, 1, 3));
        OreDictionary.registerOre("oreAluminum", new ItemStack(TRepo.oreGravel, 1, 4));
        OreDictionary.registerOre("oreAluminium", new ItemStack(TRepo.oreGravel, 1, 4));

        OreDictionary.registerOre("ingotCobalt", new ItemStack(TRepo.materials, 1, 3));
        OreDictionary.registerOre("ingotArdite", new ItemStack(TRepo.materials, 1, 4));
        OreDictionary.registerOre("ingotManyullyn", new ItemStack(TRepo.materials, 1, 5));
        OreDictionary.registerOre("ingotCopper", new ItemStack(TRepo.materials, 1, 9));
        OreDictionary.registerOre("ingotTin", new ItemStack(TRepo.materials, 1, 10));
        OreDictionary.registerOre("ingotAluminum", new ItemStack(TRepo.materials, 1, 11));
        OreDictionary.registerOre("ingotAluminium", new ItemStack(TRepo.materials, 1, 11));
        OreDictionary.registerOre("ingotBronze", new ItemStack(TRepo.materials, 1, 13));
        OreDictionary.registerOre("ingotAluminumBrass", new ItemStack(TRepo.materials, 1, 14));
        OreDictionary.registerOre("ingotAluminiumBrass", new ItemStack(TRepo.materials, 1, 14));
        OreDictionary.registerOre("ingotAlumite", new ItemStack(TRepo.materials, 1, 15));
        OreDictionary.registerOre("ingotSteel", new ItemStack(TRepo.materials, 1, 16));
        ensureOreIsRegistered("ingotIron", new ItemStack(Item.ingotIron));
        ensureOreIsRegistered("ingotGold", new ItemStack(Item.ingotGold));
        OreDictionary.registerOre("ingotObsidian", new ItemStack(TRepo.materials, 1, 18));
        OreDictionary.registerOre("ingotPigIron", new ItemStack(TRepo.materials, 1, 34));
        OreDictionary.registerOre("itemRawRubber", new ItemStack(TRepo.materials, 1, 36));

        OreDictionary.registerOre("blockCobalt", new ItemStack(TRepo.metalBlock, 1, 0));
        OreDictionary.registerOre("blockArdite", new ItemStack(TRepo.metalBlock, 1, 1));
        OreDictionary.registerOre("blockManyullyn", new ItemStack(TRepo.metalBlock, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(TRepo.metalBlock, 1, 3));
        OreDictionary.registerOre("blockBronze", new ItemStack(TRepo.metalBlock, 1, 4));
        OreDictionary.registerOre("blockTin", new ItemStack(TRepo.metalBlock, 1, 5));
        OreDictionary.registerOre("blockAluminum", new ItemStack(TRepo.metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminium", new ItemStack(TRepo.metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminumBrass", new ItemStack(TRepo.metalBlock, 1, 7));
        OreDictionary.registerOre("blockAluminiumBrass", new ItemStack(TRepo.metalBlock, 1, 7));
        OreDictionary.registerOre("blockAlumite", new ItemStack(TRepo.metalBlock, 1, 8));
        OreDictionary.registerOre("blockSteel", new ItemStack(TRepo.metalBlock, 1, 9));
        ensureOreIsRegistered("blockIron", new ItemStack(Block.blockIron));
        ensureOreIsRegistered("blockGold", new ItemStack(Block.blockGold));

        OreDictionary.registerOre("nuggetIron", new ItemStack(TRepo.materials, 1, 19));
        OreDictionary.registerOre("nuggetIron", new ItemStack(TRepo.oreBerries, 1, 0));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(TRepo.materials, 1, 20));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(TRepo.oreBerries, 1, 2));
        OreDictionary.registerOre("nuggetTin", new ItemStack(TRepo.materials, 1, 21));
        OreDictionary.registerOre("nuggetTin", new ItemStack(TRepo.oreBerries, 1, 3));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(TRepo.materials, 1, 22));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(TRepo.oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(TRepo.materials, 1, 22));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(TRepo.oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(TRepo.materials, 1, 24));
        OreDictionary.registerOre("nuggetAluminiumBrass", new ItemStack(TRepo.materials, 1, 24));
        OreDictionary.registerOre("nuggetObsidian", new ItemStack(TRepo.materials, 1, 27));
        OreDictionary.registerOre("nuggetCobalt", new ItemStack(TRepo.materials, 1, 28));
        OreDictionary.registerOre("nuggetArdite", new ItemStack(TRepo.materials, 1, 29));
        OreDictionary.registerOre("nuggetManyullyn", new ItemStack(TRepo.materials, 1, 30));
        OreDictionary.registerOre("nuggetBronze", new ItemStack(TRepo.materials, 1, 31));
        OreDictionary.registerOre("nuggetAlumite", new ItemStack(TRepo.materials, 1, 32));
        OreDictionary.registerOre("nuggetSteel", new ItemStack(TRepo.materials, 1, 33));
        OreDictionary.registerOre("nuggetGold", new ItemStack(TRepo.oreBerries, 1, 1));
        ensureOreIsRegistered("nuggetGold", new ItemStack(Item.goldNugget));
        OreDictionary.registerOre("nuggetPigIron", new ItemStack(TRepo.materials, 1, 35));

        OreDictionary.registerOre("slabCloth", new ItemStack(TRepo.woolSlab1, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("slabCloth", new ItemStack(TRepo.woolSlab2, 1, Short.MAX_VALUE));

        ensureOreIsRegistered("stoneMossy", new ItemStack(Block.stoneBrick, 1, 1));
        ensureOreIsRegistered("stoneMossy", new ItemStack(Block.cobblestoneMossy));

        OreDictionary.registerOre("crafterWood", new ItemStack(Block.workbench, 1));
        OreDictionary.registerOre("craftingTableWood", new ItemStack(Block.workbench, 1));

        OreDictionary.registerOre("torchStone", new ItemStack(TRepo.stoneTorch));

        String[] matNames = { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel",
                "blueslime" };
        for (int i = 0; i < matNames.length; i++)
            OreDictionary.registerOre(matNames[i] + "Rod", new ItemStack(TRepo.toolRod, 1, i));
        OreDictionary.registerOre("thaumiumRod", new ItemStack(TRepo.toolRod, 1, 31));

        String[] glassTypes = { "glassBlack", "glassRed", "glassGreen", "glassBrown", "glassBlue", "glassPurple", "glassCyan", "glassLightGray", "glassGray", "glassPink", "glassLime", "glassYellow",
                "glassLightBlue", "glassMagenta", "glassOrange", "glassWhite" };
        for (int i = 0; i < 16; i++)
        {
            OreDictionary.registerOre(glassTypes[15 - i], new ItemStack(TRepo.stainedGlassClear, 1, i));
        }

        BlockDispenser.dispenseBehaviorRegistry.putObject(TRepo.titleIcon, new TDispenserBehaviorSpawnEgg());

        BlockDispenser.dispenseBehaviorRegistry.putObject(TRepo.arrow, new TDispenserBehaviorArrow());

        //Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Item.slimeBall));
        OreDictionary.registerOre("slimeball", new ItemStack(TRepo.strangeFood, 1, 0));
        OreDictionary.registerOre("slimeball", new ItemStack(TRepo.materials, 1, 36));
        OreDictionary.registerOre("glass", new ItemStack(TRepo.clearGlass));
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
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TRepo.pickaxeHead, 1, 6), new ItemStack(TRepo.toolRod, 1, 2), new ItemStack(TRepo.binding, 1, 6), "");
        /*TE3 Flux*/
        ItemStack batHardened = GameRegistry.findItemStack("ThermalExpansion", "capacitorHardened", 1);
        if (batHardened != null)
        {
            TRepo.modF.batteries.add(batHardened);
        }
        ItemStack basicCell = GameRegistry.findItemStack("ThermalExpansion", "cellBasic", 1);
        if (basicCell != null)
        {
            TRepo.modF.batteries.add(basicCell);
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
            PatternBuilder.instance.registerFullMaterial(new ItemStack((Item) obj, 1, 2), 2, "Thaumium", new ItemStack(TRepo.toolShard, 1, 31), new ItemStack(TRepo.toolRod, 1, 31), 31);
            for (int meta = 0; meta < patternOutputs.length; meta++)
            {
                if (patternOutputs[meta] != null)
                    TConstructRegistry.addPartMapping(TRepo.woodPattern.itemID, meta + 1, 31, new ItemStack(patternOutputs[meta], 1, 31));
            }

            TConstructRegistry.addBowstringMaterial(1, 2, new ItemStack((Item) obj, 1, 7), new ItemStack(TRepo.bowstring, 1, 1), 1F, 1F, 0.9f);
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
                TConstructRegistry.addBowstringMaterial(2, 2, new ItemStack((Item) plantItem, 1, 7), new ItemStack(TRepo.bowstring, 1, 2), 1.2F, 0.8F, 1.3f);
            }
            catch (Exception e)
            {
            } //No need to handle
        }

        ItemStack ingotcast = new ItemStack(TRepo.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();

        /* Thermal Expansion 3 Metals */
        ArrayList<ItemStack> ores = OreDictionary.getOres("ingotNickel");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenNickelFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotLead");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenLeadFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotSilver");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenSilverFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotPlatinum");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenShinyFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotInvar");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenInvarFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenInvarFluid, 24), new FluidStack(TRepo.moltenIronFluid, 16), new FluidStack(TRepo.moltenNickelFluid, 8)); //Invar
        }
        ores = OreDictionary.getOres("ingotElectrum");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenElectrumFluid, 16), new FluidStack(TRepo.moltenGoldFluid, 8), new FluidStack(TRepo.moltenSilverFluid, 8)); //Electrum
        }

        ores = OreDictionary.getOres("blockNickel");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenNickelFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockLead");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenLeadFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockSilver");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenSilverFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockPlatinum");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenShinyFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockInvar");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenInvarFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockElectrum");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.blockLiquidValue), null, 100);
        }

        /* Extra Utilities */
        ores = OreDictionary.getOres("compressedGravel1x");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(TRepo.speedBlock, 9), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.blockLiquidValue), ores.get(0), 100);
        }
        ores = OreDictionary.getOres("compressedGravel2x"); //Higher won't save properly
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(TRepo.speedBlock, 81), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.blockLiquidValue * 9), ores.get(0), 100);
        }

        /* Rubber */
        ores = OreDictionary.getOres("itemRubber");
        if (ores.size() > 0)
        {
            FurnaceRecipes.smelting().addSmelting(TRepo.materials.itemID, 36, ores.get(0), 0.2f);
        }
        //new ItemStack(TRepo.materials, 1, 36)
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
        if (fuel.itemID == TRepo.materials.itemID && fuel.getItemDamage() == 7)
            return 26400;
        return 0;
    }

    public void addOreDictionarySmelteryRecipes ()
    {
        List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.Water, FluidType.Stone, FluidType.Ender, FluidType.Glass, FluidType.Slime });
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
        ItemStack pattern = new ItemStack(TRepo.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        for (ItemStack ore : OreDictionary.getOres("ingot" + ft.toString()))
        {
            tableCasting.addCastingRecipe(pattern, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(ore.itemID, 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(pattern, new FluidStack(TRepo.moltenGoldFluid, TConstruct.oreLiquidValue), new ItemStack(ore.itemID, 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(new ItemStack(ore.itemID, 1, ore.getItemDamage()), new FluidStack(ft.fluid, TConstruct.ingotLiquidValue), pattern, 80);
        }

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
