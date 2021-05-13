package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CongealedSlimeBlock extends Block {

  private static final VoxelShape SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 15, 15);
  public CongealedSlimeBlock(Properties properties) {
    super(properties);
  }

  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public void onLanded(IBlockReader worldIn, Entity entity) {
    if (entity.isSuppressingBounce() || !(entity instanceof LivingEntity) && !(entity instanceof ItemEntity)) {
      super.onLanded(worldIn, entity);
      // this is mostly needed to prevent XP orbs from bouncing. which completely breaks the game.
      return;
    }

    Vector3d vec3d = entity.getMotion();

    if (vec3d.y < 0) {
      double speed = entity instanceof LivingEntity ? 1.0D : 0.8D;
      entity.setMotion(vec3d.x, -vec3d.y * speed, vec3d.z);
      entity.fallDistance = 0;
      if (entity instanceof ItemEntity) {
        entity.setOnGround(false);
      }
    } else {
      super.onLanded(worldIn, entity);
    }
  }

  @Override
  public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    // no fall damage on congealed slime
    entityIn.onLivingFall(fallDistance, 0.0F);
  }

  @Override
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (!worldIn.isRemote() && !entityIn.isSuppressingBounce()) {
      Vector3d entityPosition = entityIn.getPositionVec();
      Vector3d direction = entityPosition.subtract(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
      // only bounce if within the block height, prevents bouncing on top or from two blocks vertically
      if (direction.getY() < 0.9365 && direction.getY() >= -0.0625) {
        // bounce the current speed, slightly smaller to prevent infinite bounce
        double velocity = entityPosition.subtract(entityIn.prevPosX, entityIn.prevPosY, entityIn.prevPosZ).length() * 0.95;
        // determine whether we bounce in the X or the Z direction, we want whichever is bigger
        Vector3d motion = entityIn.getMotion();
        double absX = Math.abs(direction.getX());
        double absZ = Math.abs(direction.getZ());
        if (absX > absZ) {
          // but don't bounce past the halfway point in the block, to avoid bouncing twice
          if (absZ < 0.495) {
            entityIn.setMotion(new Vector3d(velocity * Math.signum(direction.getX()), motion.getY(), motion.getZ()));
            entityIn.velocityChanged = true;
            if (velocity > 0.1) {
              worldIn.playSound(null, pos, getSoundType(state, worldIn, pos, entityIn).getStepSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
          }
        } else {
          if (absX < 0.495) {
            entityIn.setMotion(new Vector3d(motion.getX(), motion.getY(), velocity * Math.signum(direction.getZ())));
            entityIn.velocityChanged = true;
            if (velocity > 0.1) {
              worldIn.playSound(null, pos, getSoundType(state, worldIn, pos, entityIn).getStepSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
          }
        }
      }
    }
  }
}
