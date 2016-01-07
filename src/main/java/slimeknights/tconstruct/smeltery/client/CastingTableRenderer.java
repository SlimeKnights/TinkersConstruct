package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;

public class CastingTableRenderer extends TileEntitySpecialRenderer<TileCastingTable> {

  @Override
  public void renderTileEntityAt(TileCastingTable te, double x, double y, double z, float partialTicks, int destroyStage) {
    if(te.tank.getFluidAmount() == 0) {
      return;
    }

    float height = ((float)te.tank.getFluidAmount() - te.renderOffset) / (float)te.tank.getCapacity();

    if(te.renderOffset > 1.2f || te.renderOffset < -1.2f) {
      te.renderOffset -= (te.renderOffset / 12f + 0.1f) * partialTicks;
    }
    else {
      te.renderOffset = 0;
    }

    float yMin = 15/16f;
    float yMax = yMin + height *  1/16f;
    float xzMin = 1/16f;
    float xzMax = 15/16f;

    RenderUtil.renderFluidCuboid(te.tank.getFluid(), te.getPos(), x,y,z, xzMin, yMin, xzMin, xzMax, yMax, xzMax);
  }
}
