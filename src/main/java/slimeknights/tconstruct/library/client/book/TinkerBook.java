package slimeknights.tconstruct.library.client.book;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.tconstruct.library.client.book.content.ContentMaterial;
import slimeknights.tconstruct.library.client.book.content.ContentModifier;
import slimeknights.tconstruct.library.client.book.content.ContentTool;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.materials.SkullMaterialSectionTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.materials.TieredMaterialSectionTransformer;
import slimeknights.tconstruct.shared.item.TinkerBookItem.BookType;

import static slimeknights.tconstruct.library.TinkerBookIDs.ENCYCLOPEDIA_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.FANTASTIC_FOUNDRY_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.MATERIALS_BOOK_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.MIGHTY_SMELTING_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.PUNY_SMELTING_ID;
import static slimeknights.tconstruct.library.TinkerBookIDs.TINKERS_GADGETRY_ID;

public class TinkerBook extends BookData {
  public static final BookData MATERIALS_AND_YOU = BookLoader.registerBook(MATERIALS_BOOK_ID,    false, false);
  public static final BookData PUNY_SMELTING     = BookLoader.registerBook(PUNY_SMELTING_ID,     false, false);
  public static final BookData MIGHTY_SMELTING   = BookLoader.registerBook(MIGHTY_SMELTING_ID,   false, false);
  public static final BookData TINKERS_GADGETRY  = BookLoader.registerBook(TINKERS_GADGETRY_ID,  false, false);
  public static final BookData FANTASTIC_FOUNDRY = BookLoader.registerBook(FANTASTIC_FOUNDRY_ID, false, false);
  public static final BookData ENCYCLOPEDIA      = BookLoader.registerBook(ENCYCLOPEDIA_ID,      false, false);

  /**
   * Initializes the books
   */
  public static void initBook() {
    // register page types
    BookLoader.registerPageType(ContentMaterial.ID, ContentMaterial.class);
    BookLoader.registerPageType(ContentTool.ID,     ContentTool.class);
    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);

    // tool transformers
    ToolSectionTransformer armorTransformer = new ToolSectionTransformer("armor");
    MATERIALS_AND_YOU.addTransformer(ToolSectionTransformer.INSTANCE);
    MATERIALS_AND_YOU.addTransformer(armorTransformer);
    MIGHTY_SMELTING.addTransformer(ToolSectionTransformer.INSTANCE);
    FANTASTIC_FOUNDRY.addTransformer(armorTransformer);
    TINKERS_GADGETRY.addTransformer(armorTransformer);
    ENCYCLOPEDIA.addTransformer(new ToolSectionTransformer("small_tools"));
    ENCYCLOPEDIA.addTransformer(new ToolSectionTransformer("large_tools"));
    ENCYCLOPEDIA.addTransformer(armorTransformer);

    // material tier transformers
    MATERIALS_AND_YOU.addTransformer(new TieredMaterialSectionTransformer("tier_one_materials", 1, false));
    PUNY_SMELTING.addTransformer(new TieredMaterialSectionTransformer("tier_two_materials", 2, false));
    MIGHTY_SMELTING.addTransformer(new TieredMaterialSectionTransformer("tier_three_materials", 3, false));
    FANTASTIC_FOUNDRY.addTransformer(new TieredMaterialSectionTransformer("tier_four_materials", 4, false));
    TINKERS_GADGETRY.addTransformer(new SkullMaterialSectionTransformer("skull_materials", false));
    // detailed transformers
    ENCYCLOPEDIA.addTransformer(new TieredMaterialSectionTransformer("tier_one_materials", 1, true));
    ENCYCLOPEDIA.addTransformer(new TieredMaterialSectionTransformer("tier_two_materials", 2, true));
    ENCYCLOPEDIA.addTransformer(new TieredMaterialSectionTransformer("tier_three_materials", 3, true));
    ENCYCLOPEDIA.addTransformer(new TieredMaterialSectionTransformer("tier_four_materials", 4, true));
    ENCYCLOPEDIA.addTransformer(new SkullMaterialSectionTransformer("skull_materials", true));

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
    addStandardData(ENCYCLOPEDIA, ENCYCLOPEDIA_ID);
  }

  /**
   * Adds the repository and the relevant transformers to the books
   *
   * @param book Book instance
   * @param id   Book ID
   */
  private static void addStandardData(BookData book, ResourceLocation id) {
    book.addRepository(new FileRepository(new ResourceLocation(id.getNamespace(), "book/" + id.getPath())));
    book.addTransformer(BookTransformer.indexTranformer());
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
