package slimeknights.tconstruct.smeltery.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.client.model.block.CastingModel;
import slimeknights.tconstruct.smeltery.client.util.CastingItemRenderTypeBuffer;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.tank.CastingFluidHandler;
import java.util.List;

public class CastingTileEntityRenderer extends BlockEntityRenderer<CastingTileEntity> {
  public CastingTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher);
  }

  //@Override
  //public void render(CastingTileEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    //throw new RuntimeException("CRAB!"); // FIXME: PORT
  //}

  @Override
  public void render(CastingTileEntity casting, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int light, int combinedOverlayIn) {
    BlockState state = casting.getCachedState();

      // render fluids
      CastingFluidHandler tank = casting.getTank();
      // if full, start rendering with opacity for progress
      if (tank.getFluid().getAmount() == tank.getCapacity()) {
        RenderUtils.renderTransparentCuboid(matrices, buffer,new FluidCuboid() , tank.getFluid(), 1, light);
      } else {
        FluidRenderer.renderScaledCuboid(matrices, buffer, model.getFluid(), tank.getFluid(), 0, tank.getCapacity(), light, false);
      }

      // render items
      List<ModelItem> modelItems = model.getItems();
      // input is normal
      if (modelItems.size() >= 1) {
        RenderingHelper.renderItem(matrices, buffer, casting.getStack(0), modelItems.get(0), light);
      }

      // output may be the recipe output instead of the current item
      if (modelItems.size() >= 2) {
        ModelItem outputModel = modelItems.get(1);
        if(!outputModel.isHidden()) {
          // get output stack
          ItemStack output = casting.getStack(1);
          VertexConsumerProvider outputBuffer = buffer;
          if(itemOpacity > 0 && output.isEmpty()) {
            output = casting.getRecipeOutput();
            // apply a buffer wrapper to tint and add opacity
            outputBuffer = new CastingItemRenderTypeBuffer(buffer, itemOpacity, fluidOpacity);
          }
          RenderingHelper.renderItem(matrices, outputBuffer, output, outputModel, light);
        }
      }

      // pop back rotation
      if (isRotated) {
        matrices.pop();
      }
    }
  }
}
