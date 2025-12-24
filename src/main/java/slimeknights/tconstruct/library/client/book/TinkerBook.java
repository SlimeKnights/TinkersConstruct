package slimeknights.tconstruct.library.client.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.book.content.AmmoMaterialContent;
import slimeknights.tconstruct.library.client.book.content.ArmorMaterialContent;
import slimeknights.tconstruct.library.client.book.content.ContentMaterialSkull;
import slimeknights.tconstruct.library.client.book.content.ContentModifier;
import slimeknights.tconstruct.library.client.book.content.ContentTool;
import slimeknights.tconstruct.library.client.book.content.FluidEffectContent;
import slimeknights.tconstruct.library.client.book.content.MeleeHarvestMaterialContent;
import slimeknights.tconstruct.library.client.book.content.RangedMaterialContent;
import slimeknights.tconstruct.library.client.book.content.TooltipShowcaseContent;
import slimeknights.tconstruct.library.client.book.sectiontransformer.FluidEffectInjectingTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ModifierTagInjectorTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ToolTagInjectorTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.materials.TierRangeMaterialSectionTransformer;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.shared.item.TinkerBookItem.BookType;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.SkullStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.Comparator;

import static slimeknights.tconstruct.TConstruct.getResource;
import static slimeknights.tconstruct.library.TinkerBookIDs.ENCYCLOPEDIA_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.FANTASTIC_FOUNDRY_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.MATERIALS_BOOK_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.MIGHTY_SMELTING_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.PUNY_SMELTING_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.TINKERS_GADGETRY_ID;
import static slimeknights.tconstruct.library.client.book.sectiontransformer.materials.TierRangeMaterialSectionTransformer.hasStatType;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.BOOTS;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.CHESTPLATE;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.HELMET;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.LEGGINGS;
import static slimeknights.tconstruct.tools.stats.PlatingMaterialStats.SHIELD;

public class TinkerBook extends BookData {
  public static final BookData MATERIALS_AND_YOU = BookLoader.registerBook(MATERIALS_BOOK_ID,    false, false);
  public static final BookData PUNY_SMELTING     = BookLoader.registerBook(PUNY_SMELTING_ID,     false, false);
  public static final BookData MIGHTY_SMELTING   = BookLoader.registerBook(MIGHTY_SMELTING_ID,   false, false);
  public static final BookData TINKERS_GADGETRY  = BookLoader.registerBook(TINKERS_GADGETRY_ID,  false, false);
  public static final BookData FANTASTIC_FOUNDRY = BookLoader.registerBook(FANTASTIC_FOUNDRY_ID, false, false);
  public static final BookData ENCYCLOPEDIA      = BookLoader.registerBook(ENCYCLOPEDIA_ID,      false, false);
  private static final BookData[] ALL_BOOKS = {MATERIALS_AND_YOU, PUNY_SMELTING, MIGHTY_SMELTING, TINKERS_GADGETRY, FANTASTIC_FOUNDRY, ENCYCLOPEDIA};

  /**
   * Initializes the books
   */
  public static void initBook() {
    BookLoader.registerGsonTypeAdapter(Component.class, new Component.Serializer());

    // register page types
    BookLoader.registerPageType(MeleeHarvestMaterialContent.ID, MeleeHarvestMaterialContent.class);
    BookLoader.registerPageType(RangedMaterialContent.ID,       RangedMaterialContent.class);
    BookLoader.registerPageType(ArmorMaterialContent.ID,        ArmorMaterialContent.class);
    BookLoader.registerPageType(AmmoMaterialContent.ID,        ArmorMaterialContent.class);
    BookLoader.registerPageType(ContentTool.ID, ContentTool.class);
    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);
    BookLoader.registerPageType(TooltipShowcaseContent.ID, TooltipShowcaseContent.class);
    BookLoader.registerPageType(FluidEffectContent.ID, FluidEffectContent.class);

    // material types
    TierRangeMaterialSectionTransformer.registerMaterialType(getResource("melee_harvest"), MeleeHarvestMaterialContent::new,
      // sort heads first, binding exclusives last. Assuming no handle exclusive
      Comparator.comparing(hasStatType(HeadMaterialStats.ID)).reversed(),
      HeadMaterialStats.ID, HandleMaterialStats.ID, StatlessMaterialStats.BINDING.getIdentifier());
    TierRangeMaterialSectionTransformer.registerMaterialType(getResource("ranged"), RangedMaterialContent::new,
      // sort bowstrings first, anything with both in the middle (rose gold), and limbs/grips last
      Comparator.comparing(hasStatType(StatlessMaterialStats.BOWSTRING)).reversed().thenComparing(hasStatType(LimbMaterialStats.ID)),
      LimbMaterialStats.ID, GripMaterialStats.ID, StatlessMaterialStats.BOWSTRING.getIdentifier());
    TierRangeMaterialSectionTransformer.registerMaterialType(getResource("ammo"), AmmoMaterialContent::new,
      // first, we want anything with arrow heads, and thins without second
      // among heads, want anything with shafts last. If it lacks a head, it must have a shaft so that order no longer matters
      Comparator.comparing(hasStatType(StatlessMaterialStats.ARROW_HEAD)).reversed()
        .thenComparing(hasStatType(StatlessMaterialStats.FLETCHING))
        .thenComparing(hasStatType(StatlessMaterialStats.ARROW_SHAFT)),
      StatlessMaterialStats.ARROW_HEAD.getIdentifier(), StatlessMaterialStats.ARROW_SHAFT.getIdentifier(), StatlessMaterialStats.FLETCHING.getIdentifier());
    TierRangeMaterialSectionTransformer.registerMaterialType(getResource("armor"), ArmorMaterialContent::new,
      Comparator.comparing(mat -> {
        // ordering:
        // 1: cuirass exclusive
        // 2: cuirass + maille
        // 3: maille exclusive
        // 4: plating
        // 5: shield core
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        MaterialId id = mat.getIdentifier();

        // anything with a cuirass goes first
        if (registry.getMaterialStats(id, StatlessMaterialStats.CUIRASS.getIdentifier()).isPresent()) {
          // among cuirass, sort maille last so they are next to maille exclusive
          return registry.getMaterialStats(id, StatlessMaterialStats.MAILLE.getIdentifier()).isPresent() ? 2 : 1;
        }
        // anything with plating goes 4th
        if (registry.getMaterialStats(id, CHESTPLATE.getId()).isPresent()) {
          return 4;
        }
        // if it has maille, it goes before plating. Otherwise (shield cores), it goes after
        return registry.getMaterialStats(id, StatlessMaterialStats.MAILLE.getIdentifier()).isPresent() ? 3 : 5;
      }),
      HELMET.getId(), CHESTPLATE.getId(), LEGGINGS.getId(), BOOTS.getId(), SHIELD.getId(),
      StatlessMaterialStats.MAILLE.getIdentifier(), StatlessMaterialStats.CUIRASS.getIdentifier(),
      StatlessMaterialStats.SHIELD_CORE.getIdentifier());
    TierRangeMaterialSectionTransformer.registerMaterialType(getResource("skull"), ContentMaterialSkull::new,
      Comparator.comparing(TierRangeMaterialSectionTransformer.tagOrder(TinkerTags.Materials.SLIMESKULL)), SkullStats.ID);

