package slimeknights.tconstruct.smeltery.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.client.model.block.MelterModel;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

import java.util.List;

public class MelterTileEntityRenderer extends BlockEntityRenderer<MelterTileEntity> {
  public MelterTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(MelterTileEntity melter, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int light, int combinedOverlayIn) {
    BlockState state = melter.getCachedState();
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
