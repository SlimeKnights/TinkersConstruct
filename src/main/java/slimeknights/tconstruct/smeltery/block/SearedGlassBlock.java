package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.block.ConnectedTextureBlock;
import slimeknights.tconstruct.blocks.BlockProperties;

import javax.annotation.Nonnull;

public class SearedGlassBlock extends SearedBlock {

  public SearedGlassBlock(Properties properties) {
    super(properties);

    this.setDefaultState(this.stateContainer.getBaseState()
      .with(ConnectedTextureBlock.CONNECTED_DOWN, Boolean.FALSE)
      .with(ConnectedTextureBlock.CONNECTED_EAST, Boolean.FALSE)
      .with(ConnectedTextureBlock.CONNECTED_NORTH, Boolean.FALSE)
      .with(ConnectedTextureBlock.CONNECTED_SOUTH, Boolean.FALSE)
      .with(ConnectedTextureBlock.CONNECTED_UP, Boolean.FALSE)
      .with(ConnectedTextureBlock.CONNECTED_WEST, Boolean.FALSE));
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    return stateIn.with(ConnectedTextureBlock.CONNECTED_DOWN, this.isSideConnectable(stateIn, worldIn, currentPos, Direction.DOWN))
      .with(ConnectedTextureBlock.CONNECTED_EAST, this.isSideConnectable(stateIn, worldIn, currentPos, Direction.EAST))
      .with(ConnectedTextureBlock.CONNECTED_NORTH, this.isSideConnectable(stateIn, worldIn, currentPos, Direction.NORTH))
      .with(ConnectedTextureBlock.CONNECTED_SOUTH, this.isSideConnectable(stateIn, worldIn, currentPos, Direction.SOUTH))
      .with(ConnectedTextureBlock.CONNECTED_UP, this.isSideConnectable(stateIn, worldIn, currentPos, Direction.UP))
      .with(ConnectedTextureBlock.CONNECTED_WEST, this.isSideConnectable(stateIn, worldIn, currentPos, Direction.WEST));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(ConnectedTextureBlock.CONNECTED_DOWN, ConnectedTextureBlock.CONNECTED_UP, ConnectedTextureBlock.CONNECTED_NORTH, ConnectedTextureBlock.CONNECTED_SOUTH, ConnectedTextureBlock.CONNECTED_WEST, ConnectedTextureBlock.CONNECTED_EAST);
  }

  private boolean isSideConnectable(BlockState state, IWorld world, BlockPos pos, Direction side) {
    final BlockState connected = world.getBlockState(pos.offset(side));
    return this.canConnect(state, connected);
  }

  protected boolean canConnect(@Nonnull BlockState original, @Nonnull BlockState connected) {
    return original.getBlock() == connected.getBlock();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Override
  public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }
}
