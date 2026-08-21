package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.chat.Component;
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
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.DataLoadedConditionContext;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.AbstractMaterialContent;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
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
  /** JSON key triggering this transformer */
  private static final ResourceLocation KEY = TConstruct.getResource("material_tier");
  /** Range of valid material tiers */
  private static final IntRange TIER = new IntRange(0, Short.MAX_VALUE);
  /** Parsing context for material JSON */
  private static final TypedMap CONTEXT = TypedMapBuilder.builder().put(ContextKey.CONDITION_CONTEXT, DataLoadedConditionContext.INSTANCE).build();
  /** Map of registered material types */
  private static final Map<ResourceLocation,MaterialType> MATERIAL_TYPES = new HashMap<>();
  /** Transformer instance added to books */
  public static final TierRangeMaterialSectionTransformer INSTANCE = new TierRangeMaterialSectionTransformer();

  /** Internal record from the registry */
  private record MaterialType(BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> pageConstructor, Set<MaterialStatsId> visibleStats, @Nullable Comparator<IMaterial> sortComparator) {
    public Function<MaterialVariantId,AbstractMaterialContent> getMapping(boolean detailed) {
      return id -> pageConstructor.apply(id, detailed);
    }
  }

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

  /** Registers a new single stat type page, using the stat type ID as the ID */
  public static void registerMaterialType(MaterialStatsId id, BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> constructor) {
    registerMaterialType(id, constructor, id);
  }

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      JsonElement element = section.extraData.get(KEY);
      if (element != null) {
        try {
          // if an array, making multiple lists into 1 section
          if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<MaterialTier> tiers = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
              tiers.add(MaterialTier.deserialize(GsonHelper.convertToJsonObject(array.get(i), KEY.toString() + '[' + i + ']')));
            }
            MaterialTier.createPages(book, section, tiers);
          } else if (element.isJsonObject()) {
            MaterialTier tier = MaterialTier.deserialize(element.getAsJsonObject());
            tier.createPages(book, section);
          } else {
            TConstruct.LOG.error("Failed to parse material tier section data: Expected {} to be a JsonObject or JsonArray, was {}", KEY, GsonHelper.getType(element));
          }
        } catch (JsonSyntaxException e) {
          TConstruct.LOG.error("Failed to parse material tier section data", e);
        }
      }
    }
  }

  /** Helper to create a material predicate */
  public record ValidMaterial(Set<MaterialStatsId> visibleStats, IntRange tier, IJsonPredicate<MaterialVariantId> predicate) implements Predicate<IMaterial> {
    /** @deprecated use {@link #ValidMaterial(Set, IntRange, IJsonPredicate)} */
    @Deprecated(forRemoval = true)
    public ValidMaterial(Set<MaterialStatsId> visibleStats, IntRange tier, TagKey<IMaterial> tag) {
      this(visibleStats, tier, MaterialPredicate.tag(tag));
    }

    @Override
    public boolean test(IMaterial material) {
      if (!this.tier.test(material.getTier())) {
        return false;
      }
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialId id = material.getIdentifier();
      if (!predicate.matches(id)) {
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

  /**
   * Record managing the JSON syntax for material tier section transformers
   * @param name            ID used as a prefix for pages.
   * @param validMaterial   Predicate to validate materials.
   * @param pageCreator     Logic to create a page.
   * @param sortComparator  Comparator for ordering pages. If null, uses natural material order.
   */
  public record MaterialTier(String name, Predicate<IMaterial> validMaterial, Function<MaterialVariantId,AbstractMaterialContent> pageCreator, @Nullable Comparator<IMaterial> sortComparator, @Nullable Component titleSuffix) {
    /** Deserializes the tier info from JSON */
    public static MaterialTier deserialize(JsonObject json) throws JsonSyntaxException {
      // we handle tier range outside of predicate for efficiency, since the predicates have to refetch materials otherwise
      IntRange tier = TIER.getOrDefault(json, "tier");

      // load in predicate
      IJsonPredicate<MaterialVariantId> predicate = MaterialPredicate.ANY;
      if (json.has("predicate")) {
        predicate = MaterialPredicate.LOADER.getIfPresent(json, "predicate", CONTEXT);
      } else if (json.has("tag")) {
        // shortcut for simple tag predicates
        predicate = MaterialPredicate.tag(TinkerLoadables.MATERIAL_TAGS.getIfPresent(json, "tag"));
      }

      // load in type specific data
      ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
      MaterialType typeData = MATERIAL_TYPES.get(type);
      if (typeData == null) {
        throw new JsonSyntaxException("Invalid material section type " + type);
      }
      Function<MaterialVariantId,AbstractMaterialContent> pageBuilder = typeData.getMapping(GsonHelper.getAsBoolean(json, "detailed", false));

      String name;
      if (json.has("name")) {
        name = GsonHelper.getAsString(json, "name");
      } else {
        name = type.getPath();
      }

      Component titleSuffix = null;
      if (json.has("suffix")) {
        titleSuffix = Component.translatable(GsonHelper.getAsString(json, "suffix"));
      }

      // create final pages
      return new MaterialTier(name, new ValidMaterial(typeData.visibleStats(), tier, predicate), pageBuilder, typeData.sortComparator, titleSuffix);
    }

    /** Gets the material list for this tier */
    public List<IMaterial> getMaterials() {
      Stream<IMaterial> materialStream = MaterialRegistry.getMaterials().stream().filter(validMaterial);
      if (sortComparator != null) {
        materialStream = materialStream.sorted(sortComparator);
      }
      return materialStream.toList();
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

    /**
     * Creates all relevant pages for the given tier range
     * @param sectionData  Section to add the pages
     * @param prefix       Name prefix for this set of material pages
     * @param newPages     List to add the pages to, for batch processing.
     * @param overview     Current index being filled
     * @param iter         Iterator to get the next index
     * @param materials    List of materials from {@link #getMaterials()}
     * @return  New index to fill
     */
    private ContentPageIconList addPages(SectionData sectionData, String prefix, List<PageData> newPages, ContentPageIconList overview, ListIterator<ContentPageIconList> iter, List<IMaterial> materials) {
      for (IMaterial material : materials) {
        MaterialId materialId = material.getIdentifier();
        AbstractMaterialContent contentMaterial = pageCreator.apply(materialId);
        contentMaterial.titleSuffix = titleSuffix;
        PageData page = createPage(sectionData, prefix + materialId, contentMaterial.getId(), contentMaterial);
        newPages.add(page);

        SizedBookElement icon = new ItemElement(0, 0, 1f, contentMaterial.getDisplayStacks());
        Component title = contentMaterial.getTitleComponent();
        while (!overview.addLink(icon, title, page)) {
          overview = iter.next();
        }
      }
      return overview;
    }

    /**
     * Creates all the pages for the materials
     * @param book         Book data
     * @param sectionData  Section data
     */
    public void createPages(BookData book, SectionData sectionData) {
      sectionData.source = BookRepository.DUMMY;
      sectionData.parent = book;

      List<IMaterial> materialList = getMaterials();
      if (materialList.isEmpty()) {
        return;
      }

      // calculate pages needed
      List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), sectionData, book.translate(sectionData.name), book.strings.get(String.format("%s.subtext", sectionData.name)));

      // create pages and add to index
      ListIterator<ContentPageIconList> iter = listPages.listIterator();
      List<PageData> newPages = new ArrayList<>(materialList.size());
      addPages(sectionData, "", newPages, iter.next(), iter, materialList);
      // insert new pages at the beginning after index, ensures its before any padding from the next section
      sectionData.pages.addAll(listPages.size(), newPages);
    }

    /**
     * Creates all the pages for the materials
     * @param book            Book data
     * @param sectionData     Section data
     * @param tiers           List of tiers to add
     */
    public static void createPages(BookData book, SectionData sectionData, List<MaterialTier> tiers) {
      sectionData.source = BookRepository.DUMMY;
      sectionData.parent = book;

      List<List<IMaterial>> materials = tiers.stream().map(MaterialTier::getMaterials).toList();
      int size = 0;
      for (List<?> list : materials) {
        size += list.size();
      }
      if (size == 0) {
        return;
      }

      // calculate pages needed
      List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(size, sectionData, book.translate(sectionData.name), book.strings.get(String.format("%s.subtext", sectionData.name)));

      // create pages and add to index
      ListIterator<ContentPageIconList> iter = listPages.listIterator();
      ContentPageIconList overview = iter.next();

      List<PageData> newPages = new ArrayList<>(size);
      for (int i = 0; i < tiers.size(); i++) {
        MaterialTier tier = tiers.get(i);
        overview = tier.addPages(sectionData, tier.name + '.', newPages, overview, iter, materials.get(i));
      }
      // insert new pages at the beginning after index, ensures its before any padding from the next section
      sectionData.pages.addAll(listPages.size(), newPages);
    }
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


  /* Deprecated */

  /** @deprecated use {@link MaterialTier#createPages(BookData, SectionData)} */
  @Deprecated(forRemoval = true)
  public static void createPages(BookData book, SectionData sectionData, Predicate<IMaterial> validMaterial, Function<MaterialVariantId,AbstractMaterialContent> pageCreator) {
    createPages(book, sectionData, validMaterial, pageCreator, null);
  }

  /** @deprecated use {@link MaterialTier#createPages(BookData, SectionData)} */
  @Deprecated(forRemoval = true)
  public static void createPages(BookData book, SectionData sectionData, Predicate<IMaterial> validMaterial, Function<MaterialVariantId,AbstractMaterialContent> pageCreator, @Nullable Comparator<IMaterial> sortComparator) {
    new MaterialTier("", validMaterial, pageCreator, sortComparator, null).createPages(book, sectionData);
  }
}
