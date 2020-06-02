package slimeknights.tconstruct.world;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.entity.WorldEntities;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

public class WorldEvents {

  // Custom slime spawning on slime islands
  private Biome.SpawnListEntry magmaSlimeSpawn = new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 150, 4, 6);
  private Biome.SpawnListEntry blueSlimeSpawn = new Biome.SpawnListEntry(WorldEntities.blue_slime_entity, 15, 2, 4);

  @SubscribeEvent
  public void extraSlimeSpawn(WorldEvent.PotentialSpawns event) {
    if (event.getType() == EntityClassification.MONSTER) {
      // inside a magma slime island?
      if (TinkerWorld.NETHER_SLIME_ISLAND.get().isPositionInsideStructure(event.getWorld(), event.getPos().down(3)) && shouldSpawn(event.getWorld(), event.getPos())) {
        // spawn magma slime, pig zombies have weight 100
        event.getList().clear();
        event.getList().add(this.magmaSlimeSpawn);
      }
      // inside a slime island?
      if (TinkerWorld.SLIME_ISLAND.get().isPositionInsideStructure(event.getWorld(), event.getPos().down(3)) && shouldSpawn(event.getWorld(), event.getPos())) {
        // spawn blue slime, most regular mobs have weight 10
        event.getList().clear();
        event.getList().add(this.blueSlimeSpawn);
      }
    }
  }

  public boolean shouldSpawn(IWorld worldIn, BlockPos pos) {
    IFluidState ifluidstate = worldIn.getFluidState(pos);
    BlockPos down = pos.down();

    if (ifluidstate.isTagged(Tags.Fluids.SLIME) && worldIn.getFluidState(down).isTagged(Tags.Fluids.SLIME)) {
      return true;
    }

    return worldIn.getBlockState(pos.down()).getBlock() instanceof SlimeGrassBlock;
  }
}
