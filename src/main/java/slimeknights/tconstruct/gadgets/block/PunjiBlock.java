package slimeknights.tconstruct.gadgets.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class PunjiBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  private static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  private static final BooleanProperty EAST = BlockStateProperties.EAST;
  private static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
  private static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
  private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public PunjiBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any()
                                            .setValue(FACING, Direction.DOWN)
                                            .setValue(NORTH, false)
                                            .setValue(EAST, false)
                                            .setValue(NORTHEAST, false)
                                            .setValue(NORTHWEST, false)
                                            .setValue(WATERLOGGED, false));
  }

  @Nullable
  @Override
  public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return BlockPathTypes.DAMAGE_OTHER;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
    if (state.getValue(WATERLOGGED)) {
      world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    }

    // break if now invalid
    Direction direction = state.getValue(FACING);
    if (facing == direction && !state.canSurvive(world, pos)) {
      return Blocks.AIR.defaultBlockState();
    }

    // apply north and east if relevant
    Direction north = getLocalNorth(direction);
    Direction east = getLocalEast(direction);
    if (facing == north) {
      state = state.setValue(NORTH, isConnected(world, direction, facingPos));
    } else if (facing == east) {
      state = state.setValue(EAST, isConnected(world, direction, facingPos));
    }

    // always update northeast and northwest, never gets direct updates
    BlockPos northPos = pos.relative(north);
    return state.setValue(NORTHEAST, isConnected(world, direction, northPos.relative(east)))
                .setValue(NORTHWEST, isConnected(world, direction, northPos.relative(east.getOpposite())));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, NORTH, EAST, NORTHEAST, NORTHWEST, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction direction = context.getClickedFace().getOpposite();
    LevelReader world = context.getLevel();
    BlockPos pos = context.getClickedPos();

    BlockState state = this.defaultBlockState().setValue(FACING, direction);
    if (!state.canSurvive(world, pos)) {
      return null;
    }

    // apply connections
    Direction north = getLocalNorth(direction);
    Direction east = getLocalEast(direction);
    BlockPos northPos = pos.relative(north);
    return state.setValue(NORTH,     isConnected(world, direction, northPos))
                .setValue(EAST,      isConnected(world, direction, pos.relative(east)))
                .setValue(NORTHEAST, isConnected(world, direction, northPos.relative(east)))
                .setValue(NORTHWEST, isConnected(world, direction, northPos.relative(east.getOpposite())))
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
  }

  /**
   * Checks if this should connect to the block at the position
   * @param world   World instance
   * @param facing  Punji stick facing
   * @param target  Position to check
   * @return  True if connected
   */
  private boolean isConnected(LevelReader world, Direction facing, BlockPos target) {
    BlockState state = world.getBlockState(target);
    return state.getBlock() == this && state.getValue(FACING) == facing;
  }

  /**
   * Gets the facing relative north
   * @param facing  Punji stick facing
   * @return  North for the given facing
   */
  private static Direction getLocalNorth(Direction facing) {
    switch (facing) {
      case DOWN:
        return Direction.NORTH;
      case UP:
        return Direction.SOUTH;
    }
    return Direction.UP;
  }

  /**
   * Gets the facing relative east
   * @param facing  Punji stick facing
   * @return  East for the given facing
   */
  private static Direction getLocalEast(Direction facing) {
    if (facing.getAxis() == Axis.Y) {
      return Direction.EAST;
    }
    return facing.getClockWise();
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    BlockPos target = pos.relative(direction);
    return world.getBlockState(target).isFaceSturdy(world, target, direction.getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof LivingEntity) {
      float damage = 3f;
      if (entityIn.fallDistance > 0) {
        damage += entityIn.fallDistance * 1.5f + 2f;
      }
      entityIn.hurt(DamageSource.CACTUS, damage);
      ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
    }
  }

  /* Bounds */
  private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

  static {
    ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
    builder.put(Direction.DOWN, Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.375, 0.8125));
    builder.put(Direction.UP, Shapes.box(0.1875, 0.625, 0.1875, 0.8125, 1, 0.8125));
    builder.put(Direction.NORTH, Shapes.box(0.1875, 0.1875, 0, 0.8125, 0.8125, 0.375));
    builder.put(Direction.SOUTH, Shapes.box(0.1875, 0.1875, 0.625, 0.8125, 0.8125, 1));
    builder.put(Direction.EAST, Shapes.box(0.625, 0.1875, 0.1875, 1, 0.8125, 0.8125));
    builder.put(Direction.WEST, Shapes.box(0, 0.1875, 0.1875, 0.375, 0.8125, 0.8125));

    BOUNDS = builder.build();
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return BOUNDS.get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }
}
