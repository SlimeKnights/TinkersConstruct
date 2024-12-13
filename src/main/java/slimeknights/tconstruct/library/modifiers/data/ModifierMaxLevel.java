package slimeknights.tconstruct.library.modifiers.data;

import lombok.Getter;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;

import javax.annotation.Nullable;

/** Helper class to keep track the max modifier level in a modifier, floats, keeps track of max slot, and tracks all 6 slots */
public class ModifierMaxLevel {
  /** Level for each slot */
  private final float[] levels = new float[6];
  /** Max level across all slots */
  @Getter
  private float max = 0;
  /** Slot containing the max level */
  @Getter @Nullable
  private EquipmentSlot maxSlot;

  /** Sets the given value in the structure */
  public void set(EquipmentSlot slot, float level) {
    float oldLevel = levels[slot.getFilterFlag()];
    if (level != oldLevel) {
      // first, update level
      levels[slot.getFilterFlag()] = level;
      // if larger than max, new max
      if (level >= max) {
        max = level;
        maxSlot = slot;
      } else if (slot == maxSlot) {
        // if the old level was max, find new max
        max = 0;
        for (EquipmentSlot armorSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
          float value = levels[armorSlot.getFilterFlag()];
          if (value > max) {
            max = value;
            maxSlot = armorSlot;
          }
        }
      }
    }
  }

  /** Fetches the max stat level from the given living entity */
  public static float getStat(LivingEntity living, TinkerDataCapability.ComputableDataKey<ModifierMaxLevel> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).map(ModifierMaxLevel::getMax).orElse(0f);
  }
}
