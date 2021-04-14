package slimeknights.tconstruct.smeltery.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

public class MelterTileEntityRenderer extends BlockEntityRenderer<MelterTileEntity> {
  public MelterTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(MelterTileEntity melter, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int light, int combinedOverlayIn) {
    throw new RuntimeException("CRAB!");
//    BlockState state = melter.getCachedState();
//    MelterModel.BakedModel model = ModelHelper.getBakedModel(state, MelterModel.BakedModel.class);
//    if (model != null) {
//      // rotate the matrix
//      boolean isRotated = RenderingHelper.applyRotation(matrices, state);
//
//      // render fluids
//      if (!TConfig.client.tankFluidModel) {
//        RenderUtils.renderFluidTank(matrices, buffer, model.getFluid(), melter.getTank(), light, partialTicks, false);
//      }
//
//      // render items
//      List<ModelItem> modelItems = model.getItems();
//      for (int i = 0; i < modelItems.size(); i++) {
//        RenderingHelper.renderItem(matrices, buffer, melter.getMeltingInventory().getStackInSlot(i), modelItems.get(i), light);
//      }
//
//      // pop back rotation
//      if (isRotated) {
//        matrices.pop();
//      }
//    }
  }
}
