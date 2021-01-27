package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.AllArgsConstructor;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.alloying.IAlloyTank;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

/**
 * Smeltery implementation of the alloy tank, basically just a wrapper around the smeltery tank
 */
@AllArgsConstructor
public class SmelteryAlloyTank implements IAlloyTank {
  /**
   * Handler parent
   */
  private final SmelteryTank handler;

  @Override
  public int getTanks() {
    return handler.getTanks();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return handler.getFluidInTank(tank);
  }

  @Override
  public boolean canFit(FluidStack fluid, int removed) {
    // the fluid fits if the net gain in fluid fits in the empty space
    return (fluid.getAmount() - removed) <= handler.getRemainingSpace();
  }
}
