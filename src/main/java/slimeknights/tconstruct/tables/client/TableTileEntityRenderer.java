package slimeknights.tconstruct.tables.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
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
public class TableTileEntityRenderer<T extends TileEntity & IInventory> extends TileEntityRenderer<T> {
  public TableTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void render(T inventory, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int light, int combinedOverlayIn) {
    if (!inventory.isEmpty()) {
      BlockState state = inventory.getBlockState();
      TableModel.BakedModel model = ModelHelper.getBakedModel(state, TableModel.BakedModel.class);
      if (model != null) {
        boolean isRotated = RenderingHelper.applyRotation(matrices, state);
        List<ModelItem> modelItems = model.getItems();

        for(int i = 0; i < modelItems.size(); ++i) {
          RenderingHelper.renderItem(matrices, buffer, inventory.getStackInSlot(i), modelItems.get(i), light);
        }

        if (isRotated) {
          matrices.pop();
        }
      }
    }
  }

  @Override
  public boolean isGlobalRenderer(T tile) {
    return !tile.isEmpty();
  }
}
