package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Collections;
import java.util.List;

/** Rare island that spawns a random tree and a lake of clay */
public class ClayIslandStructure extends AbstractIslandStructure {
  private static final List<SpawnerData> MONSTERS = ImmutableList.of(new MobSpawnSettings.SpawnerData(TinkerWorld.terracubeEntity.get(), 30, 4, 4));

  public ClayIslandStructure() {
    super(random -> IslandVariants.SKY_CLAY);
  }

  @Override
  public List<SpawnerData> getDefaultSpawnList(MobCategory category) {
    return category == MobCategory.MONSTER ? MONSTERS : Collections.emptyList();
  }
}
