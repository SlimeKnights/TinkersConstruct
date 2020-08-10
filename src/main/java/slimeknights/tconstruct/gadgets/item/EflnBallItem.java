package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;

import javax.annotation.Nullable;
import java.util.List;

public class EflnBallItem extends SnowballItem {

  public EflnBallItem() {
    super((new Properties()).maxStackSize(16).group(TinkerGadgets.TAB_GADGETS));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    if (!playerIn.abilities.isCreativeMode) {
      itemstack.shrink(1);
    }

    worldIn.playSound((PlayerEntity) null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

    if (!worldIn.isRemote) {
      EflnBallEntity eflnBallEntity = new EflnBallEntity(worldIn, playerIn);
      eflnBallEntity.setItem(itemstack);
      eflnBallEntity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
      worldIn.addEntity(eflnBallEntity);
    }

    playerIn.addStat(Stats.ITEM_USED.get(this));
    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
}
