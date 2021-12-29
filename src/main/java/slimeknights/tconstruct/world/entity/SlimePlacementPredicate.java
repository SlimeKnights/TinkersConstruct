package slimeknights.tconstruct.world.entity;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.tags.Tag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.ServerLevelAccessor;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.Random;

/** Placement predicate using a slime type */
@RequiredArgsConstructor
public class SlimePlacementPredicate<T extends Slime> implements SpawnPredicate<T> {
  private final SlimeType slimeType;

  @Override
  public boolean test(EntityType<T> type, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
    if (world.getDifficulty() == Difficulty.PEACEFUL) {
      return false;
    }
    if (reason == MobSpawnType.SPAWNER) {
      return true;
    }
    BlockPos down = pos.below();
    Tag<Fluid> fluid = TinkerFluids.slime.get(slimeType).getLocalTag();
    if (world.getFluidState(pos).is(fluid) && world.getFluidState(down).is(fluid)) {
      return true;
    }
    return world.getBlockState(down).is(slimeType.getGrassBlockTag());
  }
}
