package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.library.TinkerPulseIds;

@Mod.EventBusSubscriber(modid = TConstruct.modID)
public class BlockEvents {

  private static boolean worldLoaded = TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID);

  // Slimy block jump stuff
  @SubscribeEvent
  public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.getEntity().getPosX(), event.getEntity().getPosY(), event.getEntity().getPosZ());
    if (event.getEntity().getEntityWorld().isAirBlock(pos)) {
      pos = pos.down();
    }
    BlockState state = event.getEntity().getEntityWorld().getBlockState(pos);
    Block block = state.getBlock();

    if (block == WorldBlocks.congealed_green_slime || block == WorldBlocks.congealed_blue_slime || block == WorldBlocks.congealed_purple_slime || block == WorldBlocks.congealed_blood_slime || block == WorldBlocks.congealed_magma_slime) {
      bounce(event.getEntity(), 0.25f);
    }
    else if (block == CommonBlocks.slimy_mud_green || block == CommonBlocks.slimy_mud_blue) {
      bounce(event.getEntity(), 0.15f);
    }
    else if (worldLoaded && (block == WorldBlocks.green_slime_dirt || block == WorldBlocks.blue_slime_dirt || block == WorldBlocks.purple_slime_dirt || block == WorldBlocks.magma_slime_dirt || block == WorldBlocks.blue_vanilla_slime_grass || block == WorldBlocks.purple_vanilla_slime_grass || block == WorldBlocks.orange_vanilla_slime_grass || block == WorldBlocks.blue_green_slime_grass || block == WorldBlocks.purple_green_slime_grass || block == WorldBlocks.orange_green_slime_grass || block == WorldBlocks.blue_blue_slime_grass || block == WorldBlocks.purple_blue_slime_grass || block == WorldBlocks.orange_blue_slime_grass || block == WorldBlocks.blue_purple_slime_grass || block == WorldBlocks.purple_purple_slime_grass || block == WorldBlocks.orange_purple_slime_grass || block == WorldBlocks.blue_magma_slime_grass || block == WorldBlocks.purple_magma_slime_grass || block == WorldBlocks.orange_magma_slime_grass)) {
      bounce(event.getEntity(), 0.06f);
    }
  }

  private static void bounce(Entity entity, float amount) {
    entity.setMotion(entity.getMotion().add(0.0D, (double) amount, 0.0D));
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }

  private BlockEvents() {}
}
