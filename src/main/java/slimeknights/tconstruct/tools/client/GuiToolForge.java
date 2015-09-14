package slimeknights.tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.tileentity.TileToolForge;
import slimeknights.tconstruct.tools.tileentity.TileToolStation;

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
