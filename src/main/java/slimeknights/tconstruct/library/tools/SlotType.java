package slimeknights.tconstruct.library.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Class handling slot types for modifiers
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlotType {
  /** Key for uppercase slot name */
  private static final String KEY_PREFIX = TConstruct.makeTranslationKey("stat", "slot.prefix.");
  /** Key for lowercase slot name */
  private static final String KEY_DISPLAY = TConstruct.makeTranslationKey("stat", "slot.display.");
  /** Map of instances for each name */
  private static final Map<String,SlotType> SLOT_TYPES = new HashMap<>();
  /** List of all slots in the order they were added */
  private static final List<SlotType> ALL_SLOTS = new ArrayList<>();

  /** Regex to validate slot type strings */
  private static final Pattern VALIDATOR = Pattern.compile("^[a-z0-9_]*$");

  /** Common slot type for modifiers with many levels */
  public static final SlotType UPGRADE = create("upgrades", 0xFFCCBA47);
  /** Slot type for protection based modifiers on armor */
  public static final SlotType ARMOR = create("armor", 0xFFA8FFA0);
  /** Rare slot type for powerful and rather exclusive modifiers */
  public static final SlotType ABILITY = create("abilities", 0xFFB8A0FF);
  /** Slot type used in the soul forge */
  public static final SlotType SOUL = create("souls", -1);

  /** Just makes sure static initialization is done early enough */
  public static void init() {}

  /** Checks if the given slot name is valid */
  public static boolean isValidName(String name) {
    return VALIDATOR.matcher(name).matches();
  }

  /**
   * Registers the given slot type.
   * Note that you will also want to define a texture for the creative modifier and JEI using {@link slimeknights.tconstruct.library.client.model.NBTKeyModel#registerExtraTexture(ResourceLocation, String, ResourceLocation)}
   * @param name     Name of the slot type
   * @param color    Color of the slot
   * @return  Slot type instance for the name, only once instance for each name
   * @apiNote
   * @throws IllegalArgumentException  Error if a name is invalid
   */
  public static SlotType create(String name, int color) {
    if (SLOT_TYPES.containsKey(name)) {
      return SLOT_TYPES.get(name);
    }
    if (!isValidName(name)) {
      throw new IllegalArgumentException("Non [a-z0-9_] character in slot name: " + name);
    }
    SlotType type = new SlotType(name, Color.fromInt(color));
    SLOT_TYPES.put(name, type);
    ALL_SLOTS.add(type);
    return type;
  }

  /** Gets an existing slot type, or creates it if missing */
  public static SlotType getOrCreate(String name) {
    return create(name, -1);
  }

  /**
   * Gets the slot type for the given name, if present
   * @param name  Name
   * @return  Type name
   */
  @Nullable
  public static SlotType getIfPresent(String name) {
    return SLOT_TYPES.get(name);
  }

  /** Reads the slot type from the packet buffer */
  public static SlotType read(PacketBuffer buffer) {
    return getOrCreate(buffer.readString());
  }

  /**
   * Gets a collection of all registered slot types. Persists between worlds, so a slot type existing does not mean its used
   * @return  Collection of all slot types
   */
  public static Collection<SlotType> getAllSlotTypes() {
    return ALL_SLOTS;
  }

  /** Name of this slot type, used for serialization */
  @Getter
  private final String name;
  /** Gets the color of this slot type */
  @Getter
  private final Color color;

  /** Cached text component display names */
  private ITextComponent displayName = null;

  /** Gets the display name for display in a title */
  public String getPrefix() {
    return KEY_PREFIX + name;
  }

  /** Gets the display name for display in a sentence */
  public ITextComponent getDisplayName() {
    if (displayName == null) {
      displayName = new TranslationTextComponent(KEY_DISPLAY + name);
    }
    return displayName;
  }

  /** Writes this slot type to the packet buffer */
  public void write(PacketBuffer buffer) {
    buffer.writeString(name);
  }

  @Override
  public String toString() {
    return "SlotType{" + name + '}';
  }

  /** Data object representing a slot type and count */
  @Data
  public static class SlotCount {
    private final SlotType type;
    private final int count;

    /**
     * Parses the slot data from the given JSON
     * @param json  JSON
     * @return  Slot count data
     */
    public static SlotCount fromJson(JsonObject json) {
      if (json.entrySet().size() != 1) {
        throw new JsonSyntaxException("Cannot set multiple slot types");
      }
      Entry<String,JsonElement> entry = json.entrySet().iterator().next();
      String typeString = entry.getKey();
      if (!SlotType.isValidName(typeString)) {
        throw new JsonSyntaxException("Invalid slot type name '" + typeString + "'");
      }
      SlotType slotType = SlotType.getOrCreate(typeString);
      int slots = JsonUtils.getIntMin(entry.getValue(), "count", 1);
      return new SlotCount(slotType, slots);
    }

    /** Reads a slot count from the packet buffer */
    @Nullable
    public static SlotCount read(PacketBuffer buffer) {
      int count = buffer.readVarInt();
      if (count > 0) {
        SlotType type = SlotType.read(buffer);
        return new SlotCount(type, count);
      }
      return null;
    }

    /** Gets the given type of slots from the given slot count object */
    public static int get(@Nullable SlotCount slots, SlotType type) {
      if (slots != null && slots.getType() == type) {
        return slots.getCount();
      }
      return 0;
    }

    /** Writes this to the packet buffer */
    public static void write(@Nullable SlotCount slots, PacketBuffer buffer) {
      if (slots == null) {
        buffer.writeVarInt(0);
      } else {
        buffer.writeVarInt(slots.getCount());
        slots.getType().write(buffer);
      }
    }

    @Override
    public String toString() {
      return "SlotCount{" + type.name + ": " + count + '}';
    }
  }
}
