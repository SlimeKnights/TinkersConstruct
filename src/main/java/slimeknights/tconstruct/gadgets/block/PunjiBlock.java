package slimeknights.tconstruct.gadgets.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

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
  public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
    return PathNodeType.DAMAGE_OTHER;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
    if (state.getValue(WATERLOGGED)) {
      world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
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
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, NORTH, EAST, NORTHEAST, NORTHWEST, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getClickedFace().getOpposite();
    IWorldReader world = context.getLevel();
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
  private boolean isConnected(IWorldReader world, Direction facing, BlockPos target) {
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
  public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    BlockPos target = pos.relative(direction);
    return world.getBlockState(target).isFaceSturdy(world, target, direction.getOpposite());
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof LivingEntity) {
      float damage = 3f;
      if (entityIn.fallDistance > 0) {
        damage += entityIn.fallDistance * 1.5f + 2f;
      }
      entityIn.hurt(DamageSource.CACTUS, damage);
      ((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 1));
    }
  }

  /* Bounds */
  private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

  static {
    ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
    builder.put(Direction.DOWN, VoxelShapes.box(0.1875, 0, 0.1875, 0.8125, 0.375, 0.8125));
    builder.put(Direction.UP, VoxelShapes.box(0.1875, 0.625, 0.1875, 0.8125, 1, 0.8125));
    builder.put(Direction.NORTH, VoxelShapes.box(0.1875, 0.1875, 0, 0.8125, 0.8125, 0.375));
    builder.put(Direction.SOUTH, VoxelShapes.box(0.1875, 0.1875, 0.625, 0.8125, 0.8125, 1));
    builder.put(Direction.EAST, VoxelShapes.box(0.625, 0.1875, 0.1875, 1, 0.8125, 0.8125));
    builder.put(Direction.WEST, VoxelShapes.box(0, 0.1875, 0.1875, 0.375, 0.8125, 0.8125));

    BOUNDS = builder.build();
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return BOUNDS.get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }
}
