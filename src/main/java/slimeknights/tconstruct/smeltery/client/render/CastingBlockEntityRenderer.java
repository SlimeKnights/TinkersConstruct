package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.tank.CastingFluidHandler;
import slimeknights.tconstruct.smeltery.client.util.CastingItemRenderTypeBuffer;

import java.util.List;

public class CastingBlockEntityRenderer implements BlockEntityRenderer<CastingBlockEntity> {
  public CastingBlockEntityRenderer(Context context) {}

  @Override
  public void render(CastingBlockEntity casting, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlayIn) {
    BlockState state = casting.getBlockState();
    List<FluidCuboid> fluids = FluidCuboid.REGISTRY.get(state, List.of());
    List<RenderItem> renderItems = RenderItem.STATE_REGISTRY.get(state, List.of());

    // rotate the matrix
    if (!fluids.isEmpty() || !renderItems.isEmpty()) {
      boolean isRotated = RenderingHelper.applyRotation(matrices, state);

      // if the recipe is in progress, start fading the item away
      int timer = casting.getTimer();
      int totalTime = casting.getCoolingTime();
      int itemOpacity = 0;
      int fluidOpacity = 0xFF;
      if (timer > 0 && totalTime > 0) {
        int opacity = (4 * 0xFF) * timer / totalTime;
        // fade item in
        itemOpacity = opacity / 4;

        // fade fluid and temperature out during last 10%
        if (opacity > 3 * 0xFF) {
          fluidOpacity = (4 * 0xFF) - opacity;
        }
      }

      // render fluids
      if (!fluids.isEmpty()) {
        CastingFluidHandler tank = casting.getTank();
        FluidStack fluidStack = tank.getFluid();
        int capacity = tank.getCapacity();
        // if full, start rendering with opacity for progress
        if (fluidStack.getAmount() == capacity) {
          for (FluidCuboid fluid : fluids) {
            RenderUtils.renderTransparentCuboid(matrices, buffer, fluid, fluidStack, fluidOpacity, light);
          }
        } else {
          // not strictly useful to scale the fluids down, but who knows what the modeler does
          for (FluidCuboid fluid : fluids) {
            FluidRenderer.renderScaledCuboid(matrices, buffer, fluid, fluidStack, 0, capacity, light, false);
          }
        }
      }

      // render renderItems
      if (!renderItems.isEmpty()) {
        // render renderItems
        // input is normal
        RenderingHelper.renderItem(matrices, buffer, casting.getItem(0), renderItems.get(0), light);

        // output may be the recipe output instead of the current item
        if (renderItems.size() >= 2) {
          RenderItem outputModel = renderItems.get(1);
          if (!outputModel.isHidden()) {
            // get output stack
            ItemStack output = casting.getItem(1);
            MultiBufferSource outputBuffer = buffer;
            if (itemOpacity > 0 && output.isEmpty()) {
              output = casting.getRecipeOutput();
              // apply a buffer wrapper to tint and add opacity
              outputBuffer = new CastingItemRenderTypeBuffer(buffer, itemOpacity, fluidOpacity);
            }
            RenderingHelper.renderItem(matrices, outputBuffer, output, outputModel, light);
          }
        }
      }

      // pop back rotation
      if (isRotated) {
        matrices.popPose();
      }
    }
  }
}
