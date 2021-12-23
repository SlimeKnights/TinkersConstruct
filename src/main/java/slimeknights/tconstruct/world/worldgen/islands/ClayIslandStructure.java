package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.List;
import java.util.Random;

/** Rare island that spawns a random tree and a lake of clay */
public class ClayIslandStructure extends AbstractIslandStructure {
  private final List<Spawners> monsters = ImmutableList.of(new MobSpawnInfo.Spawners(TinkerWorld.terracubeEntity.get(), 30, 4, 4));

  @Override
  public List<Spawners> getDefaultSpawnList() {
    return monsters;
  }

  @Override
  protected IIslandVariant getVariant(Random random) {
    return IslandVariants.SKY_CLAY;
  }
}
