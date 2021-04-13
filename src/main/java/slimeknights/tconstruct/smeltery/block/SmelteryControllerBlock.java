package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class SmelteryControllerBlock extends ControllerBlock {
  public SmelteryControllerBlock(Settings properties) {
    super(properties);
  }

//  @Override
  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
    return new SmelteryTileEntity();
  }

  @Override
  public void onPlaced(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    // check structure
    TileEntityHelper.getTile(SmelteryTileEntity.class, worldIn, pos).ifPresent(SmelteryTileEntity::updateStructure);
  }

  @Override
  @Deprecated
  public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.isOf(this)) {
      TileEntityHelper.getTile(SmelteryTileEntity.class, worldIn, pos).ifPresent(SmelteryTileEntity::invalidateStructure);
    }
    super.onStateReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
    if (state.get(ACTIVE)) {
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 6F + 2F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
      spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset);
    }
  }


  /* No rotation if active  */

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    if (state.get(ACTIVE)) {
      return state;
    }
    return super.rotate(state, rotation);
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirror) {
    if (state.get(ACTIVE)) {
      return state;
    }
    return super.mirror(state, mirror);
  }
}
