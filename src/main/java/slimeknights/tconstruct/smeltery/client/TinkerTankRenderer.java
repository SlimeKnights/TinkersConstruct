package slimeknights.tconstruct.smeltery.client;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class TinkerTankRenderer extends SmelteryTankRenderer<TileTinkerTank> {

  @Override
  public void renderTileEntityAt(@Nonnull TileTinkerTank tinkerTank, double x, double y, double z, float partialTicks, int destroyStage) {
    if(!tinkerTank.isActive()) {
      return;
    }

    // safety first!
    if(tinkerTank.minPos == null || tinkerTank.maxPos == null) {
      return;
    }

    renderFluids(tinkerTank.getTank(), tinkerTank.getPos(), tinkerTank.minPos, tinkerTank.maxPos, x, y, z);
  }
}
