package slimeknights.tconstruct.library.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.tconstruct.common.config.Config;

import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Utility that helps get the preferred item from a tag based on mod ID.
 * @param <T>  Registry type
 */
public class TagPreference<T extends IForgeRegistryEntry<T>> {
  /** Map of each tag type to the preference instance for that type */
  private static final Map<Class<?>, TagPreference<?>> PREFERENCE_MAP = new IdentityHashMap<>();

  /**
   * Gets the tag preference instance associated with the given tag collection
   * @param clazz  Tag class
   * @param <T>    Tag value type
   * @return  Tag preference instance
   */
  @SuppressWarnings("unchecked")
  public static <T extends IForgeRegistryEntry<T>> TagPreference<T> getInstance(Class<T> clazz, Supplier<ITagCollection<T>> collection) {
    // should always be the right instance as only we add entries to the map
    return (TagPreference<T>) PREFERENCE_MAP.computeIfAbsent(clazz, c -> new TagPreference<>(collection));
  }

  /**
   * Gets an instance for item tags
   * @return  Instance for item tags
   */
  public static TagPreference<Item> getItems() {
    return getInstance(Item.class, () -> TagCollectionManager.getManager().getItemTags());
  }

  /** Supplier to tag collection */
  private final Supplier<ITagCollection<T>> collection;

  /** Specific cache to this tag preference class type */
  private final Map<ResourceLocation, Optional<T>> preferenceCache = new HashMap<>();

  private TagPreference(Supplier<ITagCollection<T>> collection) {
    this.collection = collection;
    MinecraftForge.EVENT_BUS.addListener(this::clearCache);
  }

  /**
   * Clears the tag cache from the event
   * @param event  Tag event
   */
  private void clearCache(TagsUpdatedEvent.VanillaTagTypes event) {
    preferenceCache.clear();
  }

  /**
   * Gets the sort index of an entry based on the tag preference list
   * @param entry  Registry entry to check
   * @return  Sort index for that entry
   */
  private static int getSortIndex(IForgeRegistryEntry<?> entry) {
    List<String> entries = Config.COMMON.tagPreferences.get();
    // check the index of the namespace in the preference list
    int index = entries.indexOf(Objects.requireNonNull(entry.getRegistryName()).getNamespace());
    // if missing, declare last
    if (index == -1) {
      return entries.size();
    }
    return index;
  }

  /**
   * Gets the preferred value from a tag based on mod ID
   * @param tag    Tag to fetch
   * @return  Preferred value from the tag, or empty optional if the tag is empty
   */
  public Optional<T> getPreference(ITag<T> tag) {
    // fetch cached value if we have one
    ResourceLocation tagName = collection.get().getValidatedIdFromTag(tag);
    return preferenceCache.computeIfAbsent(tagName, name -> {
      // if no items, empty optional
      if (tag instanceof IOptionalNamedTag && ((IOptionalNamedTag<?>) tag).isDefaulted()) {
        return Optional.empty();
      }
      List<? extends T> elements = tag.getAllElements();
      if (elements.isEmpty()) {
        return Optional.empty();
      }

      // if size 1, quick exit
      if (elements.size() == 1) {
        return Optional.of(elements.get(0));
      }

      // copy and sort list
      List<? extends T> sortedElements = Lists.newArrayList(elements);
      sortedElements.sort(Comparator.comparingInt(TagPreference::getSortIndex));
      // return first element, its the preference
      return Optional.of(sortedElements.get(0));
    });
  }

  /**
   * Gets an entry for the given tag
   * @param tag    Tag instance
   * @param count  Output count
   * @return  Tag preference entry
   */
  public Entry<T> getEntry(ITag<T> tag, int count) {
    return new Entry<>(this, tag, count);
  }

  /**
   * Gets an entry for the given tag
   * @param tag    Tag instance
   * @return  Tag preference entry
   */
  public Entry<T> getEntry(ITag<T> tag) {
    return getEntry(tag, 1);
  }


  /**
   * Deserializes an entry from JSON
   * @param json        Json instance
   * @return  Tag entry
   * @throws JsonSyntaxException  syntax exception
   */
  public Entry<T> deserialize(JsonObject json) {
    ITag<T> tag = collection.get().get(new ResourceLocation(JSONUtils.getString(json, "tag")));
    int count = JSONUtils.getInt(json, "count", 1);
    return new Entry<T>(this, tag, count);
  }

  /**
   * Helper class representing a single tag empty instance
   * @param <T>  Registry type
   */
  @AllArgsConstructor
  public static class Entry<T extends IForgeRegistryEntry<T>> {
    private final TagPreference<T> parent;
    private final ITag<T> tag;
    private final int count;

    /**
     * Checks if this entry is present
     * @return  True if the entry is present
     */
    public boolean isPresent() {
      return parent.getPreference(tag).isPresent();
    }

    /**
     * Runs a function if this entry is present
     * @param mapper  Mapper for results
     * @return Optional of result
     */
    public <O> Optional<O> map(BiFunction<T, Integer, O> mapper) {
      return parent.getPreference(tag)
                   .map(entry -> mapper.apply(entry, count));
    }

    /**
     * Serializes the entry to JSON
     * @return  Serialized entry
     */
    public JsonElement serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("tag", parent.collection.get().getValidatedIdFromTag(tag).toString());
      if (count != 1) {
        json.addProperty("count", count);
      }
      return json;
    }
  }
}
