package slimeknights.tconstruct.library.fluid;

import net.minecraft.tileentity.TileEntity;

public class FluidTankAnimated extends FluidTankBase<TileEntity> {

  public float renderOffset;

  public FluidTankAnimated(int capacity, TileEntity parent) {
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
