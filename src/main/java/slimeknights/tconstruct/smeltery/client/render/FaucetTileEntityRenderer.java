package slimeknights.tconstruct.smeltery.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.client.model.FaucetFluidLoader;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.fluid.FluidsModel;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

import java.util.function.Function;

public class FaucetTileEntityRenderer extends BlockEntityRenderer<FaucetTileEntity> {
  public FaucetTileEntityRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(FaucetTileEntity tileEntity, float partialTicks, MatrixStack matrices, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
    FluidVolume renderFluid = tileEntity.getRenderFluid();
    if (!tileEntity.isPouring() || renderFluid.isEmpty()) {
      return;
    }

    // safety
    World world = tileEntity.getWorld();
    if (world == null) {
      return;
    }

    // fetch faucet model to determine where to render fluids
    BlockState state = tileEntity.getCachedState();
    FluidsModel.BakedModel model = ModelHelper.getBakedModel(state, FluidsModel.BakedModel.class);
    if (model != null) {
      // if side, rotate fluid model
      Direction direction = state.get(FaucetBlock.FACING);
      boolean isRotated = RenderingHelper.applyRotation(matrices, direction);

      // fluid props
      FluidAttributes attributes = renderFluid.getFluid().getAttributes();
      int color = attributes.getColor(renderFluid);
      Function<Identifier, Sprite> spriteGetter = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
      Sprite still = spriteGetter.apply(attributes.getStillTexture(renderFluid));
      Sprite flowing = spriteGetter.apply(attributes.getFlowingTexture(renderFluid));
      boolean isGas = attributes.isGaseous(renderFluid);
      combinedLightIn = FluidRenderer.withBlockLight(combinedLightIn, attributes.getLuminosity(renderFluid));

      // render all cubes in the model
      VertexConsumer buffer = bufferIn.getBuffer(FluidRenderer.RENDER_TYPE);
      for (FluidCuboid cube : model.getFluids()) {
        FluidRenderer.renderCuboid(matrices, buffer, cube, 0, still, flowing, color, combinedLightIn, isGas);
      }

      // render into the block(s) below
      FaucetFluidLoader.renderFaucetFluids(world, tileEntity.getPos(), direction, matrices, buffer, still, flowing, color, combinedLightIn);

      // if rotated, pop back rotation
      if(isRotated) {
        matrices.pop();
      }
    }
  }
}
