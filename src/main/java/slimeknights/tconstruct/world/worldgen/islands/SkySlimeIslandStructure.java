package slimeknights.tconstruct.world.worldgen.islands;

import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

/**
 * Overworld structure containing sky slimes, spawns in the sky
 */
public class SkySlimeIslandStructure extends AbstractIslandStructure {
  public SkySlimeIslandStructure() {
    super(random -> random.nextBoolean() ? IslandVariants.SKY_BLUE : IslandVariants.SKY_GREEN);
  }
}
