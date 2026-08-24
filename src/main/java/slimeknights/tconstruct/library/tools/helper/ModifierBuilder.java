package slimeknights.tconstruct.library.tools.helper;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.List;

/** Shared logic for both types of modifier NBT builders */
public interface ModifierBuilder {
  /**
   * Adds an entry to the builder
   * @param entry  Entry to add
   * @return  Builder instance
   */
  ModifierBuilder add(ModifierEntry entry);

  /**
   * Adds a single modifier to the builder
   * @param modifier  Modifier
   * @param level     Modifier level
   * @return  Builder instance
   */
  default ModifierBuilder add(ModifierId modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Level must be above 0");
    }
    // skip if it's the empty modifier, no sense tracking
    if (!modifier.equals(ModifierId.EMPTY)) {
      add(new ModifierEntry(modifier, level));
    }
    return this;
  }

  /**
   * Adds a single modifier to the builder
   * @param modifier  Modifier
   * @param level     Modifier level
   * @return  Builder instance
   */
  default ModifierBuilder add(Modifier modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Level must be above 0");
    }
    // skip if it's the empty modifier, no sense tracking
    if (modifier != ModifierManager.INSTANCE.getDefaultValue()) {
      add(new ModifierEntry(modifier, level));
    }
    return this;
  }

  /**
   * Adds an entry to the builder
   * @param entries  Entries to add
   * @return  Builder instance
   */
  default ModifierBuilder add(List<ModifierEntry> entries) {
    for (ModifierEntry entry : entries) {
      add(entry);
    }
    return this;
  }

  /**
   * Adds all modifiers from the given modifier NBT
   * @param nbt  NBT object
   * @return  Builder instance
   */
  default ModifierBuilder add(ModifierNBT nbt) {
    add(nbt.getModifiers());
    return this;
  }

  /** Builds the NBT */
  ModifierNBT build();
}
