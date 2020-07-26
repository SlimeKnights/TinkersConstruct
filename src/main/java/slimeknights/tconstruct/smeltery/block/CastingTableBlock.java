package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.smeltery.tileentity.AbstractCastingTileEntity;

import javax.annotation.Nonnull;

public class CastingTableBlock extends AbstractCastingBlock {

  private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(
    VoxelShapes.fullCube(),
    VoxelShapes.or(
      Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 10.0D, 16.0D),
      Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 10.0D, 12.0D),
      Block.makeCuboidShape(1.0D, 15.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    ), IBooleanFunction.ONLY_FIRST);

  public CastingTableBlock(Properties builder) {
    super(builder);
  }

  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new AbstractCastingTileEntity.Table();
  }
}
