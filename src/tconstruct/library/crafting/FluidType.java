package tconstruct.library.crafting;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.*;
import tconstruct.common.TContent;

public enum FluidType
{
    /** Vanilla Water Smelting **/
    Water(Block.snow.blockID, 0, 20, FluidRegistry.getFluid("water")),
    /** Iron Smelting **/
    Iron(Block.oreIron.blockID, 0, 600, TContent.moltenIronFluid),
    /** Gold  Smelting **/
    Gold(Block.oreGold.blockID, 0, 400, TContent.moltenGoldFluid),
    /** Tin  Smelting **/
    Tin(TContent.oreSlag.blockID, 4, 400, TContent.moltenTinFluid),
    /** Copper  Smelting **/
    Copper(TContent.oreSlag.blockID, 3, 550, TContent.moltenCopperFluid),
    /** Aluminum Smelting **/
    Aluminum(TContent.oreSlag.blockID, 5, 350, TContent.moltenAluminumFluid), NaturalAluminum(TContent.oreSlag.blockID, 5, 350, TContent.moltenAluminumFluid),
    /** Cobalt Smelting **/
    Cobalt(TContent.oreSlag.blockID, 1, 650, TContent.moltenCobaltFluid),
    /** Ardite Smelting **/
    Ardite(TContent.oreSlag.blockID, 2, 650, TContent.moltenArditeFluid),
    /** AluminumBrass Smelting **/
    AluminumBrass(TContent.metalBlock.blockID, 7, 350, TContent.moltenAlubrassFluid),
    /** Alumite Smelting **/
    Alumite(TContent.metalBlock.blockID, 0, 800, TContent.moltenAlumiteFluid),
    /** Manyullyn Smelting **/
    Manyullyn(TContent.metalBlock.blockID, 2, 750, TContent.moltenManyullynFluid),
    /** Bronze Smelting **/
    Bronze(TContent.metalBlock.blockID, 4, 500, TContent.moltenBronzeFluid),
    /** Steel Smelting **/
    Steel(TContent.metalBlock.blockID, 9, 700, TContent.moltenSteelFluid),
    /** Nickel Smelting **/
    Nickel(TContent.metalBlock.blockID, 0, 400, TContent.moltenNickelFluid),
    /** Lead Smelting **/
    Lead(TContent.metalBlock.blockID, 0, 400, TContent.moltenLeadFluid),
    /** Silver Smelting **/
    Silver(TContent.metalBlock.blockID, 0, 400, TContent.moltenSilverFluid),
    /** Platinum Smelting **/
    Platinum(TContent.metalBlock.blockID, 0, 400, TContent.moltenShinyFluid),
    /** Invar Smelting **/
    Invar(TContent.metalBlock.blockID, 0, 400, TContent.moltenInvarFluid),
    /** Electrum Smelting **/
    Electrum(TContent.metalBlock.blockID, 0, 400, TContent.moltenElectrumFluid),
    /** Obsidian Smelting **/
    Obsidian(Block.obsidian.blockID, 0, 750, TContent.moltenObsidianFluid),
    /** Ender Smelting **/
    Ender(TContent.metalBlock.blockID, 10, 500, TContent.moltenEnderFluid),
    /** Glass Smelting **/
    Glass(Block.glass.blockID, 0, 625, TContent.moltenGlassFluid),
    /** Stone Smelting **/
    Stone(Block.stone.blockID, 0, 800, TContent.moltenStoneFluid),
    /** Emerald Smelting **/
    Emerald(Block.oreEmerald.blockID, 0, 575, TContent.moltenEmeraldFluid),
    /** Slime Smelting **/
    Slime(TContent.slimeGel.blockID, 0, 250, TContent.blueSlimeFluid);

    public final int renderBlockID;
    public final int renderMeta;
    public final int baseTemperature;
    public final Fluid fluid;

    FluidType(int blockID, int meta, int baseTemperature, Fluid fluid)
    {
        this.renderBlockID = blockID;
        this.renderMeta = meta;
        this.baseTemperature = baseTemperature;
        this.fluid = fluid;
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