package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;

/** Wrapper around a record loadable that prints a deprecated message when used */
public record DeprecatedLoader<T extends R,R extends IHaveLoader>(GenericLoaderRegistry<R> registry, RecordLoadable<T> loader, String replacement) implements RecordLoadable<T> {
  /** Creates a deprecated loader for a modifier module */
  public static <T extends ModifierModule> RecordLoadable<T> modifier(RecordLoadable<T> loader, String replacement) {
    return new DeprecatedLoader<>(ModifierModule.LOADER, loader, replacement);
  }

  @Override
  public T deserialize(JsonObject json, TypedMap context) {
    String debug = context.get(ContextKey.DEBUG);
    debug = debug != null ? " while parsing " + debug : "";
    TConstruct.LOG.warn("Found deprecated {} loader {}{}, {}", registry.getName(), registry.getName(this), debug, replacement);
    return loader.deserialize(json, context);
  }

  @Override
  public void serialize(T object, JsonObject json) {
    loader.serialize(object, json);
  }

  @Override
  public T decode(FriendlyByteBuf buffer, TypedMap context) {
    return loader.decode(buffer, context);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, T value) {
    loader.encode(buffer, value);
  }
}
