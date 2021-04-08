package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.shared.block.SlimeType;

public class EarthSlimeSlingItem extends BaseSlimeSlingItem {

  public EarthSlimeSlingItem(Settings props) {
    super(props, SlimeType.EARTH);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft);

    // check if player was targeting a block
    BlockHitResult mop = raycast(worldIn, player, RaycastContext.FluidHandling.NONE);
    if (mop.getType() == HitResult.Type.BLOCK) {
      player.getItemCooldownManager().set(stack.getItem(), 3);

      // we fling the inverted player look vector
      Vec3d vec = player.getRotationVector().normalize();
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
