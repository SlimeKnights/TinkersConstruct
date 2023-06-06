package slimeknights.tconstruct.library.json.serializer;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.Locale;
import java.util.function.Function;

/**
 * Loader for an object with a single enum key
 * @param <O>  Object type
 * @param <T>  Loader type
 */
public record GenericEnumLoader<O extends IHaveLoader<?>, T extends Enum<T>>(
  String key,
  Class<T> enumClass,
  Function<T,O> constructor,
  Function<O,T> getter
) implements IGenericLoader<O> {
  @Override
  public O deserialize(JsonObject json) {
    return constructor.apply(JsonHelper.getAsEnum(json, key, enumClass));
  }

  @Override
  public O fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(buffer.readEnum(enumClass));
  }

  @Override
  public void serialize(O object, JsonObject json) {
    json.addProperty(key, getter.apply(object).name().toLowerCase(Locale.ROOT));
  }

  @Override
  public void toNetwork(O object, FriendlyByteBuf buffer) {
    buffer.writeEnum(getter.apply(object));
  }
}
