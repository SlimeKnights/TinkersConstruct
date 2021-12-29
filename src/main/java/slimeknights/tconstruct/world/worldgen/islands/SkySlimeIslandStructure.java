package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Overworld structure containing sky slimes, spawns in the sky
 */
public class SkySlimeIslandStructure extends AbstractIslandStructure {
  private static final List<MobSpawnSettings.SpawnerData> MONSTERS = ImmutableList.of(new MobSpawnSettings.SpawnerData(TinkerWorld.skySlimeEntity.get(), 30, 4, 4));

  @Override
  public List<SpawnerData> getDefaultSpawnList(MobCategory category) {
    return category == MobCategory.MONSTER ? MONSTERS : Collections.emptyList();
  }

  @Override
  public IIslandVariant getVariant(Random random) {
    return random.nextBoolean() ? IslandVariants.SKY_BLUE : IslandVariants.SKY_GREEN;
  }
}
