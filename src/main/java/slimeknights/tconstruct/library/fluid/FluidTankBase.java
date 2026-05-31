package slimeknights.tconstruct.library.fluid;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class FluidTankBase<T extends MantleBlockEntity> extends FluidTank {

  protected T parent;

  public FluidTankBase(int capacity, T parent) {
    super(capacity);
    this.parent = parent;
  }

  // override to fix bug with onContentsChanged during fill
  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !isFluidValid(resource)) {
      return 0;
    }
    if (action.simulate()) {
      if (fluid.isEmpty()) {
        return Math.min(capacity, resource.getAmount());
      }
      if (!fluid.isFluidEqual(resource)) {
        return 0;
      }
      return Math.min(capacity - fluid.getAmount(), resource.getAmount());
    }
    if (fluid.isEmpty()) {
      // FIX: the Forge implementation returns fluid.getAmount() here, which may be wrong if the fluid gets changed during onContentsChanged()
      // we instead use a local variable for the amount filled to guarantee its accurate
      int filled = Math.min(capacity, resource.getAmount());
      fluid = resource.copyWithAmount(filled);
      onContentsChanged();
      return filled;
    }
    if (!fluid.isFluidEqual(resource)) {
      return 0;
    }
    int filled = capacity - fluid.getAmount();

    if (resource.getAmount() < filled) {
      fluid.grow(resource.getAmount());
      filled = resource.getAmount();
    } else {
      fluid.setAmount(capacity);
    }
    if (filled > 0) {
      onContentsChanged();
    }
    return filled;
  }

  @Override
  public void onContentsChanged() {
    if (parent instanceof IFluidTankUpdater) {
      ((IFluidTankUpdater) parent).onTankContentsChanged();
    }

    parent.setChanged();
    Level level = parent.getLevel();
    if(level != null && !level.isClientSide) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(parent.getBlockPos(), this.getFluid()), level, parent.getBlockPos());
    }
  }
}
