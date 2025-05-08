package slimeknights.tconstruct.smeltery.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handler that works with a tank item to adjust its tank in NBT
 */
@RequiredArgsConstructor
public class TankItemFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
  private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
  private final TankItem tankItem;
  @Getter
  private final ItemStack container;

  /** Gets the tank on the stack */
  private FluidTank getTank() {
    // TODO: can we directly use the nested tank as our fluid handler instead of doing this wrapper?
    // might be more efficient, though it may require validating the stack size/NBT did not change externally
    return tankItem.getTank(container);
  }

  /** Updates the container from the given tank */
  private void updateContainer(FluidTank tank) {
    TankItem.setTank(container, tank);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, holder);
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return getTank().getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return TankBlockEntity.getCapacity(container.getItem()) * container.getCount();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    FluidTank tank = getTank();
    int didFill = tank.fill(resource, action);
    if (didFill > 0 && action.execute()) {
      updateContainer(tank);
    }
    return didFill;
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    FluidTank tank = getTank();
    FluidStack didDrain = tank.drain(resource, action);
    if (!didDrain.isEmpty() && action.execute()) {
      updateContainer(tank);
    }
    return didDrain;
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidTank tank = getTank();
    FluidStack didDrain = tank.drain(maxDrain, action);
    if (!didDrain.isEmpty() && action.execute()) {
      updateContainer(tank);
    }
    return didDrain;
  }
}
