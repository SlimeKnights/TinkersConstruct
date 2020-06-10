package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

public class TankTileEntityRenderer extends TileEntityRenderer<TankTileEntity> {

  public TankTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(TankTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    FluidTankAnimated tank = tile.getInternalTank();
    FluidStack liquid = tank.getFluid();

    if (!liquid.isEmpty()) {
      float height = (liquid.getAmount() - tank.getRenderOffset()) / tank.getCapacity();

      if (tank.getRenderOffset() > 1.2f || tank.getRenderOffset() < -1.2f) {
        tank.setRenderOffset(tank.getRenderOffset() - ((tank.getRenderOffset() / 12f + 0.1f) * partialTicks));
      } else {
        tank.setRenderOffset(0);
      }

      IVertexBuilder builder = bufferIn.getBuffer(RenderUtil.getBlockRenderType());

      float d = RenderUtil.FLUID_OFFSET;

      if (liquid.getFluid().getAttributes().isGaseous(liquid)) {
        RenderUtil.renderFluidCuboid(liquid, matrixStackIn, builder, combinedLightIn, d, 1f - (d + height), d, 1f - d, 1f - d, 1f - d);
      } else {
        RenderUtil.renderFluidCuboid(liquid, matrixStackIn, builder, combinedLightIn, d, d, d, 1f - d, height - d, 1f - d);
      }
    }
  }
}
