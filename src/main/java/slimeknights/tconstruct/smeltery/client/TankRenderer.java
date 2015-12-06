package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TankRenderer extends TileEntitySpecialRenderer<TileTank> {

  protected static Minecraft mc = Minecraft.getMinecraft();

  @Override
  public void renderTileEntityAt(TileTank te, double x, double y, double z, float partialTicks, int destroyStage) {
    if(te.containsFluid()) {
      FluidTankInfo info = te.getTankInfo(null)[0];
      FluidStack liquid = info.fluid;

      float height = ((float)liquid.amount - te.renderOffset) / (float)info.capacity;

      if(te.renderOffset > 1.2f || te.renderOffset < -1.2f) {
        te.renderOffset -= (te.renderOffset / 12f + 0.1f) * partialTicks;
      }
      else {
        te.renderOffset = 0;
      }

      float d = RenderUtil.FLUID_OFFSET;
      RenderUtil.renderFluidCuboid(liquid, te.getPos(), x,y,z, d, d, d, 1d-d, height-d, 1d-d);
    }
  }
}
