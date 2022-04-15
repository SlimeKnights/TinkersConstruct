package slimeknights.tconstruct.library.json.predicate;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.function.Function;

/**
 * Generic loader for a tag based JSON predicate
 * @param <T>  Tag registry key
 * @param <C>  Constructor for the predicate
 */
@RequiredArgsConstructor
public class TagPredicateLoader<T,C extends IJsonPredicate<?>> implements IGenericLoader<C> {
  private final ResourceKey<? extends Registry<T>> registry;
  private final Function<TagKey<T>,C> constructor;
  private final Function<C,TagKey<T>> getter;

  @Override
  public C deserialize(JsonObject json) {
    return constructor.apply(TagKey.create(registry, JsonHelper.getResourceLocation(json, "tag")));
  }

  @Override
  public C fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(TagKey.create(registry, buffer.readResourceLocation()));
  }

  @Override
  public void serialize(C object, JsonObject json) {
    json.addProperty("tag", getter.apply(object).location().toString());
  }

  @Override
  public void toNetwork(C object, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(getter.apply(object).location());
  }
}
