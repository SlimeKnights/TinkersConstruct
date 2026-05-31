package slimeknights.tconstruct.library.json.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.tconstruct.TConstruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** @deprecated use {@link slimeknights.mantle.recipe.condition.TagCombinationCondition#intersection(TagKey[])} */
@Deprecated(forRemoval = true)
public class TagIntersectionPresentCondition<T> implements ICondition {
  private static final ResourceLocation NAME = TConstruct.getResource("tag_intersection_present");
  public static final MapCodec<TagIntersectionPresentCondition<?>> CODEC = RecordCodecBuilder.mapCodec(
    instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("registry").forGetter(condition -> condition.names.get(0).registry().location()),
      ResourceLocation.CODEC.listOf().fieldOf("tags").forGetter(condition -> condition.names.stream().map(TagKey::location).toList())
    ).apply(instance, TagIntersectionPresentCondition::create)
  );

  private final List<TagKey<T>> names;

  public TagIntersectionPresentCondition(List<TagKey<T>> names) {
    if (names.isEmpty()) {
      throw new IllegalArgumentException("Cannot create a condition with no names");
    }
    this.names = names;
  }

  /** Creates a condition from a set of keys */
  @SafeVarargs
  public static <T> TagIntersectionPresentCondition<T> ofKeys(TagKey<T>... names) {
    return new TagIntersectionPresentCondition<>(Arrays.asList(names));
  }

  /** Creates a condition from a registry and a set of names */
  public static <T> TagIntersectionPresentCondition<T> ofNames(ResourceKey<? extends Registry<T>> registry, ResourceLocation... names) {
    return new TagIntersectionPresentCondition<>(Arrays.stream(names).map(name -> TagKey.create(registry, name)).toList());
  }

  public ResourceLocation getID() {
    return NAME;
  }

  @Override
  public MapCodec<? extends ICondition> codec() {
    return CODEC;
  }

  @Override
  public boolean test(IContext context) {
    // if there is just one tag, just needs to be filled
    List<Collection<Holder<T>>> tags = names.stream().map(context::getTag).toList();
    if (tags.size() == 1) {
      return !tags.get(0).isEmpty();
    }
    // if any remaining tag is empty, give up
    int count = tags.size();
    for (int i = 1; i < count; i++) {
      if (tags.get(i).isEmpty()) {
        return false;
      }
    }

    // all tags have something, so find the first item that is in all tags
    itemLoop:
    for (Holder<T> entry : tags.get(0)) {
      // find the first item contained in all other intersection tags
      for (int i = 1; i < count; i++) {
        if (!tags.get(i).contains(entry)) {
          continue itemLoop;
        }
      }
      // all tags contain the item? success
      return true;
    }
    // no item in all tags
    return false;
  }

  private static TagIntersectionPresentCondition<?> create(ResourceLocation registryName, List<ResourceLocation> tagNames) {
    ResourceKey<Registry<Object>> registry = ResourceKey.createRegistryKey(registryName);
    return new TagIntersectionPresentCondition<>(tagNames.stream().map(name -> TagKey.create(registry, name)).toList());
  }
}
