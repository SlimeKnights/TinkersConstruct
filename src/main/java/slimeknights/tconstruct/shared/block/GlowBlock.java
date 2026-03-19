package slimeknights.tconstruct.shared.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class GlowBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public GlowBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN));
    this.drops = BuiltInLootTables.EMPTY;
  }

  private static final Map<Direction, VoxelShape> BOUNDS = new EnumMap<>(Direction.class);
  static {
    BOUNDS.put(Direction.UP,    Block.box( 0.0D, 15.0D,  0.0D, 16.0D, 16.0D, 16.0D));
    BOUNDS.put(Direction.DOWN,  Block.box( 0.0D,  0.0D,  0.0D, 16.0D,  1.0D, 16.0D));
    BOUNDS.put(Direction.NORTH, Block.box( 0.0D,  0.0D,  0.0D, 16.0D, 16.0D,  1.0D));
    BOUNDS.put(Direction.SOUTH, Block.box( 0.0D,  0.0D, 15.0D, 16.0D, 16.0D, 16.0D));
    BOUNDS.put(Direction.EAST,  Block.box(15.0D,  0.0D,  0.0D, 16.0D, 16.0D, 16.0D));
    BOUNDS.put(Direction.WEST,  Block.box( 0.0D,  0.0D,  0.0D,  1.0D, 16.0D, 16.0D));
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return Objects.requireNonNull(BOUNDS.get(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return Shapes.empty();
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level level = context.getLevel();
    // direction of the glow to place
    Direction direction = context.getClickedFace().getOpposite();
    BlockPos pos = context.getClickedPos();
    BlockState state = this.defaultBlockState().setValue(FACING, direction);
    // if the direction is valid, place it there
    if (this.canSurvive(state, level, pos)) {
      return state;
    }
    // try all other directions
    for (Direction other : Direction.values()) {
      if (other == direction) continue;

      state = this.defaultBlockState().setValue(FACING, other);
      if (canSurvive(state, level, pos)) {
        return state;
      }
    }
    // can't place
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
    if (!this.canSurvive(state, worldIn, pos)) {
      worldIn.removeBlock(pos, false);
    }

    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    Direction facing = state.getValue(FACING);
    BlockPos placedOn = pos.relative(facing);

    boolean isSolidSide = Block.isFaceFull(level.getBlockState(placedOn).getOcclusionShape(level, pos), facing.getOpposite());
    boolean isLiquid = level.getBlockState(pos).getBlock() instanceof LiquidBlock;

    return !isLiquid && isSolidSide;
  }

  /**
   * Adds a glow block at the given location
   * @param world      World instance
   * @param pos        Position
   * @param direction  Preferred direction, may reorient
   * @return  True if a block was placed
   */
  public boolean addGlow(Level world, BlockPos pos, Direction direction) {
    // only place the block if the current block at the location is replaceable (eg, air, tall grass, etc.)
    BlockState state = world.getBlockState(pos);
    BlockState newState = this.defaultBlockState().setValue(FACING, direction);
    if (state.getBlock() != this && state.canBeReplaced()) {
      // if the location is valid, place the block directly
      if (this.canSurvive(newState, world, pos)) {
        if (!world.isClientSide) {
          world.setBlockAndUpdate(pos, newState);
        }
        return true;
      } else {
        for (Direction direction1 : Direction.values()) {
          newState = this.defaultBlockState().setValue(FACING, direction1);
          if (this.canSurvive(newState, world, pos)) {
            if (!world.isClientSide) {
              world.setBlockAndUpdate(pos, newState);
            }
            return true;
          }
        }
      }
    }

    return false;
  }

}
