package slimeknights.tconstruct.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.TinkerBook;

import javax.annotation.Nonnull;

public class TinkerBookItem extends TooltipItem {

  public TinkerBookItem() {
    super(new Item.Properties().group(TinkerRegistry.tabGeneral).maxStackSize(1));
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getHeldItem(handIn);
    if (worldIn.isRemote) {
      TinkerBook.INSTANCE.openGui(new TranslationTextComponent("item.tconstruct.book"), itemStack);
    }
    return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
  }
}
