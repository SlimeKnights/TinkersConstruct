package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.fluid.FluidsModel;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.RenderUtil;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.client.FaucetFluidLoader;
import slimeknights.tconstruct.smeltery.client.FaucetFluidLoader.FaucetFluid;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

import java.util.function.Function;

public class FaucetTileEntityRenderer extends TileEntityRenderer<FaucetTileEntity> {
  public FaucetTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(FaucetTileEntity tileEntity, float partialTicks, MatrixStack matrices, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    FluidStack drained = tileEntity.getDrained();
    if (!tileEntity.isPouring() || drained.isEmpty()) {
      return;
    }

    // safety
    World world = tileEntity.getWorld();
    if (world == null) {
      return;
    }

    // fetch faucet model to determine where to render fluids
    BlockState state = tileEntity.getBlockState();
    FluidsModel.BakedModel model = ModelHelper.getBakedModel(state, FluidsModel.BakedModel.class);
    if (model != null) {
      // if side, rotate fluid model
      Direction direction = state.get(FaucetBlock.FACING).getOpposite();
      boolean isRotated = RenderUtil.applyRotation(matrices, direction);

      // fluid props
      FluidAttributes attributes = drained.getFluid().getAttributes();
      int color = attributes.getColor(drained);
      Function<ResourceLocation, TextureAtlasSprite> spriteGetter = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
      TextureAtlasSprite still = spriteGetter.apply(attributes.getStillTexture(drained));
      TextureAtlasSprite flowing = spriteGetter.apply(attributes.getFlowingTexture(drained));
      boolean isGas = attributes.isGaseous(drained);

      // render all cubes in the model
      IVertexBuilder buffer = bufferIn.getBuffer(FluidRenderer.RENDER_TYPE);
      for (FluidCuboid cube : model.getFluids()) {
        FluidRenderer.renderCuboid(matrices, buffer, cube, 0, still, flowing, color, combinedLightIn, isGas);
      }

      // render into the block(s) below
      FaucetFluid faucetFluid = FaucetFluidLoader.get(world.getBlockState(tileEntity.getPos().down()));
      // render all cubes with the given offset
      for (FluidCuboid cube : faucetFluid.getFluids(direction)) {
        FluidRenderer.renderCuboid(matrices, buffer, cube, -1, still, flowing, color, combinedLightIn, isGas);
      }

      // if rotated, pop back rotation
      if(isRotated) {
        matrices.pop();
      }
    }
  }
}
