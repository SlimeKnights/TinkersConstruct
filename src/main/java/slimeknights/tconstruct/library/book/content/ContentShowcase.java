package slimeknights.tconstruct.library.book.content;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.tconstruct.library.book.TinkerPage;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ContentShowcase extends TinkerPage {

  public static final transient String ID = "showcase";

  public String title = null;
  public TextData[] text;
  public ItemStackData item;

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = TITLE_HEIGHT;

    if (this.title == null || this.title.isEmpty()) {
      y = 0;
    } else {
      this.addTitle(list, this.title);
    }

    if (this.item != null && (!this.item.id.isEmpty() || !this.item.itemList.isEmpty())) {
      list.add(new ItemElement(BookScreen.PAGE_WIDTH / 2 - 15, 15, 2.5f, this.item.getItems(), this.item.action));
    }

    if (this.text != null && this.text.length > 0) {
      list.add(new TextElement(0, y + 20, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - 20 - y, this.text));
    }
  }
}
