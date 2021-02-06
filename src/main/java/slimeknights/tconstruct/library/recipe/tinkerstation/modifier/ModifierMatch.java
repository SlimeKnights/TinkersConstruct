package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Helper class to make modifiers matching
 */
public abstract class ModifierMatch implements Predicate<List<ModifierEntry>> {
  /** Condition that always returns true. We require at least 0 matches from an empty list */
  public static final ModifierMatch ALWAYS = new ListMatch(Collections.emptyList(), 0);

  /**
   * Creates a new match from a single entry
   * @param modifier  Modifier
   * @param level     Level required
   * @return  Match instance
   */
  public static ModifierMatch entry(Modifier modifier, int level) {
    return new EntryMatch(new ModifierEntry(modifier, level));
  }

  /**
   * Creates a new match from a modifier with level 1
   * @param modifier  Modifier
   * @return  Match instance
   */
  public static ModifierMatch entry(Modifier modifier) {
    return new EntryMatch(new ModifierEntry(modifier, 1));
  }

  /**
   * Creates a new match from a list of entries
   * @param required  Number of entries that must match
   * @param matches  List of match options
   * @return  List modifier match
   */
  public static ModifierMatch list(int required, ModifierMatch... matches) {
    return new ListMatch(Arrays.asList(matches), required);
  }

  /**
   * Reads a modifier match from JSON
   * @param json  JSON instance
   * @return  Read instance
   */
  public static ModifierMatch deserialize(JsonObject json) {
    if (json.has("options")) {
      List<ModifierMatch> options = JsonHelper.parseList(json, "options", ModifierMatch::deserialize);
      int required = JSONUtils.getInt(json, "matches_needed");
      return new ListMatch(options, required);
    }

    ModifierEntry entry = ModifierEntry.fromJson(json);
    return new EntryMatch(entry);
  }

  /**
   * Reads a modifier match from the packet buffer
   * @param buffer  Packet buffer instance
   * @return  Modifier match instance
   */
  public static ModifierMatch read(PacketBuffer buffer) {
    int size = buffer.readVarInt();
    if (size == 1) {
      // single entry
      ModifierEntry entry = ModifierEntry.read(buffer);
      return new EntryMatch(entry);
    }
    // iterate through the requested size, reading an entry for each element
    int required = buffer.readVarInt();
    ImmutableList.Builder<ModifierMatch> builder = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      builder.add(read(buffer));
    }
    return new ListMatch(builder.build(), required);
  }

  /**
   * Serializes this entry as JSON
   * @return  JSON
   */
  public abstract JsonObject serialize();

  /**
   * Writes this match to the packet buffer
   * @param buffer  Buffer
   */
  public abstract void write(PacketBuffer buffer);

  /** Matches a single modifier entry */
  @RequiredArgsConstructor
  protected static class EntryMatch extends ModifierMatch {
    protected final ModifierEntry entry;

    @Override
    public boolean test(List<ModifierEntry> modifiers) {
      for (ModifierEntry entry : modifiers) {
        if (entry.getModifier() == this.entry.getModifier()) {
          return entry.getLevel() >= this.entry.getLevel();
        }
      }
      return false;
    }

    @Override
    public JsonObject serialize() {
      return entry.toJson();
    }

    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeVarInt(1);
      entry.write(buffer);
    }
  }

  /** Matches a list of modifier entries */
  @RequiredArgsConstructor
  protected static class ListMatch extends ModifierMatch {
    protected final List<ModifierMatch> options;
    protected final int required;

    @Override
    public boolean test(List<ModifierEntry> modifiers) {
      int matches = 0;
      for (ModifierMatch match : options) {
        if (match.test(modifiers)) {
          matches++;
        }
      }
      return matches >= required;
    }

    @Override
    public JsonObject serialize() {
      // if just one element, serialize that alone
      if (options.size() == 1) {
        return options.get(0).serialize();
      }

      // build a new object with all elements
      JsonArray array = new JsonArray();
      for (ModifierMatch match : options) {
        array.add(match.serialize());
      }
      JsonObject json = new JsonObject();
      json.add("options", array);
      json.addProperty("matches_needed", required);
      return json;
    }

    @Override
    public void write(PacketBuffer buffer) {
      if (options.size() == 1) {
        options.get(0).write(buffer);
      } else {
        buffer.writeVarInt(options.size());
        buffer.writeVarInt(required);
        for (ModifierMatch match : options) {
          match.write(buffer);
        }
      }
    }
  }
}
