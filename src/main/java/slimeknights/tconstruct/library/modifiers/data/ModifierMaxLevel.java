package slimeknights.tconstruct.library.modifiers.data;

import lombok.Getter;
import net.minecraft.inventory.EquipmentSlotType;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;

import javax.annotation.Nullable;

/** Helper class to keep track the max modifier level in a modifier, floats and keeps track of max slot */
public class ModifierMaxLevel {
  /** Level for each slot */
  private final float[] levels = new float[4];
  /** Max level across all slots */
  @Getter
  private float max = 0;
  /** Slot containing the max level */
  @Getter @Nullable
  private EquipmentSlotType maxSlot;

  /** Sets the given value in the structure */
  public void set(EquipmentSlotType slot, float level) {
    float oldLevel = levels[slot.getIndex()];
    if (level != oldLevel) {
      // first, update level
      levels[slot.getIndex()] = level;
      // if larger than max, new max
      if (level >= max) {
        max = level;
        maxSlot = slot;
      } else if (slot == maxSlot) {
        // if the old level was max, find new max
        max = 0;
        for (EquipmentSlotType armorSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
          float value = levels[armorSlot.getIndex()];
          if (value > max) {
            max = value;
            maxSlot = armorSlot;
          }
        }
      }
    }
  }
}
