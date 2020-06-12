package slimeknights.tconstruct.fluids.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.fluids.TinkerFluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidMilkBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {
  private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

  @Nonnull
  protected ItemStack container;
  public FluidMilkBucketWrapper(@Nonnull ItemStack container) {
    this.container = container;
  }

  @Nonnull
  @Override
  public ItemStack getContainer() {
    return container;
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Nonnull
  private FluidStack getFluid() {
    return new FluidStack(TinkerFluids.milk.get(), FluidAttributes.BUCKET_VOLUME);
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return FluidAttributes.BUCKET_VOLUME;
  }

  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0;
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.getFluid() != TinkerFluids.milk.get()) {
      return FluidStack.EMPTY;
    }
    return drain(resource.getAmount(), action);
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain < FluidAttributes.BUCKET_VOLUME) {
      return FluidStack.EMPTY;
    }
    if (action.execute()) {
      container = new ItemStack(Items.BUCKET);
    }
    return getFluid();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
    return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, holder);
  }
}
