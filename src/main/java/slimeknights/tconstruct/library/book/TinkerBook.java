package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.content.ContentImageText2;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.book.sectiontransformer.ModifierSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.ToolSectionTransformer;
import slimeknights.tconstruct.library.book.sectiontransformer.materials.MaterialSectionTransformer;

public class TinkerBook extends BookData {

  public final static BookData INSTANCE = BookLoader.registerBook(Util.RESOURCE, false, false);

  public static void initBook() {
    BookLoader.registerPageType(ContentImageText2.ID, ContentImageText2.class);

    // TODO: conditional file repository, read from config
    INSTANCE.addRepository(new FileRepository(Util.resource("book")));

    BookLoader.registerPageType(ContentMaterial.ID, ContentMaterial.class);
    INSTANCE.addTransformer(new MaterialSectionTransformer());

    BookLoader.registerPageType(ContentTool.ID, ContentTool.class);
    INSTANCE.addTransformer(new ToolSectionTransformer());

    BookLoader.registerPageType(ContentModifier.ID, ContentModifier.class);
    INSTANCE.addTransformer(new ModifierSectionTransformer());

    INSTANCE.addTransformer(BookTransformer.indexTranformer());
  }
}
