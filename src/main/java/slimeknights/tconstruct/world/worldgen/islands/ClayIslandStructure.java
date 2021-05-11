package slimeknights.tconstruct.world.worldgen.islands;

import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Random;

/** Rare island that spawns a random tree and a lake of clay */
public class ClayIslandStructure extends AbstractIslandStructure {
  @Override
  protected IIslandVariant getVariant(Random random) {
    return IslandVariants.SKY_CLAY;
  }
}
