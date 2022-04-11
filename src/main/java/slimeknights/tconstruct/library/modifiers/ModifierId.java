package slimeknights.tconstruct.library.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class ModifierId extends ResourceLocation {

  public ModifierId(String resourceName) {
    super(resourceName);
  }

  public ModifierId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public ModifierId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /**
   * Creates a new modifier ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static ModifierId tryParse(String string) {
    try {
      return new ModifierId(string);
    } catch (ResourceLocationException resourcelocationexception) {
      return null;
    }
  }

  /**
   * Gets a modifier ID from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static ModifierId getFromJson(JsonObject json, String key) {
    String text = GsonHelper.getAsString(json, key);
    ModifierId location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a Modifier ID, was '" + text + "'");
    }
    return location;
  }

  /**
   * Gets a modifier ID from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static ModifierId convertFromJson(JsonElement json, String key) {
    String text = GsonHelper.convertToString(json, key);
    ModifierId location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a Modifier ID, was '" + text + "'");
    }
    return location;
  }

  /** Writes an ID to the packet buffer */
  public void toNetwork(FriendlyByteBuf buf) {
    buf.writeUtf(toString());
  }

  /** Reads an ID from the packet buffer */
  public static ModifierId fromNetwork(FriendlyByteBuf buf) {
    return new ModifierId(buf.readUtf(Short.MAX_VALUE));
  }
}
