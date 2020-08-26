package slimeknights.tconstruct.tables.tileentity.table.tinkerstation;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.stream.IntStream;

public class TinkerStationInventoryWrapper implements ITinkerStationInventory {

  private final IInventory station;
  private NonNullList<ItemStack> inputs;

  public TinkerStationInventoryWrapper(IInventory station) {
    this.station = station;
  }

  @Override
  public NonNullList<ItemStack> getAllInputStacks() {
    if (this.inputs == null) {
      this.inputs = NonNullList.from(ItemStack.EMPTY, IntStream.range(0, 6)
        .mapToObj(this.station::getStackInSlot)
        .filter(itemStack -> !itemStack.isEmpty())
        .toArray(ItemStack[]::new));
    }

    return this.inputs;
  }

  /**
   * Clears the cached inputs
   */
  public void clearInputs() {
    this.inputs = null;
  }

  @Override
  public ItemStack getTinkerableStack() {
    return this.station.getStackInSlot(TinkerStationTileEntity.TINKER_SLOT);
  }

  @Override
  public int getSizeInventory() {
    return this.station.getSizeInventory();
  }

  @Override
  public boolean isEmpty() {
    for (int i = 0; i < this.station.getSizeInventory(); i++) {
      if (!this.station.getStackInSlot(i).isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return index >= 0 && index < this.station.getSizeInventory() ? this.station.getStackInSlot(index) : ItemStack.EMPTY;
  }
}
