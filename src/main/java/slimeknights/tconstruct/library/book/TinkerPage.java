package slimeknights.tconstruct.library.book;

import java.util.ArrayList;

import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementText;

public abstract class TinkerPage extends PageContent {

  public static final transient int TITLE_HEIGHT = 28;

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow) {
    TextData title = new TextData(titleText);
    title.scale = 1.2f;
    title.underlined = true;
    title.dropshadow = dropShadow;

    int w = (int) Math.ceil(parent.parent.parent.fontRenderer.getStringWidth(titleText) * title.scale);
    int x = (GuiBook.PAGE_WIDTH - w) / 2;

    list.add(new ElementText(x, 0, w, 24, title));
  }
}
