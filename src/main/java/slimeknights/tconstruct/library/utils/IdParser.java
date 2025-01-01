package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import javax.annotation.Nullable;
import java.util.function.Function;

/** Helper to parse variants of resource locations, doubles as a loadable. */
public record IdParser<T extends ResourceLocation>(Function<String, T> constructor, String name) implements StringLoadable<T> {
  /**
   * Creates a new ID from the given string
   * @param string  String
   * @return  ID, or null if invalid
   */
  @Nullable
  public T tryParse(String string) {
    try {
      return constructor.apply(string);
    } catch (ResourceLocationException resourcelocationexception) {
      return null;
    }
  }

  @Override
  public T parseString(String text, String key) {
    T location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a " + name + " ID, was '" + text + "'");
    }
    return location;
  }

  @Override
  public String getString(T object) {
    return object.toString();
  }

  @Override
  public T decode(FriendlyByteBuf buf, TypedMap context) {
    return constructor.apply(buf.readUtf(Short.MAX_VALUE));
  }

  @Override
  public void encode(FriendlyByteBuf buffer, T object) throws EncoderException {
    buffer.writeResourceLocation(object);
  }
}
