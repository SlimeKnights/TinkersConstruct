package slimeknights.tconstruct.library.fluid;

import slimeknights.mantle.tileentity.MantleTileEntity;

public class FluidTankAnimated extends FluidTankBase<MantleTileEntity> {
  private float renderOffset;

  public FluidTankAnimated(int capacity, MantleTileEntity parent) {
    super(capacity, parent);
  }

  public float getRenderOffset() {
    return renderOffset;
  }

  public void setRenderOffset(float renderOffset) {
    this.renderOffset = renderOffset;
  }
}
