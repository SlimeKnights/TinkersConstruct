package slimeknights.tconstruct.world.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class PiglinWallHeadBlock extends WallSkullBlock {
  private static final Map<Direction,VoxelShape> PIGLIN_SHAPES = Maps.newEnumMap(ImmutableMap.of(
    Direction.NORTH, Shapes.or(Block.box(3, 4, 8, 13, 12, 16), Block.box(5, 4, 7, 11, 6,  8), Block.box(6, 6, 7, 10, 8,  8)),
    Direction.SOUTH, Shapes.or(Block.box(3, 4, 0, 13, 12,  8), Block.box(5, 4, 8, 11, 6,  9), Block.box(6, 6, 8, 10, 8,  9)),
    Direction.EAST,  Shapes.or(Block.box(0, 4, 3,  8, 12, 13), Block.box(8, 4, 5,  9, 6, 11), Block.box(8, 6, 6,  9, 8, 10)),
    Direction.WEST,  Shapes.or(Block.box(8, 4, 3, 16, 12, 13), Block.box(7, 4, 5,  8, 6, 11), Block.box(7, 6, 6,  8, 8, 10))));

  public PiglinWallHeadBlock(Type type, Properties properties) {
    super(type, properties);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return PIGLIN_SHAPES.get(state.getValue(FACING));
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    if (pLevel.isClientSide) {
      return createTickerHelper(pBlockEntityType, BlockEntityType.SKULL, SkullBlockEntity::animation);
    }
    return null;
  }
}
