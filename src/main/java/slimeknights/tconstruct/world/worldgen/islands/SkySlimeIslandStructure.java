package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.biome.MobSpawnInfo;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.List;
import java.util.Random;

/**
 * Overworld structure containing sky slimes, spawns in the sky
 */
public class SkySlimeIslandStructure extends AbstractIslandStructure {
  private static final List<MobSpawnInfo.Spawners> STRUCTURE_MONSTERS = ImmutableList.of(
    new MobSpawnInfo.Spawners(TinkerWorld.skySlimeEntity.get(), 30, 4, 4));

  @Override
  public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
    return STRUCTURE_MONSTERS;
  }

  @Override
  public IIslandVariant getVariant(Random random) {
    return random.nextBoolean() ? IslandVariants.SKY_BLUE : IslandVariants.SKY_GREEN;
  }
}
