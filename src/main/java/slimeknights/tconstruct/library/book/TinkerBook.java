package slimeknights.tconstruct.library.book;

import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.book.content.ContentImageText2;
import slimeknights.tconstruct.library.book.content.ContentIndex;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.content.ContentPadding;
import slimeknights.tconstruct.library.book.content.ContentPadding.ContentLeftPadding;
import slimeknights.tconstruct.library.book.content.ContentPadding.ContentRightPadding;
import slimeknights.tconstruct.library.book.content.ContentPadding.PaddingBookTransformer;
import slimeknights.tconstruct.library.book.content.ContentShowcase;
import slimeknights.tconstruct.library.book.content.ContentTextTinkers;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.MaterialSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.TieredMaterialSectionTransformer;
import slimeknights.tconstruct.shared.item.TinkerBookItem.BookType;

public class TinkerBook extends BookData {
  private static final ResourceLocation MATERIALS_BOOK_ID = TConstruct.getResource("materials_and_you");
  private static final ResourceLocation MIGHTY_SMELTING_ID = TConstruct.getResource("mighty_smelting");
  private static final ResourceLocation PUNY_SMELTING_ID = TConstruct.getResource("puny_smelting");
  private static final ResourceLocation TINKERS_GADGETRY_ID = TConstruct.getResource("tinkers_gadgetry");
  private static final ResourceLocation FANTASTIC_FOUNDRY_ID = TConstruct.getResource("fantastic_foundry");
  private static final ResourceLocation ENCYCLOPEDIA_ID = TConstruct.getResource("encyclopedia");

  public final static BookData MATERIALS_AND_YOU = BookLoader.registerBook(MATERIALS_BOOK_ID.toString(), false, false);
  public final static BookData PUNY_SMELTING = BookLoader.registerBook(MIGHTY_SMELTING_ID.toString(), false, false);
  public final static BookData MIGHTY_SMELTING = BookLoader.registerBook(MIGHTY_SMELTING_ID.toString(), false, false);
  public final static BookData TINKERS_GADGETRY = BookLoader.registerBook(TINKERS_GADGETRY_ID.toString(), false, false);
  public final static BookData FANTASTIC_FOUNDRY = BookLoader.registerBook(FANTASTIC_FOUNDRY_ID.toString(), false, false);
  public final static BookData ENCYCLOPEDIA = BookLoader.registerBook(ENCYCLOPEDIA_ID.toString(), false, false);

  /**
   * Initializes the books
   */
  public static void initBook() {
    // register page types
    BookLoader.registerPageType(ContentTextTinkers.ID, ContentTextTinkers.class);
    BookLoader.registerPageType(ContentImageText2.ID, ContentImageText2.class);
    BookLoader.registerPageType(ContentMaterial.ID, ContentMaterial.class);
    BookLoader.registerPageType(ContentTool.ID, ContentTool.class);
    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);
    BookLoader.registerPageType(ContentIndex.ID, ContentIndex.class);
    BookLoader.registerPageType(ContentShowcase.ID, ContentShowcase.class);
    BookLoader.registerPageType(ContentPadding.LEFT_ID, ContentLeftPadding.class);
    BookLoader.registerPageType(ContentPadding.RIGHT_ID, ContentRightPadding.class);

    addData(MATERIALS_AND_YOU, MATERIALS_BOOK_ID);
    addData(PUNY_SMELTING, PUNY_SMELTING_ID);
    addData(MIGHTY_SMELTING, MIGHTY_SMELTING_ID);
    addData(TINKERS_GADGETRY, TINKERS_GADGETRY_ID);
    addData(FANTASTIC_FOUNDRY, FANTASTIC_FOUNDRY_ID);
    addData(ENCYCLOPEDIA, ENCYCLOPEDIA_ID);
  }

  /**
   * Adds the repository and the relevant transformers to the books
   *
   * @param book Book instance
   * @param id   Book ID
   */
  private static void addData(BookData book, ResourceLocation id) {
    book.addRepository(new FileRepository(id.getNamespace() + ":book/" + id.getPath()));
    book.addTransformer(MaterialSectionTransformer.INSTANCE);
    book.addTransformer(ToolSectionTransformer.INSTANCE);

    book.addTransformer(ModifierSectionTransformer.INSTANCE);
    book.addTransformer(new TieredMaterialSectionTransformer("tier_one_materials", 1));
    book.addTransformer(new TieredMaterialSectionTransformer("tier_two_materials", 2));
    book.addTransformer(new TieredMaterialSectionTransformer("tier_three_materials", 3));
    book.addTransformer(new TieredMaterialSectionTransformer("tier_four_materials", 4));

    // TODO: do we want to fire an event to add transformers to our books? Since we need the next two to be last
    book.addTransformer(BookTransformer.indexTranformer());
    // padding needs to be last to ensure page counts are right
    book.addTransformer(PaddingBookTransformer.INSTANCE);
  }

  /**
   * Gets the book for the enum value
   *
   * @param bookType Book type
   * @return Book
   */
  public static BookData getBook(BookType bookType) {
    switch (bookType) {
      default: case MATERIALS_AND_YOU:
        return MATERIALS_AND_YOU;
      case PUNY_SMELTING:
        return PUNY_SMELTING;
      case MIGHTY_SMELTING:
        return MIGHTY_SMELTING;
      case TINKERS_GADGETRY:
        return TINKERS_GADGETRY;
      case FANTASTIC_FOUNDRY:
        return FANTASTIC_FOUNDRY;
      case ENCYCLOPEDIA:
        return ENCYCLOPEDIA;
    }
  }
}
