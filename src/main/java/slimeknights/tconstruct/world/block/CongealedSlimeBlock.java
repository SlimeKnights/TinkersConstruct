package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class CongealedSlimeBlock extends Block {

  private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 15, 15);
  public CongealedSlimeBlock(Properties properties) {
    super(properties);
  }

  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
    return false;
  }

  @Nullable
  @Override
  public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
    return PathNodeType.STICKY_HONEY;
  }

  @Override
  public void updateEntityAfterFallOn(IBlockReader worldIn, Entity entity) {
    if (entity.isSuppressingBounce() || !(entity instanceof LivingEntity) && !(entity instanceof ItemEntity)) {
      super.updateEntityAfterFallOn(worldIn, entity);
      // this is mostly needed to prevent XP orbs from bouncing. which completely breaks the game.
      return;
    }

    Vector3d vec3d = entity.getDeltaMovement();

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
  public void fallOn(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    // no fall damage on congealed slime
    entityIn.causeFallDamage(fallDistance, 0.0F);
  }

  @Override
  public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (!worldIn.isClientSide() && !entityIn.isSuppressingBounce()) {
      Vector3d entityPosition = entityIn.position();
      Vector3d direction = entityPosition.subtract(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
      // only bounce if within the block height, prevents bouncing on top or from two blocks vertically
      if (direction.y() < 0.9365 && direction.y() >= -0.0625) {
        // bounce the current speed, slightly smaller to prevent infinite bounce
        double velocity = entityPosition.subtract(entityIn.xo, entityIn.yo, entityIn.zo).length() * 0.95;
        // determine whether we bounce in the X or the Z direction, we want whichever is bigger
        Vector3d motion = entityIn.getDeltaMovement();
        double absX = Math.abs(direction.x());
        double absZ = Math.abs(direction.z());
        if (absX > absZ) {
          // but don't bounce past the halfway point in the block, to avoid bouncing twice
          if (absZ < 0.495) {
            entityIn.setDeltaMovement(new Vector3d(velocity * Math.signum(direction.x()), motion.y(), motion.z()));
            entityIn.hurtMarked = true;
            if (velocity > 0.1) {
              worldIn.playSound(null, pos, getSoundType(state, worldIn, pos, entityIn).getStepSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
          }
        } else {
          if (absX < 0.495) {
            entityIn.setDeltaMovement(new Vector3d(motion.x(), motion.y(), velocity * Math.signum(direction.z())));
            entityIn.hurtMarked = true;
            if (velocity > 0.1) {
              worldIn.playSound(null, pos, getSoundType(state, worldIn, pos, entityIn).getStepSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
          }
        }
      }
    }
  }
}
