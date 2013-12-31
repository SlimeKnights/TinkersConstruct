package tconstruct.library.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.*;
import tconstruct.common.TRepo;

public enum FluidType
{
    /** Vanilla Water Smelting **/
    Water(Block.snow.blockID, 0, 20, FluidRegistry.getFluid("water"), false),
    /** Iron Smelting **/
    Iron(Block.blockIron.blockID, 0, 600, TRepo.moltenIronFluid, true),
    /** Gold  Smelting **/
    Gold(Blocks.glold_block.blockID, 0, 400, TRepo.moltenGoldFluid, false),
    /** Tin  Smelting **/
    Tin(TRepo.metalBlock.blockID, 5, 400, TRepo.moltenTinFluid, false),
    /** Copper  Smelting **/
    Copper(TRepo.metalBlock.blockID, 3, 550, TRepo.moltenCopperFluid, true),
    /** Aluminum Smelting **/
    Aluminum(TRepo.metalBlock.blockID, 6, 350, TRepo.moltenAluminumFluid, false),
    /** Natural Aluminum Smelting **/
    NaturalAluminum(TRepo.oreSlag.blockID, 6, 350, TRepo.moltenAluminumFluid, false),
    /** Cobalt Smelting **/
    Cobalt(TRepo.metalBlock.blockID, 0, 650, TRepo.moltenCobaltFluid, true),
    /** Ardite Smelting **/
    Ardite(TRepo.metalBlock.blockID, 1, 650, TRepo.moltenArditeFluid, true),
    /** AluminumBrass Smelting **/
    AluminumBrass(TRepo.metalBlock.blockID, 7, 350, TRepo.moltenAlubrassFluid, false),
    /** Alumite Smelting **/
    Alumite(TRepo.metalBlock.blockID, 8, 800, TRepo.moltenAlumiteFluid, true),
    /** Manyullyn Smelting **/
    Manyullyn(TRepo.metalBlock.blockID, 2, 750, TRepo.moltenManyullynFluid, true),
    /** Bronze Smelting **/
    Bronze(TRepo.metalBlock.blockID, 4, 500, TRepo.moltenBronzeFluid, true),
    /** Steel Smelting **/
    Steel(TRepo.metalBlock.blockID, 9, 700, TRepo.moltenSteelFluid, true),
    /** Nickel Smelting **/
    Nickel(TRepo.metalBlock.blockID, 0, 400, TRepo.moltenNickelFluid, false),
    /** Lead Smelting **/
    Lead(TRepo.metalBlock.blockID, 0, 400, TRepo.moltenLeadFluid, false),
    /** Silver Smelting **/
    Silver(TRepo.metalBlock.blockID, 0, 400, TRepo.moltenSilverFluid, false),
    /** Platinum Smelting **/
    Platinum(TRepo.metalBlock.blockID, 0, 400, TRepo.moltenShinyFluid, false),
    /** Invar Smelting **/
    Invar(TRepo.metalBlock.blockID, 0, 400, TRepo.moltenInvarFluid, false),
    /** Electrum Smelting **/
    Electrum(TRepo.metalBlock.blockID, 0, 400, TRepo.moltenElectrumFluid, false),
    /** Obsidian Smelting **/
    Obsidian(Block.obsidian.blockID, 0, 750, TRepo.moltenObsidianFluid, true),
    /** Ender Smelting **/
    Ender(TRepo.metalBlock.blockID, 10, 500, TRepo.moltenEnderFluid, false),
    /** Glass Smelting **/
    Glass(Block.sand.blockID, 0, 625, TRepo.moltenGlassFluid, false),
    /** Stone Smelting **/
    Stone(Block.stone.blockID, 0, 800, TRepo.moltenStoneFluid, true),
    /** Emerald Smelting **/
    Emerald(Block.oreEmerald.blockID, 0, 575, TRepo.moltenEmeraldFluid, false),
    /** Slime Smelting **/
    Slime(TRepo.slimeGel.blockID, 0, 250, TRepo.blueSlimeFluid, false),
    /** Pigiron Smelting **/
    PigIron(TRepo.meatBlock.blockID, 0, 610, TRepo.pigIronFluid, true),
    /** Glue Smelting **/
    Glue(TRepo.glueBlock.blockID, 0, 125, TRepo.glueFluid, false);

    public final int renderBlockID;
    public final int renderMeta;
    public final int baseTemperature;
    public final Fluid fluid;
    public final boolean isToolpart;

    FluidType(int blockID, int meta, int baseTemperature, Fluid fluid, boolean isToolpart)
    {
        this.renderBlockID = blockID;
        this.renderMeta = meta;
        this.baseTemperature = baseTemperature;
        this.fluid = fluid;
        this.isToolpart = isToolpart;
    }

    public static FluidType getFluidType (Fluid searchedFluid)
    {
        for (FluidType ft : values())
        {
            if (ft.fluid.getBlockID() == searchedFluid.getBlockID())
                return ft;
        }
        return null;
    }

    public static int getTemperatureByFluid (Fluid searchedFluid)
    {
        for (FluidType ft : values())
        {
            if (ft.fluid.getBlockID() == searchedFluid.getBlockID())
                return ft.baseTemperature;
        }
        return 800;
    }
}