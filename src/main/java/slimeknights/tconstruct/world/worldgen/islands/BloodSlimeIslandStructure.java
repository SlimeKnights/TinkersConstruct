package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Random;

public class BloodSlimeIslandStructure extends AbstractIslandStructure {
  public BloodSlimeIslandStructure() {
    super(new IIslandSettings() {
      @Override
      public IIslandVariant getVariant(Random random) {
        return IslandVariants.BLOOD;
      }

      @Override
      public int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, Random random) {
        return Math.max(generator.getSeaLevel() - 7, 0);
      }
    });
  }

  @Override
  public GenerationStep.Decoration step() {
    return GenerationStep.Decoration.UNDERGROUND_DECORATION;
  }
}
