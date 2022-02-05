package slimeknights.tconstruct.world.entity;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.Tag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;

import java.util.Random;

/** Placement predicate using a slime type */
@RequiredArgsConstructor
public class SlimePlacementPredicate<T extends Slime> implements SpawnPredicate<T> {
  private final Tag<Block> tag;

  @Override
  public boolean test(EntityType<T> type, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
    if (world.getDifficulty() == Difficulty.PEACEFUL) {
      return false;
    }
    if (reason == MobSpawnType.SPAWNER) {
      return true;
    }
    return world.getBlockState(pos.below()).is(tag);
  }
}
