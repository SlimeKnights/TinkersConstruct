package slimeknights.tconstruct.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CongealedSlimeBlock extends Block {

  private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 15, 15);
  public CongealedSlimeBlock(Properties properties) {
    super(properties);
  }

  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
    return false;
  }

  @Nullable
  @Override
  public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
    return BlockPathTypes.STICKY_HONEY;
  }

  @Override
  public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entity) {
    if (entity.isSuppressingBounce() || !(entity instanceof LivingEntity) && !(entity instanceof ItemEntity)) {
      super.updateEntityAfterFallOn(worldIn, entity);
      // this is mostly needed to prevent XP orbs from bouncing. which completely breaks the game.
      return;
    }

    Vec3 vec3d = entity.getDeltaMovement();

    if (vec3d.y < 0) {
      double speed = entity instanceof LivingEntity ? 1.0D : 0.8D;
      entity.setDeltaMovement(vec3d.x, -vec3d.y * speed, vec3d.z);
      entity.fallDistance = 0;
      if (entity instanceof ItemEntity) {
        entity.setOnGround(false);
      }
    } else {
      super.updateEntityAfterFallOn(worldIn, entity);
    }
  }

  @Override
  public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
    // no fall damage on congealed slime
    entityIn.causeFallDamage(fallDistance, 0.0F, worldIn.damageSources().fall());
  }

  @Override
  public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
    if (!worldIn.isClientSide() && !entityIn.isSuppressingBounce()) {
      Vec3 entityPosition = entityIn.position();
      Vec3 direction = entityPosition.subtract(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
      // only bounce if within the block height, prevents bouncing on top or from two blocks vertically
      if (direction.y() < 0.9365 && direction.y() >= -0.0625) {
        // bounce the current speed, slightly smaller to prevent infinite bounce
        double velocity = entityPosition.subtract(entityIn.xo, entityIn.yo, entityIn.zo).length() * 0.95;
        // determine whether we bounce in the X or the Z direction, we want whichever is bigger
        Vec3 motion = entityIn.getDeltaMovement();
        double absX = Math.abs(direction.x());
        double absZ = Math.abs(direction.z());
        if (absX > absZ) {
          // but don't bounce past the halfway point in the block, to avoid bouncing twice
          if (absZ < 0.495) {
            entityIn.setDeltaMovement(new Vec3(velocity * Math.signum(direction.x()), motion.y(), motion.z()));
            entityIn.hurtMarked = true;
            if (velocity > 0.1) {
              worldIn.playSound(null, pos, getSoundType(state, worldIn, pos, entityIn).getStepSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
          }
        } else {
          if (absX < 0.495) {
            entityIn.setDeltaMovement(new Vec3(motion.x(), motion.y(), velocity * Math.signum(direction.z())));
            entityIn.hurtMarked = true;
            if (velocity > 0.1) {
              worldIn.playSound(null, pos, getSoundType(state, worldIn, pos, entityIn).getStepSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
          }
        }
      }
    }
  }
}
