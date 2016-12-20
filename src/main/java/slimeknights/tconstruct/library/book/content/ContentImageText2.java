package slimeknights.tconstruct.library.book.content;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.ContentImageText;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementText;

@SideOnly(Side.CLIENT)
public class ContentImageText2 extends ContentImageText {

  public static final transient String ID = "imageText2";

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = TITLE_HEIGHT;

    if(title == null || title.isEmpty()) {
      y = 0;
    } else {
      addTitle(list, title);
    }

    if(image != null && image.location != null) {
      int x = (GuiBook.PAGE_HEIGHT - image.width)/2;
      list.add(new ElementImage(x, y, -1, -1, image));
      y += image.height;
    } else {
      list.add(new ElementImage(0, y, 32, 32, ImageData.MISSING));
    }

    if(text != null && text.length > 0) {
      y += 5;
      list.add(new ElementText(0, y, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - y, text));
    }
  }
}
