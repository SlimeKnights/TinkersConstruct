package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

/**
 * Items that can be repaired. They also break instead of getting removed.
 */
public interface IRepairable {

  /**
   * Try repairing the item with the given itemstacks.
   * ALL non-null itemstacks in repairItems have to be usable.
   *
   * Remove the used items from repairItems. Decrease their stacksize or set their entries to null.
   * Returns the repaired item.
   *
   * @param repairable  The item to repair
   * @param repairItems The items to repair with
   * @return The returned item or null if repairItems retairns a non-null entry that can't be used for repairing or if the tool already is fully repaired.
   */
  @Nonnull
  ItemStack repair(ItemStack repairable, NonNullList<ItemStack> repairItems);
}
