package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    List<BlockPos> posToCheck = new ArrayList<>();
    BlockPos posAttempt = new BlockPos(player.getPosX() + offX, player.getPosY() + offY, player.getPosZ() + offZ);
    while (Math.abs(offX) > .5 || Math.abs(offY) > .5 || Math.abs(offZ) > .5) { // while not too close to player
      posToCheck.add(posAttempt);
      offX -= (Math.abs(offX) > .25 ? (offX >= 0 ? 1 : -1) * .25 : 0);
      offY -= (Math.abs(offY) > .25 ? (offY >= 0 ? 1 : -1) * .25 : 0);
      offZ -= (Math.abs(offZ) > .25 ? (offZ >= 0 ? 1 : -1) * .25 : 0);
      posAttempt = new BlockPos(player.getPosX() + offX, player.getPosY() + offY, player.getPosZ() + offZ);
    }

    // get furthest teleportable block
    Optional<BlockPos> furthestPosOp  = posToCheck.stream().distinct().filter(block -> !worldIn.getBlockState(block).isSuffocating(worldIn, block)).findFirst();
    if (furthestPosOp.isPresent()) {
      player.getCooldownTracker().setCooldown(stack.getItem(), 3);

      BlockPos furthestPos = furthestPosOp.get();
      player.setPosition(furthestPos.getX(), furthestPos.getY(), furthestPos.getZ());

      // particle effect from EnderPearlEntity
      for (int i = 0; i < 32; ++i) {
        worldIn.addParticle(ParticleTypes.PORTAL, player.getPosX(), player.getPosY() + worldIn.rand.nextDouble() * 2.0D, player.getPosZ(), worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
      }
      playerServerMovement(player);
      player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
      onSuccess(player, stack);
    } else {
      playMissSound(player);
    }
  }
}
