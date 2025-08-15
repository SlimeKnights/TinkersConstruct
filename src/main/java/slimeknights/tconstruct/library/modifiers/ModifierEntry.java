package slimeknights.tconstruct.library.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.modifiers.util.OptionalModifier;
import slimeknights.tconstruct.library.module.ModuleHook;

import javax.annotation.Nullable;

/**
 * Data class holding a modifier with a level
 */
@RequiredArgsConstructor
public class ModifierEntry implements Comparable<ModifierEntry> {
  /** Key for modifier IDs in NBT and JSON */
  public static final String TAG_MODIFIER = "name";
  /** Key for modifier levels in NBT and JSON */
  public static final String TAG_LEVEL = "level";
  /** Key for incremental amount */
  public static final String TAG_AMOUNT = "amount";
  /** Key for incremental need */
  public static final String TAG_NEEDED = "needed";
  /** Cache of effective level in NBT, for quick level lookups. Not used on deserialization */
  public static final String TAG_EFFECTIVE = "effective";

  /** Empty modifier instance, default for many methods */
  public static final ModifierEntry EMPTY = new ModifierEntry(ModifierManager.EMPTY, 0);

  /** Loadable instance for parsing. Does not handle incremental, we currently disallow incremental as traits */
  public static final RecordLoadable<ModifierEntry> LOADABLE = RecordLoadable.create(
    ModifierId.PARSER.requiredField(TAG_MODIFIER, ModifierEntry::getId),
    IntLoadable.FROM_ONE.defaultField(TAG_LEVEL, 1, true, ModifierEntry::getLevel),
    ModifierEntry::new);
  /** Loadable instance for parsing with optional modifiers. If the boolean is set, modifier does not error when missing. */
  public static final RecordLoadable<ModifierEntry> OPTIONAL_LOADABLE = RecordLoadable.create(
    ModifierId.PARSER.requiredField(TAG_MODIFIER, ModifierEntry::getId),
    IntLoadable.FROM_ONE.defaultField(TAG_LEVEL, 1, true, ModifierEntry::getLevel),
    BooleanLoadable.INSTANCE.defaultField("optional", false, false, e -> e.getLazyModifier() instanceof OptionalModifier),
    (id, level, optional) -> {
      if (optional) {
        return new ModifierEntry(new OptionalModifier(id), level);
      }
      return new ModifierEntry(id, level);
    });
  /** Range of levels for a modifier including 0 (not on the tool) */
  public static final IntRange ANY_LEVEL = new IntRange(0, Short.MAX_VALUE);
  /** Range of levels for a modifier on a tool */
  public static final IntRange VALID_LEVEL = new IntRange(1, Short.MAX_VALUE);

  /** Modifier instance */
  protected final LazyModifier modifier;
  /** Current level */
  @Getter @With
  protected final int level;

  public ModifierEntry(ModifierId id, int level) {
    this(new LazyModifier(id), level);
  }

  public ModifierEntry(Modifier modifier, int level) {
    this(new LazyModifier(modifier), level);
  }

  /** Checks if the given modifier is bound */
  public boolean isBound() {
    return modifier.isBound();
  }

  /** Gets the contained modifier ID, prevents resolving the lazy modifier if not needed */
  public ModifierId getId() {
    return modifier.getId();
  }

  /** Gets the contained modifier */
  public Modifier getModifier() {
    return modifier.get();
  }

  /** Helper for efficiency, returns the lazy modifier instance directly, which can then be copied along */
  public LazyModifier getLazyModifier() {
    return modifier;
  }

  /** Gets the given hook from the modifier, returning default instance if not present */
  public final <T> T getHook(ModuleHook<T> hook) {
    return modifier.get().getHook(hook);
  }


  /* Levels */

  /**
   * {@return Entry level, possibly reduced due to the incremental amount.}
   */
  public float getEffectiveLevel() {
    return level;
  }

