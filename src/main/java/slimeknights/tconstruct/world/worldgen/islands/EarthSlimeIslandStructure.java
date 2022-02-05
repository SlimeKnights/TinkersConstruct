package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EarthSlimeIslandStructure extends AbstractIslandStructure {
  private static final List<SpawnerData> MONSTERS = ImmutableList.of(new MobSpawnSettings.SpawnerData(TinkerWorld.earthSlimeEntity.get(), 30, 4, 4));

  public EarthSlimeIslandStructure() {
    super(new IIslandSettings() {
      @Override
      public IIslandVariant getVariant(Random random) {
        return random.nextBoolean() ? IslandVariants.EARTH_BLUE : IslandVariants.EARTH_GREEN;
      }

      @Override
      public int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, Random random) {
        return Math.max(generator.getSeaLevel() - 7, 0);
      }
    });
  }

  @Override
  public List<SpawnerData> getDefaultSpawnList(MobCategory category) {
    return category == MobCategory.MONSTER ? MONSTERS : Collections.emptyList();
  }
}
