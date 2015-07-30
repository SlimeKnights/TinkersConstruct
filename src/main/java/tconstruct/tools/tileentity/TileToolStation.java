package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.common.tileentity.TileTable;
import tconstruct.library.mantle.IInventoryGui;

public class TileToolStation extends TileTable implements IInventoryGui {

  public TileToolStation() {
    super("container.stencilTable", 6);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return null;
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return null;
  }
}
