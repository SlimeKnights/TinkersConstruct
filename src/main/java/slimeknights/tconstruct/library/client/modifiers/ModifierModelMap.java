package slimeknights.tconstruct.library.client.modifiers;

import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Holds all modifier models for a given tool. */
public record ModifierModelMap(Map<ModifierId, ? extends IBakedModifierModel> modifiers, Map<String, ModifierModel> constant, List<ModifierModel> sortedConstant) {
  public static final ModifierModelMap EMPTY = new ModifierModelMap(Map.of(), Map.of(), List.of());

  /** Creates a new instance, returning empty if no data is provided */
  public static ModifierModelMap create(Map<ModifierId, ? extends IBakedModifierModel> modifiers, Map<String, ModifierModel> constant) {
    if (constant.isEmpty() && modifiers.isEmpty()) {
      return EMPTY;
    }
    List<ModifierModel> sortedConstant;
    if (constant.isEmpty()) {
      constant = Map.of();
      sortedConstant = List.of();
    } else {
      constant = Collections.unmodifiableMap(constant);
      // sort the constant models by key, that will be the list we iterate on the model
      // reverse order as the quad builder uses reverse order
      sortedConstant = constant.entrySet().stream().sorted(Entry.<String,ModifierModel>comparingByKey().reversed()).map(Entry::getValue).toList();
    }
    return new ModifierModelMap(Map.copyOf(modifiers), constant, sortedConstant);
  }

  /** Creates a new map using the given modifiers */
  public ModifierModelMap withModifiers(Map<ModifierId, ? extends IBakedModifierModel> modifiers) {
    if (constant.isEmpty() && modifiers.isEmpty()) {
      return EMPTY;
    }
    return new ModifierModelMap(Map.copyOf(modifiers), constant, sortedConstant);
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
