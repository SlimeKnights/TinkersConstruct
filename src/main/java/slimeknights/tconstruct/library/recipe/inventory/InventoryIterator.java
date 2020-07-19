package slimeknights.tconstruct.library.recipe.inventory;

import lombok.AllArgsConstructor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator that runs through an inventory
 */
@AllArgsConstructor
public class InventoryIterator implements Iterator<ItemStack> {
  /** Inventory to iterate */
  private final IInventory inventory;
  /** Current slot */
  private int current;
  /** Final slot */
  private final int last;

  @Override
  public boolean hasNext() {
    return this.current <= this.last;
  }

  @Override
  public ItemStack next() {
    if (!this.hasNext()) {
      throw new NoSuchElementException();
    }

    return this.inventory.getStackInSlot(this.current++);
  }
}
