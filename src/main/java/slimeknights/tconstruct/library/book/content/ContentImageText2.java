package slimeknights.tconstruct.library.book.content;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.ContentImageText;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ElementImage;
import slimeknights.mantle.client.screen.book.element.ElementText;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ContentImageText2 extends ContentImageText {

  public static final transient String ID = "imageText2";

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = TITLE_HEIGHT;

    if (this.title == null || this.title.isEmpty()) {
      y = 0;
    }
    else {
      this.addTitle(list, this.title);
    }

    if (this.image != null && this.image.location != null) {
      int x = (BookScreen.PAGE_HEIGHT - this.image.width) / 2;
      list.add(new ElementImage(x, y, -1, -1, this.image));
      y += this.image.height;
    }
    else {
      list.add(new ElementImage(0, y, 32, 32, ImageData.MISSING));
    }

    if (this.text != null && this.text.length > 0) {
      y += 5;
      list.add(new ElementText(0, y, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - y, this.text));
    }
  }
}
