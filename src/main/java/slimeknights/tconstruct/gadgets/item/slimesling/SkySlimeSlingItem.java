package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.shared.block.SlimeType;

public class SkySlimeSlingItem extends BaseSlimeSlingItem {

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
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;

    // don't allow free flight when using an elytra, should use fireworks
    if (player.isElytraFlying()) {
      return;
    }

    float f = getForce(stack, timeLeft);

    player.addExhaustion(0.2F);
    player.getCooldownTracker().setCooldown(stack.getItem(), 3);
    player.setSprinting(true);

    float speed = f / 3F;
    player.addVelocity(
      (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed),
      speed,
      (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed));

    playerServerMovement(player);
    onSuccess(player, stack);
    SlimeBounceHandler.addBounceHandler(player);
  }
}
