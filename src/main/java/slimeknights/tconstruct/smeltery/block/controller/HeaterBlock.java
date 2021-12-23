package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.tileentity.HeaterTileEntity;

import java.util.Random;

/**
 * Class for solid fuel heater for the melter
 */
public class HeaterBlock extends ControllerBlock {
  public HeaterBlock(Properties builder) {
    super(builder);
  }

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new HeaterTileEntity();
  }

  @Override
  protected boolean canOpenGui(BlockState state) {
    return true;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state != null) {
      return state.with(IN_STRUCTURE, context.getWorld().getBlockState(context.getPos().up()).isIn(TinkerTags.Blocks.HEATER_CONTROLLERS));
    }
    return null;
  }

  @Override
  public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.UP) {
      return state.with(IN_STRUCTURE, facingState.isIn(TinkerTags.Blocks.HEATER_CONTROLLERS));
    }
    return state;
  }

  @Override
  public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
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
