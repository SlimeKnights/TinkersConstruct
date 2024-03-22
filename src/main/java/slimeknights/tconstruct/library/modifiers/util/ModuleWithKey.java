package slimeknights.tconstruct.library.modifiers.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;

import javax.annotation.Nullable;

/**
 * Shared boilerplate for a module with a nullable key that can alternatively be the modifier ID
 */
public interface ModuleWithKey {
  /** Field for building loadables */
  LoadableField<ResourceLocation,ModuleWithKey> FIELD = Loadables.RESOURCE_LOCATION.nullableField("key", ModuleWithKey::key);

  /** Gets the key for the module */
  default ResourceLocation getKey(Modifier modifier) {
    ResourceLocation key = key();
    if (key != null) {
      return key;
    }
    return modifier.getId();
  }

  /** Gets the key field from the record */
  @Nullable
  ResourceLocation key();

  /**
   * Parses the key from JSON
   * @param json  Json object
   * @return  Key, or null if not present
   */
  @Nullable
  static ResourceLocation parseKey(JsonObject json) {
    if (json.has("key")) {
      return JsonHelper.getResourceLocation(json, "key");
    }
    return null;
  }

  /** Reads the key from the network */
  @Nullable
  static ResourceLocation fromNetwork(FriendlyByteBuf buffer) {
    if (buffer.readBoolean()) {
      return buffer.readResourceLocation();
    }
    return null;
  }

  /** Writes the key to the network */
  static void toNetwork(@Nullable ResourceLocation key, FriendlyByteBuf buffer) {
    if (key != null) {
      buffer.writeBoolean(true);
      buffer.writeResourceLocation(key);
    } else {
      buffer.writeBoolean(false);
    }
  }
}
