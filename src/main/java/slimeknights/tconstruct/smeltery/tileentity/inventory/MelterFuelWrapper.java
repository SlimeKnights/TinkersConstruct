package slimeknights.tconstruct.smeltery.tileentity.inventory;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.Simulation;
import slimeknights.tconstruct.library.recipe.fuel.IFluidInventory;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Fluid tank wrapper that weakly references a tank from a neighbor
 */
public class MelterFuelWrapper implements IFluidInventory {
  private final WeakReference<IFluidTank> tank;

  public MelterFuelWrapper(IFluidTank tank) {
    this.tank = new WeakReference<>(tank);
  }

  /**
   * Checks if this reference is still valid
   * @return  False if the stored tank is removed
   */
  public boolean isValid() {
    return this.tank.get() != null;
  }

  @Override
  public Fluid getFluid() {
    return Optional.ofNullable(tank.get())
                   .map(IFluidTank::getFluid)
                   .map(FluidVolume::getFluid)
                   .orElse(Fluids.EMPTY);
  }

  /* Melter methods */

  /**
   * Gets the contained fluid stack
   * @return  Contained fluid stack
   */
  public FluidVolume getFluidVolume() {
    return Optional.ofNullable(tank.get())
                   .map(IFluidTank::getFluid)
                   .orElse(TinkerFluids.EMPTY);
  }

  /**
   * Gets the capacity of the contained tank
   * @return  Tank capacity
   */
  public int getCapacity() {
    return Optional.ofNullable(tank.get())
                   .map(IFluidTank::getCapacity)
                   .orElse(0);
  }

  /**
   * Drains one copy of fuel from the given tank
   * @param fuel  Fuel to drain
   * @return  Ticks of fuel units
   */
  public int consumeFuel(MeltingFuel fuel) {
    IFluidTank tank = this.tank.get();
    if (tank != null) {
      int amount = fuel.getAmount(this);
      if (amount > 0) {
        // TODO: assert drained valid?
        int drained = tank.drain(amount, Simulation.EXECUTE).getAmount();
        int duration = fuel.getDuration();
        if (drained < amount) {
          return duration * drained / amount;
        } else {
          return duration;
        }
      }
    }
    return 0;
  }
}
