package slimeknights.tconstruct.world;
/*
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

@EventBusSubscriber(modid = TConstruct.modID, bus = Bus.FORGE)
public class WorldEvents {

  // Custom slime spawning on slime islands
  private Biome.SpawnListEntry magmaSlimeSpawn = new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 150, 4, 6);
  private Biome.SpawnListEntry blueSlimeSpawn = new Biome.SpawnListEntry(TinkerWorld.blueSlimeEntity.get(), 15, 2, 4);

  @SubscribeEvent
  static void extraSlimeSpawn(WorldEvent.PotentialSpawns event) {
    if (event.getType() == EntityClassification.MONSTER) {
      // inside a magma slime island?
      if (TinkerStructures.netherSlimeIsland.get().isPositionInsideStructure(event.getWorld(), event.getPos().down(3)) && shouldSpawn(event.getWorld(), event.getPos())) {
        // spawn magma slime, pig zombies have weight 100
        event.getList().clear();
        event.getList().add(this.magmaSlimeSpawn);
      }
      // inside a slime island?
      if (TinkerStructures.slimeIsland.get().isPositionInsideStructure(event.getWorld(), event.getPos().down(3)) && shouldSpawn(event.getWorld(), event.getPos())) {
        // spawn blue slime, most regular mobs have weight 10
        event.getList().clear();
        event.getList().add(this.blueSlimeSpawn);
      }
    }
  }

  public boolean shouldSpawn(IWorld worldIn, BlockPos pos) {
    FluidState ifluidstate = worldIn.getFluidState(pos);
    BlockPos down = pos.down();

    if (ifluidstate.isTagged(TinkerTags.Fluids.SLIME) && worldIn.getFluidState(down).isTagged(TinkerTags.Fluids.SLIME)) {
      return true;
    }

    return worldIn.getBlockState(pos.down()).getBlock() instanceof SlimeGrassBlock;
  }
}
*/
