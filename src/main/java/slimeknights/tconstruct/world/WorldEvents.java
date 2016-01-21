package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;

public class WorldEvents {

  // Slimy block jump stuff
  @SubscribeEvent
  public void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if(event.entity == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.entity.posX, event.entity.posY, event.entity.posZ);
    if(event.entity.worldObj.isAirBlock(pos)) {
      pos = pos.down();
    }
    IBlockState state = event.entity.worldObj.getBlockState(pos);
    Block block = state.getBlock();

    if(block == TinkerWorld.slimeBlockCongealed) {
      bounce(event.entity, 0.25f);
    }
    else if(block == TinkerCommons.blockSoil) {
      if(state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.SLIMY_MUD_GREEN ||
         state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.SLIMY_MUD_BLUE) {
        bounce(event.entity, 0.15f);
      }
    }
    else if(block == TinkerWorld.slimeDirt || block == TinkerWorld.slimeGrass) {
      bounce(event.entity, 0.06f);
    }
  }

  private void bounce(Entity entity, float amount) {
    entity.motionY += amount;
    entity.playSound(Sounds.slime_small, 0.5f + amount, 1f);
  }
}
