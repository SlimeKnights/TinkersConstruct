package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.block.entity.CastingTankBlockEntity;

import java.util.List;

public class CastingTankBlockEntityRenderer extends TankBlockEntityRenderer<CastingTankBlockEntity> {
  public CastingTankBlockEntityRenderer(Context context) {
    super(context);
  }

  @Override
  public void render(CastingTankBlockEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) { // TODO whats the deal with this
      return;
    }
    BlockState state = tile.getBlockState();
    List<FluidCuboid> fluids = FluidCuboid.REGISTRY.get(state, List.of());
    List<RenderItem> renderItems = RenderItem.REGISTRY.get(state.getBlock(), List.of());

    // rotate the matrix
    if (!fluids.isEmpty() || !renderItems.isEmpty()) {
      boolean isRotated = RenderingHelper.applyRotation(matrixStack, state);

      // render the fluid
      if (!fluids.isEmpty()) {
        FluidTankAnimated tank = tile.getTank();
        for (FluidCuboid fluid : fluids) {
          RenderUtils.renderFluidTank(matrixStack, buffer, fluid, tank, combinedLightIn, partialTicks, true);
        }
      }

      // render renderItems
      if (!renderItems.isEmpty()) {
        ItemStack inputItem = tile.getItem(CastingTankBlockEntity.INPUT);
        ItemStack outputItem = tile.getItem(CastingTankBlockEntity.OUTPUT);
        if (!inputItem.isEmpty()) {
          RenderingHelper.renderItem(matrixStack, buffer, inputItem, renderItems.get(0), combinedLightIn);
        } else if (!outputItem.isEmpty()) {
          RenderingHelper.renderItem(matrixStack, buffer, outputItem, renderItems.get(0), combinedLightIn);
        }
      }

      // pop back rotation
      if (isRotated) {
        matrixStack.popPose();
      }
    }
  }
}
