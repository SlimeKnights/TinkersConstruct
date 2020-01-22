package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.library.TinkerRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class GlowBallItem extends SnowballItem {

  public GlowBallItem() {
    super((new Properties()).maxStackSize(16).group(TinkerRegistry.tabGadgets));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    if (!playerIn.abilities.isCreativeMode) {
      itemstack.shrink(1);
    }

    worldIn.playSound((PlayerEntity) null, playerIn.getPosX(), playerIn.getPosY(),playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
    if (!worldIn.isRemote) {
      GlowballEntity glowballEntity = new GlowballEntity(worldIn, playerIn);
      glowballEntity.setItem(itemstack);
      glowballEntity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
      worldIn.addEntity(glowballEntity);
    }

    playerIn.addStat(Stats.ITEM_USED.get(this));
    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    if (I18n.hasKey(stack.getTranslationKey() + ".tooltip")) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + LocUtils.translateRecursive(stack.getTranslationKey() + ".tooltip", new Object[0])));
    }

    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
}
