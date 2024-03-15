package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

public class BloodSlimeIslandStructure extends AbstractIslandStructure {
  public BloodSlimeIslandStructure() {
    super(new IIslandSettings() {
      @Override
      public IIslandVariant getVariant(RandomSource random) {
        return IslandVariants.BLOOD;
      }

      @Override
      public int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, RandomSource random, RandomState randomState) {
        return Math.max(generator.getSeaLevel() - 7, 0);
      }
    });
  }

  @Override
  public GenerationStep.Decoration step() {
    return GenerationStep.Decoration.UNDERGROUND_DECORATION;
  }
}
