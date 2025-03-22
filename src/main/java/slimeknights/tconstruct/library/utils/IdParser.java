package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Helper to parse variants of resource locations, doubles as a loadable.
 * @see ResourceId
 */
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
  public T parseString(String text, String key, TypedMap context) {
    try {
      return constructor.apply(text);
    } catch (ResourceLocationException ex) {
      throw new JsonSyntaxException("Expected " + key + " to be a " + name + " ID, received invalid characters", ex);
    }
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


  /* Parsing helpers */

  /** Splits the given string into a namespace and path, using the given default mod ID */
  public static String[] decompose(String defaultDomain, String location, char separator) {
    String[] parts = { defaultDomain, location };
    int loc = location.indexOf(separator);
    if (loc >= 0) {
      parts[1] = location.substring(loc + 1);
      if (loc >= 1) {
        parts[0] = location.substring(0, loc);
      }
    }

    return parts;
  }

  /** Splits the given string into a namespace and path, using the given default mod ID */
  public static String[] decompose(String defaultDomain, String location) {
    return decompose(defaultDomain, location, ':');
  }

  /**
   * Attempts to read a resource ID from the given string reader
   * @param defaultDomain  Domain to use if the domain is unset
   * @param reader         Reader to read from
   * @return  Resource location, or exception if invalid
   * @throws CommandSyntaxException  If parsing fails
   */
  public static ResourceLocation read(String defaultDomain, StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    while(reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
      reader.skip();
    }
    String string = reader.getString().substring(start, reader.getCursor());
    String[] parts = decompose(defaultDomain, string);
    try {
      return new ResourceLocation(parts[0], parts[1]);
    } catch (ResourceLocationException ex) {
      reader.setCursor(start);
      throw ResourceLocation.ERROR_INVALID.createWithContext(reader);
    }
  }
}
