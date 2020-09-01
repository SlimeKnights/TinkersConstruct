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
      this.inputs = NonNullList.from(ItemStack.EMPTY, IntStream.range(0, 5)
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
}
