package slimeknights.tconstruct.library.json.serializer;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

import java.util.function.Function;

/**
 * Loader for a string value
 * @param <O>
 */
public record GenericStringLoader<O extends IHaveLoader<?>>(String key, Function<String,O> constructor, Function<O,String> getter) implements IGenericLoader<O> {
  @Override
  public O deserialize(JsonObject json) {
    return constructor.apply(GsonHelper.getAsString(json, key));
  }

  @Override
  public void serialize(O object, JsonObject json) {
    json.addProperty(key, getter.apply(object));
  }

  @Override
  public O fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(buffer.readUtf(Short.MAX_VALUE));
  }

  @Override
  public void toNetwork(O object, FriendlyByteBuf buffer) {
    buffer.writeUtf(getter.apply(object));
  }
}
