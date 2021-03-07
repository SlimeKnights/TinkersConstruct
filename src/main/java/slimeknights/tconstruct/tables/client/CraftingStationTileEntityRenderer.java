package slimeknights.tconstruct.tables.client;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import slimeknights.mantle.client.render.InventoryTileEntityRenderer;

public class CraftingStationTileEntityRenderer<T extends TileEntity & IInventory> extends InventoryTileEntityRenderer<T> {
  public CraftingStationTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public boolean isGlobalRenderer(T tile) {
    return !tile.isEmpty();
  }
}
