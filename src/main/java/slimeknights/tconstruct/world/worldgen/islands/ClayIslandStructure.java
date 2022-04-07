package slimeknights.tconstruct.world.worldgen.islands;

import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

/** Rare island that spawns a random tree and a lake of clay */
public class ClayIslandStructure extends AbstractIslandStructure {
  public ClayIslandStructure() {
    super(random -> IslandVariants.SKY_CLAY);
  }
}
