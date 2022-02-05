package slimeknights.tconstruct.library.client.modifiers;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.Material;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This interface converts from tool data to quads for a modifier. The default just loads a flat texture, but can be extended to dynamically load textures.
 * There is one copy of a class implementing this interface per modifier
 */
public interface IUnbakedModifierModel {
  /**
   * Bakes the modifier model for a particular tool
   * @param smallTextureGetter  Gets a texture for the given name, returning null if the texture does not exist
   * @param largeTextureGetter  Gets a texture for the given name for large tools, returning null if the texture does not exist. Will be null if the tool does not do large textures
   * @return  Baked model using the given textures, or null if the modifier cannot be displayed on this tool (e.g. missing textures)
   */
  @Nullable
  IBakedModifierModel forTool(Function<String,Material> smallTextureGetter, Function<String,Material> largeTextureGetter);

  /**
   * Configures the modifier model using the given JSON data. Triggered when a model uses a json object instead of a string to select model type
   * @param data  Data from the modifiers model file
   * @return  Configured unbaked model
   */
  default IUnbakedModifierModel configure(JsonObject data) {
    return this;
  }
}
