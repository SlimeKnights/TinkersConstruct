package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.shared.block.StickySlimeBlock;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseSlimeSlingItem extends TooltipItem {

  public BaseSlimeSlingItem(Properties props) {
    super(props);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    playerIn.setActiveHand(hand);
    return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
  }

  /** How long it takes to use or consume an item */
  @Override
  public int getUseDuration(ItemStack stack) {
    return 72000;
  }

  /** returns the action that specifies what animation to play when the items is being used */
  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  /** Determines how much force a charged right click item will release on player letting go
   * To be used in conjunction with onPlayerStoppedUsing
   * @param stack - Item used (get from onPlayerStoppedUsing)
   * @param timeLeft - (get from onPlayerStoppedUsing)
   * @return appropriate charge for item */
  public float getForce(ItemStack stack, int timeLeft) {
    int i = this.getUseDuration(stack) - timeLeft;
    float f = i / 20.0F;
    f = (f * f + f * 2.0F) / 3.0F;
    f *= 4f;

    if (f > 6f) {
      f = 6f;
    }
    return f;
  }
}
