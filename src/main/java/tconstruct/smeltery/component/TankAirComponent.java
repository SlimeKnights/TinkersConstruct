package tconstruct.smeltery.component;

import java.util.ArrayList;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.component.MultiFluidTank;

public class TankAirComponent extends MultiFluidTank
{
    public TankAirComponent()
    {
    }

    public TankAirComponent(int i)
    {
        super(i);
    }

    public void overrideFluids (ArrayList<FluidStack> fluids)
    {
        fluidlist = fluids;
    }
}