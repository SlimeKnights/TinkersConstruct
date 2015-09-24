package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import java.util.Set;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.tileentity.TileToolStation;

public class ContainerToolForge extends ContainerToolStation {

  public ContainerToolForge(InventoryPlayer playerInventory, TileToolStation tile) {
    super(playerInventory, tile);
  }

  @Override
  protected Set<ToolCore> getBuildableTools() {
    return TinkerRegistry.getToolForgeCrafting();
  }
}
