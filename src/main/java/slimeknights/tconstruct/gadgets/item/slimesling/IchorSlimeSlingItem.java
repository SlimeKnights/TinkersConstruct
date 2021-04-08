package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import slimeknights.tconstruct.shared.block.SlimeType;

public class IchorSlimeSlingItem extends BaseSlimeSlingItem {

  public IchorSlimeSlingItem(Settings props) {
    super(props, SlimeType.ICHOR);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft) / 2;

    float range = 5F;
    Vec3d start = player.getCameraPosVec(1F);
    Vec3d look = player.getRotationVector();
    Vec3d direction = start.add(look.x * range, look.y * range, look.z * range);
    Box bb = player.getBoundingBox().stretch(look.x * range, look.y * range, look.z * range).stretch(1, 1, 1);

    EntityHitResult emop = ProjectileUtil.getEntityCollision(worldIn, player, start, direction, bb, (e) -> e instanceof LivingEntity);
    if (emop != null) {
      LivingEntity target = (LivingEntity) emop.getEntity();
      double targetDist = start.squaredDistanceTo(target.getCameraPosVec(1F));

      // cancel if there's a block in the way
      BlockHitResult mop = raycast(worldIn, player, RaycastContext.FluidHandling.NONE);
      double blockDist = mop.getBlockPos().getSquaredDistance(start.x, start.y, start.z, true);
      if (mop.getType() == HitResult.Type.BLOCK && targetDist > blockDist) {
        playMissSound(player);
        return;
      }

      player.getItemCooldownManager().set(stack.getItem(), 3);
      target.takeKnockback(f , -look.x, -look.z);
      playerServerMovement(target);
      onSuccess(player, stack);
    } else {
      playMissSound(player);
    }
  }
}
