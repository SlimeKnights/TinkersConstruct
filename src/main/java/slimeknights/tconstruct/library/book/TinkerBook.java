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
  private static final ResourceLocation SMELTING_BOOK_ID = Util.getResource("mighty_smelting");

  public final static BookData MATERIALS = BookLoader.registerBook(MATERIALS_BOOK_ID.toString(), false, false);
  public final static BookData SMELTING = BookLoader.registerBook(SMELTING_BOOK_ID.toString(), false, false);

  /** Initializes the books */
  public static void initBook() {
    // register page types
    BookLoader.registerPageType(ContentImageText2.ID, ContentImageText2.class);
    BookLoader.registerPageType(ContentMaterial.ID, ContentMaterial.class);
    BookLoader.registerPageType(ContentTool.ID, ContentTool.class);
    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);

    addData(MATERIALS, MATERIALS_BOOK_ID);
    addData(SMELTING, SMELTING_BOOK_ID);
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
      case MATERIALS_AND_YOU: return MATERIALS;
      case MIGHTY_SMELTING: return SMELTING;
    }
    return MATERIALS;
  }
}
