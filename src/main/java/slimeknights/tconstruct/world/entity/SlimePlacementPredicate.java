package slimeknights.tconstruct.world.entity;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.EntitySpawnPlacementRegistry.IPlacementPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.Random;

/** Placement predicate using a slime type */
@RequiredArgsConstructor
public class SlimePlacementPredicate<T extends SlimeEntity> implements IPlacementPredicate<T> {
  private final SlimeType slimeType;

  @Override
  public boolean test(EntityType<T> type, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
    if (world.getDifficulty() == Difficulty.PEACEFUL) {
      return false;
    }
    if (reason == SpawnReason.SPAWNER) {
      return true;
    }
    BlockPos down = pos.down();
    ITag<Fluid> fluid = TinkerFluids.slime.get(slimeType).getLocalTag();
    if (world.getFluidState(pos).isTagged(fluid) && world.getFluidState(down).isTagged(fluid)) {
      return true;
    }
    return world.getBlockState(down).isIn(slimeType.getGrassBlockTag());
  }
}
