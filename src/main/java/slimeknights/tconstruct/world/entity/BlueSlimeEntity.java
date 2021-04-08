package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;

public class BlueSlimeEntity extends SlimeEntity {

  public BlueSlimeEntity(EntityType<? extends BlueSlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  public static boolean canSpawnHere(EntityType<BlueSlimeEntity> entityType, WorldAccess worldIn, SpawnReason spawnReason, BlockPos pos, Random random) {
    FluidState fluidState = worldIn.getFluidState(pos);
    BlockPos down = pos.down();

    if (fluidState.isIn(TinkerTags.Fluids.SLIME) && worldIn.getFluidState(down).isIn(TinkerTags.Fluids.SLIME)) {
      return true;
    }

    return worldIn.getBlockState(pos.down()).getBlock() instanceof SlimeGrassBlock;
  }

  @Override
  protected ParticleEffect getParticles() {
    return TinkerWorld.slimeParticle.get();
  }
}
