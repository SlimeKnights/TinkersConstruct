package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TileToolStation;

public class ContainerToolStation extends ContainerMultiModule<TileToolStation> {

  public ContainerToolStation(InventoryPlayer playerInventory, TileToolStation tile) {
    super(tile);

    this.addPlayerInventory(playerInventory, 8, 84);
  }
}
