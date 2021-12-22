package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.shared.block.SlimeType;

public class EarthSlimeSlingItem extends BaseSlimeSlingItem {

  public EarthSlimeSlingItem(Properties props) {
    super(props, SlimeType.EARTH);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!entityLiving.isOnGround() || !(entityLiving instanceof PlayerEntity)) {
      return;
    }

    // check if player was targeting a block
    PlayerEntity player = (PlayerEntity) entityLiving;
    BlockRayTraceResult mop = rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
    if (mop.getType() == RayTraceResult.Type.BLOCK) {
      // we fling the inverted player look vector
      float f = getForce(stack, timeLeft);
      Vector3d vec = player.getLookVec().normalize();
      player.addVelocity(vec.x * -f,
                         vec.y * -f / 3f,
                         vec.z * -f);
      SlimeBounceHandler.addBounceHandler(player);

      if (!worldIn.isRemote) {
        player.getCooldownTracker().setCooldown(stack.getItem(), 3);
        onSuccess(player, stack);
      }
    } else {
      playMissSound(player);
    }
  }
}
