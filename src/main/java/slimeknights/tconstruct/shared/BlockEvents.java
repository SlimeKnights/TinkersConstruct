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
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

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

    if (WorldBlocks.congealed_slime.contains(block)) {
      bounce(event.getEntity(), 0.25f);
    } else if (block == CommonBlocks.slimy_mud_green.get() || block == CommonBlocks.slimy_mud_blue.get()) {
      bounce(event.getEntity(), 0.15f);
    } else if (worldLoaded && (WorldBlocks.slime_dirt.contains(block) || WorldBlocks.vanilla_slime_grass.contains(block) || WorldBlocks.green_slime_grass.contains(block) || WorldBlocks.blue_slime_grass.contains(block) || WorldBlocks.purple_slime_grass.contains(block) || WorldBlocks.magma_slime_grass.contains(block))) {
      bounce(event.getEntity(), 0.06f);
    }
  }

  private static void bounce(Entity entity, float amount) {
    entity.setMotion(entity.getMotion().add(0.0D, (double) amount, 0.0D));
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }

  private BlockEvents() {}
}
