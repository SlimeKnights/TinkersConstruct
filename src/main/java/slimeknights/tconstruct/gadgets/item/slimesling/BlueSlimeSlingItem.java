package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

public class BlueSlimeSlingItem extends BaseSlimeSlingItem {

  public BlueSlimeSlingItem(Properties props) {
    super(props);
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

    if (player instanceof ServerPlayerEntity) {
      ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
      TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(player), playerMP);
    }

    player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
    SlimeBounceHandler.addBounceHandler(player);
  }
}
