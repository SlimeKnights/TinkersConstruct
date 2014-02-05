package tconstruct.library.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import tconstruct.common.TRepo;

public enum FluidType
{
    /** Vanilla Water Smelting **/
    Water(Blocks.snow, 0, 20, FluidRegistry.getFluid("water"), false),
    /** Iron Smelting **/
    Iron(Blocks.iron_block, 0, 600, TRepo.moltenIronFluid, true),
    /** Gold  Smelting **/
    Gold(Blocks.gold_block, 0, 400, TRepo.moltenGoldFluid, false),
    /** Tin  Smelting **/
    Tin(TRepo.metalBlock, 5, 400, TRepo.moltenTinFluid, false),
    /** Copper  Smelting **/
    Copper(TRepo.metalBlock, 3, 550, TRepo.moltenCopperFluid, true),
    /** Aluminum Smelting **/
    Aluminum(TRepo.metalBlock, 6, 350, TRepo.moltenAluminumFluid, false),
    /** Natural Aluminum Smelting **/
    NaturalAluminum(TRepo.oreSlag, 6, 350, TRepo.moltenAluminumFluid, false),
    /** Cobalt Smelting **/
    Cobalt(TRepo.metalBlock, 0, 650, TRepo.moltenCobaltFluid, true),
    /** Ardite Smelting **/
    Ardite(TRepo.metalBlock, 1, 650, TRepo.moltenArditeFluid, true),
    /** AluminumBrass Smelting **/
    AluminumBrass(TRepo.metalBlock, 7, 350, TRepo.moltenAlubrassFluid, false),
    /** Alumite Smelting **/
    Alumite(TRepo.metalBlock, 8, 800, TRepo.moltenAlumiteFluid, true),
    /** Manyullyn Smelting **/
    Manyullyn(TRepo.metalBlock, 2, 750, TRepo.moltenManyullynFluid, true),
    /** Bronze Smelting **/
    Bronze(TRepo.metalBlock, 4, 500, TRepo.moltenBronzeFluid, true),
    /** Steel Smelting **/
    Steel(TRepo.metalBlock, 9, 700, TRepo.moltenSteelFluid, true),
    /** Nickel Smelting **/
    Nickel(TRepo.metalBlock, 0, 400, TRepo.moltenNickelFluid, false),
    /** Lead Smelting **/
    Lead(TRepo.metalBlock, 0, 400, TRepo.moltenLeadFluid, false),
    /** Silver Smelting **/
    Silver(TRepo.metalBlock, 0, 400, TRepo.moltenSilverFluid, false),
    /** Platinum Smelting **/
    Platinum(TRepo.metalBlock, 0, 400, TRepo.moltenShinyFluid, false),
    /** Invar Smelting **/
    Invar(TRepo.metalBlock, 0, 400, TRepo.moltenInvarFluid, false),
    /** Electrum Smelting **/
    Electrum(TRepo.metalBlock, 0, 400, TRepo.moltenElectrumFluid, false),
    /** Obsidian Smelting **/
    Obsidian(Blocks.obsidian, 0, 750, TRepo.moltenObsidianFluid, true),
    /** Ender Smelting **/
    Ender(TRepo.metalBlock, 10, 500, TRepo.moltenEnderFluid, false),
    /** Glass Smelting **/
    Glass(Blocks.sand, 0, 625, TRepo.moltenGlassFluid, false),
    /** Stone Smelting **/
    Stone(Blocks.stone, 0, 800, TRepo.moltenStoneFluid, true),
    /** Emerald Smelting **/
    Emerald(Blocks.emerald_ore, 0, 575, TRepo.moltenEmeraldFluid, false),
    /** Slime Smelting **/
    Slime(TRepo.slimeGel, 0, 250, TRepo.blueSlimeFluid, false),
    /** Pigiron Smelting **/
    PigIron(TRepo.meatBlock, 0, 610, TRepo.pigIronFluid, true),
    /** Glue Smelting **/
    Glue(TRepo.glueBlock, 0, 125, TRepo.glueFluid, false);

    public final Block renderBlock;
    public final int renderMeta;
    public final int baseTemperature;
    public final Fluid fluid;
    public final boolean isToolpart;

    FluidType(Block b, int meta, int baseTemperature, Fluid fluid, boolean isToolpart)
    {
        this.renderBlock = b;
        this.renderMeta = meta;
        this.baseTemperature = baseTemperature;
        this.fluid = fluid;
        this.isToolpart = isToolpart;
    }

    public static FluidType getFluidType (Fluid searchedFluid)
    {
        for (FluidType ft : values())
        {
            if (ft.fluid == searchedFluid)
                return ft;
        }
        return null;
    }

    public static int getTemperatureByFluid (Fluid searchedFluid)
    {
        for (FluidType ft : values())
        {
            if (ft.fluid == searchedFluid)
                return ft.baseTemperature;
        }
        return 800;
    }
}