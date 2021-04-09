package slimeknights.tconstruct.smeltery.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.materials.MaterialValues;

import org.jetbrains.annotations.Nullable;

/** Capability handler instance for the copper can item */
@AllArgsConstructor
public class CopperCanFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
  private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

  @Getter
  private final ItemStack container;

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);
  }


  /* Tank properties */

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public boolean isFluidValid(int tank, FluidVolume stack) {
    return true;
  }

  @Override
  public int getTankCapacity(int tank) {
    return MaterialValues.INGOT;
  }

  /** Gets the contained fluid */
  private Fluid getFluid() {
    return CopperCanItem.getFluid(container);
  }

  @Override
  public FluidVolume getFluidInTank(int tank) {
    return new FluidVolume(getFluid(), MaterialValues.INGOT);
  }


  /* Interaction */

  @Override
  public int fill(FluidVolume resource, FluidAction action) {
    // must not be filled, must have enough
    if (getFluid() != Fluids.EMPTY || resource.getAmount() < MaterialValues.INGOT) {
      return 0;
    }
    // update fluid and return
    if (action.execute()) {
      CopperCanItem.setFluid(container, resource.getFluid());
    }
    return MaterialValues.INGOT;
  }

  @Override
  public FluidVolume drain(FluidVolume resource, FluidAction action) {
    // must be draining at least an ingot
    if (resource.isEmpty() || resource.getAmount() < MaterialValues.INGOT) {
      return TinkerFluids.EMPTY;
    }
    // must have a fluid, must match what they are draining
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY || fluid != resource.getFluid()) {
      return TinkerFluids.EMPTY;
    }
    // output 1 ingot
    FluidVolume output = new FluidVolume(fluid, MaterialValues.INGOT);
    if (action.execute()) {
      CopperCanItem.setFluid(container, Fluids.EMPTY);
    }
    return output;
  }

  @Override
  public FluidVolume drain(int maxDrain, FluidAction action) {
    // must be draining at least an ingot
    if (maxDrain < MaterialValues.INGOT) {
      return TinkerFluids.EMPTY;
    }
    // must have a fluid
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY) {
      return TinkerFluids.EMPTY;
    }
    // output 1 ingot
    FluidVolume output = new FluidVolume(fluid, MaterialValues.INGOT);
    if (action.execute()) {
      CopperCanItem.setFluid(container, Fluids.EMPTY);
    }
    return output;
  }
}
