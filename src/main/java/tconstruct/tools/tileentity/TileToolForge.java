package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import tconstruct.tools.client.GuiToolForge;

public class TileToolForge extends TileToolStation {

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiToolForge(inventoryplayer, world, pos, this);
  }
}
