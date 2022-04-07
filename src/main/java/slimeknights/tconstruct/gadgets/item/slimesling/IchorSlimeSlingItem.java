package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.shared.block.SlimeType;

public class IchorSlimeSlingItem extends BaseSlimeSlingItem {

  public IchorSlimeSlingItem(Properties props) {
    super(props, SlimeType.ICHOR);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    if (worldIn.isClientSide || !(entityLiving instanceof Player player)) {
      return;
    }

    float f = getForce(stack, timeLeft) / 2;

    float range = 5F;
    Vec3 start = player.getEyePosition(1F);
    Vec3 look = player.getLookAngle();
    Vec3 direction = start.add(look.x * range, look.y * range, look.z * range);
    AABB bb = player.getBoundingBox().expandTowards(look.x * range, look.y * range, look.z * range).expandTowards(1, 1, 1);

    EntityHitResult emop = ProjectileUtil.getEntityHitResult(worldIn, player, start, direction, bb, (e) -> e instanceof LivingEntity);
    if (emop != null) {
      LivingEntity target = (LivingEntity) emop.getEntity();
      double targetDist = start.distanceToSqr(target.getEyePosition(1F));

      // cancel if there's a block in the way
      BlockHitResult mop = getPlayerPOVHitResult(worldIn, player, ClipContext.Fluid.NONE);
      double blockDist = mop.getBlockPos().distToCenterSqr(start);
      if (mop.getType() == HitResult.Type.BLOCK && targetDist > blockDist) {
        playMissSound(player);
        return;
      }

      player.getCooldowns().addCooldown(stack.getItem(), 3);
      target.knockback(f , -look.x, -look.z);
      if (player instanceof ServerPlayer playerMP) {
        TinkerNetwork.getInstance().sendVanillaPacket(new ClientboundSetEntityMotionPacket(player), playerMP);
      }
      onSuccess(player, stack);
    } else {
      playMissSound(player);
    }
  }
}
