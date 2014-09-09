package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.CastingRecipe;

/**
 * Fires when somebody tries to pour a liquid into a metal cast.
 *
 * Set result to DENY to prevent casting.
 */
public abstract class SmelteryCastEvent extends Event
{
    public final CastingRecipe recipe;
    public final FluidStack fluid;

    public SmelteryCastEvent(CastingRecipe recipe, FluidStack fluid)
    {
        this.recipe = recipe;
        this.fluid = fluid;
    }

    public static class CastingTable extends SmelteryCastEvent
    {
        public CastingTable(CastingRecipe recipe, FluidStack fluid)
        {
            super(recipe, fluid);
        }
    }

    public static class CastingBasin extends SmelteryCastEvent
    {
        public CastingBasin(CastingRecipe recipe, FluidStack fluid)
        {
            super(recipe, fluid);
        }
    }
}
