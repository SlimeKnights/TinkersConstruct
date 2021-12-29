package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
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
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    ItemStack itemStackIn = playerIn.getItemInHand(hand);
    playerIn.startUsingItem(hand);
    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
  }

  /** How long it takes to use or consume an item */
  @Override
  public int getUseDuration(ItemStack stack) {
    return 72000;
  }

  /** returns the action that specifies what animation to play when the items is being used */
  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.BOW;
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
  protected void onSuccess(Player player, ItemStack sling) {
    player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1f, 1f);
    sling.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
  }

  protected void playMissSound(Player player) {
    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1f, .5f);
  }
}
