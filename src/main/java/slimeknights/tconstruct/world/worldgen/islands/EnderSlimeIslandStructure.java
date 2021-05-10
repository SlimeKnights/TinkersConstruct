package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.List;
import java.util.Random;

public class EnderSlimeIslandStructure extends AbstractIslandStructure {
  // TODO: end slimes
  private static final List<Spawners> STRUCTURE_MONSTERS = ImmutableList.of(
    new MobSpawnInfo.Spawners(EntityType.SHULKER, 30, 4, 4));

  @Override
  public List<Spawners> getDefaultSpawnList() {
    return STRUCTURE_MONSTERS;
  }

  @Override
  public IIslandVariant getVariant(Random random) {
    return IslandVariants.ENDER;
  }
}
