package slimeknights.tconstruct.gadgets.block;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PunjiBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  public static final BooleanProperty EAST = BlockStateProperties.EAST;
  public static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
  public static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public PunjiBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN).with(NORTH, false).with(EAST, false).with(NORTHEAST, false).with(NORTHWEST, false).with(WATERLOGGED, false));
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    Direction direction = stateIn.get(FACING);

    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }

    if (!stateIn.isValidPosition(worldIn, currentPos)) {
      return Blocks.AIR.getDefaultState();
    }

    int off = -direction.ordinal() % 2;

    Direction face1 = Direction.values()[(direction.ordinal() + 2) % 6];
    Direction face2 = Direction.values()[(direction.ordinal() + 4 + off) % 6];

    // North/East Connector
    BlockState north = worldIn.getBlockState(currentPos.offset(face1));
    BlockState east = worldIn.getBlockState(currentPos.offset(face2));

    if (north.getBlock() == this && north.get(FACING) == direction) {
      stateIn = stateIn.with(NORTH, true);
    }
    else {
      stateIn = stateIn.with(NORTH, false);
    }

    if (east.getBlock() == this && east.get(FACING) == direction) {
      stateIn = stateIn.with(EAST, true);
    }
    else {
      stateIn = stateIn.with(EAST, false);
    }

    // Diagonal connections
    BlockState northeast = worldIn.getBlockState(currentPos.offset(face1).offset(face2));
    BlockState northwest = worldIn.getBlockState(currentPos.offset(face1).offset(face2.getOpposite()));

    if (northeast.getBlock() == this && northeast.get(FACING) == direction) {
      stateIn = stateIn.with(NORTHEAST, true);
    }
    else {
      stateIn = stateIn.with(NORTHEAST, false);
    }

    if (northwest.getBlock() == this && northwest.get(FACING) == direction) {
      stateIn = stateIn.with(NORTHWEST, true);
    }
    else {
      stateIn = stateIn.with(NORTHWEST, false);
    }

    return stateIn;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, NORTH, EAST, NORTHEAST, NORTHWEST, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getFace().getOpposite();
    IWorldReader iworldreader = context.getWorld();
    BlockPos blockpos = context.getPos();

    BlockState state = this.getDefaultState().with(FACING, direction);

    int off = -direction.ordinal() % 2;

    Direction face1 = Direction.values()[(direction.ordinal() + 2) % 6];
    Direction face2 = Direction.values()[(direction.ordinal() + 4 + off) % 6];

    // North/East Connector
    BlockState north = iworldreader.getBlockState(blockpos.offset(face1));
    BlockState east = iworldreader.getBlockState(blockpos.offset(face2));

    if (north.getBlock() == this && north.get(FACING) == direction) {
      state = state.with(NORTH, true);
    }

    if (east.getBlock() == this && east.get(FACING) == direction) {
      state = state.with(EAST, true);
    }

    // Diagonal connections
    BlockState northeast = iworldreader.getBlockState(blockpos.offset(face1).offset(face2));
    BlockState northwest = iworldreader.getBlockState(blockpos.offset(face1).offset(face2.getOpposite()));

    if (northeast.getBlock() == this && northeast.get(FACING) == direction) {
      state = state.with(NORTHEAST, true);
    }

    if (northwest.getBlock() == this && northwest.get(FACING) == direction) {
      state = state.with(NORTHWEST, true);
    }

    IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());

    state = state.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);

    if (state.isValidPosition(iworldreader, blockpos)) {
      return state;
    }
    else {
      return null;
    }
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    Direction direction = state.get(FACING);
    BlockPos blockpos = pos.offset(direction);
    BlockState blockstate = worldIn.getBlockState(blockpos);
    return blockstate.func_224755_d(worldIn, blockpos, direction.getOpposite());
  }

  @Override
  @Deprecated
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof LivingEntity) {
      float damage = 3f;
      if (entityIn.fallDistance > 0) {
        damage += entityIn.fallDistance * 1.5f + 2f;
      }
      entityIn.attackEntityFrom(DamageSource.CACTUS, damage);
      ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 1));
    }
  }

  /* Bounds */
  private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

  static {
    ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
    builder.put(Direction.DOWN, VoxelShapes.create(0.1875, 0, 0.1875, 0.8125, 0.375, 0.8125));
    builder.put(Direction.UP, VoxelShapes.create(0.1875, 0.625, 0.1875, 0.8125, 1, 0.8125));
    builder.put(Direction.NORTH, VoxelShapes.create(0.1875, 0.1875, 0, 0.8125, 0.8125, 0.375));
    builder.put(Direction.SOUTH, VoxelShapes.create(0.1875, 0.1875, 0.625, 0.8125, 0.8125, 1));
    builder.put(Direction.EAST, VoxelShapes.create(0.625, 0.1875, 0.1875, 1, 0.8125, 0.8125));
    builder.put(Direction.WEST, VoxelShapes.create(0, 0.1875, 0.1875, 0.375, 0.8125, 0.8125));

    BOUNDS = builder.build();
  }

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return BOUNDS.get(state.get(FACING));
  }

  @Override
  public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public IFluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
  }
}
