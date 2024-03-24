package slimeknights.tconstruct.library.modifiers;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import java.util.Objects;

/**
 * Data class holding a modifier with a level
 */
@RequiredArgsConstructor
public class ModifierEntry implements Comparable<ModifierEntry> {
  /** Loadable instance for parsing */
  public static final RecordLoadable<ModifierEntry> LOADABLE = RecordLoadable.create(
    ModifierId.PARSER.requiredField("name", ModifierEntry::getId),
    IntLoadable.FROM_ONE.defaultField("level", 1, true, ModifierEntry::getLevel),
    ModifierEntry::new);

  /** Modifier instance */
  private final LazyModifier modifier;
  /** Current level */
  @Getter @With
  private final int level;

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

  /**
   * Gets the level scaled based on attributes of modifier data. Used mainly for incremental modifiers.
   * @param tool  Tool context
   * @return  Entry level, possibly adjusted by tool properties
   */
  public float getEffectiveLevel(IToolContext tool) {
    return modifier.get().getEffectiveLevel(tool, level);
  }

  /** Gets the given hook from the modifier, returning default instance if not present */
  public final <T> T getHook(ModifierHook<T> hook) {
    return modifier.get().getHook(hook);
  }

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

  /**
   * Parses a modifier entry from JSON
   * @param json  JSON object
   * @return  Parsed JSON
   */
  public static ModifierEntry fromJson(JsonObject json) {
    return LOADABLE.deserialize(json);
  }

  /**
   * Converts this entry to JSON
   * @param json  JSON object to fill
   * @return  Json object of entry
   */
  public JsonObject toJson(JsonObject json) {
    LOADABLE.serialize(this, json);
    return json;
  }

  /**
   * Converts this entry to JSON
   * @return  Json object of entry
   */
  public JsonObject toJson() {
    return toJson(new JsonObject());
  }

  /**
   * Reads this modifier entry from the packet buffer
   * @param buffer  Buffer instance
   * @return  Read entry
   */
  public static ModifierEntry read(FriendlyByteBuf buffer) {
    return LOADABLE.fromNetwork(buffer);
  }

  /**
   * Writes this modifier entry to the packet buffer
   * @param buffer  Buffer instance
   */
  public void write(FriendlyByteBuf buffer) {
    LOADABLE.toNetwork(this, buffer);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModifierEntry entry = (ModifierEntry)o;
    return this.matches(entry.getId()) && level == entry.level;
  }

  @Override
  public int hashCode() {
    return 31 * modifier.hashCode() + Objects.hash(level);
  }

  @Override
  public String toString() {
    return "ModifierEntry{" + modifier.getId() + ",level=" + level + '}';
  }
}
