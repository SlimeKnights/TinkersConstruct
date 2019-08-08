package slimeknights.tconstruct.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.TinkerBook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TinkerBookItem extends Item {

  public TinkerBookItem() {
    super(new Item.Properties().group(TinkerRegistry.tabGeneral).maxStackSize(1));
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getHeldItem(handIn);
    if (worldIn.isRemote) {
      TinkerBook.INSTANCE.openGui(new TranslationTextComponent("Tinker's Book"), itemStack);
    }
    return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    if (I18n.hasKey(super.getTranslationKey(stack) + ".tooltip")) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + LocUtils.translateRecursive(super.getTranslationKey(stack) + ".tooltip")));
    }
  }
}
