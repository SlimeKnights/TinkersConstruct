package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TankRenderer extends TileEntitySpecialRenderer<TileTank> {

  protected static Minecraft mc = Minecraft.getMinecraft();

  @Override
  public void render(@Nonnull TileTank tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    FluidTankAnimated tank = tile.getInternalTank();
    FluidStack liquid = tank.getFluid();

    if(liquid != null) {

      float height = (liquid.amount - tank.renderOffset) / tank.getCapacity();

      if(tank.renderOffset > 1.2f || tank.renderOffset < -1.2f) {
        tank.renderOffset -= (tank.renderOffset / 12f + 0.1f) * partialTicks;
      }
      else {
        tank.renderOffset = 0;
      }

      float d = RenderUtil.FLUID_OFFSET;

      if(liquid.getFluid().isGaseous(liquid)) {
        RenderUtil.renderFluidCuboid(liquid, tile.getPos(), x, y, z, d, 1f - (d + height), d, 1d - d, 1d - d, 1d - d);
      } else {
        RenderUtil.renderFluidCuboid(liquid, tile.getPos(), x, y, z, d, d, d, 1d - d, height - d, 1d - d);
      }
    }
  }
}
