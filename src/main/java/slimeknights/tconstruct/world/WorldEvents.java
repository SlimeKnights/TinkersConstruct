package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;
import slimeknights.tconstruct.world.worldgen.MagmaSlimeIslandGenerator;
import slimeknights.tconstruct.world.worldgen.SlimeIslandGenerator;

public class WorldEvents {

  // Slimy block jump stuff
  @SubscribeEvent
  public void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if(event.getEntity() == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
    if(event.getEntity().worldObj.isAirBlock(pos)) {
      pos = pos.down();
    }
    IBlockState state = event.getEntity().worldObj.getBlockState(pos);
    Block block = state.getBlock();

    if(block == TinkerWorld.slimeBlockCongealed) {
      bounce(event.getEntity(), 0.25f);
    }
    else if(block == TinkerCommons.blockSoil) {
      if(state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.SLIMY_MUD_GREEN ||
         state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.SLIMY_MUD_BLUE) {
        bounce(event.getEntity(), 0.15f);
      }
    }
    else if(block == TinkerWorld.slimeDirt || block == TinkerWorld.slimeGrass) {
      bounce(event.getEntity(), 0.06f);
    }
  }

  private void bounce(Entity entity, float amount) {
    entity.motionY += amount;
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }

  // Custom slime spawning on slime islands
  Biome.SpawnListEntry magmaSlimeSpawn = new Biome.SpawnListEntry(EntityMagmaCube.class, 150, 4, 6);
  Biome.SpawnListEntry blueSlimeSpawn = new Biome.SpawnListEntry(EntityBlueSlime.class, 15, 2, 4);

  @SubscribeEvent
  public void extraSlimeSpawn(WorldEvent.PotentialSpawns event) {
    if(event.getType() == EnumCreatureType.MONSTER || event.getType() == EnumCreatureType.WATER_CREATURE) {
      // inside a magma slime island?
      if(MagmaSlimeIslandGenerator.INSTANCE.isSlimeIslandAt(event.getWorld(), event.getPos().down(3))) {
        // spawn magma slime, pig zombies have weight 100
        event.getList().clear();
        event.getList().add(magmaSlimeSpawn);
      }
      // inside a slime island?
      if(SlimeIslandGenerator.INSTANCE.isSlimeIslandAt(event.getWorld(), event.getPos().down(3))) {
        // spawn blue slime, most regular mobs have weight 10
        event.getList().clear();
        event.getList().add(blueSlimeSpawn);
      }
    }
  }
}
