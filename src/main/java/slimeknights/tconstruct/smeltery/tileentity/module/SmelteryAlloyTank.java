package slimeknights.tconstruct.smeltery.tileentity.module;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.tconstruct.library.recipe.alloying.IAlloyTank;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

/**
 * Smeltery implementation of the alloy tank, basically just a wrapper around the smeltery tank
 */
public class SmelteryAlloyTank implements IAlloyTank {
  /**
   * Handler parent
   */
  private final SmelteryTank handler;

  public SmelteryAlloyTank(SmelteryTank handler) {
    this.handler = handler;
  }

  /** Current temperature. Provided as a getter and setter as there are a few contexts with different source for temperature */
  private int temperature;

  @Override
  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

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
