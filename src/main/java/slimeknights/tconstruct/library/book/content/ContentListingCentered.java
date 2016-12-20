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
import slimeknights.tconstruct.library.book.elements.ElementListingCentered;
import slimeknights.tconstruct.library.book.TinkerPage;

@SideOnly(Side.CLIENT)
public class ContentListingCentered extends TinkerPage {

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
    int yOff = 0;
    if(title != null) {
      addTitle(list, title, false);
      yOff = 20;
    }

    int y = yOff;
    int x = 0;
    int w = GuiBook.PAGE_WIDTH;

    for(TextData data : entries) {
      int ex = x + w / 2 - book.fontRenderer.getStringWidth(data.text) / 2;

      list.add(new ElementListingCentered(ex, y, w, 9, data));
      y += 9;
    }
  }
}
