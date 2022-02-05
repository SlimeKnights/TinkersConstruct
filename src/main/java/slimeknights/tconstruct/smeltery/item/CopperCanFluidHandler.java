package slimeknights.tconstruct.smeltery.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.recipe.FluidValues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Capability handler instance for the copper can item */
@AllArgsConstructor
public class CopperCanFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
  private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

  @Getter
  private final ItemStack container;

  @Nonnull
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
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  @Override
  public int getTankCapacity(int tank) {
    return FluidValues.INGOT;
  }

  /** Gets the contained fluid */
  private Fluid getFluid() {
    return CopperCanItem.getFluid(container);
  }

  /** Gets the contained fluid */
  @Nullable
  private CompoundTag getFluidTag() {
    return CopperCanItem.getFluidTag(container);
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return new FluidStack(getFluid(), FluidValues.INGOT, getFluidTag());
  }


  /* Interaction */

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    // must not be filled, must have enough
    if (getFluid() != Fluids.EMPTY || resource.getAmount() < FluidValues.INGOT) {
      return 0;
    }
    // update fluid and return
    if (action.execute()) {
      CopperCanItem.setFluid(container, resource);
    }
    return FluidValues.INGOT;
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    // must be draining at least an ingot
    if (resource.isEmpty() || resource.getAmount() < FluidValues.INGOT) {
      return FluidStack.EMPTY;
    }
    // must have a fluid, must match what they are draining
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY || fluid != resource.getFluid()) {
      return FluidStack.EMPTY;
    }
    // output 1 ingot
    FluidStack output = new FluidStack(fluid, FluidValues.INGOT, getFluidTag());
    if (action.execute()) {
      CopperCanItem.setFluid(container, FluidStack.EMPTY);
    }
    return output;
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    // must be draining at least an ingot
    if (maxDrain < FluidValues.INGOT) {
      return FluidStack.EMPTY;
    }
    // must have a fluid
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY) {
      return FluidStack.EMPTY;
    }
    // output 1 ingot
    FluidStack output = new FluidStack(fluid, FluidValues.INGOT, getFluidTag());
    if (action.execute()) {
      CopperCanItem.setFluid(container, FluidStack.EMPTY);
    }
    return output;
  }
}
