package slimeknights.tconstruct.smeltery.block.channel;

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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.TwoWay;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.OneWay;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

import static slimeknights.mantle.datagen.MantleTags.Blocks.ATTACHED_GAUGES;
import static slimeknights.tconstruct.common.TinkerTags.Blocks.CHANNELS;
import static slimeknights.tconstruct.smeltery.block.channel.AbstractChannelBlock.ConnectionType.NONE;
import static slimeknights.tconstruct.smeltery.block.channel.AbstractChannelBlock.ConnectionType.ONE_WAY;
import static slimeknights.tconstruct.smeltery.block.channel.AbstractChannelBlock.ConnectionType.TWO_WAY;

public abstract class AbstractChannelBlock
  extends Block implements EntityBlock {

  private static final Component SIDE_IN = TConstruct.makeTranslation("block", "channel.side.in");
  private static final Component SIDE_OUT = TConstruct.makeTranslation("block", "channel.side.out");
  private static final Component SIDE_NONE = TConstruct.makeTranslation("block", "channel.side.none");
  private static final Component DOWN_IN = TConstruct.makeTranslation("block", "channel.down.in");
  private static final Component DOWN_OUT = TConstruct.makeTranslation("block", "channel.down.out");
  private static final Component DOWN_NONE = TConstruct.makeTranslation("block", "channel.down.none");
  private static final Component UP_IN = TConstruct.makeTranslation("block", "channel.up.in");
  private static final Component UP_OUT = TConstruct.makeTranslation("block", "channel.up.out");
  private static final Component UP_NONE = TConstruct.makeTranslation("block", "channel.up.none");

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
  public final ConnectionType up;
  public final ConnectionType down;


  public enum ConnectionType {
    NONE,
    ONE_WAY,
    TWO_WAY
  }

  public AbstractChannelBlock(Properties props, ConnectionType up, ConnectionType down) {
    super(props);
    this.up = up;
    this.down = down;
    this.shapes = createShapes();
  }


  public TwoWay getCurrentFlowOnSide(Direction side, BlockState state) {
    return switch (side) {
      case UP -> switch (this.up) {
        case NONE -> TwoWay.NONE;
        case ONE_WAY -> state.getValue(UP_1WAY).asTwoWay();
        case TWO_WAY -> state.getValue(UP_2WAY);
      };
      case DOWN -> switch (this.down) {
        case NONE -> TwoWay.NONE;
        case ONE_WAY -> state.getValue(DOWN_1WAY).asTwoWay();
        case TWO_WAY -> state.getValue(DOWN_2WAY);
      };
      default -> (TwoWay) state.getValue(getProperty(side));
    };
  }

  /**
   * Makes an int key from a set of booleans. The range of this should be [0,{@link AbstractChannelBlock#createShapes() createShapes().length}) as it is used to index the shapes array.
   *
   * @return {@link AbstractChannelBlock#shapes} index key
   * @apiNote Called during the super call in your constructor so you cannot use field values in here. {@link AbstractChannelBlock#up} and {@link AbstractChannelBlock#down} are both set though.
   */
  protected abstract int makeShapeKey(boolean up, boolean down, boolean north, boolean south, boolean west, boolean east);

  /**
   * Voxel bounds for each of the state shapes
   */
  protected final VoxelShape[] shapes;

  /**
   * Create the array of shapes for this shape, indexed by {@link AbstractChannelBlock#makeShapeKey}
   *
   * @return Array of shapes
   * @apiNote Called during the super call in your constructor so you cannot use field values in here. {@link AbstractChannelBlock#up} and {@link AbstractChannelBlock#down} are both set though.
   */
  protected abstract VoxelShape[] createShapes();

  @Override
  @SuppressWarnings("deprecation")
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

    // we cannot in this direction, so ignore
    if (side == Direction.DOWN && this.up == NONE) {
      return state;
    }
    if (side == Direction.UP && this.down == NONE) {
      return state;
    }

    // if placed on a vertical face and we can connect one way in that direction, connect that way
    if (side == Direction.UP && down == ONE_WAY) {
      return state.setValue(DOWN_1WAY, OneWay.TRUE);
    }
    if (side == Direction.DOWN && up == ONE_WAY) {
      return state.setValue(DOWN_1WAY, OneWay.TRUE);
    }

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

    @SuppressWarnings("unchecked") // Safe as the other values that can have that method return OneWay return before this
    EnumProperty<TwoWay> prop = (EnumProperty<TwoWay>) getProperty(side.getOpposite());
    return state.setValue(prop, connection);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    // ignore changes from directions we don't connect to
    if ((facing == Direction.UP && this.up == NONE) || (facing == Direction.DOWN && this.down == NONE))
      return state;

    // vertical directions can only care about if they are connected or not
    if (facing == Direction.DOWN && this.down == ONE_WAY) {
      if (state.getValue(DOWN_1WAY).canFlow() && facingState.isAir()) {
        state = state.setValue(DOWN_1WAY, OneWay.FALSE);
      }
      return state;
    }
    if (facing == Direction.UP && this.up == ONE_WAY) {
      if (state.getValue(UP_1WAY).canFlow() && facingState.isAir()) {
        state = state.setValue(UP_1WAY, OneWay.FALSE);
      }
      return state;
    }


    @SuppressWarnings("unchecked") // Safe as the other values that can have that method return OneWay return before this
    EnumProperty<TwoWay> prop = (EnumProperty<TwoWay>) getProperty(facing);

    // if the change was from another channel, copy, but invert its connection
    if (facingState.is(CHANNELS)) {
      TwoWay oppositeConnection = facingState.getValue(getProperty(facing.getOpposite())).reverseFlowTwoWay();
      state = state.setValue(prop, oppositeConnection);
    } else {
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
    // if we cannot connect in this direction, ignore the connection
    if ((side == Direction.DOWN && this.down == NONE) || (side == Direction.UP && this.up == NONE)) return state;

    if (side == Direction.DOWN && this.down == ONE_WAY) {
      if (!state.getValue(DOWN_1WAY).canFlow() && canConnect(world, pos, side)) {
        player.displayClientMessage(DOWN_OUT, true);
        return state.setValue(DOWN_1WAY, OneWay.TRUE);
      } else if (state.getValue(DOWN_1WAY).canFlow()) {
        player.displayClientMessage(DOWN_NONE, true);
        return state.setValue(DOWN_1WAY, OneWay.FALSE);
      }
    } else if (side == Direction.UP && this.up == ONE_WAY) {
      if (!state.getValue(UP_1WAY).canFlow() && canConnect(world, pos, side)) {
        player.displayClientMessage(UP_OUT, true);
        return state.setValue(UP_1WAY, OneWay.TRUE);
      } else if (state.getValue(UP_1WAY).canFlow()) {
        player.displayClientMessage(UP_NONE, true);
        return state.setValue(UP_1WAY, OneWay.FALSE);
      }
    } else {
      @SuppressWarnings("unchecked") // safe as the vertical directions cannot reach this with a value other than TWO_WAY
      EnumProperty<TwoWay> prop = (EnumProperty<TwoWay>) getProperty(side);
      TwoWay connection = state.getValue(prop);
      BlockPos facingPos = pos.relative(side);
      // if facing another channel, toggle to next connection prop
      BlockState facingState = world.getBlockState(facingPos);
      TwoWay newConnect = connection.getNext(player.isShiftKeyDown());
      // if its not a fluid handler, cannot set out
      if (newConnect == TwoWay.OUT && facingState.getBlock() != this && !isFluidHandler(world, side.getOpposite(), facingPos)) {
        newConnect = newConnect.getNext(player.isShiftKeyDown());
      }
      player.displayClientMessage(getConnectionToggleMessage(side, newConnect), true);
      return state.setValue(prop, newConnect);
    }

    return null;
  }

  /** Make generics happy */
  private <C extends Enum<C> & ChannelConnection> BlockState mutateProperty(BlockState state, EnumProperty<C> prop, UnaryOperator<C> op) {
    return state.setValue(prop, op.apply(state.getValue(prop)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    Direction hitFace = hit.getDirection();
    if (world.getBlockState(pos.relative(hitFace)).canBeReplaced()) {
      ItemStack stack = player.getItemInHand(hand);

      // if the player is holding a channel, skip unless we clicked the top
      // they can shift click to place one on the top
      if (stack.getItem() instanceof BlockItem item && RegistryHelper.contains(CHANNELS, item.getBlock())) {
        return InteractionResult.PASS;
      }
      // if they are holding a gauge, set the side to in to make it easier to place a gauge on it
      if (hitFace != Direction.DOWN && stack.getItem() instanceof BlockItem item && RegistryHelper.contains(ATTACHED_GAUGES, item.getBlock())) {

        // for sides, need to toggle the property on
        BlockState newState;
        EnumProperty<? extends ChannelConnection> prop = getProperty(hitFace);
        newState = mutateProperty(state, prop, c -> c.canFlow() ? c : ChannelConnection.getNext(c, false));
        if (state != newState) world.setBlockAndUpdate(pos, newState);

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
      if (!world.isClientSide && world.getBlockEntity(pos) instanceof ChannelBlockEntity te) {
        te.refreshNeighbor(newState, side);
      }
      world.setBlockAndUpdate(pos, newState);
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.PASS;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
    if (!world.isClientSide && world.getBlockEntity(pos) instanceof ChannelBlockEntity be) {
      be.removeCachedNeighbor(Util.directionFromOffset(pos, fromPos));
    }
  }

  @Override
  @Deprecated
  @SuppressWarnings("deprecation")
  public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
    return side.getAxis().isHorizontal() && adjacentBlockState.is(this) && state.getValue(getProperty(side))
      .canFlow() && adjacentBlockState.getValue(getProperty(side.getOpposite())).canFlow();
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

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    var up = this.up != NONE && state.getValue(getProperty(Direction.UP)).canFlow();
    var down = this.down != NONE && state.getValue(getProperty(Direction.DOWN)).canFlow();
    return shapes[makeShapeKey(up, down, state.getValue(NORTH).canFlow(), state.getValue(SOUTH).canFlow(), state.getValue(WEST).canFlow(), state.getValue(EAST).canFlow())];
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    switch (this.down) {
      case ONE_WAY -> builder.add(DOWN_1WAY);
      case TWO_WAY -> builder.add(DOWN_2WAY);
    }
    switch (this.up) {
      case ONE_WAY -> builder.add(UP_1WAY);
      case TWO_WAY -> builder.add(UP_2WAY);
    }
    builder.add(NORTH);
    builder.add(EAST);
    builder.add(SOUTH);
    builder.add(WEST);
  }

  /* Helpers for sides */

  public EnumProperty<? extends ChannelConnection> getProperty(Direction dir) {
    return switch (dir) {
      case DOWN -> switch (this.down) {
        case NONE -> throw new IllegalArgumentException();
        case ONE_WAY -> DOWN_1WAY;
        case TWO_WAY -> DOWN_2WAY;
      };
      case UP -> switch (this.up) {
        case NONE -> throw new IllegalArgumentException();
        case ONE_WAY -> UP_1WAY;
        case TWO_WAY -> UP_2WAY;
      };
      case NORTH -> NORTH;
      case SOUTH -> SOUTH;
      case WEST -> WEST;
      case EAST -> EAST;
    };
  }

  public Component getConnectionToggleMessage(Direction dir, TwoWay way) {
    return switch (dir) {
      case UP -> switch (way) {
        case NONE -> UP_NONE;
        case IN -> UP_IN;
        case OUT -> UP_OUT;
      };
      case DOWN -> switch (way) {
        case NONE -> DOWN_NONE;
        case IN -> DOWN_IN;
        case OUT -> DOWN_OUT;
      };
      default -> switch (way) {
        case NONE -> SIDE_NONE;
        case IN -> SIDE_IN;
        case OUT -> SIDE_OUT;
      };
    };
  }
}
