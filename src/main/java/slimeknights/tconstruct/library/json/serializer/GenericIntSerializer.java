package slimeknights.tconstruct.library.json.serializer;

import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * Generic serializer for classes that just have a single int value
 * @deprecated use {@link slimeknights.mantle.data.GenericIntSerializer}
 */
@Deprecated
public class GenericIntSerializer<T extends IHaveLoader<?>> extends slimeknights.mantle.data.GenericIntSerializer<T> {
  public GenericIntSerializer(String key, IntFunction<T> constructor, ToIntFunction<T> getter) {
    super(key, constructor, getter);
  }
}
