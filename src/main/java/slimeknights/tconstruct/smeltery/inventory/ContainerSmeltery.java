package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class ContainerSmeltery extends ContainerMultiModule<TileSmeltery> {

  protected ContainerSideInventory sideInventory;

  public ContainerSmeltery(InventoryPlayer inventoryPlayer, TileSmeltery tile) {
    super(tile);

    sideInventory = new ContainerSideInventory(tile, tile, 0, 0, calcColumns());
    addSubContainer(sideInventory, true);

    addPlayerInventory(inventoryPlayer, 8, 84);
  }

  public int calcColumns() {
    return 3;
  }
}
