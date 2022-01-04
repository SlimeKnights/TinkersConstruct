package slimeknights.tconstruct.library.client.book.content;

import com.google.common.collect.Lists;
import lombok.Getter;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.tconstruct.library.client.book.elements.ListingCenteredElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContentListingCentered extends PageContent {

  @Getter
  private String title;
  private final List<TextData> entries = Lists.newArrayList();

  public void addEntry(String text, @Nullable PageData link) {
    TextData data = new TextData(text);
    if (link != null) {
      data.action = "mantle:go-to-page-rtn " + link.parent.name + "." + link.name;
    }
    this.entries.add(data);
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if (this.title != null) {
      this.addTitle(list, this.title, false);
      yOff = getTitleHeight();
    }

    int y = yOff;
    int x = 0;
    int w = BookScreen.PAGE_WIDTH;

    for (TextData data : this.entries) {
      int ex = x + w / 2 - book.fontRenderer.width(data.text) / 2;

      list.add(new ListingCenteredElement(ex, y, w, 9, data));
      y += 9;
    }
  }
}
