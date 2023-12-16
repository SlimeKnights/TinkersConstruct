package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NBT object containing all current modifiers
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class ModifierNBT {
  public static final String TAG_MODIFIER = "name";
  public static final String TAG_LEVEL = "level";

  /** Instance containing no modifiers */
  public static final ModifierNBT EMPTY = new ModifierNBT(Collections.emptyList());

  /** Sorted list of modifiers */
  @Getter
  private final List<ModifierEntry> modifiers;

  /**
   * Checks if the NBT has no modifiers
   * @return  True if there are no modifiers
   */
  public boolean isEmpty() {
    return modifiers.isEmpty();
  }

  /**
   * Gets the modifier entry for a modifier
   * @param modifier  Modifier to check
   * @return  Modifier entry, or null if absent
   */
  @Nullable
  public ModifierEntry getEntry(ModifierId modifier) {
    for (ModifierEntry entry : modifiers) {
      if (entry.matches(modifier)) {
        return entry;
      }
    }
    return null;
  }

  /**
   * Gets the level of a modifier
   * @param modifier  Modifier to check
   * @return  Modifier level, or 0 if modifier is missing
   */
  public int getLevel(ModifierId modifier) {
    for (ModifierEntry entry : modifiers) {
      if (entry.matches(modifier)) {
        return entry.getLevel();
      }
    }
    return 0;
  }

  /**
   * Creates a copy of this NBT with the given modifier added. Result will be unsorted
   * Do not use if you need to make multiple additions, use {@link ModifierNBT.Builder}
   * @param modifier  Modifier
   * @param level     Levels of the modifier to add
   * @return  Instance with the given modifier
   */
  public ModifierNBT withModifier(ModifierId modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above zero");
    }

    // rather than using the builder, just use a raw list builder
    // easier for adding a single entry, and the cases that call this method don't care about sorting
    ImmutableList.Builder<ModifierEntry> builder = ImmutableList.builder();
    boolean found = false;
    for (ModifierEntry entry : this.modifiers) {
      // first match increases the level
      // shouldn't be a second match (all the methods are protected), but just in case we prevent modifier duplication
      if (!found && entry.matches(modifier)) {
        builder.add(entry.withLevel(entry.getLevel() + level));
        found = true;
      } else {
        builder.add(entry);
      }
    }
    // if no matching modifier, create a new entry
    if (!found) {
      builder.add(new ModifierEntry(modifier, level));
    }
    return new ModifierNBT(builder.build());
  }

  /**
   * Creates a copy of this NBT without the given modifier
   * @param modifier  Modifier to remove
   * @param level     Level to remove
   * @return  ModifierNBT without the given modifier
   */
  public ModifierNBT withoutModifier(ModifierId modifier, int level) {
    if (level <= 0) {
      throw new IllegalArgumentException("Invalid level, must be above zero");
    }

    // rather than using the builder, just use a raw list builder
    // easier for adding a single entry, and the cases that call this method don't care about sorting
    ImmutableList.Builder<ModifierEntry> builder = ImmutableList.builder();
    for (ModifierEntry entry : this.modifiers) {
      if (entry.matches(modifier) && level > 0) {
        if (entry.getLevel() > level) {
          builder.add(entry.withLevel(entry.getLevel() - level));
          level = 0;
        } else {
          level -= entry.getLevel();
        }
      } else {
        builder.add(entry);
      }
    }
    return new ModifierNBT(builder.build());
  }

  /** Re-adds the modifier list from NBT */
  public static ModifierNBT readFromNBT(@Nullable Tag inbt) {
    if (inbt == null || inbt.getId() != Tag.TAG_LIST) {
      return EMPTY;
    }

    ListTag listNBT = (ListTag)inbt;
    if (listNBT.getElementType() != Tag.TAG_COMPOUND) {
      return EMPTY;
    }

    ImmutableList.Builder<ModifierEntry> builder = ImmutableList.builder();
    for (int i = 0; i < listNBT.size(); i++) {
      CompoundTag tag = listNBT.getCompound(i);
      if (tag.contains(TAG_MODIFIER) && tag.contains(TAG_LEVEL)) {
        ModifierId id = ModifierId.tryParse(tag.getString(TAG_MODIFIER));
        int level = tag.getInt(TAG_LEVEL);
        if (id != null && level > 0) {
          builder.add(new ModifierEntry(id, level));
        }
      }
    }
    return new ModifierNBT(builder.build());
  }

  /** Writes these modifiers to NBT */
  public ListTag serializeToNBT() {
    ListTag list = new ListTag();
    for (ModifierEntry entry : modifiers) {
      CompoundTag tag = new CompoundTag();
      tag.putString(TAG_MODIFIER, entry.getId().toString());
      tag.putShort(TAG_LEVEL, (short)entry.getLevel());
      list.add(tag);
    }
    return list;
  }

  /**
   * Creates a new builder for modifier NBT
   * @return  Builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for creating a modifier list with multiple additions. Builder results will be sorted
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    /** Intentionally using modifiers to ensure they are resolved */
    private final Map<Modifier, Integer> modifiers = new LinkedHashMap<>();

    /**
     * Adds a single modifier to the builder
     * @param modifier  Modifier
     * @param level     Modifier level
     * @return  Builder instance
     */
    public Builder add(Modifier modifier, int level) {
      if (level <= 0) {
        throw new IllegalArgumentException("Level must be above 0");
      }
      // skip if its the empty modifier, no sense tracking
      if (modifier != ModifierManager.INSTANCE.getDefaultValue()) {
        Integer value = modifiers.get(modifier);
        if (value != null) {
          level += value;
        }
        modifiers.put(modifier, level);
      }
      return this;
    }

    /**
     * Adds an entry to the builder
     * @param entry  Entry to add
     * @return  Builder instance
     */
    public Builder add(ModifierEntry entry) {
      add(entry.getModifier(), entry.getLevel());
      return this;
    }

    /**
     * Adds an entry to the builder
     * @param entries  Entries to add
     * @return  Builder instance
     */
    public Builder add(List<ModifierEntry> entries) {
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
    public Builder add(ModifierNBT nbt) {
      add(nbt.getModifiers());
      return this;
    }

    /** Builds the NBT */
    public ModifierNBT build() {
      // converts the map into a list of entries, priority sorted
      // note priority is negated so higher numbers go first
      List<ModifierEntry> list = modifiers.entrySet().stream()
                                          .map(entry -> new ModifierEntry(entry.getKey(), entry.getValue()))
                                          // sort on priority, falls back to the order they were added
                                          .sorted(Comparator.comparingInt(entry -> -entry.getModifier().getPriority()))
                                          .collect(Collectors.toList());
      // its rare to see no modifiers, but no sense creating a new instance for that
      if (list.isEmpty()) {
        return EMPTY;
      }
      return new ModifierNBT(ImmutableList.copyOf(list));
    }
  }
}
