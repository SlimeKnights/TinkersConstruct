package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Rotation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.List;
import java.util.Random;

public class BloodSlimeIslandStructure extends AbstractIslandStructure {
  private static final List<MobSpawnInfo.Spawners> STRUCTURE_MONSTERS = ImmutableList.of(
    new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 150, 4, 6)
  );

  @Override
  public GenerationStage.Decoration getDecorationStage() {
    return GenerationStage.Decoration.UNDERGROUND_DECORATION;
  }

  @Override
  public IIslandVariant getVariant(Random random) {
    return IslandVariants.BLOOD;
  }

  @Override
  public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
    return STRUCTURE_MONSTERS;
  }

  @Override
  protected int getHeight(ChunkGenerator generator, Rotation rotation, int x, int z, Random random) {
    return Math.max(generator.getSeaLevel() - 7, 0);
  }
}
