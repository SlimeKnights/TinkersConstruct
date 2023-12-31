package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.network.packet.ISimplePacket;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

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
   * TODO 1.19: rename to {@code convertToIntMin}
   * @param json  Json element to parse as an integer
   * @param key   Key to read
   * @param min   Minimum
   * @return  Read int
   * @throws JsonSyntaxException if the key is not an int or below the min
   */
  public static int getIntMin(JsonElement json, String key, int min) {
    int value = GsonHelper.convertToInt(json, key);
    if (value < min) {
      throw new JsonSyntaxException(key + " must be at least " + min);
    }
    return value;
  }

  /** @deprecated use {@link JsonHelper#convertToEntry(IForgeRegistry, JsonElement, String)} */
  @Deprecated
  public static <T extends IForgeRegistryEntry<T>> T convertToEntry(IForgeRegistry<T> registry, JsonElement element, String key) {
    return JsonHelper.convertToEntry(registry, element, key);
  }

  /** @deprecated use {@link JsonHelper#getAsEntry(IForgeRegistry, JsonObject, String)} */
  @Deprecated
  public static <T extends IForgeRegistryEntry<T>> T getAsEntry(IForgeRegistry<T> registry, JsonObject parent, String key) {
    return JsonHelper.getAsEntry(registry, parent, key);
  }

  /** @deprecated use {@link JsonHelper#getJson(Resource)} */
  @Deprecated
  @Nullable
  public static JsonObject getJson(Resource resource) {
    return JsonHelper.getJson(resource);
  }

  /** @deprecated use {@link JsonHelper#convertToEnum(JsonElement, String, Class)} */
  @Deprecated
  public static <T extends Enum<T>> T convertToEnum(JsonElement element, String key, Class<T> enumClass) {
    return JsonHelper.convertToEnum(element, key, enumClass);
  }

  /** @deprecated use {@link JsonHelper#getAsEnum(JsonObject, String, Class)} */
  @Deprecated
  public static <T extends Enum<T>> T getAsEnum(JsonObject json, String key, Class<T> enumClass) {
    return JsonHelper.getAsEnum(json, key, enumClass);
  }

  /** @deprecated use {@link JsonHelper#getFileInAllDomainsAndPacks(ResourceManager, String, String)} */
  @Deprecated
  public static List<JsonObject> getFileInAllDomainsAndPacks(ResourceManager manager, String path) {
    return JsonHelper.getFileInAllDomainsAndPacks(manager, path, null);
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

  /** Creates a JSON object with the given type set, makes using {@link slimeknights.mantle.data.GenericRegisteredSerializer} eaiser */
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
    String itemName = Objects.requireNonNull(result.getItem().getRegistryName()).toString();
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
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @param fallback  Fallback if key is not present
   * @return  Resource location parsed
   */
  public static ResourceLocation getResourceLocation(JsonObject json, String key, ResourceLocation fallback) {
    if (json.has(key)) {
      return JsonHelper.getResourceLocation(json, key);
    }
    return fallback;
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


  /* Block States */

  /**
   * Converts the given JSON element into a block state
   * @param element  Element to convert
   * @param key      Element key
   * @return  Block state
   * @throws JsonSyntaxException  if a property does not parse or the element is the wrong type
   */
  public static BlockState convertToBlockState(JsonElement element, String key) {
    // primitive means its a block directly
    if (element.isJsonPrimitive()) {
      return JsonHelper.convertToEntry(ForgeRegistries.BLOCKS, element, key).defaultBlockState();
    }
    if (element.isJsonObject()) {
      return convertToBlockState(element.getAsJsonObject());
    }
    throw new JsonSyntaxException("Expected " + key + " to be a string or an object, was " + GsonHelper.getType(element));
  }

  /**
   * Converts the given JSON element into a block state
   * @param parent   Parent containing the block state
   * @param key      Element key
   * @return  Block state
   * @throws JsonSyntaxException  if a property does not parse or the element is missing or the wrong type
   */
  public static BlockState getAsBlockState(JsonObject parent, String key) {
    if (parent.has(key)) {
      return convertToBlockState(parent.get(key), key);
    }
    throw new JsonSyntaxException("Missing " + key + ", expected to find a string or an object");
  }

  /**
   * Sets the property
   * @param state     State before changes
   * @param property  Property to set
   * @param name      Value name
   * @param <T>  Type of property
   * @return  State with the property
   * @throws JsonSyntaxException  if the property has no element with the given name
   */
  private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<T> property, String name) {
    Optional<T> value = property.getValue(name);
    if (value.isPresent()) {
      return state.setValue(property, value.get());
    }
    throw new JsonSyntaxException("Property " + property + " does not contain value " + name);
  }

  /**
   * Converts the given JSON object into a block state
   * @param json  Json object containing "block" and "properties"
   * @return  Block state
   * @throws JsonSyntaxException  if any property name or property value is invalid
   */
  public static BlockState convertToBlockState(JsonObject json) {
    Block block = JsonHelper.getAsEntry(ForgeRegistries.BLOCKS, json, "block");
    BlockState state = block.defaultBlockState();
    if (json.has("properties")) {
      StateDefinition<Block,BlockState> definition = block.getStateDefinition();
      for (Entry<String,JsonElement> entry : GsonHelper.getAsJsonObject(json, "properties").entrySet()) {
        String key = entry.getKey();
        Property<?> property = definition.getProperty(key);
        if (property == null) {
          throw new JsonSyntaxException("Property " + key + " does not exist in block " + block);
        }
        state = setValue(state, property, GsonHelper.convertToString(entry.getValue(), key));
      }
    }
    return state;
  }

  /**
   * Serializes the given block state to JSON, essentially writes all values that differ from the state.
   * @param state  State
   * @return  JsonPrimitive of the block name if it matches the default state, JsonObject otherwise
   */
  public static JsonElement serializeBlockState(BlockState state) {
    Block block = state.getBlock();
    if (state == block.defaultBlockState()) {
      return new JsonPrimitive(Objects.requireNonNull(block.getRegistryName()).toString());
    }
    return serializeBlockState(state, new JsonObject());
  }

  /** Serializes the property if it differs in the default state */
  private static <T extends Comparable<T>> void serializeProperty(BlockState serialize, Property<T> property, BlockState defaultState, JsonObject json) {
    T value = serialize.getValue(property);
    if (!value.equals(defaultState.getValue(property))) {
      json.addProperty(property.getName(), property.getName(value));
    }
  }

  /**
   * Serializes the given block state to JSON, essentially writes all values that differ from the state
   * @param state  State
   * @return  JsonObject containing properties that differ from the default state
   */
  public static JsonObject serializeBlockState(BlockState state, JsonObject json) {
    Block block = state.getBlock();
    json.addProperty("block", Objects.requireNonNull(block.getRegistryName()).toString());
    BlockState defaultState = block.defaultBlockState();
    JsonObject properties = new JsonObject();
    for (Property<?> property : block.getStateDefinition().getProperties()) {
      serializeProperty(state, property, defaultState, properties);
    }
    if (properties.size() > 0) {
      json.add("properties", properties);
    }
    return json;
  }
}
