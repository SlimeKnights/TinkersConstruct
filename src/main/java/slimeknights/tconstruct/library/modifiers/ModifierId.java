package slimeknights.tconstruct.library.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.utils.IdParser;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class ModifierId extends ResourceLocation {
  public static final IdParser<ModifierId> PARSER = new IdParser<>(ModifierId::new, "Modifier");

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
    return PARSER.tryParse(string);
  }

  /**
   * Gets a modifier ID from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static ModifierId getFromJson(JsonObject json, String key) {
    return PARSER.getFromJson(json, key);
  }

  /**
   * Gets a modifier ID from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static ModifierId convertFromJson(JsonElement json, String key) {
    return PARSER.convertFromJson(json, key);
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
