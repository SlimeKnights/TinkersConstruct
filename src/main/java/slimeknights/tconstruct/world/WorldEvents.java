package slimeknights.tconstruct.world;

import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldEvents {

  // Custom slime spawning on slime islands
  Biome.SpawnListEntry magmaSlimeSpawn = new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 150, 4, 6);
  //TODO Biome.SpawnListEntry blueSlimeSpawn = new Biome.SpawnListEntry(BlueSlimeEntity.class, 15, 2, 4);

  @SubscribeEvent
  public void extraSlimeSpawn(WorldEvent.PotentialSpawns event) {
    //TODO
    /*if (event.getType() == EntityClassification.MONSTER) {
      // inside a magma slime island?
      if (MagmaSlimeIslandGenerator.INSTANCE.isSlimeIslandAt(event.getWorld(), event.getPos().down(3))) {
        // spawn magma slime, pig zombies have weight 100
        event.getList().clear();
        event.getList().add(this.magmaSlimeSpawn);
      }
      // inside a slime island?
      if (SlimeIslandGenerator.INSTANCE.isSlimeIslandAt(event.getWorld(), event.getPos().down(3))) {
        // spawn blue slime, most regular mobs have weight 10
        event.getList().clear();
        event.getList().add(this.blueSlimeSpawn);
      }
    }*/
  }
}
