package slimeknights.tconstruct.shared.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tconstruct.library.book.TinkerBook;

import net.minecraft.world.item.Item.Properties;

public class TinkerBookItem extends LecternBookItem {
  private final BookType bookType;
  public TinkerBookItem(Properties props, BookType bookType) {
    super(props);
    this.bookType = bookType;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (world.isClientSide) {
      TinkerBook.getBook(bookType).openGui(hand, stack);
    }
    return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
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
