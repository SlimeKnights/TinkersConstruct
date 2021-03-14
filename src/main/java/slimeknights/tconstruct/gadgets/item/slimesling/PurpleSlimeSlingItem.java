package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

public class PurpleSlimeSlingItem extends BaseSlimeSlingItem {

  public PurpleSlimeSlingItem(Properties props) {
    super(props);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft);

    Vector3d look = player.getLookVec();
    double offX = look.x * f;
    double offY = look.y * f + 1;
    double offZ = look.z * f;

    // TODO: Ensure that this is a good location, and I won't end up 5 meters underground
    // attemptTeleport seems a little bit too restrictive to me. It will still be something to play with
    if (player.attemptTeleport(player.getPosX() + offX, player.getPosY() + offY, player.getPosZ() + offZ, true)) {
      if (player instanceof ServerPlayerEntity) {
        ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
        TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(player), playerMP);
      }

      // particle effect from EnderPearlEntity
      for (int i = 0; i < 32; ++i) {
        worldIn.addParticle(ParticleTypes.PORTAL, player.getPosX(), player.getPosY() + worldIn.rand.nextDouble() * 2.0D, player.getPosZ(), worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
      }

      player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
      player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }
  }
}
