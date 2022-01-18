package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.client.model.block.TankModel.Baked;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;

@Log4j2
public class TankBlockEntityRenderer<T extends BlockEntity & ITankBlockEntity> implements BlockEntityRenderer<T> {
  public TankBlockEntityRenderer(Context context) {}

  @Override
  public void render(T tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) {
      return;
    }
    // render the fluid
    Baked<?> model = ModelHelper.getBakedModel(tile.getBlockState(), Baked.class);
    if (model != null) {
      RenderUtils.renderFluidTank(matrixStack, buffer, model.getFluid(), tile.getTank(), combinedLightIn, partialTicks, true);
    }
  }
}
