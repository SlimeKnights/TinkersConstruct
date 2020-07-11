package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.tesr.TankModel;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

@Log4j2
public class TankTileEntityRenderer extends TileEntityRenderer<TankTileEntity> {
  public TankTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(TankTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) {
      return;
    }
    // render the fluid
    TankModel.BakedModel model = RenderUtil.getBakedModel(tile.getBlockState(), TankModel.BakedModel.class);
    if (model != null) {
      RenderUtil.renderScaledCuboid(matrixStack, buffer, model.getFluid(), tile.getInternalTank(), combinedLightIn, partialTicks, true);
    }
  }
}
