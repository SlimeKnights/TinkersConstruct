package slimeknights.tconstruct.common.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.function.Function;

/** Loadable for dealing with  */
@RequiredArgsConstructor
public abstract class LegacyLoadable<T> implements RecordLoadable<T> {
  protected final RecordLoadable<T> base;

  @Override
  public JsonElement serialize(T object) {
    return base.serialize(object);
  }

  @Override
  public void serialize(T object, JsonObject json) {
    base.serialize(object, json);
  }


  /* NBT */

  @Override
  public void encode(FriendlyByteBuf buffer, T value) {
    base.encode(buffer, value);
  }

  @Override
  public T decode(FriendlyByteBuf buffer, TypedMap context) {
    return base.decode(buffer, context);
  }


  /* Fields */

  @Override
  public <P> LoadableField<T, P> nullableField(String key, Function<P, T> getter) {
    return base.nullableField(key, getter);
  }

  @Override
  public <P> LoadableField<T, P> defaultField(String key, T defaultValue, boolean serializeDefault, Function<P, T> getter) {
    return base.defaultField(key, defaultValue, serializeDefault, getter);
  }
}
