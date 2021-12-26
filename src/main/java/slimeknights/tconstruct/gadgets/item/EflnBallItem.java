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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;

import javax.annotation.Nullable;
import java.util.List;

// TODO: this is the same code as glowball, consider shared class
public class EflnBallItem extends SnowballItem {

  public EflnBallItem() {
    super((new Properties()).stacksTo(16).tab(TinkerGadgets.TAB_GADGETS));
  }

  @Override
  public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getItemInHand(handIn);
    if (!playerIn.abilities.instabuild) {
      itemstack.shrink(1);
    }

    worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), Sounds.THROWBALL_THROW.getSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
    if (!worldIn.isClientSide()) {
      EflnBallEntity eflnBallEntity = new EflnBallEntity(worldIn, playerIn);
      eflnBallEntity.setItem(itemstack);
      eflnBallEntity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 0.0F, 1.5F, 1.0F);
      worldIn.addFreshEntity(eflnBallEntity);
    }

    playerIn.awardStat(Stats.ITEM_USED.get(this));
    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }
}
