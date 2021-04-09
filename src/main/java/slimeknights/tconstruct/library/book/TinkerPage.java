package slimeknights.tconstruct.library.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public abstract class TinkerPage extends PageContent {

  public static final transient int TITLE_HEIGHT = 28;

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow) {
    this.addTitle(list, titleText, dropShadow, 0);
  }

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow, int y) {
    TextData title = new TextData(titleText);
    title.scale = 1.2f;
    title.underlined = true;
    title.dropshadow = dropShadow;

    int w = (int) Math.ceil(this.parent.parent.parent.fontRenderer.getWidth(titleText) * title.scale);
    int x = (BookScreen.PAGE_WIDTH - w) / 2;

    list.add(new TextElement(x, y, w, 24, title));
  }
}
