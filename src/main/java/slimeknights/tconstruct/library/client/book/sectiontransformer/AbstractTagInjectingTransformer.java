package slimeknights.tconstruct.library.client.book.sectiontransformer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Logic for injecting pages based on a tag value */
@RequiredArgsConstructor
public abstract class AbstractTagInjectingTransformer<T> extends BookTransformer {
  private static final Comparator<PageData> COMPARATOR = Comparator.comparing(PageData::getTitle);

  private final ResourceKey<? extends Registry<T>> registry;
  private final ResourceLocation key;
  private final ResourceLocation pageType;

  /** Gets values in the tag */
  protected Iterator<T> getTagEntries(TagKey<T> tag) {
    return RegistryHelper.getTagValueStream(tag).iterator();
  }

  /** Gets the name to use for a page given its value */
  protected abstract ResourceLocation getId(T value);

  /** Creates a fallback page for when data does not exist */
  protected abstract PageContent createFallback(T value);

  /**
   * Inserts generated pages into the section
   * @param section    Section to insert pages
   * @param extraData  Data map from the section or page
   * @param index      Index to start inserting pages
   * @return  Number of pages added
   */
  protected int addPages(SectionData section, Map<ResourceLocation, JsonElement> extraData, int index) {
    JsonElement element = extraData.get(key);
    if (element != null) {
      try {
        // load the page and the folder to search
        JsonObject load = GsonHelper.convertToJsonObject(element, key.toString());
        String path = GsonHelper.getAsString(load, "path");
        boolean sort = GsonHelper.getAsBoolean(load, "sort", true);
        // not using standard default as we only want the trailing dot when not empty
        String prefix = "";
        if (load.has("prefix")) {
          prefix = GsonHelper.getAsString(load, "prefix") + ".";
        }

        // use a helper to allow changing how the tag is fetched for modifiers
        List<PageData> newPages = new ArrayList<>();
        Iterator<T> iterator = getTagEntries(TagKey.create(registry, JsonHelper.getResourceLocation(load, "tag")));
        while (iterator.hasNext()) {
          // create the page
          T value = iterator.next();
          PageData newPage = new PageData(true);
          newPage.parent = section;
          newPage.source = section.source;
          newPage.type = pageType;

          // the name and path are created from the ID directly
          // use the book domain as the folder, the object domain as page name
          ResourceLocation id = getId(value);
          newPage.name = prefix + id.getNamespace() + "." + id.getPath();
          String data = path + "/" + id.getNamespace() + "_" + id.getPath() + ".json";
          // if the path exists load the page, otherwise use a fallback option
          if (section.source.resourceExists(section.source.getResourceLocation(data))) {
            newPage.data = data;
          } else {
            newPage.content = createFallback(value);
          }
          newPage.load();
          newPages.add(newPage);
        }
        // sort new pages by title
        if (sort) {
          newPages.sort(COMPARATOR);
        }

        section.pages.addAll(index, newPages);
        return newPages.size();
      } catch (JsonParseException e) {
        TConstruct.LOG.error("Failed to parse tag for book page injecting", e);
      }
    }
    return 0;
  }

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      int i = addPages(section, section.extraData, 0);
      for (; i < section.pages.size(); i++) {
        // want to insert after the page in the loop
        // the index increase means we will continue iteration after the last new page
        i += addPages(section, section.pages.get(i).extraData, i+1);
      }
    }
  }
}
