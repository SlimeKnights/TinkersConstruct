package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.HeaterTileEntity;

import java.util.Random;

/**
 * Class for solid fuel heater for the melter
 */
public class HeaterBlock extends ControllerBlock {
  public HeaterBlock(Settings builder) {
    super(builder);
  }

//  @Override
  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
    return new HeaterTileEntity();
  }

  @Override
  protected boolean canOpenGui(BlockState state) {
    return true;
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    BlockState state = super.getPlacementState(context);
    if (state != null) {
      return state.with(ACTIVE, context.getWorld().getBlockState(context.getBlockPos().up()).isOf(TinkerSmeltery.searedMelter.get()));
    }
    return null;
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.UP) {
      return state.with(ACTIVE, facingState.isOf(TinkerSmeltery.searedMelter.get()));
    }
    return state;
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
    if (state.get(ACTIVE)) {
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 14F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
      spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset);
    }
  }
}
