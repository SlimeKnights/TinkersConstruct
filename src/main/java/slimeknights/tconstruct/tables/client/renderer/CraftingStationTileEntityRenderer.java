package slimeknights.tconstruct.tables.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import slimeknights.tconstruct.tables.block.TableBlock;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

public class CraftingStationTileEntityRenderer extends TileEntityRenderer<CraftingStationTileEntity> {

  public CraftingStationTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(CraftingStationTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
    if (this.renderDispatcher.renderInfo != null && tileEntityIn.getDistanceSq(this.renderDispatcher.renderInfo.getProjectedView().x, this.renderDispatcher.renderInfo.getProjectedView().y, this.renderDispatcher.renderInfo.getProjectedView().z) < 128d) {
      if (tileEntityIn.getWorld() == null) return;

      if (tileEntityIn.isInventoryEmpty()) return;

      Direction facing = Direction.NORTH;

      if (tileEntityIn.getBlockState().has(TableBlock.FACING)) {
        facing = tileEntityIn.getBlockState().get(TableBlock.FACING);
      }

      final double spacing = .189;
      final double offset = .31;

      matrixStackIn.translate(0, 1.0625, 0);

      for (int i = 0; i < 9; i++) {
        ItemStack item = tileEntityIn.getStackInSlot(i);
        if (item.isEmpty()) continue;

        matrixStackIn.push();

        matrixStackIn.translate(spacing * (i / 3) + offset, 0, spacing * (i % 3) + offset);
        matrixStackIn.rotate(facing.getRotation());

        matrixStackIn.scale(0.25F, 0.25F, 0.25F);

        int lightAbove = WorldRenderer.getCombinedLight(tileEntityIn.getWorld(), tileEntityIn.getPos().up());
        Minecraft.getInstance().getItemRenderer().renderItem(item, TransformType.FIXED, lightAbove, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
        matrixStackIn.pop();
      }
    }
  }
}
