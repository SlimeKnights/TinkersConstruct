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
import slimeknights.tconstruct.smeltery.tileentity.CastingBasinTileEntity;

import javax.annotation.Nonnull;

public class CastingBasinBlock extends CastingBlock {


  private static final VoxelShape INSIDE = Block.makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
  protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(),
    VoxelShapes.or(
      Block.makeCuboidShape(0.0D, 0.0D, 5.0D, 16.0D, 2.0D, 11.0D),
      Block.makeCuboidShape(5.0D, 0.0D, 0.0D, 11.0D, 2.0D, 16.0D),
      Block.makeCuboidShape(2.0D, 0.0D, 3.0D, 14.0D, 3.0D, 14.0D),
      Block.makeCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 13.0D, 16.0D),
      Block.makeCuboidShape(0.0D, 5.0D, 7.0D, 16.0D, 13.0D, 9.0D),
      INSIDE), IBooleanFunction.ONLY_FIRST);

  public CastingBasinBlock(Properties builder) {
    super(builder);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new CastingBasinTileEntity();
  }
}
