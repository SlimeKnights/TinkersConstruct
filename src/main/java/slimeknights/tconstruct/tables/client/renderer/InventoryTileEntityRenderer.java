package slimeknights.tconstruct.tables.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
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
    if (station.isEmpty()) return;

    // first, find the model for item display locations
    BlockState state = station.getBlockState();
    InventoryModel.BakedModel model = RenderUtil.getBakedModel(state, InventoryModel.BakedModel.class);
    if (model != null) {
      // if the block is rotatable, rotate item display
      boolean isRotated = RenderUtil.applyRotation(matrices, state);

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
