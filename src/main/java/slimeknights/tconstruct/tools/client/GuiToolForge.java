package slimeknights.tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.tools.tileentity.TileToolStation;

public class GuiToolForge extends GuiToolStation {

  public GuiToolForge(InventoryPlayer playerInv, World world, BlockPos pos, TileToolStation tile) {
    super(playerInv, world, pos, tile);

    metal();
  }
}
