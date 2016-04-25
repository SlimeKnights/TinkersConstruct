package slimeknights.tconstruct.library.book;

import java.util.ArrayList;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.gui.book.element.BookElement;

public class ContentPageIconList extends PageContent {

  protected final int width;
  protected final int height;

  public ContentPageIconList(int size) {
    this(size, size);
  }

  public ContentPageIconList(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {

  }
}
