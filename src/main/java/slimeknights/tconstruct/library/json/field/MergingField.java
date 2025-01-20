package slimeknights.tconstruct.library.json.field;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.util.typed.TypedMap;

/**
 * Field which creates a JSON object to contain its value.
 * @param field  Determines how the contents are added to the object. Getter goes from the target parent.
 * @param key    Key of the object inside the parent JSON
 * @param mode   Determines how the field deals with the object not being present. Note it always will error if the field contains a non-object.
 * @param <T>  Field type
 * @param <P>  Parent type
 */
public record MergingField<T,P>(LoadableField<T,P> field, String key, MissingMode mode) implements LoadableField<T,P> {
  public enum MissingMode {
    /** Object must exist, and will be created during datagen */
    DISALLOWED,
    /** Object is optional at runtime, but will be created at datagen */
    CREATE,
    /** Object is optional at runtime and will not be created at datagen */
    IGNORE
  }

  @Override
  public T get(JsonObject json, String key, TypedMap context) {
    // disallowed really just improves the error message over create in the case of disallowed being a required field
    if (mode == MissingMode.DISALLOWED || json.has(key)) {
      return field.get(GsonHelper.getAsJsonObject(json, key), context);
    } else {
      return field.get(new JsonObject(), context);
    }
  }

  @Override
  public void serialize(P parent, JsonObject json) {
    // if we have the object, write to it
    if (json.has(key)) {
      field.serialize(parent, GsonHelper.getAsJsonObject(json, key));
    } else if (mode != MissingMode.IGNORE) {
      // if we don't have the object, create it unless to ignore
      JsonObject writeTo = new JsonObject();
      field.serialize(parent, writeTo);
      json.add(key, writeTo);
    }
  }

  @Override
  public T decode(FriendlyByteBuf buffer, TypedMap context) {
    return field.decode(buffer, context);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, P parent) {
    field.encode(buffer, parent);
  }
}
