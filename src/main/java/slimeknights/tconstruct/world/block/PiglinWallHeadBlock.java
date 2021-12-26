package slimeknights.tconstruct.world.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock.ISkullType;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.Map;

import net.minecraft.block.AbstractBlock.Properties;

public class PiglinWallHeadBlock extends WallSkullBlock {
  private static final Map<Direction,VoxelShape> PIGLIN_SHAPES = Maps.newEnumMap(ImmutableMap.of(
    Direction.NORTH, VoxelShapes.or(Block.box(3, 4, 8, 13, 12, 16), Block.box(5, 4, 7, 11, 6,  8), Block.box(6, 6, 7, 10, 8,  8)),
    Direction.SOUTH, VoxelShapes.or(Block.box(3, 4, 0, 13, 12,  8), Block.box(5, 4, 8, 11, 6,  9), Block.box(6, 6, 8, 10, 8,  9)),
    Direction.EAST,  VoxelShapes.or(Block.box(0, 4, 3,  8, 12, 13), Block.box(8, 4, 5,  9, 6, 11), Block.box(8, 6, 6,  9, 8, 10)),
    Direction.WEST,  VoxelShapes.or(Block.box(8, 4, 3, 16, 12, 13), Block.box(7, 4, 5,  8, 6, 11), Block.box(7, 6, 6,  8, 8, 10))));

  public PiglinWallHeadBlock(ISkullType type, Properties properties) {
    super(type, properties);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return PIGLIN_SHAPES.get(state.getValue(FACING));
  }
}
