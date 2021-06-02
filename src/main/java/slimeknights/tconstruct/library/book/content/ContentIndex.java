package slimeknights.tconstruct.library.book.content;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.element.BookElement;

import java.util.ArrayList;

public class ContentIndex extends ContentListing {
  public static final transient String ID = "index";

  private transient boolean loaded = false;

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    // load as late as possible, to ensure the index is properly filled
    if (!loaded) {
      loaded = true;
      parent.parent.pages.forEach(page -> {
        if (page != parent) {
          addEntry(book.translate(page.getTitle()), page);
        }
      });
    }
    super.build(book, list, rightSide);
  }
}
