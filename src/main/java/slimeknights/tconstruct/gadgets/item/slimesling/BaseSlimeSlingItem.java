package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nonnull;

public abstract class BaseSlimeSlingItem extends TooltipItem {

  private final SlimeType type;
  public BaseSlimeSlingItem(Properties props, SlimeType type) {
    super(props);
    this.type = type;
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return repair.getItem() == TinkerCommons.slimeball.get(type);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
    ItemStack itemStackIn = playerIn.getItemInHand(hand);
    playerIn.startUsingItem(hand);
    return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
  }

  /** How long it takes to use or consume an item */
  @Override
  public int getUseDuration(ItemStack stack) {
    return 72000;
  }

  /** returns the action that specifies what animation to play when the items is being used */
  @Override
  public UseAction getUseAnimation(ItemStack stack) {
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

  /** Plays the success sound and damages the sling */
  protected void onSuccess(PlayerEntity player, ItemStack sling) {
    player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1f, 1f);
    sling.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
  }

  protected void playMissSound(PlayerEntity player) {
    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1f, .5f);
  }
}
