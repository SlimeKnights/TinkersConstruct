package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.utils.EntityUtil;
import slimeknights.tconstruct.shared.block.StickySlimeBlock;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

import javax.annotation.Nonnull;

public class SlimeSlingItem extends TooltipItem {

  StickySlimeBlock.SlimeType slimeType;
  LivingEntity magmaSlingTarget;
  Vector3d magmaSlingVec;


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


//  /**
//   * Called when the player interacts with an entity
//   */
//  @Override
//  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
//    magmaSlingTarget = target;
//    magmaSlingVec = playerIn.getLookVec().normalize();
//    return ActionResultType.PASS;
//  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {

    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    // has to be on ground to do something (except Blue)
    if ((!player.isOnGround() && this.slimeType != StickySlimeBlock.SlimeType.BLUE) || player.isElytraFlying()) {
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
      case GREEN: // might need to be scaled down
      case BLOOD: // Leaving this with old functionality for now. Should be removed when/if SlimeTypes changes
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
      case BLUE: // needs to be scaled up
        if (f > 5) {
          player.addExhaustion(0.2F);
          player.getCooldownTracker().setCooldown(stack.getItem(), 3);
          player.setSprinting(true);

          float speed = f / 3F;
          player.setVelocity(
            (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed),
            player.getMotion().getY() + speed,
            (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed));

          if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
            TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(player), playerMP);
          }

          player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
          SlimeBounceHandler.addBounceHandler(player);
        }
        break;
      case MAGMA:
        // TODO: Finish implementing
        mop = EntityUtil.raytraceEntityPlayerLook(player, 3.2F);
        if (mop == null) {
          return;
        }
        if (mop.getType() == RayTraceResult.Type.ENTITY) {
          Entity entity = ((EntityRayTraceResult) mop).getEntity();
          Vector3d vec = player.getLookVec().normalize();
          entity.setVelocity(vec.x * f,
            vec.y * f / 3f,
            vec.z * f);
          player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
        }
        break;
      case PURPLE:
        // TODO: Implement
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
