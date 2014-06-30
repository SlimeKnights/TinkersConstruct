package tconstruct.library.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.world.TinkerWorld;

public enum FluidType
{
    /** Vanilla Water Smelting **/
    Water(Blocks.snow, 0, 20, FluidRegistry.getFluid("water"), false),
    /** Iron Smelting **/
    Iron(Blocks.iron_block, 0, 600, TinkerSmeltery.moltenIronFluid, true),
    /** Gold Smelting **/
    Gold(Blocks.gold_block, 0, 400, TinkerSmeltery.moltenGoldFluid, false),
    /** Tin Smelting **/
    Tin(TinkerWorld.metalBlock, 5, 400, TinkerSmeltery.moltenTinFluid, false),
    /** Copper Smelting **/
    Copper(TinkerWorld.metalBlock, 3, 550, TinkerSmeltery.moltenCopperFluid, true),
    /** Aluminum Smelting **/
    Aluminum(TinkerWorld.metalBlock, 6, 350, TinkerSmeltery.moltenAluminumFluid, false),
    /** Natural Aluminum Smelting **/
    NaturalAluminum(TinkerWorld.oreSlag, 6, 350, TinkerSmeltery.moltenAluminumFluid, false),
    /** Cobalt Smelting **/
    Cobalt(TinkerWorld.metalBlock, 0, 650, TinkerSmeltery.moltenCobaltFluid, true),
    /** Ardite Smelting **/
    Ardite(TinkerWorld.metalBlock, 1, 650, TinkerSmeltery.moltenArditeFluid, true),
    /** AluminumBrass Smelting **/
    AluminumBrass(TinkerWorld.metalBlock, 7, 350, TinkerSmeltery.moltenAlubrassFluid, false),
    /** Alumite Smelting **/
    Alumite(TinkerWorld.metalBlock, 8, 800, TinkerSmeltery.moltenAlumiteFluid, true),
    /** Manyullyn Smelting **/
    Manyullyn(TinkerWorld.metalBlock, 2, 750, TinkerSmeltery.moltenManyullynFluid, true),
    /** Bronze Smelting **/
    Bronze(TinkerWorld.metalBlock, 4, 500, TinkerSmeltery.moltenBronzeFluid, true),
    /** Steel Smelting **/
    Steel(TinkerWorld.metalBlock, 9, 700, TinkerSmeltery.moltenSteelFluid, true),
    /** Nickel Smelting **/
    Nickel(TinkerWorld.metalBlock, 0, 400, TinkerSmeltery.moltenNickelFluid, false),
    /** Lead Smelting **/
    Lead(TinkerWorld.metalBlock, 0, 400, TinkerSmeltery.moltenLeadFluid, false),
    /** Silver Smelting **/
    Silver(TinkerWorld.metalBlock, 0, 400, TinkerSmeltery.moltenSilverFluid, false),
    /** Platinum Smelting **/
    Platinum(TinkerWorld.metalBlock, 0, 400, TinkerSmeltery.moltenShinyFluid, false),
    /** Invar Smelting **/
    Invar(TinkerWorld.metalBlock, 0, 400, TinkerSmeltery.moltenInvarFluid, false),
    /** Electrum Smelting **/
    Electrum(TinkerWorld.metalBlock, 0, 400, TinkerSmeltery.moltenElectrumFluid, false),
    /** Obsidian Smelting **/
    Obsidian(Blocks.obsidian, 0, 750, TinkerSmeltery.moltenObsidianFluid, true),
    /** Ender Smelting **/
    Ender(TinkerWorld.metalBlock, 10, 500, TinkerSmeltery.moltenEnderFluid, false),
    /** Glass Smelting **/
    Glass(Blocks.sand, 0, 625, TinkerSmeltery.moltenGlassFluid, false),
    /** Stone Smelting **/
    Stone(Blocks.stone, 0, 800, TinkerSmeltery.moltenStoneFluid, true),
    /** Emerald Smelting **/
    Emerald(Blocks.emerald_ore, 0, 575, TinkerSmeltery.moltenEmeraldFluid, false),
    /** Slime Smelting **/
    Slime(TinkerWorld.slimeGel, 0, 250, TinkerWorld.blueSlimeFluid, false),
    /** Pigiron Smelting **/
    PigIron(TinkerWorld.meatBlock, 0, 610, TinkerSmeltery.pigIronFluid, true),
    /** Glue Smelting **/
    Glue(TinkerSmeltery.glueBlock, 0, 125, TinkerSmeltery.glueFluid, false);

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