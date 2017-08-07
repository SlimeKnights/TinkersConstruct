package slimeknights.tconstruct.gadgets.block;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockBouncePad extends BlockSlimeChannel {
  public BlockBouncePad() {
    super();
  }
  
  @Override
  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    if (!state.getValue(POWERED)) {
      Vec3d vec = Vec3d.ZERO;

      double speed = 0.25;

      final EnumFacing side = state.getValue(SIDE);
      
      final BlockSlimeChannel.ChannelDirection dir = state.getValue(DIRECTION);
      final ArrayList<EnumFacing> flows = dir.getFlowDiagonals(side);
      
      for (EnumFacing flow : flows) {
        vec = vec.add(new Vec3d(flow.getDirectionVec()).scale(speed)); // add flow directions
      }
      
      vec = vec.add(new Vec3d(side.getOpposite().getDirectionVec()).scale(speed*2)); // add the bounce

      if (entityIn instanceof EntityItem) {
        entityIn.posY += 1;
      }
      entityIn.fallDistance = 0.0F;
      entityIn.addVelocity(vec.x, vec.y, vec.z);
      worldIn.playSound(null, pos, this.blockSoundType.getStepSound(), entityIn.getSoundCategory(), this.blockSoundType.getVolume() / 2, this.blockSoundType.getPitch() * 0.65f);
    }
  }
}
