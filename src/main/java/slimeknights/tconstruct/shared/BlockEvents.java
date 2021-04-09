package slimeknights.tconstruct.shared;

import slimeknights.tconstruct.world.TinkerWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BlockEvents {

  // Slimy block jump stuff
  //LivingEvent.LivingJumpEvent event
  //TODO: mixin this event
  public static void onLivingJump(Entity entity) {
    if (entity == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
    if (entity.getEntityWorld().isAir(pos)) {
      pos = pos.down();
    }
    BlockState state = entity.getEntityWorld().getBlockState(pos);
    Block block = state.getBlock();

    if (TinkerWorld.congealedSlime.contains(block)) {
      bounce(entity, 0.25f);
    } else if (TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block)) {
      bounce(entity, 0.06f);
    }
  }

  private static void bounce(Entity entity, float amount) {
    entity.setVelocity(entity.getVelocity().add(0.0D, (double) amount, 0.0D));
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }

  private BlockEvents() {}
}
