package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentPageIconList;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.AbstractMaterialContent;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Section transformer to show a range of materials tiers in the book
 */
public class TierRangeMaterialSectionTransformer extends BookTransformer {
  private static final ResourceLocation KEY = TConstruct.getResource("material_tier");
  private static final IntRange TIER = new IntRange(0, Short.MAX_VALUE);

  private static final Map<ResourceLocation,MaterialType> MATERIAL_TYPES = new HashMap<>();

  public static final TierRangeMaterialSectionTransformer INSTANCE = new TierRangeMaterialSectionTransformer();

  /** Registers a new group of stat types to show on a page */
  public static void registerMaterialType(ResourceLocation id, BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> constructor, @Nullable Comparator<IMaterial> sortComparator, MaterialStatsId... stats) {
    if (MATERIAL_TYPES.putIfAbsent(id, new MaterialType(constructor, ImmutableSet.copyOf(stats), sortComparator)) != null) {
      throw new IllegalArgumentException("Duplicate material stat group " + id);
    }
  }

  /** Registers a new group of stat types to show on a page */
  public static void registerMaterialType(ResourceLocation id, BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> constructor, MaterialStatsId... stats) {
    registerMaterialType(id, constructor, null, stats);
  }

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      JsonElement element = section.extraData.get(KEY);
      if (element != null) {
        try {
          JsonObject json = GsonHelper.convertToJsonObject(element, KEY.toString());
          IntRange tier = TIER.getOrDefault(json, "tier");
          Function<MaterialVariantId,AbstractMaterialContent> pageBuilder;
          Set<MaterialStatsId> visibleStats;
          TagKey<IMaterial> tag = TinkerLoadables.MATERIAL_TAGS.getOrDefault(json, "tag", null);
          ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
          MaterialType typeData = MATERIAL_TYPES.get(type);
          if (typeData == null) {
            throw new JsonSyntaxException("Invalid material section type " + type);
          }
          visibleStats = typeData.visibleStats();
          pageBuilder = typeData.getMapping(GsonHelper.getAsBoolean(json, "detailed", false));
          createPages(book, section, new ValidMaterial(visibleStats, tier, tag), pageBuilder, typeData.sortComparator);
        } catch (JsonSyntaxException e) {
          TConstruct.LOG.error("Failed to parse material tier section data", e);
        }
      }
    }
  }

  /** Helper to create a material predicate */
  public record ValidMaterial(Set<MaterialStatsId> visibleStats, IntRange tier, @Nullable TagKey<IMaterial> tag) implements Predicate<IMaterial> {
    @Override
    public boolean test(IMaterial material) {
      if (!this.tier.test(material.getTier())) {
        return false;
      }
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialId id = material.getIdentifier();
      if (tag != null && !registry.isInTag(id, tag)) {
        return false;
      }
      // only show material stats for types with the proper stat types, as otherwise the page will be empty
      // if you want to show another stat type, just override this method/implement the parent class
      for (IMaterialStats stats : registry.getAllStats(id)) {
        if (visibleStats.contains(stats.getIdentifier())) {
          return true;
        }
      }
      return false;
    }
  }

  /** Internal record from the registry */
  private record MaterialType(BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> pageConstructor, Set<MaterialStatsId> visibleStats, @Nullable Comparator<IMaterial> sortComparator) {
    public Function<MaterialVariantId,AbstractMaterialContent> getMapping(boolean detailed) {
      return id -> pageConstructor.apply(id, detailed);
    }
  }

  /** Helper to add a page to the section */
  private static PageData createPage(SectionData data, String name, ResourceLocation type, PageContent content) {
    PageData page = new PageData(true);
    page.source = data.source;
    page.parent = data;
    page.name = name;
    page.type = type;
    page.content = content;
    page.load();
    return page;
  }

  /** @deprecated use {@link #createPages(BookData, SectionData, Predicate, Function, Comparator)} */
  @Deprecated(forRemoval = true)
  public static void createPages(BookData book, SectionData sectionData, Predicate<IMaterial> validMaterial, Function<MaterialVariantId,AbstractMaterialContent> pageCreator) {
    createPages(book, sectionData, validMaterial, pageCreator, null);
  }

  /**
   * Creates all the pages for the materials
   * @param book            Book data
   * @param sectionData     Section data
   * @param validMaterial   Predicate to validate materials
   * @param pageCreator     Logic to create a page
   */
  public static void createPages(BookData book, SectionData sectionData, Predicate<IMaterial> validMaterial, Function<MaterialVariantId,AbstractMaterialContent> pageCreator, @Nullable Comparator<IMaterial> sortComparator) {
    sectionData.source = BookRepository.DUMMY;
    sectionData.parent = book;

    Stream<IMaterial> materialStream = MaterialRegistry.getMaterials().stream().filter(validMaterial);
    if (sortComparator != null) {
      materialStream = materialStream.sorted(sortComparator);
    }
    List<IMaterial> materialList = materialStream.toList();
    if (materialList.isEmpty()) {
      return;
    }

    // calculate pages needed
    List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), sectionData, book.translate(sectionData.name), book.strings.get(String.format("%s.subtext", sectionData.name)));

    // create pages and add to index
    ListIterator<ContentPageIconList> iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    List<PageData> newPages = new ArrayList<>(materialList.size());
    for (IMaterial material : materialList) {
      MaterialId materialId = material.getIdentifier();
      AbstractMaterialContent contentMaterial = pageCreator.apply(materialId);
      PageData page = createPage(sectionData, materialId.toString(), contentMaterial.getId(), contentMaterial);
      newPages.add(page);

      SizedBookElement icon = new ItemElement(0, 0, 1f, contentMaterial.getDisplayStacks());
      while (!overview.addLink(icon, contentMaterial.getTitleComponent(), page)) {
        overview = iter.next();
      }
    }
    // insert new pages at the beginning after index, ensures its before any padding from the next section
    sectionData.pages.addAll(listPages.size(), newPages);
  }


  /* Helpers */

  /** Creates a feature extractor for a comparator that sorts based on a stat type being present, with order absent -> present */
  public static Function<IMaterial,Boolean> hasStatType(MaterialStatsId statType) {
    return mat -> MaterialRegistry.getInstance().getMaterialStats(mat.getIdentifier(), statType).isPresent();
  }

  /** Creates a feature extractor for a comparator that sorts based on a stat type being present, with order absent -> present */
  public static Function<IMaterial,Boolean> hasStatType(IMaterialStats statType) {
    return hasStatType(statType.getIdentifier());
  }

  /** Creates a feature extractor for a comparator that sorts based on tag order */
  public static Function<IMaterial,Integer> tagOrder(TagKey<IMaterial> tag) {
    return mat -> {
      List<IMaterial> values = MaterialRegistry.getInstance().getTagValues(tag);
      int index = values.indexOf(mat);
      return index == -1 ? values.size() : index;
    };
  }
}
