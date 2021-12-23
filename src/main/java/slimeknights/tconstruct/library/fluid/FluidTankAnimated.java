package slimeknights.tconstruct.library.fluid;

import slimeknights.mantle.tileentity.MantleTileEntity;

public class FluidTankAnimated extends FluidTankBase<MantleTileEntity> {

  public float renderOffset;

  public FluidTankAnimated(int capacity, MantleTileEntity parent) {
    super(capacity, parent);
  }

  @Override
  protected void sendUpdate(int amount) {
    if(amount != 0) {
      renderOffset += amount;
      super.sendUpdate(amount);
    }
  }
}
