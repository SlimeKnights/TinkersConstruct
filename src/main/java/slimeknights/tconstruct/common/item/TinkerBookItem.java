package slimeknights.tconstruct.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.library.book.TinkerBook;

import org.jetbrains.annotations.NotNull;

public class TinkerBookItem extends TooltipItem {
  private final BookType bookType;
  public TinkerBookItem(Settings props, BookType bookType) {
    super(props);
    this.bookType = bookType;
  }

  @Override
  public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getStackInHand(handIn);
    if (worldIn.isClient) {
      TinkerBook.getBook(bookType).openGui(this.getName(itemStack), itemStack);
    }
    return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
  }

  /** Simple enum to allow selecting the book on the client */
  public enum BookType {
    MATERIALS_AND_YOU,
    PUNY_SMELTING,
    MIGHTY_SMELTING,
    TINKERS_GADGETRY
  }
}
