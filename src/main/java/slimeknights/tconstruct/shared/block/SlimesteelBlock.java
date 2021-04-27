package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SlimesteelBlock extends Block {

  public SlimesteelBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    if (entityIn.isSuppressingBounce()) {
      super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    } else {
      entityIn.onLivingFall(fallDistance, 0.0F);
    }

  }

  @Override
  public void onLanded(IBlockReader worldIn, Entity entity) {
    if (entity.isSuppressingBounce()) {
      super.onLanded(worldIn, entity);
    } else {
      Vector3d vector3d = entity.getMotion();
      if (vector3d.y < 0) {
        double d0 = entity instanceof LivingEntity ? 0.75 : 0.6;
        entity.setMotion(vector3d.x, -vector3d.y * d0, vector3d.z);
      }
    }
  }
}
