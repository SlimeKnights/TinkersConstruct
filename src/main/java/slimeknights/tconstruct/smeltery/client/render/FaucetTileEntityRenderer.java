package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

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

    // check how far into the 2nd block we want to render
    World world = tileEntity.getWorld();
    if (world == null) {
      return;
    }
    IVertexBuilder buffer = bufferIn.getBuffer(RenderUtil.getBlockRenderType());
    // TODO: Remove hardcoding
    float yMin = -15f / 16f;

    Direction direction = tileEntity.getBlockState().get(FaucetBlock.FACING);
    if (direction == Direction.UP) {
      RenderUtil.renderFluidCuboid(drained, matrices, buffer, combinedLightIn, 0.375f, 0, 0.375f, 0.625f, 1f, 0.625f);
      // render in the block beneath
      if (yMin < 0) {
        RenderUtil.renderFluidCuboid(drained, matrices, buffer, combinedLightIn, 0.375f, yMin, 0.375f, 0.625f, 0f, 0.625f);
      }
    }
    // for horizontal we use custom rendering so we can rotate it and have the flowing texture in the faucet part
    // default direction is north because that makes the fluid flow into the right direction through the UVs
    if (direction.getHorizontalIndex() >= 0) {
      float r = -90f * (2 + direction.getHorizontalIndex());
      float o = 0.5f;
      matrices.push();
      // custom rendering for flowing on top
      Minecraft.getInstance().textureManager.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
      int color = drained.getFluid().getAttributes().getColor(drained);
      TextureAtlasSprite flowing = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(drained.getFluid().getAttributes().getFlowingTexture(drained));

      matrices.translate(o, 0, o);
      matrices.rotate(Vector3f.YP.rotationDegrees(r));
      matrices.translate(-o, 0, -o);

      float x1 = 0.375f;
      float x2 = 0.625f;
      float y1 = 0.375f;
      float y2 = 0.625f;
      float z1 = 0f;
      float z2 = 0.375f;

      matrices.push();
      matrices.translate(x1, y1, z1);
      Matrix4f matrix = matrices.getLast().getMatrix();
      // the stuff in the faucet
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.DOWN, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.NORTH, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.EAST, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.WEST, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.UP, color, combinedLightIn, 0, true);
      matrices.pop();
      
      // the stuff flowing down
      y1 = 0f;
      z1 = 0.375f;
      z2 = 0.5f;
      matrices.push();
      matrices.translate(x1, y1, z1);
      matrix = matrices.getLast().getMatrix();
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.DOWN, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.NORTH, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.EAST, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.SOUTH, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.WEST, color, combinedLightIn, 0, true);
      RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.UP, color, combinedLightIn, 0, true);
      matrices.pop();
      
      // render in the block beneath
      if (yMin < 0) {
        y1 = yMin;
        y2 = 0;
        matrices.push();
        matrices.translate(x1, y1, z1);
        matrix = matrices.getLast().getMatrix();
        RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.DOWN, color, combinedLightIn, 0, true);
        RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.NORTH, color, combinedLightIn, 0, true);
        RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.EAST, color, combinedLightIn, 0, true);
        RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.SOUTH, color, combinedLightIn, 0, true);
        RenderUtil.putTexturedQuad(buffer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.WEST, color, combinedLightIn, 0, true);
        matrices.pop();
      }

      matrices.pop();
    }
  }
}
