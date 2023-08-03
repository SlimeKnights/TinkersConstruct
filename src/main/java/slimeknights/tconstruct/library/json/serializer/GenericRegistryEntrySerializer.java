package slimeknights.tconstruct.library.json.serializer;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.Objects;
import java.util.function.Function;

/**
 * Serializer for an object with a registry entry parameter
 * @param <O>  Object type
 * @param <V>  Registry entry type
 */
public record GenericRegistryEntrySerializer<O extends IHaveLoader<?>,V extends IForgeRegistryEntry<V>>(
  String key,
  IForgeRegistry<V> registry,
  Function<V,O> constructor,
  Function<O,V> getter
) implements IGenericLoader<O> {

  @Override
  public O deserialize(JsonObject json) {
    return constructor.apply(JsonHelper.getAsEntry(registry, json, key));
  }

  @Override
  public void serialize(O object, JsonObject json) {
    json.addProperty(key, Objects.requireNonNull(getter.apply(object).getRegistryName()).toString());
  }

  @Override
  public O fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(buffer.readRegistryIdUnsafe(registry));
  }

  @Override
  public void toNetwork(O object, FriendlyByteBuf buffer) {
    buffer.writeRegistryIdUnsafe(registry, getter.apply(object));
  }
}
