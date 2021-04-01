package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.shared.block.SlimeType;

public class IchorSlimeSlingItem extends BaseSlimeSlingItem {

  public IchorSlimeSlingItem(Properties props) {
    super(props, SlimeType.ICHOR);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft) / 2;

    float range = 5F;
    Vector3d start = player.getEyePosition(1F);
    Vector3d look = player.getLookVec();
    Vector3d direction = start.add(look.x * range, look.y * range, look.z * range);
    AxisAlignedBB bb = player.getBoundingBox().expand(look.x * range, look.y * range, look.z * range).expand(1, 1, 1);

    EntityRayTraceResult emop = ProjectileHelper.rayTraceEntities(worldIn, player, start, direction, bb, (e) -> e instanceof LivingEntity);
    if (emop != null) {
      LivingEntity target = (LivingEntity) emop.getEntity();
      double targetDist = start.squareDistanceTo(target.getEyePosition(1F));

      // cancel if there's a block in the way
      BlockRayTraceResult mop = rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
      double blockDist = mop.getPos().distanceSq(start.x, start.y, start.z, true);
      if (mop.getType() == RayTraceResult.Type.BLOCK && targetDist > blockDist) {
        playMissSound(player);
        return;
      }

      player.getCooldownTracker().setCooldown(stack.getItem(), 3);
      target.applyKnockback(f , -look.x, -look.z);
      playerServerMovement(target);
      onSuccess(player, stack);
    } else {
      playMissSound(player);
    }
  }
}
