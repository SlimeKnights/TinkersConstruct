package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BloodSlimeIslandStructure extends AbstractIslandStructure {
  private static final List<MobSpawnSettings.SpawnerData> STRUCTURE_MONSTERS = ImmutableList.of(
    new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 150, 4, 6)
  );

  @Override
  public GenerationStep.Decoration step() {
    return GenerationStep.Decoration.UNDERGROUND_DECORATION;
  }

  @Override
  public IIslandVariant getVariant(Random random) {
    return IslandVariants.BLOOD;
  }

  @Override
  public List<SpawnerData> getDefaultSpawnList(MobCategory category) {
    return category == MobCategory.MONSTER ? STRUCTURE_MONSTERS : Collections.emptyList();
  }

  @Override
  protected int getHeight(ChunkGenerator generator, Rotation rotation, int x, int z, Random random) {
    return Math.max(generator.getSeaLevel() - 7, 0);
  }
}
