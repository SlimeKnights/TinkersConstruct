package slimeknights.tconstruct.smeltery.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

public class CastingTileEntityRenderer extends BlockEntityRenderer<CastingTileEntity> {
  public CastingTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(CastingTileEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    throw new RuntimeException("CRAB!"); // FIXME: PORT
  }

//  @Override
//  public void render(CastingTileEntity casting, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int light, int combinedOverlayIn) {
//    BlockState state = casting.getCachedState();
//    CastingModel.BakedModel model = ModelHelper.getBakedModel(state, CastingModel.BakedModel.class);
//    if (model != null) {
//      // rotate the matrix
//      boolean isRotated = RenderingHelper.applyRotation(matrices, state);
//
//      // if the recipe is in progress, start fading the item away
//      int timer = casting.getTimer();
//      int itemOpacity = 0;
//      int fluidOpacity = 0xFF;
//      if (timer > 0) {
//        int totalTime = casting.getRecipeTime();
//        int opacity = (4 * 0xFF) * timer / totalTime;
//        // fade item in
//        itemOpacity = opacity / 4;
//
//        // fade fluid and temperature out during last 10%
//        if (opacity > 3 * 0xFF) {
//          fluidOpacity = (4 * 0xFF) - opacity;
//        } else {
//          fluidOpacity = 0xFF;
//        }
//      }
//
//      // render fluids
//      CastingFluidHandler tank = casting.getTank();
//      // if full, start rendering with opacity for progress
//      if (tank.getFluid().getAmount() == tank.getCapacity()) {
//        RenderUtils.renderTransparentCuboid(matrices, buffer, model.getFluid(), tank.getFluid(), fluidOpacity, light);
//      } else {
//        FluidRenderer.renderScaledCuboid(matrices, buffer, model.getFluid(), tank.getFluid(), 0, tank.getCapacity(), light, false);
//      }
//
//      // render items
//      List<ModelItem> modelItems = model.getItems();
//      // input is normal
//      if (modelItems.size() >= 1) {
//        RenderingHelper.renderItem(matrices, buffer, casting.getStack(0), modelItems.get(0), light);
//      }
//
//      // output may be the recipe output instead of the current item
//      if (modelItems.size() >= 2) {
//        ModelItem outputModel = modelItems.get(1);
//        if(!outputModel.isHidden()) {
//          // get output stack
//          ItemStack output = casting.getStack(1);
//          VertexConsumerProvider outputBuffer = buffer;
//          if(itemOpacity > 0 && output.isEmpty()) {
//            output = casting.getRecipeOutput();
//            // apply a buffer wrapper to tint and add opacity
//            outputBuffer = new CastingItemRenderTypeBuffer(buffer, itemOpacity, fluidOpacity);
//          }
//          RenderingHelper.renderItem(matrices, outputBuffer, output, outputModel, light);
//        }
//      }
//
//      // pop back rotation
//      if (isRotated) {
//        matrices.pop();
//      }
//    }
//  }
}
