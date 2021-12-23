package slimeknights.tconstruct.library.book.content;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.book.TinkerPage;

import java.util.ArrayList;

/** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentText} */
@Deprecated
public class ContentTextTinkers extends TinkerPage {
  public static final transient String ID = "tinkers_text";

  public String title = null;
  public TextData[] text;

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    TConstruct.LOG.warn("Using deprecated page ID {}", ID);
    int y;
    if (this.title != null && !this.title.isEmpty()) {
      y = 16;
      this.addTitle(list, this.title, false, 0x000000);
    } else {
      y = 0;
    }
    if (this.text != null && this.text.length > 0) {
      list.add(new TextElement(0, y, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - y, this.text));
    }
  }
}
