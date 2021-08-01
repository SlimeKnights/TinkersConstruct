package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;

/**
 * Clay based slime cube
 */
public class TerracubeEntity extends SlimeEntity {
  public TerracubeEntity(EntityType<? extends TerracubeEntity> type, World worldIn) {
    super(type, worldIn);
  }

  /**
   * Checks if a slime can spawn at the given location
   */
  public static boolean canSpawnHere(EntityType<? extends SlimeEntity> entityType, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
    BlockPos down = pos.down();
    if (worldIn.getFluidState(pos).isTagged(FluidTags.WATER) && worldIn.getFluidState(down).isTagged(FluidTags.WATER)) {
      return true;
    }
    return reason == SpawnReason.SPAWNER || worldIn.getBlockState(down).canEntitySpawn(worldIn, down, entityType);
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.2f * this.getJumpFactor();
  }

  @Override
  protected float func_225512_er_() {
    return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 2;
  }

  @Override
  protected IParticleData getSquishParticle() {
    return TinkerWorld.terracubeParticle.get();
  }

  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return 0;
  }
}
