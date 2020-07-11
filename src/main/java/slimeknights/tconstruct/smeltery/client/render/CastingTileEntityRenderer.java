package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.data.ModelItem;
import slimeknights.tconstruct.library.client.model.tesr.CastingModel;
import slimeknights.tconstruct.smeltery.tileentity.AbstractCastingTileEntity;

import java.util.List;

public class CastingTileEntityRenderer extends TileEntityRenderer<AbstractCastingTileEntity> {
  public CastingTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(AbstractCastingTileEntity casting, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int light, int combinedOverlayIn) {
    BlockState state = casting.getBlockState();
    CastingModel.BakedModel model = RenderUtil.getBakedModel(state, CastingModel.BakedModel.class);
    if (model != null) {
      // rotate the matrix
      boolean isRotated = RenderUtil.applyRotation(matrices, state);

      // render fluids
      RenderUtil.renderScaledCuboid(matrices, buffer, model.getFluid(), casting.getTank(), light, partialTicks, false);

      // render items if near enough
      // TODO: progress animation
      List<ModelItem> modelItems = model.getItems();
      for (int i = 0; i < modelItems.size(); i++) {
        RenderUtil.renderItem(matrices, buffer, casting.getStackInSlot(i), modelItems.get(i), light);
      }

      // pop back rotation
      if (isRotated) {
        matrices.pop();
      }
    }
  }
}
