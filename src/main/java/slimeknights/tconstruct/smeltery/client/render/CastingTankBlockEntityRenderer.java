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
    BlockState state = tile.getBlockState();
    List<FluidCuboid> fluids = FluidCuboid.REGISTRY.get(state, List.of());
    List<RenderItem> renderItems = RenderItem.STATE_REGISTRY.get(state, List.of());

    // rotate the matrix
    boolean isRotated = RenderingHelper.applyRotation(matrixStack, state);

    // render the fluid
    if (!fluids.isEmpty()) {
      FluidTankAnimated tank = tile.getTank();
      for (FluidCuboid fluid : fluids) {
        RenderUtils.renderFluidTank(matrixStack, buffer, fluid, tank, combinedLightIn, partialTicks, true);
      }
    }

    // render renderItems
    for (int i = 0; i < renderItems.size(); i++) {
      RenderingHelper.renderItem(matrixStack, buffer, tile.getItemHandler().getStackInSlot(i), renderItems.get(i), combinedLightIn);
    }

    // pop back rotation
    if (isRotated) {
      matrixStack.popPose();
    }
  }
}
