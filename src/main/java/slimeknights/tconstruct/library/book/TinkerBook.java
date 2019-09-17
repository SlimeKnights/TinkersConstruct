package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.ModuleFileRepository;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.content.ContentImageText2;

public class TinkerBook extends BookData {

  public final static BookData INSTANCE = BookLoader.registerBook(Util.RESOURCE, false, false);

  public static void initBook() {
    BookLoader.registerPageType(ContentImageText2.ID, ContentImageText2.class);
    INSTANCE.addRepository(new ModuleFileRepository(TConstruct.pulseManager, Util.resource("book")));
    INSTANCE.addTransformer(BookTransformer.indexTranformer());
  }
}
