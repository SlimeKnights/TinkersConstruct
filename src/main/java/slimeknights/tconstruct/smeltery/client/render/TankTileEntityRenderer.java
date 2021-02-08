package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.client.model.block.TankModel;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

@Log4j2
public class TankTileEntityRenderer<T extends TileEntity & ITankTileEntity> extends TileEntityRenderer<T> {

  public TankTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(T tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) {
      return;
    }
    // render the fluid
    TankModel.BakedModel<?> model = ModelHelper.getBakedModel(tile.getBlockState(), TankModel.BakedModel.class);
    if (model != null) {
      RenderUtils.renderFluidTank(matrixStack, buffer, model.getFluid(), tile.getTank(), combinedLightIn, partialTicks, true);
    }
  }
}