  /**
   * Returns {@code level - 1} if we have an incremental amount level, level otherwise. Equivalent to flooring {@link #getEffectiveLevel()}
   * @return  Level of the modifier, possibly reduced due to a partial level
   */
  public int intEffectiveLevel() {
    return level;
  }


  /* Incremental */

  /**
   * Gets the incremental amount
   * @param fallback  Value to return if not incremental
   */
  public int getAmount(int fallback) {
    return fallback;
  }

  /** Gets the amount needed for this incremental level, will be 0 for non-incremental */
  public int getNeeded() {
    return 0;
  }

  /** Gets the display name for this entry */
  public Component getDisplayName() {
    return modifier.get().getDisplayName(level);
  }


  /* Withers */

  /**
   * Adds the given amount to the modifier
   * @param amount  Amount to add, if 0 no change will be made
   * @param needed  Amount needed, if 0 no change will be made
   * @return  Modifier entry, will be
   */
  public ModifierEntry addAmount(int amount, int needed) {
    // if a need but no amount, the level won't change so don't make a new instance
    if (needed <= 0 || amount <= 0) {
      return this;
    }
    // static constructor automatically handles cases of needed being 0/amount being full (inon-incremental level raise) and amount being 0 (ignore level raise)
    return IncrementalModifierEntry.of(modifier, level + 1, amount, needed);
  }

  /**
   * Combines this entry with the other entry
   * @param other  Other entry to merge, precondition is the same modifier type
   * @return  New merged modifier
   */
  public ModifierEntry merge(ModifierEntry other) {
    if (!this.getId().equals(other.getId())) {
      throw new IllegalArgumentException("Modifiers do not match, have " + getId() + " but was given " + other.getId());
    }
    // we know we are not incremental, so the only thing to merge is our level with their level
    // this automatically handles other being incremental
    return other.withLevel(level + other.level);
  }


  /* Comparison */

  /** Checks if this entry matches the given modifier */
  public boolean matches(ModifierId id) {
    return modifier.getId().equals(id);
  }

  /** Checks if this entry matches the given modifier */
  public boolean matches(Modifier modifier) {
    return matches(modifier.getId());
  }

  /** Checks if the modifier is in the given tag */
  public boolean matches(TagKey<Modifier> tag) {
    return modifier.is(tag);
  }

  @Override
  public int compareTo(ModifierEntry other) {
    Modifier mod1 = this.getModifier(), mod2 = other.getModifier();
    int priority1 = mod1.getPriority(), priority2 = mod2.getPriority();
    // sort by priority first if different
    if (priority1 != priority2) {
      // reversed order so higher goes first
      return Integer.compare(priority2, priority1);
    }
    // fallback to ID path, approximates localized name so we get mostly alphabetical sort in the tooltip
    return mod1.getId().getPath().compareTo(mod2.getId().getPath());
  }


  /* Serializing */

  /** Reads a modifier entry from NBT */
  public static ModifierEntry readFromNBT(CompoundTag tag) {
    if (tag.contains(TAG_MODIFIER, Tag.TAG_STRING)) {
      ModifierId id = ModifierId.tryParse(tag.getString(TAG_MODIFIER));
      int level = tag.getInt(TAG_LEVEL);
      if (id != null && level > 0) {
        // incremental just has more tags, if they are missing they will just 0 and of will give us the base class
        return IncrementalModifierEntry.of(id, level, tag.getInt(TAG_AMOUNT), tag.getInt(TAG_NEEDED));
      }
    }
    return EMPTY;
  }

  /** Writes this tag to NBT */
  public CompoundTag serializeToNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString(TAG_MODIFIER, modifier.getId().toString());
    tag.putInt(TAG_LEVEL, level);
    return tag;
  }


  /** Object */

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModifierEntry entry = (ModifierEntry)o;
    return this.matches(entry.getId()) && level == entry.level;
  }

  @Override
  public int hashCode() {
    return 31 * modifier.hashCode() + level;
  }

  @Override
  public String toString() {
    return "ModifierEntry{" + modifier.getId() + ",level=" + level + '}';
  }
}
