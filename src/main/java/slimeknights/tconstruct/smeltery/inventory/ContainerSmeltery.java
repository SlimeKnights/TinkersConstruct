package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class ContainerSmeltery extends ContainerMultiModule<TileSmeltery> {

  public ContainerSmeltery(InventoryPlayer inventoryPlayer, TileSmeltery tile) {
    super(tile);

    addPlayerInventory(inventoryPlayer, 8, 84);
  }
}
