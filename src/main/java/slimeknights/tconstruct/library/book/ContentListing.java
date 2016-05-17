package slimeknights.tconstruct.library.book;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import slimeknights.mantle.client.book.action.protocol.ProtocolGoToPage;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementText;

public class ContentListing extends TinkerPage {

  public String title;
  private final List<TextData> entries = Lists.newArrayList();

  public void addEntry(String text, PageData link) {
    TextData data = new TextData(text);
    if(link != null) {
      data.action = ProtocolGoToPage.GO_TO_RTN + ":" + link.parent.name + "." + link.name;
    }
    entries.add(data);
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = 0;
    if(title != null) {
      addTitle(list, title, false);
      y = 20;
    }

    int x = 15;
    int w = GuiBook.PAGE_WIDTH - x*2;
    for(TextData data : entries) {
      list.add(new ElementTextBoldHover(x, y, w, 9, data));
      y += 9;
    }
  }
}
