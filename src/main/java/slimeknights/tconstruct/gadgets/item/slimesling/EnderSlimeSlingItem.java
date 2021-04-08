package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnderSlimeSlingItem extends BaseSlimeSlingItem {

  public EnderSlimeSlingItem(Settings props) {
    super(props, SlimeType.ENDER);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft);

    Vec3d look = player.getRotationVector();
    double offX = look.x * f;
    double offY = look.y * f + 1; // add extra to help with bad collisions
    double offZ = look.z * f;

    List<BlockPos> posToCheck = new ArrayList<>();
    BlockPos posAttempt = new BlockPos(player.getX() + offX, player.getY() + offY, player.getZ() + offZ);
    while (Math.abs(offX) > .5 || Math.abs(offY) > .5 || Math.abs(offZ) > .5) { // while not too close to player
      posToCheck.add(posAttempt);
      offX -= (Math.abs(offX) > .25 ? (offX >= 0 ? 1 : -1) * .25 : 0);
      offY -= (Math.abs(offY) > .25 ? (offY >= 0 ? 1 : -1) * .25 : 0);
      offZ -= (Math.abs(offZ) > .25 ? (offZ >= 0 ? 1 : -1) * .25 : 0);
      posAttempt = new BlockPos(player.getX() + offX, player.getY() + offY, player.getZ() + offZ);
    }

    // get furthest teleportable block
    Optional<BlockPos> furthestPosOp  = posToCheck.stream().distinct().filter(block -> !worldIn.getBlockState(block).shouldSuffocate(worldIn, block)).findFirst();
    if (furthestPosOp.isPresent()) {
      player.getItemCooldownManager().set(stack.getItem(), 3);

      BlockPos furthestPos = furthestPosOp.get();
      player.updatePosition(furthestPos.getX(), furthestPos.getY(), furthestPos.getZ());

      // particle effect from EnderPearlEntity
      for (int i = 0; i < 32; ++i) {
        worldIn.addParticle(ParticleTypes.PORTAL, player.getX(), player.getY() + worldIn.random.nextDouble() * 2.0D, player.getZ(), worldIn.random.nextGaussian(), 0.0D, worldIn.random.nextGaussian());
      }
      playerServerMovement(player);
      player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
      onSuccess(player, stack);
    } else {
      playMissSound(player);
    }
  }
}
