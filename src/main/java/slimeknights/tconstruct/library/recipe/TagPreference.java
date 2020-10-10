package slimeknights.tconstruct.library.recipe;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility that helps get the preferred item from a tag based on mod ID.
 * TODO: clear this somewhere
 */
@NoArgsConstructor
public class TagPreference<T extends IForgeRegistryEntry<T>> {
  /** Map of each tag type to the preference instance for that type */
  private static final Map<Class<?>, TagPreference<?>> PREFERENCE_MAP = new IdentityHashMap<>();
  /** Specific cache to this tag preference class type */
  private final Map<ResourceLocation, Optional<T>> preferenceCache = new HashMap<>();

  /**
   * Gets the preferred value from a tag based on mod ID
   * @param clazz  Tag class type
   * @param tag    Tag to fetch
   * @param <T>    Tag value type
   * @return  Preferred value from the tag, or empty optional if the tag is empty
   */
  @SuppressWarnings("unchecked")
  public static <T extends IForgeRegistryEntry<T>> Optional<T> getPreference(Class<T> clazz, INamedTag<? extends T> tag) {
    // should always be the right instance as only we add entries to the map
    TagPreference<T> preference = (TagPreference<T>) PREFERENCE_MAP.computeIfAbsent(clazz, c -> new TagPreference<>());

    // fetch cached value if we have one
    // TODO: is it possible to cache by tag instance so we don't need a named tag? alternatively, can we fetch the tag name in a more generic way?
    return preference.preferenceCache.computeIfAbsent(tag.getName(), name -> {
      // if no items, empty optional
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
   * Gets the sort index of an entry based on the tag preference list
   * @param entry  Registry entry to check
   * @return  Sort index for that entry
   */
  private static int getSortIndex(IForgeRegistryEntry<?> entry) {
    // TODO: cache this, its a bit inefficient to fetch every time
    List<String> entries = Collections.emptyList();//Config.COMMON.tagPreferences.get();
    // check the index of the namespace in the preference list
    int index = entries.indexOf(Objects.requireNonNull(entry.getRegistryName()).getNamespace());
    // if missing, declare last
    if (index == -1) {
      return entries.size();
    }
    return index;
  }
}
