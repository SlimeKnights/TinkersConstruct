package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.common.tileentity.TileTable;
import tconstruct.library.mantle.IInventoryGui;
import tconstruct.tools.client.GuiCraftingStation;
import tconstruct.tools.inventory.ContainerCraftingStation;

public class TileCraftingStation extends TileTable implements IInventoryGui {

  public TileCraftingStation() {
    super("container.craftingStation", 9);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerCraftingStation(inventoryplayer, world, pos, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiCraftingStation(inventoryplayer, world, pos, this);
  }
}
