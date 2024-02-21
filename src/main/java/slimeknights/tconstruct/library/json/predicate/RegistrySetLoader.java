package slimeknights.tconstruct.library.json.predicate;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Generic loader for reading a loadable object with a set of registry objects
 * @param <R>  Registry type
 * @param <T>  Loader object type
 */
public record RegistrySetLoader<R extends IForgeRegistryEntry<R>, T extends IHaveLoader<?>>(IForgeRegistry<R> registry, Function<Set<R>, T> constructor, Function<T, Set<R>> getter, String name) implements IGenericLoader<T> {
  @Override
  public T deserialize(JsonObject json) {
    Set<R> set = ImmutableSet.copyOf(JsonHelper.parseList(json, name, (element, jsonKey) -> {
      ResourceLocation objectKey = JsonHelper.convertToResourceLocation(element, jsonKey);
      if (registry.containsKey(objectKey)) {
        return registry.getValue(objectKey);
      }
      throw new JsonSyntaxException("Unknown " + name + " '" + objectKey + "'");
    }));
    return constructor.apply(set);
  }

  @Override
  public void serialize(T object, JsonObject json) {
    JsonArray array = new JsonArray();
    for (R entry : getter.apply(object)) {
      array.add(Objects.requireNonNull(entry.getRegistryName()).toString());
    }
    json.add(name, array);
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    ImmutableSet.Builder<R> builder = ImmutableSet.builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      builder.add(buffer.readRegistryIdUnsafe(registry));
    }
    return constructor.apply(builder.build());
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    Set<R> set = getter.apply(object);
    buffer.writeVarInt(set.size());
    for (R entry : set) {
      buffer.writeRegistryIdUnsafe(registry, entry);
    }
  }
}