    // add transformers that load modifiers from tags
    ToolSectionTransformer armorTransformer = new ToolSectionTransformer("armor");
    for (BookData book : ALL_BOOKS) {
      book.addTransformer(ToolTagInjectorTransformer.INSTANCE);
      book.addTransformer(ModifierTagInjectorTransformer.INSTANCE);
      book.addTransformer(armorTransformer);
    }

    // tool transformers
    // TODO: migrate to using extraData instead of hardcoded names
    MATERIALS_AND_YOU.addTransformer(ToolSectionTransformer.INSTANCE);
    MIGHTY_SMELTING.addTransformer(ToolSectionTransformer.INSTANCE);
    TINKERS_GADGETRY.addTransformer(new ToolSectionTransformer("staffs"));
    TINKERS_GADGETRY.addTransformer(new ToolSectionTransformer("ancient_tools"));
    ENCYCLOPEDIA.addTransformer(ToolSectionTransformer.INSTANCE);

    // modifier transformers
    ModifierSectionTransformer upgrades = new ModifierSectionTransformer("upgrades");
    ModifierSectionTransformer defense = new ModifierSectionTransformer("defense");
    ModifierSectionTransformer slotless = new ModifierSectionTransformer("slotless");
    ModifierSectionTransformer abilities = new ModifierSectionTransformer("abilities");
    PUNY_SMELTING.addTransformer(upgrades);
    PUNY_SMELTING.addTransformer(slotless);
    MIGHTY_SMELTING.addTransformer(defense);
    MIGHTY_SMELTING.addTransformer(abilities);
    ENCYCLOPEDIA.addTransformer(upgrades);
    ENCYCLOPEDIA.addTransformer(defense);
    ENCYCLOPEDIA.addTransformer(slotless);
    ENCYCLOPEDIA.addTransformer(abilities);

    // TODO: do we want to fire an event to add transformers to our books? Since we need the next two to be last
    addStandardData(MATERIALS_AND_YOU, MATERIALS_BOOK_ID);
    addStandardData(PUNY_SMELTING, PUNY_SMELTING_ID);
    addStandardData(MIGHTY_SMELTING, MIGHTY_SMELTING_ID);
    addStandardData(FANTASTIC_FOUNDRY, FANTASTIC_FOUNDRY_ID);
    addStandardData(TINKERS_GADGETRY, TINKERS_GADGETRY_ID);
    addStandardData(ENCYCLOPEDIA, ENCYCLOPEDIA_ID, FluidEffectInjectingTransformer.INSTANCE);
  }

  /**
   * Adds the repository and the relevant transformers to the books
   *
   * @param book Book instance
   * @param id   Book ID
   */
  @SuppressWarnings("removal")
  private static void addStandardData(BookData book, ResourceLocation id, BookTransformer... extraTransformers) {
    book.addRepository(new FileRepository(new ResourceLocation(id.getNamespace(), "book/" + id.getPath())));
    book.addTransformer(BookTransformer.indexTranformer());
    book.addTransformer(TierRangeMaterialSectionTransformer.INSTANCE);

    // any transformers that go after tier range
    for (BookTransformer transformer : extraTransformers) {
      book.addTransformer(transformer);
    }

    // padding needs to be last to ensure page counts are right
    book.addTransformer(BookTransformer.paddingTransformer());
  }

  /**
   * Gets the book for the enum value
   *
   * @param bookType Book type
   * @return Book
   */
  public static BookData getBook(BookType bookType) {
    return switch (bookType) {
      case MATERIALS_AND_YOU -> MATERIALS_AND_YOU;
      case PUNY_SMELTING     -> PUNY_SMELTING;
      case MIGHTY_SMELTING   -> MIGHTY_SMELTING;
      case TINKERS_GADGETRY  -> TINKERS_GADGETRY;
      case FANTASTIC_FOUNDRY -> FANTASTIC_FOUNDRY;
      case ENCYCLOPEDIA      -> ENCYCLOPEDIA;
    };
  }
}
