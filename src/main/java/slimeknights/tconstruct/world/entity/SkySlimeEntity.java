package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import java.util.Random;

public class SkySlimeEntity extends SlimeEntity {

  public SkySlimeEntity(EntityType<? extends SkySlimeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  /**
   * Checks if a slime can spawn at the given location
   */
  public static boolean canSpawnHere(EntityType<? extends SlimeEntity> entityType, IWorld worldIn, SpawnReason spawnReason, BlockPos pos, Random random) {
    BlockPos down = pos.down();
    if (worldIn.getFluidState(pos).isTagged(TinkerTags.Fluids.SLIME) && worldIn.getFluidState(down).isTagged(TinkerTags.Fluids.SLIME)) {
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
    return TinkerWorld.skySlimeParticle.get();
  }
}
