package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterial;

/**
 * Items that can be repaired. They also break instead of getting removed.
 */
public interface IRepairable {

  /**
   * Repairs the given stack by the given amount
   * @param repairable  Repairable item
   * @param amount      Amount to repair
   * @return  Repaired stack. Will be the same instance as repairable
   */
  ItemStack repairItem(ItemStack repairable, int amount);

  /**
   * Checks to see if the item passed needs to be repaired or not.
   *
   * @param repairable the item to check if it needs to be repaired
   * @return if the item needs repaired
   */
  boolean needsRepair(ItemStack repairable);

  /**
   * Checks to see if the item can be repaired with the given material
   *
   * @param repairable  Stack to repair
   * @param material   the material to check
   * @return if the item needs repaired
   */
  boolean canRepairWith(ItemStack repairable, IMaterial material);
}
