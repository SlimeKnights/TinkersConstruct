package slimeknights.tconstruct.library.book;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;

public class ContentPageIconList extends TinkerPage {

  protected final int width;
  protected final int height;
  public String title;

  protected List<ElementPageIconLink> elements = Lists.newArrayList();

  public ContentPageIconList(int size) {
    this(size, size);
  }

  public ContentPageIconList(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void addLink(SizedBookElement element, String name, PageData pageData) {
    elements.add(new ElementPageIconLink(0, 0, element, name, pageData));
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if(title != null) {
      addTitle(list, title, false);
      yOff = 20;
    }

    int offset = 15;
    int x = offset;
    int y = yOff;
    int pageW = GuiBook.PAGE_WIDTH - 2 * offset;
    int pageH = GuiBook.PAGE_HEIGHT - yOff;

    float scale = 2.5f;
    int scaledWidth = width;
    int scaledHeight = height;
    boolean fits = false;
    while(!fits && scale > 1f) {
      scale -= 0.25f;
      scaledWidth = (int) (width * scale);
      scaledHeight = (int) (height * scale);
      int rows = pageW / scaledWidth;
      int cols = pageH / scaledHeight;
      fits = rows * cols >= elements.size();
    }

    for(ElementPageIconLink element : elements) {
      element.x = x;
      element.y = y;
      element.displayElement.x = x + (int) (scale * (width - element.displayElement.width) / 2);
      element.displayElement.y = y + (int) (scale * (height - element.displayElement.height) / 2);

      element.width = scaledWidth;
      element.height = scaledHeight;
      if(element.displayElement instanceof ElementItem) {
        ((ElementItem) element.displayElement).scale = scale;
      }

      list.add(element);

      x += scaledWidth;

      if(x > GuiBook.PAGE_WIDTH - offset - scaledWidth) {
        x = offset;
        y += scaledHeight;
        // do not draw over the page
        if(y > GuiBook.PAGE_HEIGHT - scaledHeight) {
          break;
        }
      }
    }
  }
}
