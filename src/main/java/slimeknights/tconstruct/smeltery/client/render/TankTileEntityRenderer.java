package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.tesr.TankModel;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

@Log4j2
public class TankTileEntityRenderer extends TileEntityRenderer<TankTileEntity> {
  public TankTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(TankTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) {
      return;
    }
    FluidTankAnimated tank = tile.getInternalTank();
    FluidStack liquid = tank.getFluid();

    if (!liquid.isEmpty()) {
      // update render offset
      if (tank.getRenderOffset() > 1.2f || tank.getRenderOffset() < -1.2f) {
        tank.setRenderOffset(tank.getRenderOffset() - ((tank.getRenderOffset() / 12f + 0.1f) * partialTicks));
      } else {
        tank.setRenderOffset(0);
      }

      // fetch fluid information from the model
      TankModel.BakedModel model = RenderUtil.getBakedModel(tile.getBlockState(), TankModel.BakedModel.class);
      if (model != null) {
        float height = (liquid.getAmount() - tank.getRenderOffset()) / tank.getCapacity();
        RenderUtil.renderScaledCuboid(matrixStackIn, bufferIn, model.getFluid(), liquid, height, combinedLightIn, true);
      }
    }
  }
}
