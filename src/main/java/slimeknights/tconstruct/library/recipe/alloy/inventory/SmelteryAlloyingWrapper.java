package slimeknights.tconstruct.library.recipe.alloy.inventory;

import lombok.AllArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;

import java.util.List;

@AllArgsConstructor
public class SmelteryAlloyingWrapper implements IAlloyInventory {
  private final SmelteryTank tank;
  @Override
  public List<FluidStack> getFluidStacks() {
    return tank.getFluids();
  }

  @Override
  public Fluid getFluid() {
    return Fluids.EMPTY;
  }
}
