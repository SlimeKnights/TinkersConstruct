package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.events.teleport.SlimeslingTeleportEvent;
import slimeknights.tconstruct.shared.block.SlimeType;

public class EnderSlimeSlingItem extends BaseSlimeSlingItem {

  public EnderSlimeSlingItem(Properties props) {
    super(props, SlimeType.ENDER);
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
    double offY = look.y * f + 1; // add extra to help with bad collisions
    double offZ = look.z * f;

    // find teleport target
    BlockPos furthestPos = null;
    while (Math.abs(offX) > .5 || Math.abs(offY) > .5 || Math.abs(offZ) > .5) { // while not too close to player
      BlockPos posAttempt = new BlockPos(player.getPosX() + offX, player.getPosY() + offY, player.getPosZ() + offZ);

      // if we do not have a position yet, see if this one is valid
      if (furthestPos == null) {
        if (worldIn.getWorldBorder().contains(posAttempt) && !worldIn.getBlockState(posAttempt).isSuffocating(worldIn, posAttempt)) {
          furthestPos = posAttempt;
        }
      } else {
        // if we already have a position, clear if the new one is unbreakable
        if (worldIn.getBlockState(posAttempt).getBlockHardness(worldIn, posAttempt) == -1) {
          furthestPos = null;
        }
      }

      // update for next iteration
      offX -= (Math.abs(offX) > .25 ? (offX >= 0 ? 1 : -1) * .25 : 0);
      offY -= (Math.abs(offY) > .25 ? (offY >= 0 ? 1 : -1) * .25 : 0);
      offZ -= (Math.abs(offZ) > .25 ? (offZ >= 0 ? 1 : -1) * .25 : 0);
    }

    // get furthest teleportable block
    if (furthestPos != null) {
      player.getCooldownTracker().setCooldown(stack.getItem(), 3);

      SlimeslingTeleportEvent event = new SlimeslingTeleportEvent(player, furthestPos.getX() + 0.5f, furthestPos.getY(), furthestPos.getZ() + 0.5f, stack);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
        player.setPosition(event.getTargetX(), event.getTargetY(), event.getTargetZ());

        // particle effect from EnderPearlEntity
        for (int i = 0; i < 32; ++i) {
          worldIn.addParticle(ParticleTypes.PORTAL, player.getPosX(), player.getPosY() + worldIn.rand.nextDouble() * 2.0D, player.getPosZ(), worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
        }
        playerServerMovement(player);
        player.playSound(Sounds.SLIME_SLING_TELEPORT.getSound(), 1f, 1f);
        onSuccess(player, stack);
        return;
      }
    }
    playMissSound(player);
  }
}
