package slimeknights.tconstruct.library.book;

import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tconstruct.common.item.TinkerBookItem.BookType;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.content.ContentImageText2;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.MaterialSectionTransformer;

public class TinkerBook extends BookData {
  private static final ResourceLocation MATERIALS_BOOK_ID = Util.getResource("materials_and_you");
  private static final ResourceLocation MIGHTY_SMELTING_ID = Util.getResource("mighty_smelting");
  private static final ResourceLocation PUNY_SMELTING_ID = Util.getResource("puny_smelting");

  public final static BookData MATERIALS_AND_YOU = BookLoader.registerBook(MATERIALS_BOOK_ID.toString(), false, false);
  public final static BookData PUNY_SMELTING = BookLoader.registerBook(MIGHTY_SMELTING_ID.toString(), false, false);
  public final static BookData MIGHTY_SMELTING = BookLoader.registerBook(MIGHTY_SMELTING_ID.toString(), false, false);

  /** Initializes the books */
  public static void initBook() {
    // register page types
    BookLoader.registerPageType(ContentImageText2.ID, ContentImageText2.class);
    BookLoader.registerPageType(ContentMaterial.ID, ContentMaterial.class);
    BookLoader.registerPageType(ContentTool.ID, ContentTool.class);
    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);

    addData(MATERIALS_AND_YOU, MATERIALS_BOOK_ID);
    addData(PUNY_SMELTING, PUNY_SMELTING_ID);
    addData(MIGHTY_SMELTING, MIGHTY_SMELTING_ID);
  }

  /**
   * Adds the repository and the relevant transformers to the books
   * @param book  Book instance
   * @param id    Book ID
   */
  private static void addData(BookData book, ResourceLocation id) {
    book.addRepository(new FileRepository(id.getNamespace() + ":book/" + id.getPath()));
    book.addTransformer(new MaterialSectionTransformer());
    book.addTransformer(new ToolSectionTransformer());
    book.addTransformer(new ModifierSectionTransformer());
    book.addTransformer(BookTransformer.indexTranformer());
  }

  /**
   * Gets the book for the enum value
   * @param bookType  Book type
   * @return  Book
   */
  public static BookData getBook(BookType bookType) {
    switch (bookType) {
      case MATERIALS_AND_YOU: return MATERIALS_AND_YOU;
      case PUNY_SMELTING: return PUNY_SMELTING;
      case MIGHTY_SMELTING: return MIGHTY_SMELTING;
    }
    return MATERIALS_AND_YOU;
  }
}
