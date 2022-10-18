package slimeknights.tconstruct.library.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Static helpers for generic tag loading */
public class GenericTagUtil {
  private GenericTagUtil() {}

  /** Creates a map of reverse tags for the given map of tags */
  public static <T, I extends ResourceLocation> Map<I,Set<TagKey<T>>> reverseTags(ResourceKey<? extends Registry<T>> registry, Function<T,I> keyMapper, Map<ResourceLocation,Tag<T>> tags) {
    Map<I,ImmutableSet.Builder<TagKey<T>>> reverseTags = new HashMap<>();
    Function<I,Builder<TagKey<T>>> makeSet = id -> ImmutableSet.builder();
    for (Entry<ResourceLocation,Tag<T>> entry : tags.entrySet()) {
      TagKey<T> key = TagKey.create(registry, entry.getKey());
      for (T value : entry.getValue().getValues()) {
        reverseTags.computeIfAbsent(keyMapper.apply(value), makeSet).add(key);
      }
    }
    return reverseTags.entrySet().stream()
                      .collect(Collectors.toMap(Entry::getKey, entry->entry.getValue().build()));
  }

  /** Decodes a map of tags from the packet */
  public static <T> Map<ResourceLocation,Tag<T>> decodeTags(FriendlyByteBuf buf, Function<ResourceLocation,T> valueGetter) {
    ImmutableMap.Builder<ResourceLocation,Tag<T>> builder = ImmutableMap.builder();
    int mapSize = buf.readVarInt();
    for (int i = 0; i < mapSize; i++) {
      ResourceLocation tagId = buf.readResourceLocation();
      int tagSize = buf.readVarInt();
      ImmutableList.Builder<T> tagBuilder = ImmutableList.builder();
      for (int j = 0; j < tagSize; j++) {
        tagBuilder.add(valueGetter.apply(buf.readResourceLocation()));
      }
      builder.put(tagId, new Tag<>(tagBuilder.build()));
    }
    return builder.build();
  }

  /** Writes a map of tags to a packet */
  public static <T> void encodeTags(FriendlyByteBuf buf, Function<T,ResourceLocation> keyGetter, Map<ResourceLocation,Tag<T>> tags) {
    buf.writeVarInt(tags.size());
    for (Entry<ResourceLocation,Tag<T>> entry : tags.entrySet()) {
      buf.writeResourceLocation(entry.getKey());
      List<T> values = entry.getValue().getValues();
      buf.writeVarInt(values.size());
      for (T value : values) {
        buf.writeResourceLocation(keyGetter.apply(value));
      }
    }
  }
}
