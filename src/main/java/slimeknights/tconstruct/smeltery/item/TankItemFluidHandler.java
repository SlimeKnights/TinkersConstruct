package slimeknights.tconstruct.smeltery.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.tconstruct.smeltery.tileentity.component.TankTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Handler that works with a tank item to adjust its tank in NBT
 */
@RequiredArgsConstructor
public class TankItemFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
  private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
  @Getter
  private final ItemStack container;

  /** Gets the tank on the stack */
  private FluidTank getTank() {
    return TankItem.getFluidTank(container);
  }

  /** Updates the container from the given tank */
  private void updateContainer(FluidTank tank) {
    TankItem.setTank(container, tank);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);
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
    return TankTileEntity.getCapacity(container.getItem());
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
