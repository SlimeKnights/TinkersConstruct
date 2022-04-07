package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Random;

public class EarthSlimeIslandStructure extends AbstractIslandStructure {
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
}
