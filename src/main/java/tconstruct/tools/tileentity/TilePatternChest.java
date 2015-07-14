package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.common.tileentity.TileInventory;
import tconstruct.library.mantle.IInventoryGui;
import tconstruct.tools.client.GuiPatternChest;
import tconstruct.tools.inventory.ContainerPatternChest;

public class TilePatternChest extends TileInventory implements IInventoryGui {

  public TilePatternChest() {
    super("container.patternChest", 30);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPatternChest(3, 10, inventoryplayer, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPatternChest(inventoryplayer, world, pos, this);
  }
}
