package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import slimeknights.mantle.client.book.action.protocol.ProtocolGoToPage;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.tconstruct.library.book.elements.ElementListingLeft;
import slimeknights.tconstruct.library.book.TinkerPage;

@SideOnly(Side.CLIENT)
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

  public boolean hasEntries() {
    return entries.size() > 0;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if(title != null) {
      addTitle(list, title, false);
      yOff = 20;
    }

    int y = yOff;
    int x = 0;
    int w = GuiBook.PAGE_WIDTH;
    int line_height = 9;

    int bot = GuiBook.PAGE_HEIGHT - 30;

    if(entries.size() * line_height + yOff > bot) {
      w /= 2;
    }

    for(TextData data : entries) {
      list.add(createListingElement(y, x, w, line_height, data));
      y += line_height;

      if(y > bot) {
        x += w;
        y = yOff;
      }
    }
  }

  protected ElementListingLeft createListingElement(int y, int x, int w, int line_height, TextData data) {
    return new ElementListingLeft(x, y, w, line_height, data);
  }
}
