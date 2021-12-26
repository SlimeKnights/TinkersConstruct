package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class SlimesteelBlock extends Block {

  public SlimesteelBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void fallOn(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    if (entityIn.isSuppressingBounce()) {
      super.fallOn(worldIn, pos, entityIn, fallDistance);
    } else {
      entityIn.causeFallDamage(fallDistance, 0.0F);
    }

  }

  @Override
  public void updateEntityAfterFallOn(IBlockReader worldIn, Entity entity) {
    if (entity.isSuppressingBounce()) {
      super.updateEntityAfterFallOn(worldIn, entity);
    } else {
      Vector3d vector3d = entity.getDeltaMovement();
      if (vector3d.y < 0) {
        double d0 = entity instanceof LivingEntity ? 0.75 : 0.6;
        entity.setDeltaMovement(vector3d.x, -vector3d.y * d0, vector3d.z);
      }
    }
  }

  @Override
  public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
    return false;
  }
}
