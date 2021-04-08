package slimeknights.tconstruct.tables.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.library.client.model.block.TableModel;

import java.util.List;

/**
 * Same as {@link slimeknights.mantle.client.render.InventoryTileEntityRenderer}, but uses {@link TableModel}.
 * TODO: migrate to an interface in Mantle
 * @param <T>  Tile entity type
 */
public class TableTileEntityRenderer<T extends BlockEntity & Inventory> extends BlockEntityRenderer<T> {
  public TableTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(T inventory, float partialTicks, MatrixStack matrices, VertexConsumerProvider buffer, int light, int combinedOverlayIn) {
    if (!inventory.isEmpty()) {
      BlockState state = inventory.getCachedState();
      TableModel.BakedModel model = ModelHelper.getBakedModel(state, TableModel.BakedModel.class);
      if (model != null) {
        boolean isRotated = RenderingHelper.applyRotation(matrices, state);
        List<ModelItem> modelItems = model.getItems();

        for(int i = 0; i < modelItems.size(); ++i) {
          RenderingHelper.renderItem(matrices, buffer, inventory.getStack(i), modelItems.get(i), light);
        }

        if (isRotated) {
          matrices.pop();
        }
      }
    }
  }

  @Override
  public boolean rendersOutsideBoundingBox(T tile) {
    return !tile.isEmpty();
  }
}
