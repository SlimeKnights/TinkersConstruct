package slimeknights.tconstruct.shared.item;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.book.BookScreenOpener;
import slimeknights.mantle.item.AbstractBookItem;
import slimeknights.tconstruct.library.client.book.TinkerBook;

public class TinkerBookItem extends AbstractBookItem {
  private final BookType bookType;
  public TinkerBookItem(Properties props, BookType bookType) {
    super(props);
    this.bookType = bookType;
  }

  @Override
  public BookScreenOpener getBook(ItemStack stack) {
    return TinkerBook.getBook(bookType);
  }

  /** Simple enum to allow selecting the book on the client */
  public enum BookType {
    MATERIALS_AND_YOU,
    PUNY_SMELTING,
    MIGHTY_SMELTING,
    TINKERS_GADGETRY,
    FANTASTIC_FOUNDRY,
    ENCYCLOPEDIA
  }
}
