package slimeknights.tconstruct.tools.common.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.common.tileentity.TileToolForge;

public class GuiToolForge extends GuiToolStation {

  public GuiToolForge(InventoryPlayer playerInv, World world, BlockPos pos, TileToolForge tile) {
    super(playerInv, world, pos, tile);

    metal();
  }

  @Override
  public Set<ToolCore> getBuildableItems() {
    return TinkerRegistry.getToolForgeCrafting();
  }
}
