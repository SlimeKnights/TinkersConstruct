package tconstruct.plugins.ic2;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.plugins.ICompatPlugin;

public class IC2 implements ICompatPlugin
{

    private static final String IC2_UUM_FLUIDNAME = "uumatter";
    private Fluid fluidUUM;

    @Override
    public String getModId ()
    {
        return "IC2";
    }

    @Override
    public void preInit ()
    {

    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("[IC2] Preparing for shenanigans.");

        fluidUUM = FluidRegistry.getFluid(IC2_UUM_FLUIDNAME);
        if (fluidUUM == null)
            return;

        // Useful stuff
        // ItemStack ingotCast = new ItemStack(TRepo.metalPattern, 1, 0);
        // ItemStack gemCast = new ItemStack(TRepo.metalPattern, 1, 26);
        // FluidStack fluidStack = new FluidStack(fluidUUM, 1);
        FluidStack fluidStackBlock = new FluidStack(fluidUUM, 4500);
        // LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

        // Table casting
        // tableCasting.addCastingRecipe(new ItemStack(Item.ingotIron),
        // fluidStack, ingotCast, 20);
        // tableCasting.addCastingRecipe(new ItemStack(Item.emerald),
        // fluidStack, gemCast, 20);

        // Block casting
        // basinCasting.addCastingRecipe(new ItemStack(Block.blockIron),
        // fluidStackBlock, null, true, 50); // Iron convenience
        // Dirt <-> Diamonds
        // basinCasting.addCastingRecipe(new ItemStack(Block.dirt), new
        // FluidStack(fluidUUM, 500), new ItemStack(Block.blockDiamond), true,
        // 50);
        basinCasting.addCastingRecipe(new ItemStack(Blocks.diamond_block), fluidStackBlock, new ItemStack(Blocks.dirt), true, 50);
        // RS cycle
        // basinCasting.addCastingRecipe(new ItemStack(Block.blockRedstone),
        // fluidStack, new ItemStack(Item.redstone), true, 50);
        // basinCasting.addCastingRecipe(new ItemStack(Block.hardenedClay, 14,
        // 1), fluidStack, new ItemStack(Block.blockRedstone), true, 50);
        // basinCasting.addCastingRecipe(new ItemStack(Item.dyePowder, 1, 1),
        // fluidStack, new ItemStack(Block.hardenedClay, 14, 1), true, 50);
        // basinCasting.addCastingRecipe(new ItemStack(Block.blockRedstone),
        // fluidStack, new ItemStack(Item.dyePowder, 1, 1), true, 50);

        // Alloy Mixing
        // 1mB Anything + 1mB UU = 2mB UU
        /*
         * Maybe a bit *too* trolly for (Map.Entry<String, Fluid> ent :
         * FluidRegistry.getRegisteredFluids().entrySet()) { if (ent.getValue()
         * != fluidUUM) Smeltery.addAlloyMixing(new FluidStack(fluidUUM, 2), new
         * FluidStack(fluidUUM, 1), new FluidStack(ent.getValue(), 1)); }
         */
    }

    @Override
    public void postInit ()
    {

    }

}
