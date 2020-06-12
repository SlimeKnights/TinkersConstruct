package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.TankModel;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

@Log4j2
public class TankTileEntityRenderer extends TileEntityRenderer<TankTileEntity> {
  private static final float FROM_SCALED = RenderUtil.FLUID_OFFSET * 16;
  private static final float TO_SCALED = 16 - FROM_SCALED;
  private static final Vector3f FROM_FALLBACK = new Vector3f(FROM_SCALED, FROM_SCALED, FROM_SCALED);
  private static final Vector3f TO_FALLBACK = new Vector3f(TO_SCALED, TO_SCALED, TO_SCALED);

  public TankTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(TankTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    if (Config.CLIENT.tankFluidModel.get()) {
      return;
    }
    FluidTankAnimated tank = tile.getInternalTank();
    FluidStack liquid = tank.getFluid();

    if (!liquid.isEmpty()) {
      float height = (liquid.getAmount() - tank.getRenderOffset()) / tank.getCapacity();

      if (tank.getRenderOffset() > 1.2f || tank.getRenderOffset() < -1.2f) {
        tank.setRenderOffset(tank.getRenderOffset() - ((tank.getRenderOffset() / 12f + 0.1f) * partialTicks));
      } else {
        tank.setRenderOffset(0);
      }

      IVertexBuilder builder = bufferIn.getBuffer(RenderUtil.getBlockRenderType());

      // determine where to draw the fluid based on the model
      TankModel.BakedModel model = RenderUtil.getBakedModel(tile.getBlockState().getBlock(), TankModel.BakedModel.class);
      Vector3f from = FROM_FALLBACK, to = TO_FALLBACK;
      if (model != null) {
        from = model.getFrom();
        to = model.getTo();
      }

      // gas renders upside down
      float minY = from.getY();
      float maxY = to.getY();
      float fromY, toY;
      if (liquid.getFluid().getAttributes().isGaseous(liquid)) {
        toY = maxY;
        fromY = maxY + (minY - maxY) * height;
      } else {
        fromY = minY;
        toY = minY + (maxY - minY) * height;
      }
      RenderUtil.renderScaledFluidCuboid(liquid, matrixStackIn, builder, combinedLightIn, from.getX(), fromY, from.getZ(), to.getX(), toY, to.getZ());
    }
  }
}
