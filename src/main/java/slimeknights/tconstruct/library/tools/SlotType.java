package slimeknights.tconstruct.library.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Class handling slot types for modifiers
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlotType {
  /** Loadable for a slot type */
  public static final StringLoadable<SlotType> LOADABLE = StringLoadable.DEFAULT.comapFlatMap((name, error) -> {
    if (!isValidName(name)) {
      throw error.create("Invalid slot type name '" + name + '\'');
    }
    return SlotType.getOrCreate(name);
  }, SlotType::getName);

  /** Key for uppercase slot name */
  private static final String KEY_PREFIX = TConstruct.makeTranslationKey("stat", "slot.prefix.");
  /** Key for lowercase slot name */
  public static final String KEY_DISPLAY = TConstruct.makeTranslationKey("stat", "slot.display.");
  /** Map of instances for each name */
  private static final Map<String,SlotType> SLOT_TYPES = new HashMap<>();
  /** List of all slots in the order they were added */
  private static final List<SlotType> ALL_SLOTS = new ArrayList<>();

  /** Regex to validate slot type strings */
  private static final Pattern VALIDATOR = Pattern.compile("^[a-z0-9_]*$");

  /** Common slot type for modifiers with many levels */
  public static final SlotType UPGRADE = create("upgrades", 0xFFCCBA47);
  /** Slot type for protection based modifiers on armor */
  public static final SlotType DEFENSE = create("defense", 0xFFA8FFA0);
  /** Rare slot type for powerful and rather exclusive modifiers */
  public static final SlotType ABILITY = create("abilities", 0xFFB8A0FF);
  /** Slot type used in the soul forge */
  public static final SlotType SOUL = create("souls", -1);

  /** Just makes sure static initialization is done early enough */
  public static void init() {}

  /** Checks if the given slot name is valid */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean isValidName(String name) {
    return VALIDATOR.matcher(name).matches();
  }

  /**
   * Registers the given slot type.
   * Note that you will also want to define a texture for the creative modifier and JEI using {@link slimeknights.mantle.client.model.NBTKeyModel#registerExtraTexture(ResourceLocation, String, ResourceLocation)}
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
    SlotType type = new SlotType(name, TextColor.fromRgb(color));
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
  public static SlotType read(FriendlyByteBuf buffer) {
    return getOrCreate(buffer.readUtf());
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
  private final TextColor color;

  /** Cached text component display names */
  private Component displayName = null;

  /** Gets the display name for display in a title */
  public String getPrefix() {
    return KEY_PREFIX + name;
  }

  /** Gets the display name for display in a sentence */
  public Component getDisplayName() {
    if (displayName == null) {
      displayName = Component.translatable(KEY_DISPLAY + name);
    }
    return displayName;
  }

  /** Writes this slot type to the packet buffer */
  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(name);
  }

  @Override
  public String toString() {
    return "SlotType{" + name + '}';
  }

  /** Data object representing a slot type and count */
  public record SlotCount(SlotType type, int count) {
    public static final Loadable<SlotCount> LOADABLE = new Loadable<>() {
      @Override
      public SlotCount convert(JsonElement element, String key) {
        JsonObject json = GsonHelper.convertToJsonObject(element, key);
        if (json.entrySet().size() != 1) {
          throw new JsonSyntaxException("Cannot set multiple slot types");
        }
        Entry<String,JsonElement> entry = json.entrySet().iterator().next();
        String typeString = entry.getKey();
        if (!SlotType.isValidName(typeString)) {
          throw new JsonSyntaxException("Invalid slot type name '" + typeString + "'");
        }
        SlotType slotType = SlotType.getOrCreate(typeString);
        int slots = IntLoadable.FROM_ONE.convert(entry.getValue(), "count");
        return new SlotCount(slotType, slots);
      }

      @Override
      public JsonElement serialize(SlotCount slots) {
        JsonObject json = new JsonObject();
        json.addProperty(slots.type.getName(), slots.count);
        return json;
      }

      @Override
      public SlotCount decode(FriendlyByteBuf buffer) {
        return new SlotCount(SlotType.read(buffer), buffer.readVarInt());
      }

      @Override
      public void encode(FriendlyByteBuf buffer, SlotCount slots) {
        buffer.writeVarInt(slots.count());
        slots.type().write(buffer);
      }

      @Override
      public <P> LoadableField<SlotCount,P> nullableField(String key, Function<P,SlotCount> getter) {
        return new NullableSlotCountField<>(key, getter);
      }
    };

    /** Nullable field which compacts slot counts in the buffer */
    private record NullableSlotCountField<P>(String key, Function<P,SlotCount> getter) implements LoadableField<SlotCount,P> {
      @Nullable
      @Override
      public SlotCount get(JsonObject json) {
        return LOADABLE.getOrDefault(json, key, null);
      }

      @Override
      public void serialize(P parent, JsonObject json) {
        SlotCount count = getter.apply(parent);
        if (count != null) {
          json.add(key, LOADABLE.serialize(count));
        }
      }

      @Nullable
      @Override
      public SlotCount decode(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        if (count == 0) {
          return null;
        }
        return new SlotCount(SlotType.read(buffer), count);
      }

      @Override
      public void encode(FriendlyByteBuf buffer, P parent) {
        SlotCount slotCount = getter.apply(parent);
        if (slotCount == null) {
          buffer.writeVarInt(0);
        } else {
          buffer.writeVarInt(slotCount.count);
          slotCount.type.write(buffer);
        }
      }
    }

    /**
     * Gets the type for the given slot count
     */
    @Nullable
    public static SlotType type(@Nullable SlotCount count) {
      if (count == null) {
        return null;
      }
      return count.type();
    }

    /** Gets the given type of slots from the given slot count object */
    public static int get(@Nullable SlotCount slots, SlotType type) {
      if (slots != null && slots.type() == type) {
        return slots.count();
      }
      return 0;
    }

    @Override
    public String toString() {
      return "SlotCount{" + type.name + ": " + count + '}';
    }
  }
}
