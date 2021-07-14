package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.action.protocol.ProtocolGoToPage;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.tconstruct.library.book.TinkerPage;
import slimeknights.tconstruct.library.book.elements.ListingLeftElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ContentListing extends TinkerPage {

  public String title = null;
  public String subText = null;
  private transient final List<TextData> entries = Lists.newArrayList();

  public void addEntry(String text, @Nullable PageData link) {
    TextData data = new TextData(text);
    if (link != null) {
      data.action = ProtocolGoToPage.GO_TO_RTN + ":" + link.parent.name + "." + link.name;
    }
    this.entries.add(data);
  }

  public boolean hasEntries() {
    return this.entries.size() > 0;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if (this.title != null) {
      this.addTitle(list, this.title, false);
      yOff = 16;
    }
    if (this.subText != null) {
      int height = this.addText(list, this.subText, false, 0, yOff);
      yOff += height;
    }

    // 16 gives space for the bottom and ensures a round number, yOff ensures the top is not counted
    int lineHeight = 10;
    int columnHeight = BookScreen.PAGE_HEIGHT - yOff - 16;
    if (columnHeight % lineHeight != 0) {
      columnHeight -= columnHeight % lineHeight;
    }

    // determine how wide we can make each column, support up to 3
    int w = BookScreen.PAGE_WIDTH;
    int totalHeight = this.entries.size() * lineHeight;
    if (totalHeight > columnHeight) {
      if (totalHeight > (columnHeight * 2)) {
        w /= 3;
      } else {
        w /= 2;
      }
    }

    int x = 0;
    int y = 0;
    for (TextData data : this.entries) {
      int height = this.parent.parent.parent.fontRenderer.getWordWrappedHeight("- " + data.text, w) * lineHeight / 9;
      list.add(this.createListingElement(y + yOff, x, w, height, data));
      y += height;
      if (y >= columnHeight) {
        x += w;
        y = 0;
      }
    }
  }

  private ListingLeftElement createListingElement(int y, int x, int w, int line_height, TextData data) {
    return new ListingLeftElement(x, y, w, line_height, data);
  }
}
