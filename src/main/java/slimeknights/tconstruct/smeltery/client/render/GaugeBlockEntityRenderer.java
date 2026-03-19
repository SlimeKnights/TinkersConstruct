package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.tconstruct.smeltery.block.entity.GaugeBlockEntity;

import java.util.List;

/** Renderer for the obisidian gauge block */
public class GaugeBlockEntityRenderer implements BlockEntityRenderer<GaugeBlockEntity> {
  public GaugeBlockEntityRenderer(Context context) {}

  @Override
  public void render(GaugeBlockEntity tile, float pPartialTick, PoseStack matrices, MultiBufferSource buffer, int light, int pPackedOverlay) {
    List<FluidCuboid> fluids = FluidCuboid.REGISTRY.get(tile.getBlockState(), List.of());
    if (!fluids.isEmpty()) {
      IFluidHandler tank = tile.getTank();
      if (tank.getTanks() > 0) {
        FluidStack fluid = tank.getFluidInTank(0);
        if (!fluids.isEmpty()) {
          FluidRenderer.renderCuboids(matrices, buffer.getBuffer(MantleRenderTypes.FLUID), fluids, fluid, light);
        }
      }
    }
  }
}
