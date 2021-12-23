package slimeknights.tconstruct.library.book.content;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.ContentImageText;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.tconstruct.TConstruct;

import java.util.ArrayList;

/** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentImageText} */
@Deprecated
public class ContentImageText2 extends ContentImageText {
  public static final transient String ID = "imageText2";

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    TConstruct.LOG.warn("Using deprecated page {} in book", ID);
    super.build(book, list, rightSide);
  }
}
