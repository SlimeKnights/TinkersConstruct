package tconstruct.plugins.ic2;

import mantle.module.ILoadableModule;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.plugins.ICompatPlugin;

public class IC2 implements ILoadableModule
{

    private static final String IC2_UUM_FLUIDNAME = "uumatter";

    @SuppressWarnings("unused")
    public static String modId = "IC2";

    @Override
    public void preInit ()
    {

    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("[IC2] Preparing for shenanigans.");

        Fluid fluidUUM = FluidRegistry.getFluid(IC2_UUM_FLUIDNAME);
        if (fluidUUM == null)
            return;

        FluidStack fluidStackBlock = new FluidStack(fluidUUM, 4500);
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

        // Block casting
        basinCasting.addCastingRecipe(new ItemStack(Blocks.diamond_block), fluidStackBlock, new ItemStack(Blocks.dirt), true, 50);
    }

    @Override
    public void postInit ()
    {

    }

}
