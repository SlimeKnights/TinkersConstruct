package slimeknights.tconstruct.library.client.book.content;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentPadding.ContentRightPadding;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.tconstruct.library.client.book.elements.PageIconLinkElement;

import java.util.ArrayList;
import java.util.List;

/** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPageIconList} */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public class ContentPageIconList extends PageContent {

  protected final int width;
  protected final int height;

  @Getter
  public String title;
  public String subText;
  public float maxScale = 2.5f;

  protected List<PageIconLinkElement> elements = Lists.newArrayList();

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
  public boolean addLink(SizedBookElement element, Component name, PageData pageData) {
    if (this.elements.size() >= this.getMaxIconCount()) {
      return false;
    }
    this.elements.add(new PageIconLinkElement(0, 0, element, name, pageData));
    return true;
  }

  public int getMaxIconCount() {
    return this.getMaxColumns() * this.getMaxRows();
  }

  public int getMaxRows() {
    int totalHeight = BookScreen.PAGE_HEIGHT;
    if (title != null) {
      totalHeight -= getTitleHeight();
    }
    if (subText != null) {
      totalHeight -= 16 + this.parent.parent.parent.fontRenderer.wordWrapHeight(subText, BookScreen.PAGE_WIDTH) * 12 / 9;
    }
    return totalHeight / this.height;
  }

  public int getMaxColumns() {
    return (BookScreen.PAGE_WIDTH - 30) / this.width;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if (this.title != null) {
      this.addTitle(list, this.title, false);
      yOff = getTitleHeight();
    }

    if(this.subText != null) {
      int height = this.addText(list, this.subText, false, 0, yOff);
      yOff = height + 16;
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

    for (PageIconLinkElement element : this.elements) {
      element.x = x;
      element.y = y;
      element.displayElement.x = x + (int) (scale * (this.width - element.displayElement.width) / 2);
      element.displayElement.y = y + (int) (scale * (this.height - element.displayElement.height) / 2);

      element.width = scaledWidth;
      element.height = scaledHeight;
      if (element.displayElement instanceof ItemElement) {
        ((ItemElement) element.displayElement).scale = scale;
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

  /** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPageIconList#getPagesNeededForItemCount(int, SectionData, String, String)} */
  @Deprecated(forRemoval = true)
  public static List<ContentPageIconList> getPagesNeededForItemCount(int count, SectionData data, String title, String subText) {
    List<ContentPageIconList> listPages = new ArrayList<>();
    List<PageData> newPages = new ArrayList<>();
    while (count > 0) {
      ContentPageIconList overview = new ContentPageIconList();
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.content = overview;
      page.load();

      // wait to add to the page list until after we added the padding page
      newPages.add(page);

      overview.title = title;
      overview.subText = subText;

      listPages.add(overview);

      count -= overview.getMaxIconCount();
    }

    // ensure same size for all
    if (listPages.size() > 1) {
      listPages.forEach(page -> page.maxScale = 1f);
    }

    // add a padding page if we have an even number of index pages, so you see both together
    if (listPages.size() % 2 == 0) {
      PageData padding = new PageData(true);
      padding.source = data.source;
      padding.parent = data;
      padding.content = new ContentRightPadding();
      padding.load();
      // hack: add padding to the previous section so section links start at the index
      int sectionIndex = data.parent.sections.indexOf(data);
      if (data.pages.isEmpty() && sectionIndex > 0) {
        data.parent.sections.get(sectionIndex - 1).pages.add(padding);
      } else {
        data.pages.add(padding);
      }
    }

    // padding done, can add new pages
    data.pages.addAll(newPages);

    return listPages;
  }
}
