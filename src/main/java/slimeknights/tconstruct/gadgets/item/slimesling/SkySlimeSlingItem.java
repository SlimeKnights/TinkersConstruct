package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.shared.block.SlimeType;

public class SkySlimeSlingItem extends BaseSlimeSlingItem {
  private static final float DEGREE_TO_RAD = (float) Math.PI / 180.0F;

  public SkySlimeSlingItem(Properties props) {
    super(props, SlimeType.SKY);
  }

  @Override
  public float getForce(ItemStack stack, int timeLeft) {
    int i = this.getUseDuration(stack) - timeLeft;
    float f = i / 20.0F;
    f = (f * f + f * 2.0F) / 3.0F;
    f *= 4f;

    if (f > 3f) {
      f = 3f;
    }
    return f;
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof Player player)) {
      return;
    }

    // don't allow free flight when using an elytra, should use fireworks
    if (player.isFallFlying()) {
      return;
    }

    player.causeFoodExhaustion(0.2F);
    //player.setSprinting(true);

    float f = getForce(stack, timeLeft);
    float speed = f / 3F;
    Vec3 look = player.getLookAngle();
    player.push(
      (look.x * speed),
      (1 + look.y) * speed / 2f,
      (look.z * speed));

    SlimeBounceHandler.addBounceHandler(player);
    if (!worldIn.isClientSide) {
      player.getCooldowns().addCooldown(stack.getItem(), 3);
      onSuccess(player, stack);
    }
  }
}
