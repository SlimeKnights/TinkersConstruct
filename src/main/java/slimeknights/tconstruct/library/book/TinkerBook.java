package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.tconstruct.library.Util;

public class TinkerBook extends BookData {

  public final static BookData INSTANCE = BookLoader.registerBook(Util.RESOURCE, false, false);

}
