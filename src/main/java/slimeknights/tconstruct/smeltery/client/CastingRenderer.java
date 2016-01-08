package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;

public class CastingRenderer<T extends TileCasting> extends TileEntitySpecialRenderer<T> {

  protected final float yMin;
  protected final float yMax;
  protected final float xzMin;
  protected final float xzMax;

  public CastingRenderer(float yMin, float yMax, float xzMin, float xzMax) {
    this.yMin = yMin;
    this.yMax = yMax;
    this.xzMin = xzMin;
    this.xzMax = xzMax;
  }

  @Override
  public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
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

    float yh = yMin + (yMax - yMin) * height;

    RenderUtil.renderFluidCuboid(te.tank.getFluid(), te.getPos(), x,y,z, xzMin, yMin, xzMin, xzMax, yh, xzMax);
  }

  public static class Table extends CastingRenderer<TileCastingTable> {

    public Table() {
      super(15/16f, 1f, 1/16f, 15/16f);
    }
  }

  public static class Basin extends CastingRenderer<TileCastingBasin> {

    public Basin() {
      super(4/16f, 1f, 2/16f, 14/16f);
    }
  }
}
