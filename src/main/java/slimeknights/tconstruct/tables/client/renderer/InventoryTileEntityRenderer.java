package slimeknights.tconstruct.tables.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.IInventory;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.model.data.ModelItem;
import slimeknights.tconstruct.library.client.model.tesr.InventoryModel;

import java.util.List;

public class InventoryTileEntityRenderer<T extends TileEntity & IInventory> extends TileEntityRenderer<T> {

  public InventoryTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(T station, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int light, int combinedOverlayIn) {
    if (isVisible(station)) {
      if (station.isEmpty()) return;

      // first, find the model for item display locations
      BlockState state = station.getBlockState();
      InventoryModel.BakedModel model = RenderUtil.getBakedModel(state, InventoryModel.BakedModel.class);
      if (model != null) {
        // if the block is rotatable, rotate item display
        boolean isRotated = station.getBlockState().has(BlockStateProperties.HORIZONTAL_FACING);
        if (isRotated) {
          Direction facing = station.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
          matrices.push();
          matrices.translate(0.5, 0, 0.5);
          matrices.rotate(Vector3f.YP.rotationDegrees(-90f * (2 + facing.getHorizontalIndex())));
          matrices.translate(-0.5, 0, -0.5);
        }

        // render items
        List<ModelItem> modelItems = model.getItems();
        for (int i = 0; i < modelItems.size(); i++) {
          RenderUtil.renderItem(matrices, buffer, station.getStackInSlot(i), modelItems.get(i), light);
        }

        // pop back rotation
        if (isRotated) {
          matrices.pop();
        }
      }
    }
  }

  /**
   * Checks if the tile entity is visible
   * @param te  Tile entity to check
   * @return  True if visible, false if not
   */
  private boolean isVisible(TileEntity te) {
    if (this.renderDispatcher.renderInfo == null) {
      return false;
    }
    Vec3d view = this.renderDispatcher.renderInfo.getProjectedView();
    return te.getDistanceSq(view.getX(), view.getY(), view.getZ()) < 128d;
  }
}
