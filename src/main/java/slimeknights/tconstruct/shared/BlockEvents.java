package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.world.TinkerWorld;

public class BlockEvents {

  private static boolean worldLoaded = TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID);

  // Slimy block jump stuff
  @SubscribeEvent
  public void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
    if (event.getEntity().getEntityWorld().isAirBlock(pos)) {
      pos = pos.down();
    }
    BlockState state = event.getEntity().getEntityWorld().getBlockState(pos);
    Block block = state.getBlock();

    if (block == TinkerCommons.congealed_green_slime || block == TinkerCommons.congealed_blue_slime || block == TinkerCommons.congealed_purple_slime || block == TinkerCommons.congealed_blood_slime || block == TinkerCommons.congealed_magma_slime) {
      this.bounce(event.getEntity(), 0.25f);
    }
    else if (block == TinkerCommons.slimy_mud_green || block == TinkerCommons.slimy_mud_blue) {
      this.bounce(event.getEntity(), 0.15f);
    }
    else if (worldLoaded && (block == TinkerWorld.green_slime_dirt || block == TinkerWorld.blue_slime_dirt || block == TinkerWorld.purple_slime_dirt || block == TinkerWorld.magma_slime_dirt || block == TinkerWorld.blue_vanilla_slime_grass || block == TinkerWorld.purple_vanilla_slime_grass || block == TinkerWorld.orange_vanilla_slime_grass || block == TinkerWorld.blue_green_slime_grass || block == TinkerWorld.purple_green_slime_grass || block == TinkerWorld.orange_green_slime_grass || block == TinkerWorld.blue_blue_slime_grass || block == TinkerWorld.purple_blue_slime_grass || block == TinkerWorld.orange_blue_slime_grass || block == TinkerWorld.blue_purple_slime_grass || block == TinkerWorld.purple_purple_slime_grass || block == TinkerWorld.orange_purple_slime_grass || block == TinkerWorld.blue_magma_slime_grass || block == TinkerWorld.purple_magma_slime_grass || block == TinkerWorld.orange_magma_slime_grass)) {
      this.bounce(event.getEntity(), 0.06f);
    }
  }

  private void bounce(Entity entity, float amount) {
    entity.setMotion(entity.getMotion().add(0.0D, (double) amount, 0.0D));
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }
}
