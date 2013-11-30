package tconstruct.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.blocks.logic.AdaptiveDrainLogic;
import tconstruct.blocks.logic.AdaptiveSmelteryLogic;
import tconstruct.blocks.logic.CastingBasinLogic;
import tconstruct.blocks.logic.CastingChannelLogic;
import tconstruct.blocks.logic.CastingTableLogic;
import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.blocks.logic.FaucetLogic;
import tconstruct.blocks.logic.FrypanLogic;
import tconstruct.blocks.logic.FurnaceLogic;
import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.blocks.logic.PartBuilderLogic;
import tconstruct.blocks.logic.PatternChestLogic;
import tconstruct.blocks.logic.SmelteryDrainLogic;
import tconstruct.blocks.logic.SmelteryLogic;
import tconstruct.blocks.logic.StencilTableLogic;
import tconstruct.blocks.logic.TankAirLogic;
import tconstruct.blocks.logic.TileEntityLandmine;
import tconstruct.blocks.logic.ToolForgeLogic;
import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.items.blocks.BarricadeItem;
import tconstruct.items.blocks.CastingChannelItem;
import tconstruct.items.blocks.CraftedSoilItemBlock;
import tconstruct.items.blocks.CraftingSlabItemBlock;
import tconstruct.items.blocks.GlassBlockItem;
import tconstruct.items.blocks.GlassPaneItem;
import tconstruct.items.blocks.GravelOreItem;
import tconstruct.items.blocks.HamboneItemBlock;
import tconstruct.items.blocks.ItemBlockLandmine;
import tconstruct.items.blocks.LavaTankItemBlock;
import tconstruct.items.blocks.MetadataItemBlock;
import tconstruct.items.blocks.MetalItemBlock;
import tconstruct.items.blocks.MetalOreItemBlock;
import tconstruct.items.blocks.MultiBrickFancyItem;
import tconstruct.items.blocks.MultiBrickItem;
import tconstruct.items.blocks.OreberryBushItem;
import tconstruct.items.blocks.OreberryBushSecondItem;
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
import tconstruct.items.blocks.ToolStationItemBlock;
import tconstruct.items.blocks.WoolSlab1Item;
import tconstruct.items.blocks.WoolSlab2Item;
import tconstruct.library.TConstructRegistry;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.registry.GameRegistry;

