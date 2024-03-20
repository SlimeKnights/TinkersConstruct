package slimeknights.tconstruct.library.utils;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import slimeknights.mantle.network.packet.ISimplePacket;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** Helpers for a few JSON related tasks */
public class JsonUtils {
  private JsonUtils() {}

  /**
   * Reads an integer with a minimum value
   * @param json  Json
   * @param key   Key to read
   * @param min   Minimum and default value
   * @return  Read int
   * @throws JsonSyntaxException if the key is not an int or below the min
   */
  public static int getIntMin(JsonObject json, String key, int min) {
    int value = GsonHelper.getAsInt(json, key, min);
    if (value < min) {
      throw new JsonSyntaxException(key + " must be at least " + min);
    }
    return value;
  }

  /**
   * Reads an integer with a minimum value
   * @param json  Json element to parse as an integer
   * @param key   Key to read
   * @param min   Minimum
   * @return  Read int
   * @throws JsonSyntaxException if the key is not an int or below the min
   */
  public static int convertToIntMin(JsonElement json, String key, int min) {
    int value = GsonHelper.convertToInt(json, key);
    if (value < min) {
      throw new JsonSyntaxException(key + " must be at least " + min);
    }
    return value;
  }

  /** Called when the player logs in to send packets */
  public static void syncPackets(OnDatapackSyncEvent event, ISimplePacket... packets) {
    JsonHelper.syncPackets(event, TinkerNetwork.getInstance(), packets);
  }

  /** Creates a JSON object with the given key set to a resource location */
  public static JsonObject withLocation(String key, ResourceLocation value) {
    JsonObject json = new JsonObject();
    json.addProperty(key, value.toString());
    return json;
  }

  /** Creates a JSON object with the given type set, makes using {@link slimeknights.mantle.data.gson.GenericRegisteredSerializer} easier */
  public static JsonObject withType(ResourceLocation type) {
    return withLocation("type", type);
  }

  /**
   * Reads the result from the given JSON
   * @param element  element to parse
   * @param name    Tag name
   * @return  Item stack result
   * @throws com.google.gson.JsonSyntaxException If the syntax is invalid
   */
  public static ItemStack convertToItemStack(JsonElement element, String name) {
    if (element.isJsonPrimitive()) {
      return new ItemStack(GsonHelper.convertToItem(element, name));
    } else {
      return CraftingHelper.getItemStack(GsonHelper.convertToJsonObject(element, name), true);
    }
  }

  /**
   * Reads the result from the given JSON
   * @param parent  Parent JSON
   * @param name    Tag name
   * @return  Item stack result
   * @throws com.google.gson.JsonSyntaxException If the syntax is invalid
   */
  public static ItemStack getAsItemStack(JsonObject parent, String name) {
    return convertToItemStack(JsonHelper.getElement(parent, name), name);
  }

  /**
   * Serializes the given result to JSON
   * @param result  Result
   * @return  JSON element
   */
  public static JsonElement serializeItemStack(ItemStack result) {
    // if the item has NBT, write both, else write just the name
    String itemName = Registry.ITEM.getKey(result.getItem()).toString();
    if (result.hasTag()) {
      JsonObject jsonResult = new JsonObject();
      jsonResult.addProperty("item", itemName);
      int count = result.getCount();
      if (count > 1) {
        jsonResult.addProperty("count", count);
      }
      jsonResult.addProperty("nbt", Objects.requireNonNull(result.getTag()).toString());
      return jsonResult;
    } else {
      return new JsonPrimitive(itemName);
    }
  }

  /**
   * Parses a color as a string
   * @param color  Color to parse
   * @return  Parsed string
   */
  public static int parseColor(@Nullable String color) {
    if (color == null || color.isEmpty()) {
      return -1;
    }
    // only support 6 character colors here, simplified over the mantle version
    int length = color.length();
    if (length == 6) {
      try {
        return Integer.parseInt(color, 16);
      } catch (NumberFormatException ex) {
        // NO-OP
      }
    }
    throw new JsonSyntaxException("Invalid color '" + color + "'");
  }

  /** Writes the color as a 6 character string */
  public static String colorToString(int color) {
    return String.format("%06X", color);
  }


  /* Enum set helpers */

  /**
   * Parses a set from the given parent object
   * @param parent      Parent object
   * @param key         Key to fetch
   * @param enumClass  Enum class
   * @param <E>  Enum type
   * @return  Set of elements
   */
  public static <E extends Enum<E>> Set<E> deserializeEnumSet(JsonObject parent, String key, Class<E> enumClass) {
    return Set.copyOf(JsonHelper.parseList(parent, key, (e, k) -> JsonHelper.convertToEnum(e, k, enumClass)));
  }

  /** Writes an enum collection to a JSON array */
  public static <E extends Enum<E>> JsonArray serializeEnumCollection(Collection<E> elements) {
    JsonArray list = new JsonArray();
    for (E element : elements) {
      list.add(element.name().toLowerCase(Locale.ROOT));
    }
    return list;
  }

  /**
   * Reads a set of enums from the buffer
   * @param buffer     Buffer instance
   * @param enumClass  Enum class
   * @param <E>  Enum type
   * @return  Set of elements
   */
  public static <E extends Enum<E>> Set<E> readEnumSet(FriendlyByteBuf buffer, Class<E> enumClass) {
    int size = buffer.readVarInt();
    ImmutableSet.Builder<E> builder = ImmutableSet.builder();
    for (int i = 0; i < size; i++) {
      builder.add(buffer.readEnum(enumClass));
    }
    return builder.build();
  }

  /** Writes a collection of enums to the buffer */
  public static <E extends Enum<E>> void writeEnumCollection(FriendlyByteBuf buffer, Collection<E> collection) {
    buffer.writeVarInt(collection.size());
    for (E element : collection) {
      buffer.writeEnum(element);
    }
  }
}
