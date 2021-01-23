package slimeknights.tconstruct.library.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionManager;
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
}
