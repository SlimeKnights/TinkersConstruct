package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CongealedSlimeBlock extends Block {

  private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 10, 16);
  public CongealedSlimeBlock(Settings properties) {
    super(properties);
  }

  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @Override
  public void onEntityLand(BlockView worldIn, Entity entity) {
    if (!(entity instanceof LivingEntity) && !(entity instanceof ItemEntity)) {
      super.onEntityLand(worldIn, entity);
      // this is mostly needed to prevent XP orbs from bouncing. which completely breaks the game.
      return;
    }

    Vec3d vec3d = entity.getVelocity();

    if (vec3d.y < 0) {
      double speed = entity instanceof LivingEntity ? 1.0D : 0.8D;
      entity.setVelocity(vec3d.x, -vec3d.y * speed, vec3d.z);
      entity.fallDistance = 0;
      if (entity instanceof ItemEntity) {
        entity.setOnGround(false);
      }
    } else {
      super.onEntityLand(worldIn, entity);
    }
  }

  @Override
  public void onLandedUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    // no fall damage on congealed slime
    entityIn.handleFallDamage(fallDistance, 0.0F);
  }
}
