package slimeknights.tconstruct.smeltery.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import lombok.AllArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialValues;

/** Capability handler instance for the copper can item */
@AllArgsConstructor
public class CopperCanFluidHandler {// implements IFluidHandlerItem, ICapabilityProvider {
/*  private final Optional<IFluidHandlerItem> holder = Optional.of(() -> this);

  @Getter
  private final ItemStack container;

  @Override
  public <T> Optional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);
  }*/


  /* Tank properties */

  public int getTanks() {
    return 1;
  }


  public boolean isFluidValid(int tank, FluidVolume stack) {
    return true;
  }


  public int getTankCapacity(int tank) {
    return MaterialValues.INGOT.asInt(1000);
  }

  /** Gets the contained fluid */
  private Fluid getFluid() {
    throw new RuntimeException("CRAB!"); // FIXME: PORT
//    return CopperCanItem.getFluid(container);
  }


  public FluidVolume getFluidInTank(int tank) {
    return FluidVolume.create(getFluid(), MaterialValues.INGOT.asInt(1000));
  }


  /* Interaction */

  public int fill(FluidVolume resource, Simulation action) {
    // must not be filled, must have enough
    if (getFluid() != Fluids.EMPTY || resource.getAmount() < MaterialValues.INGOT.asInt(1000)) {
      return 0;
    }
    // update fluid and return
    if (action.isAction()) {
      throw new RuntimeException("CRAB!"); // FIXME: PORT
//      CopperCanItem.setFluid(container, resource.getRawFluid());
    }
    return MaterialValues.INGOT.asInt(1000);
  }

//  @Override
  public FluidVolume drain(FluidVolume resource, Simulation action) {
    // must be draining at least an ingot
    if (resource.isEmpty() || resource.getAmount() < MaterialValues.INGOT.asInt(1000)) {
      return TinkerFluids.EMPTY;
    }
    // must have a fluid, must match what they are draining
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY || fluid != resource.getRawFluid()) {
      return TinkerFluids.EMPTY;
    }
    // output 1 ingot
    FluidVolume output = FluidVolume.create(fluid, MaterialValues.INGOT.asInt(1000));
    if (action.isAction()) {
      throw new RuntimeException("CRAB!"); // FIXME: PORT
//      CopperCanItem.setFluid(container, Fluids.EMPTY);
    }
    return output;
  }

  public FluidVolume drain(int maxDrain, Simulation action) {
    // must be draining at least an ingot
    if (maxDrain < MaterialValues.INGOT.asInt(1000)) {
      return TinkerFluids.EMPTY;
    }
    // must have a fluid
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY) {
      return TinkerFluids.EMPTY;
    }
    // output 1 ingot
    FluidVolume output = FluidVolume.create(fluid, MaterialValues.INGOT.asInt(1000));
    if (action.isAction()) {
//      CopperCanItem.setFluid(container, Fluids.EMPTY);
    }
    return output;
  }
}
