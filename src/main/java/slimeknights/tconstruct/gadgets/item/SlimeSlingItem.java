package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.block.StickySlimeBlock;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

import javax.annotation.Nonnull;

public class SlimeSlingItem extends TooltipItem {

  StickySlimeBlock.SlimeType slimeType;

  public SlimeSlingItem(StickySlimeBlock.SlimeType slimeType, Properties props) {
    super(props);
    this.slimeType = slimeType;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    playerIn.setActiveHand(hand);
    return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    // has to be on ground to do something
    if (!player.isOnGround()) {
      return;
    }

    // copy chargeup code from bow \o/
    int i = this.getUseDuration(stack) - timeLeft;
    float f = i / 20.0F;
    f = (f * f + f * 2.0F) / 3.0F;
    f *= 4f;

    if (f > 6f) {
      f = 6f;
    }

    switch (this.slimeType) {
      case GREEN:
        // check if player was targeting a block
        RayTraceResult mop = rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
        if (mop.getType() == RayTraceResult.Type.BLOCK) {
          // we fling the inverted player look vector
          Vector3d vec = player.getLookVec().normalize();
          player.addVelocity(vec.x * -f,
            vec.y * -f / 3f,
            vec.z * -f);

          if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
            TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(player), playerMP);
          }

          player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
          SlimeBounceHandler.addBounceHandler(player);
        }
        break;
      case BLUE:
        
        break;
      case MAGMA:

        break;
      case PURPLE:

        break;
      case BLOOD:

        break;
    }


  }

  /**
   * How long it takes to use or consume an item
   */
  @Override
  public int getUseDuration(ItemStack stack) {
    return 72000;
  }

  /**
   * returns the action that specifies what animation to play when the items is being used
   */
  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }
}
