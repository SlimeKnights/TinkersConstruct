package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;

public class ContainerSearedFurnace extends ContainerMultiModule<TileSearedFurnace> {

  protected ContainerSideInventory<TileSearedFurnace> sideInventory;
  protected int oldFuel;
  protected int oldFuelQuality;
  protected int[] oldHeats;
  protected int[] oldHeatsRequired;
  private int inventorySize;

  public ContainerSearedFurnace(InventoryPlayer inventoryPlayer, TileSearedFurnace tile) {
    super(tile);

    sideInventory = new ContainerSearedFurnaceSideInventory(tile, 0, 0, calcColumns());
    addSubContainer(sideInventory, true);

    // player stuffs
    addPlayerInventory(inventoryPlayer, 8, 84);

    oldFuel = 0;
    oldFuelQuality = 0;
    inventorySize = tile.getSizeInventory();
    oldHeats = new int[inventorySize];
    oldHeatsRequired = new int[inventorySize];
  }

  public int calcColumns() {
    return 3; // makes me think of https://xkcd.com/221/
  }

  @Override
  public void addListener(IContainerListener listener) {
    super.addListener(listener);

    listener.sendWindowProperty(this, 0, tile.getFuel());
    listener.sendWindowProperty(this, 1, tile.fuelQuality);

    for(int i = 0; i < inventorySize; i++) {
      listener.sendWindowProperty(this, i + 2, tile.getTemperature(i));
      listener.sendWindowProperty(this, i + inventorySize + 2, tile.getTempRequired(i));
    }
  }

  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();

    // changed fuel data
    int fuel = tile.getFuel();
    if(fuel != oldFuel) {
      oldFuel = fuel;
      for(IContainerListener crafter : this.listeners) {
        crafter.sendWindowProperty(this, 0, fuel);
      }
    }
    fuel = tile.fuelQuality;
    if(fuel != oldFuelQuality) {
      oldFuelQuality = fuel;
      for(IContainerListener crafter : this.listeners) {
        crafter.sendWindowProperty(this, 1, fuel);
      }
    }

    // send changed heats
    for(int i = 0; i < inventorySize; i++) {
      int temp = tile.getTemperature(i);
      if(temp != oldHeats[i]) {
        oldHeats[i] = temp;
        for(IContainerListener crafter : this.listeners) {
          crafter.sendWindowProperty(this, i + 2, temp);
        }
      }
      temp = tile.getTempRequired(i);
      if(temp != oldHeatsRequired[i]) {
        oldHeatsRequired[i] = temp;
        for(IContainerListener crafter : this.listeners) {
          crafter.sendWindowProperty(this, i + 2 + inventorySize, temp);
        }
      }
    }
  }

  @Override
  public void updateProgressBar(int id, int data) {
    // first two indexes are fuel, specifically fuel and fuelQuality
    if(id < 2) {
      tile.updateFuelFromPacket(id, data);
    }
    // next is a set the size of the inventory of current temperatures
    else if(id < inventorySize + 2) {
      tile.updateTemperatureFromPacket(id - 2, data);
    }
    // lastly is another inventorySize set of required temps
    else if(id < (inventorySize * 2) + 2) {
      tile.updateTempRequiredFromPacket(id - 2 - inventorySize, data);
    }
  }

}
