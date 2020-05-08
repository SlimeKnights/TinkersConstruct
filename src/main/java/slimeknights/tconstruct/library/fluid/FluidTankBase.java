package slimeknights.tconstruct.library.fluid;

import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.tileentity.MantleTileEntity;

public class FluidTankBase<T extends MantleTileEntity> extends FluidTank {

  protected T parent;

  public FluidTankBase(int capacity, T parent) {
    super(capacity);
    this.parent = parent;
  }

  @Override
  protected void onContentsChanged() {
    if (parent instanceof IFluidTankUpdater) {
      ((IFluidTankUpdater) parent).onTankContentsChanged();
    }

    parent.markDirty();
  }
}
