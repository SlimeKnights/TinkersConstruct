package slimeknights.tconstruct.library.book;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementText;

@SideOnly(Side.CLIENT)
public abstract class TinkerPage extends PageContent {

  public static final transient int TITLE_HEIGHT = 28;

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow) {
    addTitle(list, titleText, dropShadow, 0);
  }

  public void addTitle(ArrayList<BookElement> list, String titleText, boolean dropShadow, int y) {
    TextData title = new TextData(titleText);
    title.scale = 1.2f;
    title.underlined = true;
    title.dropshadow = dropShadow;

    int w = (int) Math.ceil(parent.parent.parent.fontRenderer.getStringWidth(titleText) * title.scale);
    int x = (GuiBook.PAGE_WIDTH - w) / 2;

    list.add(new ElementText(x, y, w, 24, title));
  }
}
