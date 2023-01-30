package slimeknights.tconstruct.library.modifiers.data;

import lombok.Getter;
import net.minecraft.world.entity.EquipmentSlot;

/** Helper class to keep track the max vanilla level in a modifier, ints and only on four armor slots */
public class VanillaMaxLevel {
  /** Level for each slot */
  private final int[] levels = new int[4];
  /** Max level across all slots */
  @Getter
  private int max = 0;

  /** Sets the given vanilla level in the structure */
  public void set(EquipmentSlot slot, int level) {
    int oldLevel = levels[slot.getIndex()];
    if (level != oldLevel) {
      levels[slot.getIndex()] = level;
      // if new max, update max
      if (level > max) {
        max = level;
      } else if (max == oldLevel) {
        // if was max before, search for replacement max
        max = 0;
        for (int value : levels) {
          if (value > max) {
            max = value;
          }
        }
      }
    }
  }
}
