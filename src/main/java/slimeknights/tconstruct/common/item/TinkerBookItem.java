package slimeknights.tconstruct.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.library.book.TinkerBook;

import org.jetbrains.annotations.Nonnull;

public class TinkerBookItem extends TooltipItem {

  public TinkerBookItem(Settings props) {
    super(props);
  }

  @Nonnull
  @Override
  public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getStackInHand(handIn);
    if (worldIn.isClient) {
      TinkerBook.INSTANCE.openGui(new TranslatableText("item.tconstruct.book"), itemStack);
    }
    return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
  }
}
