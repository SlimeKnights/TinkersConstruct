package slimeknights.tconstruct.smeltery.block.channel;

import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Contract;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.TwoWay;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.OneWay;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static slimeknights.tconstruct.common.TinkerTags.Blocks.CHANNELS;

public abstract class AbstractChannelBlock<U extends Enum<U> & ChannelConnection<U>, D extends Enum<D> & ChannelConnection<D>>
  extends Block implements EntityBlock {

  private static final Component SIDE_IN = TConstruct.makeTranslation("block", "channel.side.in");
  private static final Component SIDE_OUT = TConstruct.makeTranslation("block", "channel.side.out");
  private static final Component SIDE_NONE = TConstruct.makeTranslation("block", "channel.side.none");
  private static final Component DOWN_OUT = TConstruct.makeTranslation("block", "channel.down.out");
  private static final Component DOWN_NONE = TConstruct.makeTranslation("block", "channel.down.none");
  private static final Map<TwoWay, Component> SIDE_CONNECTION = Util.make(new EnumMap<>(TwoWay.class), map -> {
    map.put(TwoWay.IN, SIDE_IN);
    map.put(TwoWay.OUT, SIDE_OUT);
    map.put(TwoWay.NONE, SIDE_NONE);
  });

  /**
   * Properties for the channel
   */
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final EnumProperty<OneWay> DOWN_1WAY = EnumProperty.create("down", OneWay.class);
  public static final EnumProperty<OneWay> UP_1WAY = EnumProperty.create("up", OneWay.class);
  public static final EnumProperty<TwoWay> DOWN_2WAY = EnumProperty.create("down", TwoWay.class);
  public static final EnumProperty<TwoWay> UP_2WAY = EnumProperty.create("up", TwoWay.class);
  public static final EnumProperty<TwoWay> NORTH = EnumProperty.create("north", TwoWay.class);
  public static final EnumProperty<TwoWay> SOUTH = EnumProperty.create("south", TwoWay.class);
  public static final EnumProperty<TwoWay> WEST = EnumProperty.create("west", TwoWay.class);
  public static final EnumProperty<TwoWay> EAST = EnumProperty.create("east", TwoWay.class);
  public static final Map<Direction, EnumProperty<TwoWay>> DIRECTION_MAP = Util.make(new EnumMap<>(Direction.class),
    map -> {
      map.put(Direction.NORTH, NORTH);
      map.put(Direction.SOUTH, SOUTH);
      map.put(Direction.WEST, WEST);
      map.put(Direction.EAST, EAST);
    });
  @Nullable public final EnumProperty<U> up;
  @Nullable public final EnumProperty<D> down;
  private final Map<Direction, EnumProperty<? extends ChannelConnection<?>>> directionalProperties;


  public AbstractChannelBlock(Properties props, @Nullable EnumProperty<U> upConnection,
    @Nullable EnumProperty<D> downConnection) {
    super(props);
    this.up = upConnection;
    this.down = downConnection;
    this.directionalProperties = new EnumMap<>(DIRECTION_MAP);

    if (upConnection != null) {directionalProperties.put(Direction.UP, upConnection);}
    if (downConnection != null) {directionalProperties.put(Direction.UP, upConnection);}
  }

  /**
   * Gets the default value for the property in a given direction.
   *
   * @param dir The direction to get for
   * @param on If this should be a default 'on' value, otherwise is a default 'off' value
   * @return a default value for the enum property
   */
  protected static <C extends Enum<C> & ChannelConnection<C>> C getDefaultValue(EnumProperty<C> dir, boolean on) {
    var values = dir.getValueClass().getEnumConstants();
    return values[on ? values.length - 1 : 0];
  }

  /**
   * Makes an int key from a set of booleans.
   *
   * @return {@link AbstractChannelBlock#bounds} index key
   * @apiNote Called before your constructor is called so you cannot use field values in here.
   */
  protected abstract int makeShapeKey(U up, D down, boolean north, boolean south, boolean west, boolean east);

  /**
   * Voxel bounds for each of the state shapes
   */
  protected final VoxelShape[] bounds = createShapes();

  /**
   * Create the array of shapes for this shape, indexed by {@link AbstractChannelBlock#makeShapeKey}
   *
   * @return Array of shapes
   * @apiNote Called during the super call your constructor is called so you cannot use field values in here.
   */
  protected abstract VoxelShape[] createShapes();

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
    return false;
  }

  /* Basic block logic */

  /**
   * Checks if the block at the given position is a fluid handler
   *
   * @param world World instance
   * @param side  Side to check
   * @param pos   Position to check
   * @return True if it's a fluid handler
   */
  private static boolean isFluidHandler(LevelAccessor world, Direction side, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);
    return te != null && te.getCapability(ForgeCapabilities.FLUID_HANDLER, side).isPresent();
  }

  /**
   * Checks if the block can connect on the given side
   *
   * @param world       World instance
   * @param facingState State facing
   * @param facingPos   Position facing
   * @param side        Side facing
   * @return True if the channel can connect
   */
  private boolean canConnect(LevelAccessor world, Direction side, BlockState facingState, BlockPos facingPos) {
    if (facingState.getBlock() == this) {
      return true;
    }
    return isFluidHandler(world, side.getOpposite(), facingPos);
  }

  /**
   * Checks if the block can connect on the given side
   *
   * @param world World instance
   * @param pos   Channel position
   * @param side  Side to check
   * @return True if the channel can connect
   */
  protected boolean canConnect(LevelAccessor world, BlockPos pos, Direction side) {
    BlockPos facingPos = pos.relative(side);
    return canConnect(world, side, world.getBlockState(facingPos), facingPos);
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = this.defaultBlockState().setValue(POWERED, world.hasNeighborSignal(pos));
    Direction side = context.getClickedFace();

		// if placed on a fluid handler, connect to that
		TwoWay connection = TwoWay.NONE;
    BlockPos placedOn = pos.relative(side.getOpposite());
    // on another channel means in or out
    if (world.getBlockState(placedOn).is(this)) {
      Player player = context.getPlayer();
      connection = player != null && player.isShiftKeyDown() ? TwoWay.IN : TwoWay.OUT;
    } else if (isFluidHandler(world, side, placedOn)) {
      connection = TwoWay.OUT;
    }
    return state.setValue(DIRECTION_MAP.get(side.getOpposite()), connection);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    // if the change was from another channel, copy, but invert its connection
    EnumProperty<TwoWay> prop = getDirectionProperty(facing);
    if (prop != null)
      if (facingState.is(CHANNELS)) {
        state = state.setValue(prop, facingState.getValue(getDirectionProperty(facing.getOpposite())).reverseFlow());
      }
      else {
        // out is only valid if facing a fluid handler
        TwoWay connection = state.getValue(prop);
        if (connection != TwoWay.NONE && facingState.isAir()) {
          state = state.setValue(prop, TwoWay.NONE);
        }
      }

    return state;
  }

  @Nullable
  private BlockState interactWithSide(BlockState state, Level world, BlockPos pos, Player player, Direction side) {
    if (side == Direction.DOWN) {
      if (!state.getValue(DOWN_1WAY).canFlow() && canConnect(world, pos, side)) {
        player.displayClientMessage(DOWN_OUT, true);
        return state.setValue(DOWN_1WAY, OneWay.TRUE);
      }
      else if (state.getValue(DOWN_1WAY).canFlow()) {
        player.displayClientMessage(DOWN_NONE, true);
        return state.setValue(DOWN_1WAY, OneWay.FALSE);
      }
    } else {
      EnumProperty<TwoWay> prop = DIRECTION_MAP.get(side);
      TwoWay connection = state.getValue(prop);
      BlockPos facingPos = pos.relative(side);
      // if facing another channel, toggle to next connection prop
      BlockState facingState = world.getBlockState(facingPos);
      TwoWay newConnect = connection.getNext(player.isShiftKeyDown());
      // if its not a fluid handler, cannot set out
      if (newConnect == TwoWay.OUT && facingState.getBlock() != this && !isFluidHandler(world, side.getOpposite(),
        facingPos)) {
        newConnect = newConnect.getNext(player.isShiftKeyDown());
      }
      player.displayClientMessage(SIDE_CONNECTION.get(newConnect), true);
      return state.setValue(prop, newConnect);
    }

    return null;
  }

  /** Helper method so that generics behave */
  private <E extends Enum<E> & ChannelConnection<E>> BlockState mutateDirection(BlockState state, Direction dir, UnaryOperator<E> mutator) {
    EnumProperty<E> prop = getDirectionProperty(dir);
    if (prop == null) return state;
    return state.setValue(prop, mutator.apply(state.getValue(prop)));
  }

  /** Helper to hide the unchecked cast. Technically it overcasts due to the caller defining the return type, but we trust ourselves. */
  @Nullable
  @SuppressWarnings("unchecked")
  private <E extends Enum<E> & ChannelConnection<E>> EnumProperty<E> getDirectionProperty(Direction dir) {
    return (EnumProperty<E>) directionalProperties.get(dir);
  }

  @Override
  @SuppressWarnings("deprecation")
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    Direction hitFace = hit.getDirection();
    if (world.getBlockState(pos.relative(hitFace)).canBeReplaced()) {
      // if the player is holding a channel, skip unless we clicked the top
      // they can shift click to place one on the top
      ItemStack stack = player.getItemInHand(hand);
      if (stack.getItem() == this.asItem()) {
        return InteractionResult.PASS;
      }
      // if they are holding a gauge, set the side to in to make it easier to place a gauge on it
      if (hitFace != Direction.DOWN && stack.getItem() instanceof BlockItem blockItem && RegistryHelper.contains(
        MantleTags.Blocks.ATTACHED_GAUGES, blockItem.getBlock())) {
        // for sides, need to toggle the property on

        BlockState newState = mutateDirection(state, hitFace, c -> c.canFlow() ? c : c.getNext(false));
        if (state != newState)
          world.setBlockAndUpdate(pos, newState);
        // pass to let them place it
        return InteractionResult.PASS;
      }
    }

    // default to using the clicked side, though null (is that valid?) and up act as down
    Direction side = hitFace == Direction.UP ? Direction.DOWN : hitFace;
    if (player.isShiftKeyDown() && side != Direction.DOWN) {
      side = side.getOpposite();
    }

    // try each of the sides, if clicked use that
    Vec3 hitVec = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
    // map X and Z coords to a direction
    if (hitVec.z() < 0.25f) {
      side = Direction.NORTH;
    } else if (hitVec.z() > 0.75f) {
      side = Direction.SOUTH;
    } else if (hitVec.x() < 0.25f) {
      side = Direction.WEST;
    } else if (hitVec.x() > 0.75f) {
      side = Direction.EAST;
    }

    // toggle the side clicked
    BlockState newState = interactWithSide(state, world, pos, player, side);

    // if we have changes, apply them and return success
    if (newState != null) {
      Direction finalSide = side;
      if (!world.isClientSide) {
        BlockEntityHelper.get(ChannelBlockEntity.class, world, pos).ifPresent(te -> te.refreshNeighbor(newState, finalSide));
      }
      world.setBlockAndUpdate(pos, newState);
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.PASS;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    if (!worldIn.isClientSide) {
      BlockEntityHelper.get(ChannelBlockEntity.class, worldIn, pos)
        .ifPresent(te -> te.removeCachedNeighbor(Util.directionFromOffset(pos, fromPos)));
    }
  }

  @Override
  @Deprecated
  public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
    return side.getAxis().isHorizontal() && adjacentBlockState.is(this) && state.getValue(DIRECTION_MAP.get(side))
      .canFlow() && adjacentBlockState.getValue(DIRECTION_MAP.get(side.getOpposite())).canFlow();
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new ChannelBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> givenType) {
    return BlockEntityHelper.serverTicker(pLevel, givenType, TinkerSmeltery.channel.get(), ChannelBlockEntity.SERVER_TICKER);
  }

}
