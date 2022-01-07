package slimeknights.tconstruct.world.worldgen.islands;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Collections;
import java.util.List;

public class EnderSlimeIslandStructure extends AbstractIslandStructure {
  private static final List<SpawnerData> MONSTERS = ImmutableList.of(new MobSpawnSettings.SpawnerData(TinkerWorld.enderSlimeEntity.get(), 30, 4, 4));

  public EnderSlimeIslandStructure() {
    super(rand -> IslandVariants.ENDER);
  }

  @Override
  public List<SpawnerData> getDefaultSpawnList(MobCategory category) {
    return category == MobCategory.MONSTER ? MONSTERS : Collections.emptyList();
  }
}
