package slimeknights.tconstruct.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.TinkerBook;

public class ItemTinkerBook extends Item {

  public ItemTinkerBook() {
    this.setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    TinkerBook.INSTANCE.openGui(itemStackIn);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }
}
