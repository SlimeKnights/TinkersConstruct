package slimeknights.tconstruct.library.fluid;

import slimeknights.mantle.tileentity.MantleTileEntity;

public class FluidTankAnimated extends FluidTankBase<MantleTileEntity> {
  public float renderOffset;

  public FluidTankAnimated(int capacity, MantleTileEntity parent) {
    super(capacity, parent);
  }
}
