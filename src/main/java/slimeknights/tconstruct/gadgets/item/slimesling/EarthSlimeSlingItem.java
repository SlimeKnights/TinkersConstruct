package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.shared.block.SlimeType;

public class EarthSlimeSlingItem extends BaseSlimeSlingItem {

  public EarthSlimeSlingItem(Properties props) {
    super(props, SlimeType.EARTH);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft);

    // check if player was targeting a block
    BlockRayTraceResult mop = rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
    if (mop.getType() == RayTraceResult.Type.BLOCK) {
      player.getCooldownTracker().setCooldown(stack.getItem(), 3);

      // we fling the inverted player look vector
      Vector3d vec = player.getLookVec().normalize();
      player.addVelocity(vec.x * -f,
        vec.y * -f / 3f,
        vec.z * -f);

      playerServerMovement(player);
      SlimeBounceHandler.addBounceHandler(player);
      onSuccess(player, stack);
    } else {
      playMissSound(player);
    }
  }
}
