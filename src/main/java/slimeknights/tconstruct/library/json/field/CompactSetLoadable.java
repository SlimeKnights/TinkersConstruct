package slimeknights.tconstruct.library.json.field;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.Loadable;

import java.util.Set;

/**
 * Set loadable which can also load from a simple field.
 * TODO: migrate to Mantle's version once a new build is up.
 */
public class CompactSetLoadable<T> implements Loadable<Set<T>> {
  private final Loadable<T> base;
  private final Loadable<Set<T>> setLoadable;

  public CompactSetLoadable(Loadable<T> base, boolean allowEmpty) {
    this.base = base;
    this.setLoadable = base.set(allowEmpty ? 0 : 1);
  }

  @Override
  public Set<T> convert(JsonElement element, String key) {
    if (!element.isJsonArray()) {
      return Set.of(base.convert(element, key));
    }
    return setLoadable.convert(element, key);
  }

  @Override
  public JsonElement serialize(Set<T> collection) {
    if (collection.size() == 1) {
      JsonElement element = base.serialize(collection.iterator().next());
      // only return if its not an array; arrays means a conflict with deserializing
      // there is a small waste of work here in the case of array but you shouldn't be using compact with array serializing elements anyway
      if (!element.isJsonArray()) {
        return element;
      }
    }
    return setLoadable.serialize(collection);
  }

  @Override
  public Set<T> decode(FriendlyByteBuf buffer) {
    return setLoadable.decode(buffer);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, Set<T> value) {
    setLoadable.encode(buffer, value);
  }
}
