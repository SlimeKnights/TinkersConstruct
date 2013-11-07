package tconstruct.library.crafting;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.*;
import tconstruct.common.TContent;

public enum FluidType
{
    /** Vanilla Water Smelting **/
    Water(Block.snow.blockID, 0, 20, FluidRegistry.getFluid("water"), false),
    /** Iron Smelting **/
    Iron(Block.oreIron.blockID, 0, 600, TContent.moltenIronFluid, true),
    /** Gold  Smelting **/
    Gold(Block.oreGold.blockID, 0, 400, TContent.moltenGoldFluid, false),
    /** Tin  Smelting **/
    Tin(TContent.oreSlag.blockID, 4, 400, TContent.moltenTinFluid, false),
    /** Copper  Smelting **/
    Copper(TContent.oreSlag.blockID, 3, 550, TContent.moltenCopperFluid, true),
    /** Aluminum Smelting **/
    Aluminum(TContent.oreSlag.blockID, 5, 350, TContent.moltenAluminumFluid, false),
    /** Natural Aluminum Smelting **/
    NaturalAluminum(TContent.oreSlag.blockID, 5, 350, TContent.moltenAluminumFluid, false),
    /** Cobalt Smelting **/
    Cobalt(TContent.oreSlag.blockID, 1, 650, TContent.moltenCobaltFluid, true),
    /** Ardite Smelting **/
    Ardite(TContent.oreSlag.blockID, 2, 650, TContent.moltenArditeFluid, true),
    /** AluminumBrass Smelting **/
    AluminumBrass(TContent.metalBlock.blockID, 7, 350, TContent.moltenAlubrassFluid, false),
    /** Alumite Smelting **/
    Alumite(TContent.metalBlock.blockID, 0, 800, TContent.moltenAlumiteFluid, true),
    /** Manyullyn Smelting **/
    Manyullyn(TContent.metalBlock.blockID, 2, 750, TContent.moltenManyullynFluid, true),
    /** Bronze Smelting **/
    Bronze(TContent.metalBlock.blockID, 4, 500, TContent.moltenBronzeFluid, true),
    /** Steel Smelting **/
    Steel(TContent.metalBlock.blockID, 9, 700, TContent.moltenSteelFluid, true),
    /** Nickel Smelting **/
    Nickel(TContent.metalBlock.blockID, 0, 400, TContent.moltenNickelFluid, false),
    /** Lead Smelting **/
    Lead(TContent.metalBlock.blockID, 0, 400, TContent.moltenLeadFluid, false),
    /** Silver Smelting **/
    Silver(TContent.metalBlock.blockID, 0, 400, TContent.moltenSilverFluid, false),
    /** Platinum Smelting **/
    Platinum(TContent.metalBlock.blockID, 0, 400, TContent.moltenShinyFluid, false),
    /** Invar Smelting **/
    Invar(TContent.metalBlock.blockID, 0, 400, TContent.moltenInvarFluid, false),
    /** Electrum Smelting **/
    Electrum(TContent.metalBlock.blockID, 0, 400, TContent.moltenElectrumFluid, false),
    /** Obsidian Smelting **/
    Obsidian(Block.obsidian.blockID, 0, 750, TContent.moltenObsidianFluid, true),
    /** Ender Smelting **/
    Ender(TContent.metalBlock.blockID, 10, 500, TContent.moltenEnderFluid, false),
    /** Glass Smelting **/
    Glass(Block.sand.blockID, 0, 625, TContent.moltenGlassFluid, false),
    /** Stone Smelting **/
    Stone(Block.stone.blockID, 0, 800, TContent.moltenStoneFluid, true),
    /** Emerald Smelting **/
    Emerald(Block.oreEmerald.blockID, 0, 575, TContent.moltenEmeraldFluid, false),
    /** Slime Smelting **/
    Slime(TContent.slimeGel.blockID, 0, 250, TContent.blueSlimeFluid, false);

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