package tconstruct.library;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ColorFluid extends Fluid
{
    public final int color;
    public ColorFluid(String fluidName, int color)
    {
        super(fluidName);
        this.color = color;
    }
    
    public int getColor ()
    {
        return color;
    }
    
    public int getColor (FluidStack stack)
    {
        return getColor();
    }
}
