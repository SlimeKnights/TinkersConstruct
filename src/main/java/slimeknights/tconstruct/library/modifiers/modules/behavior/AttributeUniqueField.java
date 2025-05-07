package slimeknights.tconstruct.library.modifiers.modules.behavior;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.function.Function;

/** Field for the unique key for an attribute. If the key is unset in JSON, defaults to the modifier name */
public record AttributeUniqueField<P>(String key, Function<P,String> getter) implements LoadableField<String,P> {
  public AttributeUniqueField(Function<P, String> getter) {
    this("unique", getter);
  }

  @Override
  public String get(JsonObject json, String key, TypedMap context) {
    if (json.has(key)) {
      return GsonHelper.getAsString(json, key);
    }
    ResourceLocation id = context.get(ContextKey.ID);
    if (id == null) {
      throw new JsonParseException("Missing modifier ID in context, cannot default " + key);
    }
    return id.getNamespace() + ".modifier." + id.getPath();
  }

  @Override
  public void serialize(P parent, JsonObject json) {
    String unique = getter.apply(parent);
    if (!unique.isEmpty()) {
      json.addProperty(key, unique);
    }
  }

  @Override
  public String decode(FriendlyByteBuf buffer, TypedMap typedMap) {
    return buffer.readUtf();
  }

  @Override
  public void encode(FriendlyByteBuf buffer, P parent) {
    buffer.writeUtf(getter.apply(parent));
  }
}
