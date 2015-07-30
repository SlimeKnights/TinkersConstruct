package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.common.tileentity.TileTable;
import tconstruct.library.mantle.IInventoryGui;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.client.GuiStencilTable;
import tconstruct.tools.inventory.ContainerStencilTable;

public class TileStencilTable extends TileTable implements IInventoryGui {

  public TileStencilTable() {
    super("container.stencilTable", 1);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerStencilTable(inventoryplayer, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiStencilTable(inventoryplayer, world, pos, this);
  }
}
