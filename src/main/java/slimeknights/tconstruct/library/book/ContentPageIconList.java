package slimeknights.tconstruct.library.book;

import com.google.common.collect.Lists;

import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;

public class ContentPageIconList extends TinkerPage {

  protected final int width;
  protected final int height;

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
    addTitle(list, TextFormatting.DARK_GRAY + "Materials", false); // todo: localization

    int offset = 15;
    int x = offset;
    int y = 20;
    int pageW = GuiBook.PAGE_WIDTH - 2*offset;
    int pageH = GuiBook.PAGE_HEIGHT - 20;

    float scale = 2.5f;
    boolean fits = false;
    while(!fits && scale > 1f) {
      scale -= 0.25f;
      int rows = (int) (pageW / (width * scale));
      int cols = (int) (pageH / (height * scale));
      fits = rows*cols >= elements.size();
    }

    int scaledWidth = (int)(width*scale);
    int scaledHeight = (int)(height*scale);

    for(ElementPageIconLink element : elements) {
      element.x = x;
      element.y = y;
      element.displayElement.x = x + (int)(scale * (width - element.displayElement.width)/2);
      element.displayElement.y = y + (int)(scale * (height - element.displayElement.height)/2);

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
        if(y > GuiBook.PAGE_HEIGHT - scale * scaledHeight) {
          break;
        }
      }
    }
  }
}
