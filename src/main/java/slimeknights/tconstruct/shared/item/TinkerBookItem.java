package slimeknights.tconstruct.shared.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tconstruct.library.book.TinkerBook;

public class TinkerBookItem extends LecternBookItem {
  private final BookType bookType;
  public TinkerBookItem(Properties props, BookType bookType) {
    super(props);
    this.bookType = bookType;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (world.isRemote) {
      TinkerBook.getBook(bookType).openGui(hand, stack);
    }
    return new ActionResult<>(ActionResultType.SUCCESS, stack);
  }

  @Override
  public void openLecternScreenClient(BlockPos pos, ItemStack stack) {
    TinkerBook.getBook(bookType).openGui(pos, stack);
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
