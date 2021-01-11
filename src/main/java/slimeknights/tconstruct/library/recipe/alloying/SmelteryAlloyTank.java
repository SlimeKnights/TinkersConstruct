package slimeknights.tconstruct.library.recipe.alloying;

import lombok.AllArgsConstructor;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

/** Smeltery implementation of the alloy tank, basically just a wrapper around the smeltery tank */
@AllArgsConstructor
public class SmelteryAlloyTank implements IAlloyTank {
  /** Handler parent */
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
  public int getRemainingSpace() {
    return handler.getRemainingSpace();
  }
}
