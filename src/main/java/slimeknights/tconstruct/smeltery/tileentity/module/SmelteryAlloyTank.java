package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.tconstruct.library.recipe.alloying.IAlloyTank;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

/**
 * Smeltery implementation of the alloy tank, basically just a wrapper around the smeltery tank
 */
@RequiredArgsConstructor
public class SmelteryAlloyTank implements IAlloyTank {
  /**
   * Handler parent
   */
  private final SmelteryTank handler;
  /** Current temperature. Provided as a getter and setter as there are a few contexts with different source for temperature */
  @Getter @Setter
  private int temperature;

  @Override
  public int getTanks() {
    return handler.getTanks();
  }

  @Override
  public FluidVolume getFluidInTank(int tank) {
    return handler.getFluidInTank(tank);
  }

  @Override
  public boolean canFit(FluidVolume fluid, int removed) {
    // the fluid fits if the net gain in fluid fits in the empty space
    return (fluid.getAmount() - removed) <= handler.getRemainingSpace();
  }
}
