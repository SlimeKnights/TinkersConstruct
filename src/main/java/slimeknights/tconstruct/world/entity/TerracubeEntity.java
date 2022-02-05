package slimeknights.tconstruct.world.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;

/**
 * Clay based slime cube
 */
public class TerracubeEntity extends Slime {
  public TerracubeEntity(EntityType<? extends TerracubeEntity> type, Level worldIn) {
    super(type, worldIn);
  }

  /**
   * Checks if a slime can spawn at the given location
   */
  public static boolean canSpawnHere(EntityType<? extends Slime> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
    if (world.getDifficulty() == Difficulty.PEACEFUL) {
      return false;
    }
    if (reason == MobSpawnType.SPAWNER) {
      return true;
    }
    BlockPos down = pos.below();
    if (world.getFluidState(pos).is(FluidTags.WATER) && world.getFluidState(down).is(FluidTags.WATER)) {
      return true;
    }
    return world.getBlockState(down).isValidSpawn(world, down, entityType) && Monster.isDarkEnoughToSpawn(world, pos, random);
  }

  @Override
  protected float getJumpPower() {
    return 0.2f * this.getBlockJumpFactor();
  }

  @Override
  protected float getAttackDamage() {
    return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 2;
  }

  @Override
  protected ParticleOptions getParticleType() {
    return TinkerWorld.terracubeParticle.get();
  }

  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return 0;
  }
}
