package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;

public class FaucetBlock extends Block implements EntityBlock {
  public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;
  private static final EnumMap<Direction,VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
    Direction.DOWN,  Shapes.join(box( 4, 10,  4, 12, 16, 12), box( 6, 10,  6, 10, 16, 10), BooleanOp.ONLY_FIRST),
    Direction.NORTH, Shapes.join(box( 4,  4, 10, 12, 10, 16), box( 6,  6, 10, 10, 10, 16), BooleanOp.ONLY_FIRST),
    Direction.SOUTH, Shapes.join(box( 4,  4,  0, 12, 10,  6), box( 6,  6,  0, 10, 10,  6), BooleanOp.ONLY_FIRST),
    Direction.WEST,  Shapes.join(box(10,  4,  4, 16, 10, 12), box(10,  6,  6, 16, 10, 10), BooleanOp.ONLY_FIRST),
    Direction.EAST,  Shapes.join(box( 0,  4,  4,  6, 10, 12), box( 0,  6,  6,  6, 10, 10), BooleanOp.ONLY_FIRST)));

  public FaucetBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  /* Blockstate */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction dir = context.getClickedFace();
    if (dir == Direction.UP) {
      dir = Direction.DOWN;
    }
    return this.defaultBlockState().setValue(FACING, dir);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPES.get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
    return false;
  }


  /* Tile entity */

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new FaucetBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
    return BlockEntityHelper.serverTicker(pLevel, type, TinkerSmeltery.faucet.get(), FaucetBlockEntity.SERVER_TICKER);
  }

  @SuppressWarnings("deprecation")
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    if (world.getBlockEntity(pos) instanceof FaucetBlockEntity faucet) {
      faucet.activate();
    }
    return InteractionResult.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (!world.isClientSide && world.getBlockEntity(pos) instanceof FaucetBlockEntity faucet) {
      faucet.neighborChanged(fromPos);
      faucet.handleRedstone(world.hasNeighborSignal(pos));
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
    if (world.getBlockEntity(pos) instanceof FaucetBlockEntity faucet) {
      faucet.activate();
    }
  }

  /* Display */

  private static final Vector3f RED = new Vector3f(1.0F, 0.0F, 0.0F);

  /**
   * Adds particles to the faucet
   * @param state    Faucet state
   * @param worldIn  World instance
   * @param pos      Faucet position
   */
  private static void addParticles(BlockState state, LevelAccessor worldIn, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    double x = (double)pos.getX() + 0.5D - 0.3D * (double)direction.getStepX();
    double y = (double)pos.getY() + 0.5D - 0.3D * (double)direction.getStepY();
    double z = (double)pos.getZ() + 0.5D - 0.3D * (double)direction.getStepZ();
    worldIn.addParticle(new DustParticleOptions(RED, 0.5f), x, y, z, 0.0D, 0.0D, 0.0D);
  }

  @Override
  public void animateTick(BlockState stateIn, Level world, BlockPos pos, RandomSource rand) {
    if (rand.nextFloat() < 0.25F && world.getBlockEntity(pos) instanceof FaucetBlockEntity faucet
        && faucet.isPouring() && faucet.getRenderFluid().isEmpty()) {
      addParticles(stateIn, world, pos);
    }
  }
}
