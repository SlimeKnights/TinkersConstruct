package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;

public class ContainerSmeltery extends ContainerMultiModule<TileSmeltery> {

  protected ContainerSideInventory<TileSmeltery> sideInventory;

  protected int[] oldHeats;

  public ContainerSmeltery(InventoryPlayer inventoryPlayer, TileSmeltery tile) {
    super(tile);

    sideInventory = new ContainerSmelterySideInventory(tile, 0, 0, calcColumns());
    addSubContainer(sideInventory, true);

    addPlayerInventory(inventoryPlayer, 8, 84);

    oldHeats = new int[tile.getSizeInventory()];
  }

  public int calcColumns() {
    return 3;
  }

  @Override
  public void addListener(IContainerListener listener) {
    super.addListener(listener);

    for(int i = 0; i < oldHeats.length; i++) {
      listener.sendWindowProperty(this, i, tile.getTemperature(i));
    }
  }

  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();

    // send changed heats
    for(int i = 0; i < oldHeats.length; i++) {
      int temp = tile.getTemperature(i);
      if(temp != oldHeats[i]) {
        oldHeats[i] = temp;
        for(IContainerListener crafter : this.listeners) {
          crafter.sendWindowProperty(this, i, temp);
        }
      }
    }
  }

  @Override
  public void updateProgressBar(int id, int data) {
    // id = index of the melting progress to update
    // data = temperature

    tile.updateTemperatureFromPacket(id, data);
  }
}
