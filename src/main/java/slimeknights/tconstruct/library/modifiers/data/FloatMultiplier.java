package slimeknights.tconstruct.library.modifiers.data;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/** Tinker data key that stores a float that can be multiplied by many sources */
public class FloatMultiplier {
  private final Map<ResourceLocation,Float> values = new HashMap<>();
  private float calculatedValue = 1.0f;

  /** Recalculates the modifier from the map, called as a last resort */
  private void recalculate() {
    float newValue = 1.0f;
    for (float value : values.values()) {
      newValue *= value;
    }
    calculatedValue = newValue;
  }

  /** Sets the modifier for a key to the given value */
  public void set(ResourceLocation key, float value) {
    if (value == 1.0f) {
      remove(key);
      return;
    }
    Float original = values.put(key, value);
    // if there was a value before, need to remove it
    if (original != null) {
      // if someone added 0 for some annoying reason, all we can do is recalculate from scratch
      if (original == 0) {
        recalculate();
      } else {
        calculatedValue *= value / original;
      }
    } else {
      calculatedValue *= value;
    }
  }

  /** Removes the modifier associated with the given key */
  public void remove(ResourceLocation key) {
    Float value = values.remove(key);
    if (value != null) {
      if (value == 0) {
        recalculate();
      } else {
        calculatedValue /= value;
      }
    }
  }

  /** Gets the value stored in the multiplier */
  public float getValue() {
    return calculatedValue;
  }
}
