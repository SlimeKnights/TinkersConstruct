package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

public class CastingBasinBlock extends AbstractCastingBlock implements BlockEntityProvider {

  private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(
    VoxelShapes.fullCube(),
    VoxelShapes.union(
      Block.createCuboidShape(0.0D, 0.0D, 5.0D, 16.0D, 2.0D, 11.0D),
      Block.createCuboidShape(5.0D, 0.0D, 0.0D, 11.0D, 2.0D, 16.0D),
      Block.createCuboidShape(2.0D, 0.0D, 3.0D, 14.0D, 3.0D, 14.0D),
      Block.createCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 13.0D, 16.0D),
      Block.createCuboidShape(0.0D, 5.0D, 7.0D, 16.0D, 13.0D, 9.0D),
      Block.createCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D)),
    BooleanBiFunction.ONLY_FIRST);

  public CastingBasinBlock(Settings builder) {
    super(builder);
  }

  @Deprecated
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @NotNull
  @Override
  public BlockEntity createBlockEntity(BlockView iBlockReader) {
    return new CastingTileEntity.Basin();
  }
}
