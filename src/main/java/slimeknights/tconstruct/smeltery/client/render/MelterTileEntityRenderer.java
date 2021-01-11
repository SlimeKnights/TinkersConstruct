package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.client.model.block.MelterModel;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

import java.util.List;

public class MelterTileEntityRenderer extends TileEntityRenderer<MelterTileEntity> {
  public MelterTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(MelterTileEntity melter, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int light, int combinedOverlayIn) {
    BlockState state = melter.getBlockState();
    MelterModel.BakedModel model = ModelHelper.getBakedModel(state, MelterModel.BakedModel.class);
    if (model != null) {
      // rotate the matrix
      boolean isRotated = RenderingHelper.applyRotation(matrices, state);

      // render fluids
      if (!Config.CLIENT.tankFluidModel.get()) {
        RenderUtils.renderFluidTank(matrices, buffer, model.getFluid(), melter.getTank(), light, partialTicks, false);
      }

      // render items
      List<ModelItem> modelItems = model.getItems();
      for (int i = 0; i < modelItems.size(); i++) {
        RenderingHelper.renderItem(matrices, buffer, melter.getMeltingInventory().getStackInSlot(i), modelItems.get(i), light);
      }

      // pop back rotation
      if (isRotated) {
        matrices.pop();
      }
    }
  }
}
