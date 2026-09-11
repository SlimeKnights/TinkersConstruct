package slimeknights.tconstruct.library.client.modifiers.block;

import slimeknights.tconstruct.library.client.modifiers.block.model.BlockModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Holds all modifier models for a given tool. */
public record BlockModifierModelMap(Map<ModifierId, ? extends IBakedBlockModifierModel> modifiers, Map<String, BlockModifierModel> constant, List<BlockModifierModel> sortedConstant) {
  public static final BlockModifierModelMap EMPTY = new BlockModifierModelMap(Map.of(), Map.of(), List.of());

  /** Creates a new instance, returning empty if no data is provided */
  public static BlockModifierModelMap create(Map<ModifierId, ? extends IBakedBlockModifierModel> modifiers, Map<String, BlockModifierModel> constant) {
    if (constant.isEmpty() && modifiers.isEmpty()) {
      return EMPTY;
    }
    List<BlockModifierModel> sortedConstant;
    if (constant.isEmpty()) {
      constant = Map.of();
      sortedConstant = List.of();
    } else {
      constant = Collections.unmodifiableMap(constant);
      // sort the constant models by key, that will be the list we iterate on the model
      // reverse order as the quad builder uses reverse order
      sortedConstant = constant.entrySet().stream().sorted(Entry.<String,BlockModifierModel>comparingByKey().reversed()).map(Entry::getValue).toList();
    }
    return new BlockModifierModelMap(Map.copyOf(modifiers), constant, sortedConstant);
  }

  /** Creates a new map using the given modifiers */
  public BlockModifierModelMap withModifiers(Map<ModifierId, ? extends IBakedBlockModifierModel> modifiers) {
    if (constant.isEmpty() && modifiers.isEmpty()) {
      return EMPTY;
    }
    return new BlockModifierModelMap(Map.copyOf(modifiers), constant, sortedConstant);
  }

  /** Checks if this instance has no modifiers */
  public boolean isEmpty() {
    return constant.isEmpty() && modifiers.isEmpty();
  }

  /** Gets the model for the given modifier */
  @Nullable
  public IBakedBlockModifierModel get(ModifierId modifier) {
    return modifiers.get(modifier);
  }
}
