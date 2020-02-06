package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ElementItem;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.tconstruct.library.book.TinkerPage;
import slimeknights.tconstruct.library.book.elements.ElementPageIconLink;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ContentPageIconList extends TinkerPage {

  protected final int width;
  protected final int height;

  public String title;
  public float maxScale = 2.5f;

  protected List<ElementPageIconLink> elements = Lists.newArrayList();

  public ContentPageIconList() {
    this(20);
  }

  public ContentPageIconList(int size) {
    this(size, size);
  }

  public ContentPageIconList(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Returns false if the page is full
   */
  public boolean addLink(SizedBookElement element, String name, PageData pageData) {
    if (this.elements.size() >= this.getMaxIconCount()) {
      return false;
    }
    this.elements.add(new ElementPageIconLink(0, 0, element, name, pageData));
    return true;
  }

  public int getMaxIconCount() {
    return this.getMaxColumns() * this.getMaxRows();
  }

  public int getMaxRows() {
    return (BookScreen.PAGE_HEIGHT - (this.title != null ? 20 : 0)) / this.height;
  }

  public int getMaxColumns() {
    return (BookScreen.PAGE_WIDTH - 30) / this.width;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if (this.title != null) {
      this.addTitle(list, this.title, false);
      yOff = 20;
    }

    int offset = 15;
    int x = offset;
    int y = yOff;
    int pageW = BookScreen.PAGE_WIDTH - 2 * offset;
    int pageH = BookScreen.PAGE_HEIGHT - yOff;

    float scale = this.maxScale;
    int scaledWidth = this.width;
    int scaledHeight = this.height;
    boolean fits = false;
    while (!fits && scale > 1f) {
      scale -= 0.25f;
      scaledWidth = (int) (this.width * scale);
      scaledHeight = (int) (this.height * scale);
      int rows = pageW / scaledWidth;
      int cols = pageH / scaledHeight;
      fits = rows * cols >= this.elements.size();
    }

    for (ElementPageIconLink element : this.elements) {
      element.x = x;
      element.y = y;
      element.displayElement.x = x + (int) (scale * (this.width - element.displayElement.width) / 2);
      element.displayElement.y = y + (int) (scale * (this.height - element.displayElement.height) / 2);

      element.width = scaledWidth;
      element.height = scaledHeight;
      if (element.displayElement instanceof ElementItem) {
        ((ElementItem) element.displayElement).scale = scale;
      }

      list.add(element);

      x += scaledWidth;

      if (x > BookScreen.PAGE_WIDTH - offset - scaledWidth) {
        x = offset;
        y += scaledHeight;
        // do not draw over the page
        if (y > BookScreen.PAGE_HEIGHT - scaledHeight) {
          break;
        }
      }
    }
  }

  public static List<ContentPageIconList> getPagesNeededForItemCount(int count, SectionData data, String title) {
    List<ContentPageIconList> listPages = Lists.newArrayList();

    while (count > 0) {
      ContentPageIconList overview = new ContentPageIconList();
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.content = overview;
      page.load();

      data.pages.add(page);

      overview.title = title;

      listPages.add(overview);

      count -= overview.getMaxIconCount();
    }

    // ensure same size for all
    if (listPages.size() > 1) {
      listPages.forEach(page -> page.maxScale = 1f);
    }

    return listPages;
  }
}
