package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;

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
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifierNBT {
  protected static final String TAG_MODIFIER = "name";
  protected static final String TAG_LEVEL = "level";

  /** Instance containing no modifiers */
  static final ModifierNBT EMPTY = new ModifierNBT(Collections.emptyList());

  /** Sorted list of modifiers */
  @Getter
  private final List<ModifierEntry> modifiers;

  /**
   * Gets the level of a modifier
   * @param modifier  Modifier to check
   * @return  Modifier level, or 0 if modifier is missing
   */
  public int getLevel(Modifier modifier) {
    for (ModifierEntry entry : modifiers) {
      if (entry.getModifier() == modifier) {
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
  public ModifierNBT withModifier(Modifier modifier, int level) {
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
      if (!found && entry.getModifier() == modifier) {
        builder.add(new ModifierEntry(modifier, entry.getLevel() + level));
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

  /** Re-adds the modifier list from NBT */
  public static ModifierNBT readFromNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != NBT.TAG_LIST) {
      return EMPTY;
    }

    ListNBT listNBT = (ListNBT)inbt;
    if (listNBT.getTagType() != NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    ImmutableList.Builder<ModifierEntry> builder = ImmutableList.builder();
    for (int i = 0; i < listNBT.size(); i++) {
      CompoundNBT tag = listNBT.getCompound(i);
      if (tag.contains(TAG_MODIFIER) && tag.contains(TAG_LEVEL)) {
        ModifierId id = ModifierId.tryCreate(tag.getString(TAG_MODIFIER));
        int level = tag.getInt(TAG_LEVEL);
        if (id != null && level > 0) {
          Modifier modifier = TinkerRegistries.MODIFIERS.getValue(id);
          if (modifier != null && !TinkerRegistries.EMPTY.equals(modifier.getRegistryName())) {
            builder.add(new ModifierEntry(modifier, level));
          }
        }
      }
    }
    return new ModifierNBT(builder.build());
  }

  /** Writes these modifiers to NBT */
  public ListNBT serializeToNBT() {
    ListNBT list = new ListNBT();
    for (ModifierEntry entry : modifiers) {
      CompoundNBT tag = new CompoundNBT();
      tag.putString(TAG_MODIFIER, entry.getModifier().getId().toString());
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
      Integer value = modifiers.get(modifier);
      if (value != null) {
        level += value;
      }
      modifiers.put(modifier, level);
      return this;
    }

    /**
     * Adds a list of entries to the builder
     * @param entries  List of modifier entries
     * @return  Builder instance
     */
    public Builder add(List<ModifierEntry> entries) {
      for (ModifierEntry entry : entries) {
        add(entry.getModifier(), entry.getLevel());
      }
      return this;
    }

    /**
     * Adds all modifiers from the given modifier NBT
     * @param nbt  NBT object
     * @return  Builder instance
     */
    public Builder add(ModifierNBT nbt) {
      add(nbt.modifiers);
      return this;
    }

    /** Builds the NBT */
    public ModifierNBT build() {
      // converts the map into a list of entries, priority sorted
      // note priority is negated so higher numbers go first
      List<ModifierEntry> list = modifiers.entrySet().stream()
        .map(entry -> new ModifierEntry(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparingInt(entry -> -entry.getModifier().getPriority()))
        .collect(Collectors.toList());
      return new ModifierNBT(ImmutableList.copyOf(list));
    }
  }
}
