package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.network.packet.ISimplePacket;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;

import javax.annotation.Nullable;
import java.util.List;

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
}
