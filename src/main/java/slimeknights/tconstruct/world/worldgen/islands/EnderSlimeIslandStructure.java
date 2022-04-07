package slimeknights.tconstruct.world.worldgen.islands;

import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

public class EnderSlimeIslandStructure extends AbstractIslandStructure {
  public EnderSlimeIslandStructure() {
    super(rand -> IslandVariants.ENDER);
  }
}
