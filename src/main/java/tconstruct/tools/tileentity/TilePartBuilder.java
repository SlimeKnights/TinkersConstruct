package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.common.tileentity.TileTable;
import tconstruct.library.mantle.IInventoryGui;
import tconstruct.tools.client.GuiPartBuilder;
import tconstruct.tools.inventory.ContainerPartBuilder;

public class TilePartBuilder extends TileTable implements IInventoryGui {

  public TilePartBuilder() {
    super("container.partBuilder", 2);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPartBuilder(inventoryplayer, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPartBuilder(inventoryplayer, world, pos, this);
  }
}
