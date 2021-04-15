package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;

public class BlueSlimeEntity extends SlimeEntity {

  public BlueSlimeEntity(EntityType<? extends BlueSlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  public static boolean canSpawnHere(EntityType<BlueSlimeEntity> entityType, IWorld worldIn, SpawnReason spawnReason, BlockPos pos, Random random) {
    FluidState fluidState = worldIn.getFluidState(pos);
    BlockPos down = pos.down();

    if (fluidState.isTagged(TinkerTags.Fluids.SLIME) && worldIn.getFluidState(down).isTagged(TinkerTags.Fluids.SLIME)) {
      return true;
    }

    return worldIn.getBlockState(pos.down()).getBlock() instanceof SlimeGrassBlock;
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return (float)Math.sqrt(this.getSlimeSize()) * this.getJumpFactor() / 2;
  }

  @Override
  protected IParticleData getSquishParticle() {
    return TinkerWorld.slimeParticle.get();
  }
}
