package slimeknights.tconstruct.library.book;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.ElementText;
import slimeknights.tconstruct.common.ClientProxy;

public abstract class TinkerPage extends PageContent {

  public static final transient int TITLE_HEIGHT = 28;

  // todo: custom title

  public void addTitle(ArrayList<BookElement> list, String titleText, ItemStack itemStack, String text2) {
    TextData title = new TextData(titleText);
    title.scale = 1.5f;
    title.underlined = true;

    int w = (int)Math.ceil(ClientProxy.fontRenderer.getStringWidth(titleText) * title.scale);
    int x = GuiBook.PAGE_WIDTH / 2 - w / 2;

    if(itemStack != null) {
      list.add(new ElementItem(x - 28, -2, title.scale, itemStack));
    }
    if(text2 != null) {
      TextData titleShadow = new TextData(text2);
      titleShadow.scale = title.scale;
      titleShadow.underlined = title.underlined;
      list.add(new ElementText(x+1, 5+1, w, 24, titleShadow));
    }

    list.add(new ElementText(x, 5, w, 24, title));
  }
}
