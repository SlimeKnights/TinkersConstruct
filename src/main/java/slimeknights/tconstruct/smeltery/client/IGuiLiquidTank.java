package slimeknights.tconstruct.smeltery.client;

import net.minecraftforge.fluids.FluidStack;

public interface IGuiLiquidTank {
  FluidStack getFluidStackAtPosition(int mouseX, int mouseY);
}
