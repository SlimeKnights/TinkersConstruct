package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.data.ModelItem;
import slimeknights.tconstruct.library.client.model.tesr.CastingModel;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.client.util.CastingItemRenderTypeBuffer;
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

      // if the recipe is in progress, start fading the item away
      int timer = casting.getTimer();
      int itemOpacity = 0;
      int fluidOpacity = 0xFF;
      if (timer > 0) {
        int totalTime = casting.getRecipeTime();
        int opacity = (4 * 0xFF) * timer / totalTime;
        // fade item in
        itemOpacity = opacity / 4;

        // fade fluid and temperature out during last 10%
        if (opacity > 3 * 0xFF) {
          fluidOpacity = (4 * 0xFF) - opacity;
        } else {
          fluidOpacity = 0xFF;
        }
      }

      // render fluids
      FluidTankAnimated tank = casting.getTank();
      // if full, start rendering with opacity for progress
      if (tank.getFluidAmount() == tank.getCapacity() && tank.getRenderOffset() == 0) {
        RenderUtil.renderTransparentCuboid(matrices, buffer, model.getFluid(), tank.getFluid(), fluidOpacity, light, false);
      } else {
        RenderUtil.renderScaledCuboid(matrices, buffer, model.getFluid(), tank, light, partialTicks, false);
      }

      // render items
      List<ModelItem> modelItems = model.getItems();
      // input is normal
      if (modelItems.size() >= 1) {
        RenderUtil.renderItem(matrices, buffer, casting.getStackInSlot(0), modelItems.get(0), light);
      }

      // output may be the recipe output instead of the current item
      if (modelItems.size() >= 2) {
        ModelItem outputModel = modelItems.get(1);
        if(!outputModel.isEmpty()) {
          // get output stack
          ItemStack output = casting.getStackInSlot(1);
          IRenderTypeBuffer outputBuffer = buffer;
          if(itemOpacity > 0 && output.isEmpty()) {
            output = casting.getRecipeOutput();
            // apply a buffer wrapper to tint and add opacity
            outputBuffer = new CastingItemRenderTypeBuffer(buffer, itemOpacity, fluidOpacity);
          }
          RenderUtil.renderItem(matrices, outputBuffer, output, outputModel, light);
        }
      }

      // pop back rotation
      if (isRotated) {
        matrices.pop();
      }
    }
  }
}
