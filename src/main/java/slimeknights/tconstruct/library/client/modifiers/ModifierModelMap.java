package slimeknights.tconstruct.library.client.modifiers;

import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

/** Holds all modifier models for a given tool. */
public record ModifierModelMap(Map<String, ModifierModel> constant, Map<ModifierId, ? extends IBakedModifierModel> modifiers) {
  public static final ModifierModelMap EMPTY = new ModifierModelMap(Map.of(), Map.of());

  /** Creates a new instance, returning empty if no data is provided */
  public static ModifierModelMap create(Map<String, ModifierModel> constant, Map<ModifierId, ? extends IBakedModifierModel> modifiers) {
    if (constant.isEmpty() && modifiers.isEmpty()) {
      return EMPTY;
    }
    return new ModifierModelMap(constant.isEmpty() ? Map.of() : Collections.unmodifiableMap(constant), Map.copyOf(modifiers));
  }

  /** Checks if this instance has no modifiers */
  public boolean isEmpty() {
    return constant.isEmpty() && modifiers.isEmpty();
  }

  /** Gets the model for the given modifier */
  @Nullable
  public IBakedModifierModel get(ModifierId modifier) {
    return modifiers.get(modifier);
  }
}