public class TRecipes
{
    static void registerBlockRecipes ()
    {
        //Tool Tables
        GameRegistry.registerBlock(TRepo.toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartBuilderLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");
        GameRegistry.registerBlock(TRepo.toolForge, MetadataItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");
        GameRegistry.registerBlock(TRepo.craftingStationWood, "CraftingStation");
        GameRegistry.registerTileEntity(CraftingStationLogic.class, "CraftingStation");
        GameRegistry.registerBlock(TRepo.craftingSlabWood, CraftingSlabItemBlock.class, "CraftingSlab");
        GameRegistry.registerBlock(TRepo.furnaceSlab, "FurnaceSlab");
        GameRegistry.registerTileEntity(FurnaceLogic.class, "TConstruct.Furnace");
        GameRegistry.registerBlock(TRepo.furnaceSlab, "FurnaceSlab");
        GameRegistry.registerTileEntity(FurnaceLogic.class, "TConstruct.Furnace");

        GameRegistry.registerBlock(TRepo.heldItemBlock, "HeldItemBlock");
        GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");

        GameRegistry.registerBlock(TRepo.craftedSoil, CraftedSoilItemBlock.class, "CraftedSoil");

        GameRegistry.registerBlock(TRepo.searedSlab, SearedSlabItem.class, "SearedSlab");
        GameRegistry.registerBlock(TRepo.speedSlab, SpeedSlabItem.class, "SpeedSlab");

        GameRegistry.registerBlock(TRepo.metalBlock, MetalItemBlock.class, "MetalBlock");
        GameRegistry.registerBlock(TRepo.meatBlock, HamboneItemBlock.class, "MeatBlock");

        OreDictionary.registerOre("hambone", new ItemStack(TRepo.meatBlock));
        GameRegistry.addRecipe(new ItemStack(TRepo.meatBlock), "mmm", "mbm", "mmm", 'b', new ItemStack(Item.bone), 'm', new ItemStack(Item.porkRaw));

        GameRegistry.registerBlock(TRepo.glueBlock, "GlueBlock");
        OreDictionary.registerOre("blockRubber", new ItemStack(TRepo.glueBlock));

        GameRegistry.registerBlock(TRepo.woolSlab1, WoolSlab1Item.class, "WoolSlab1");
        GameRegistry.registerBlock(TRepo.woolSlab2, WoolSlab2Item.class, "WoolSlab2");

        //Smeltery stuff
        GameRegistry.registerBlock(TRepo.smeltery, SmelteryItemBlock.class, "Smeltery");
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
        GameRegistry.registerBlock(TRepo.lavaTank, LavaTankItemBlock.class, "LavaTank");
        GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

        GameRegistry.registerBlock(TRepo.searedBlock, SearedTableItemBlock.class, "SearedBlock");
        GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "CastingBasin");

        GameRegistry.registerBlock(TRepo.castingChannel, CastingChannelItem.class, "CastingChannel");
        GameRegistry.registerTileEntity(CastingChannelLogic.class, "CastingChannel");

        GameRegistry.registerBlock(TRepo.tankAir, "TankAir");
        GameRegistry.registerTileEntity(TankAirLogic.class, "tconstruct.tank.air");

        //Traps
        GameRegistry.registerBlock(TRepo.landmine, ItemBlockLandmine.class, "Redstone.Landmine");
        GameRegistry.registerTileEntity(TileEntityLandmine.class, "Landmine");
        GameRegistry.registerBlock(TRepo.punji, "trap.punji");
        GameRegistry.registerBlock(TRepo.barricadeOak, BarricadeItem.class, "trap.barricade.oak");
        GameRegistry.registerBlock(TRepo.barricadeSpruce, BarricadeItem.class, "trap.barricade.spruce");
        GameRegistry.registerBlock(TRepo.barricadeBirch, BarricadeItem.class, "trap.barricade.birch");
        GameRegistry.registerBlock(TRepo.barricadeJungle, BarricadeItem.class, "trap.barricade.jungle");
        GameRegistry.registerBlock(TRepo.slimeExplosive, MetadataItemBlock.class, "explosive.slime");

        GameRegistry.registerBlock(TRepo.dryingRack, "Armor.DryingRack");
        GameRegistry.registerTileEntity(DryingRackLogic.class, "Armor.DryingRack");
        //fluids

        //Slime Islands
        GameRegistry.registerBlock(TRepo.slimeGel, SlimeGelItemBlock.class, "slime.gel");
        GameRegistry.registerBlock(TRepo.slimeGrass, SlimeGrassItemBlock.class, "slime.grass");
        GameRegistry.registerBlock(TRepo.slimeTallGrass, SlimeTallGrassItem.class, "slime.grass.tall");
        GameRegistry.registerBlock(TRepo.slimeLeaves, SlimeLeavesItemBlock.class, "slime.leaves");
        GameRegistry.registerBlock(TRepo.slimeSapling, SlimeSaplingItemBlock.class, "slime.sapling");
        GameRegistry.registerBlock(TRepo.slimeChannel, "slime.channel");
        GameRegistry.registerBlock(TRepo.slimePad, "slime.pad");
        TConstructRegistry.drawbridgeState[TRepo.slimePad.blockID] = 1;

        //Decoration
        GameRegistry.registerBlock(TRepo.stoneTorch, "decoration.stonetorch");
        GameRegistry.registerBlock(TRepo.stoneLadder, "decoration.stoneladder");
        GameRegistry.registerBlock(TRepo.multiBrick, MultiBrickItem.class, "decoration.multibrick");
        GameRegistry.registerBlock(TRepo.multiBrickFancy, MultiBrickFancyItem.class, "decoration.multibrickfancy");

        //Ores
        GameRegistry.registerBlock(TRepo.oreBerry, OreberryBushItem.class, "ore.berries.one");
        GameRegistry.registerBlock(TRepo.oreBerrySecond, OreberryBushSecondItem.class, "ore.berries.two");
        GameRegistry.registerBlock(TRepo.oreSlag, MetalOreItemBlock.class, "SearedBrick");
        GameRegistry.registerBlock(TRepo.oreGravel, GravelOreItem.class, "GravelOre");

        GameRegistry.registerBlock(TRepo.speedBlock, SpeedBlockItem.class, "SpeedBlock");

        //Glass
        GameRegistry.registerBlock(TRepo.clearGlass, GlassBlockItem.class, "GlassBlock");
        GameRegistry.registerBlock(TRepo.glassPane, GlassPaneItem.class, "GlassPane");
        GameRegistry.registerBlock(TRepo.stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");
        GameRegistry.registerBlock(TRepo.stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        //Rail
        GameRegistry.registerBlock(TRepo.woodenRail, "rail.wood");
    }

    static void registerItemRecipes ()
    {

    }

}
