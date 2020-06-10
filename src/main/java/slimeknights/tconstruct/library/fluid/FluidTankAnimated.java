package slimeknights.tconstruct.library.fluid;

import lombok.Getter;
import lombok.Setter;
import slimeknights.mantle.tileentity.MantleTileEntity;

public class FluidTankAnimated extends FluidTankBase<MantleTileEntity> {
  @Getter @Setter
  private float renderOffset;

  public FluidTankAnimated(int capacity, MantleTileEntity parent) {
    super(capacity, parent);
  }
}
