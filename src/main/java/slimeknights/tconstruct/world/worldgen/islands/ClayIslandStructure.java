package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Rare island that spawns a random tree and a lake of clay */
public class ClayIslandStructure extends AbstractIslandStructure {
  private static final List<SpawnerData> MONSTERS = ImmutableList.of(new MobSpawnSettings.SpawnerData(TinkerWorld.terracubeEntity.get(), 30, 4, 4));

  @Override
  public List<SpawnerData> getDefaultSpawnList(MobCategory category) {
    return category == MobCategory.MONSTER ? MONSTERS : Collections.emptyList();
  }

  @Override
  protected IIslandVariant getVariant(Random random) {
    return IslandVariants.SKY_CLAY;
  }
}
