package slimeknights.tconstruct.gadgets.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.gadgets.entity.EFLNEntity;

import javax.annotation.Nullable;
import java.util.List;


/** @deprecated use {@link slimeknights.tconstruct.library.tools.item.ModifiableShurikenItem} with {@link slimeknights.tconstruct.library.modifiers.modules.combat.ProjectileExplosionModule} */
@Deprecated
public class EFLNItem extends SnowballItem {
  public EFLNItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
    ItemStack stack = playerIn.getItemInHand(handIn);
    if (!playerIn.getAbilities().instabuild) {
      stack.shrink(1);
    }

    level.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), Sounds.THROWBALL_THROW.getSound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
    if (!level.isClientSide()) {
      EFLNEntity efln = new EFLNEntity(level, playerIn);
      efln.setItem(stack);
      efln.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F, 1.0F);
      level.addFreshEntity(efln);
    }

    playerIn.awardStat(Stats.ITEM_USED.get(this));
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.appendHoverText(stack, level, tooltip, flag);
  }
}
