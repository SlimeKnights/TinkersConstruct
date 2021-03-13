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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
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
import java.util.List;

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
        // check if player was targeting a block
        BlockRayTraceResult mop = rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
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
        break;
      case MAGMA:
        float range = 5F;
        Vector3d start = player.getEyePosition(1F);
        Vector3d look = player.getLookVec();
        Vector3d direction = start.add(look.x * range, look.y * range, look.z * range);
        AxisAlignedBB bb = player.getBoundingBox().expand(look.x * range, look.y * range, look.z * range).expand(1, 1, 1);
        List<Entity> entitiesInArea = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, bb);
        double dist = range;
        Entity closestEntity = null;
        for (Entity entity : entitiesInArea) {
          if (entity.getBoundingBox().intersects(start, direction)) {
            if (look.distanceTo(entity.getLookVec()) < dist) {
              dist = look.distanceTo(entity.getLookVec());
              closestEntity = entity;
            }
          }
        }
        // TODO: Ensure there isn't a block in the way
        if (closestEntity != null) {
          closestEntity.addVelocity(look.x * f,
            look.y * f / 3f,
            look.z * f);
          player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
        }
        break;
      case PURPLE:
        look = player.getLookVec();
        double offX = look.x * f;
        double offY = look.y * f;
        double offZ = look.z * f;

        player.setPosition(player.getPosX() + offX, player.getPosY() + offY, player.getPosZ() + offZ);
        player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
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
