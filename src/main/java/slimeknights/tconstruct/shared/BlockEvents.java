package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.world.TinkerWorld;

public class BlockEvents {

  private static boolean worldLoaded = TConstruct.pulseManager.isPulseLoaded(TinkerWorld.PulseId);

  // Slimy block jump stuff
  @SubscribeEvent
  public void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if(event.getEntity() == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
    if(event.getEntity().getEntityWorld().isAirBlock(pos)) {
      pos = pos.down();
    }
    IBlockState state = event.getEntity().getEntityWorld().getBlockState(pos);
    Block block = state.getBlock();

    if(block == TinkerCommons.blockSlimeCongealed) {
      bounce(event.getEntity(), 0.25f);
    }
    else if(block == TinkerCommons.blockSoil) {
      if(state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.SLIMY_MUD_GREEN ||
         state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.SLIMY_MUD_BLUE) {
        bounce(event.getEntity(), 0.15f);
      }
    }
    else if(worldLoaded && (block == TinkerWorld.slimeDirt || block == TinkerWorld.slimeGrass)) {
      bounce(event.getEntity(), 0.06f);
    }
  }

  private void bounce(Entity entity, float amount) {
    entity.motionY += amount;
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }
}
