package slimeknights.tconstruct.fluids.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/** Magma bottle instance, which lights the drinker on fire */
public class MagmaBottleItem extends Item {
  private final int fireTime;
  public MagmaBottleItem(Properties props, int fireTime) {
    super(props);
    this.fireTime = fireTime;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, context, tooltip, flagIn);
    tooltip.add(Component.translatable(
      "potion.withDuration",
      Blocks.FIRE.getName(),
      StringUtil.formatTickDuration(fireTime * 20, 20.0f)
    ).withStyle(MobEffectCategory.HARMFUL.getTooltipFormatting()));
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    player.startUsingItem(hand);
    return InteractionResultHolder.consume(player.getItemInHand(hand));
  }

  @Override
  public int getUseDuration(ItemStack pStack, LivingEntity entity) {
    return 32;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack pStack) {
    return UseAnim.DRINK;
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    living.igniteForSeconds(fireTime);
    ItemStack container = stack.getCraftingRemainingItem();
    Player player = living instanceof Player p ? p : null;
    if (player == null || !player.getAbilities().instabuild) {
      stack.shrink(1);
      container = container.copy();
      if (stack.isEmpty()) {
        return container;
      }
      if (player != null) {
        if (!player.getInventory().add(container)) {
          player.drop(container, false);
        }
      }
    }
    return stack;
  }
}
