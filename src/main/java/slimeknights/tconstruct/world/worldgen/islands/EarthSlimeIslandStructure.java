package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.util.Rotation;
import net.minecraft.world.gen.ChunkGenerator;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Random;

public class EarthSlimeIslandStructure extends AbstractIslandStructure {
  @Override
  public IIslandVariant getVariant(Random random) {
    return random.nextBoolean() ? IslandVariants.EARTH_BLUE : IslandVariants.EARTH_GREEN;
  }

  @Override
  protected int getHeight(ChunkGenerator generator, Rotation rotation, int x, int z, Random random) {
    return generator.getSeaLevel() - 9;
  }
}
