package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class ChannelBlock extends Block implements EntityBlock {
	private static final Component SIDE_IN = TConstruct.makeTranslation("block", "channel.side.in");
	private static final Component SIDE_OUT = TConstruct.makeTranslation("block", "channel.side.out");
	private static final Component SIDE_NONE = TConstruct.makeTranslation("block", "channel.side.none");
	private static final Component DOWN_OUT = TConstruct.makeTranslation("block", "channel.down.out");
	private static final Component DOWN_NONE = TConstruct.makeTranslation("block", "channel.down.none");
	private static final Map<ChannelConnection,Component> SIDE_CONNECTION = Util.make(new EnumMap<>(ChannelConnection.class), map -> {
		map.put(ChannelConnection.IN, SIDE_IN);
		map.put(ChannelConnection.OUT, SIDE_OUT);
		map.put(ChannelConnection.NONE, SIDE_NONE);
	});

	/** Properties for the channel */
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<ChannelConnection> NORTH = EnumProperty.create("north", ChannelConnection.class);
	public static final EnumProperty<ChannelConnection> SOUTH = EnumProperty.create("south", ChannelConnection.class);
	public static final EnumProperty<ChannelConnection> WEST = EnumProperty.create("west", ChannelConnection.class);
	public static final EnumProperty<ChannelConnection> EAST = EnumProperty.create("east", ChannelConnection.class);
	public static final Map<Direction,EnumProperty<ChannelConnection>> DIRECTION_MAP = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
		map.put(Direction.EAST, EAST);
	});

	/** Voxel bounds for each of the four cardinal directions */
	private static final Map<Direction,VoxelShape> SIDE_BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.NORTH, Shapes.join(box( 4, 4,  0, 12, 9,  4), box( 6, 6,  0, 10, 9,  4), BooleanOp.ONLY_FIRST));
		map.put(Direction.SOUTH, Shapes.join(box( 4, 4, 12, 12, 9, 16), box( 6, 6, 12, 10, 9, 16), BooleanOp.ONLY_FIRST));
		map.put(Direction.WEST,  Shapes.join(box( 0, 4,  4,  4, 9, 12), box( 0, 6,  6,  4, 9, 10), BooleanOp.ONLY_FIRST));
		map.put(Direction.EAST,  Shapes.join(box(12, 4,  4, 16, 9, 12), box(12, 6,  6, 16, 9, 10), BooleanOp.ONLY_FIRST));
	});

	/**
	 * Makes a int key from a set of booleans
	 * @return  Bounds index key
	 */
	private static int makeKey(boolean down, boolean north, boolean south, boolean west, boolean east) {
		return (down ? 0b00001 : 0) | (north ? 0b00010 : 0) | (south ? 0b00100 : 0) | (west ? 0b01000 : 0) | (east ? 0b10000 : 0);
	}

	/** Voxel bounds for each of the state shapes */
	private static final VoxelShape[] BOUNDS;
	static {
		// center without down connection
		VoxelShape centerUnconnected = Shapes.joinUnoptimized(
				box(4, 4, 4, 12, 9, 12),
				Shapes.or(box(6, 6, 4, 10, 9, 12), box(4, 6, 6, 12, 9, 10)),
				BooleanOp.ONLY_FIRST);
		// center with down connection
		VoxelShape centerConnected = Shapes.joinUnoptimized(
				box(4, 2, 4, 12, 9, 12),
				Shapes.or(box(6, 6, 4, 10, 9, 12), box(4, 6, 6, 12, 9, 10), box(6, 2, 6, 10, 9, 10)),
				BooleanOp.ONLY_FIRST);
		// bounds for unconnected walls
		VoxelShape northWall = box( 6, 6,  4, 10, 9,  6);
		VoxelShape southWall = box( 6, 6, 10, 10, 9, 12);
		VoxelShape westWall  = box( 4, 6,  6,  6, 9, 10);
		VoxelShape eastWall  = box(10, 6,  6, 12, 9, 10);

		// iterate through each direction
		BOUNDS = new VoxelShape[32];
		boolean[] bools = {false, true};
		for (boolean down : bools) {
			VoxelShape center = down ? centerConnected : centerUnconnected;
			for (boolean north : bools) {
				VoxelShape northBounds = north ? SIDE_BOUNDS.get(Direction.NORTH) : northWall;
				for (boolean south : bools) {
					VoxelShape southBounds = south ? SIDE_BOUNDS.get(Direction.SOUTH) : southWall;
					for (boolean west : bools) {
						VoxelShape westBounds = west ? SIDE_BOUNDS.get(Direction.WEST) : westWall;
						for (boolean east : bools) {
							VoxelShape eastBounds = east ? SIDE_BOUNDS.get(Direction.EAST) : eastWall;
							BOUNDS[makeKey(down, north, south, west, east)] = Shapes.or(center, northBounds, southBounds, westBounds, eastBounds);
						}
					}
				}
			}
		}
	}

	public ChannelBlock(Properties props) {
		super(props);

		this.registerDefaultState(this.defaultBlockState()
														 .setValue(DOWN, false)
														 .setValue(NORTH, ChannelConnection.NONE)
														 .setValue(SOUTH, ChannelConnection.NONE)
														 .setValue(WEST, ChannelConnection.NONE)
														 .setValue(EAST, ChannelConnection.NONE));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return BOUNDS[makeKey(state.getValue(DOWN), state.getValue(NORTH).canFlow(), state.getValue(SOUTH).canFlow(), state.getValue(WEST).canFlow(), state.getValue(EAST).canFlow())];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
		builder.add(DOWN, POWERED);
		DIRECTION_MAP.values().forEach(builder::add);
	}

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
    return false;
  }

	/* Basic block logic */

	/**
	 * Checks if the block at the given position is a fluid handler
	 * @param world  World instance
	 * @param side   Side to check
	 * @param pos    Position to check
	 * @return  True if its a fluid handler
	 */
	private static boolean isFluidHandler(LevelAccessor world, Direction side, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		return te != null && te.getCapability(ForgeCapabilities.FLUID_HANDLER, side).isPresent();
	}

	/**
	 * Checks if the block can connect on the given side
	 * @param world        World instance
	 * @param facingState  State facing
	 * @param facingPos    Position facing
	 * @param side         Side facing
	 * @return  True if the channel can connect
	 */
	private boolean canConnect(LevelAccessor world, Direction side, BlockState facingState, BlockPos facingPos) {
		if (facingState.getBlock() == this) {
			return true;
		}
		return isFluidHandler(world, side.getOpposite(), facingPos);
	}

	/**
	 * Checks if the block can connect on the given side
	 * @param world  World instance
	 * @param pos    Channel position
	 * @param side   Side to check
	 * @return  True if the channel can connect
	 */
	private boolean canConnect(LevelAccessor world, BlockPos pos, Direction side) {
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

    // we cannot connect upwards, so done here
		if (side == Direction.DOWN) {
		  return state;
    }

		// if placed on the top face, try to connect down
		if (side == Direction.UP) {
			return state.setValue(DOWN, canConnect(world, pos, Direction.DOWN));
		}

		// if placed on a fluid handler, connect to that
		ChannelConnection connection = ChannelConnection.NONE;
    BlockPos placedOn = pos.relative(side.getOpposite());
    // on another channel means in or out
    if (world.getBlockState(placedOn).is(this)) {
      Player player = context.getPlayer();
      connection = player != null && player.isShiftKeyDown() ? ChannelConnection.IN : ChannelConnection.OUT;
    } else if (isFluidHandler(world, side, placedOn)) {
      connection = ChannelConnection.OUT;
    }
    return state.setValue(DIRECTION_MAP.get(side.getOpposite()), connection);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		// down only cares about connected or not
		if (facing == Direction.DOWN) {
			if (state.getValue(DOWN) && facingState.isAir()) {
				state = state.setValue(DOWN, false);
			}
			return state;
		}

		// sides may connect to channels directly
		if (facing != Direction.UP) {
			// if the change was from another channel, copy, but invert its connection
			EnumProperty<ChannelConnection> prop = DIRECTION_MAP.get(facing);
			if (facingState.is(this)) {
				state = state.setValue(prop, facingState.getValue(DIRECTION_MAP.get(facing.getOpposite())).getOpposite());
			} else {
				// out is only valid if facing a fluid handler
				ChannelConnection connection = state.getValue(prop);
				if (connection != ChannelConnection.NONE && facingState.isAir()) {
					state = state.setValue(prop, ChannelConnection.NONE);
				}
			}
		}

		return state;
	}

	@Nullable
	private BlockState interactWithSide(BlockState state, Level world, BlockPos pos, Player player, Direction side) {
		if (side == Direction.DOWN) {
			if (!state.getValue(DOWN) && canConnect(world, pos, side)) {
				player.displayClientMessage(DOWN_OUT, true);
				return state.setValue(DOWN, true);
			} else if (state.getValue(DOWN)) {
				player.displayClientMessage(DOWN_NONE, true);
				return state.setValue(DOWN, false);
			}
		} else {
			EnumProperty<ChannelConnection> prop = DIRECTION_MAP.get(side);
			ChannelConnection connection = state.getValue(prop);
			BlockPos facingPos = pos.relative(side);
			// if facing another channel, toggle to next connection prop
			BlockState facingState = world.getBlockState(facingPos);
			ChannelConnection newConnect = connection.getNext(player.isShiftKeyDown());
			// if its not a fluid handler, cannot set out
			if (newConnect == ChannelConnection.OUT && facingState.getBlock() != this && !isFluidHandler(world, side.getOpposite(), facingPos)) {
				newConnect = newConnect.getNext(player.isShiftKeyDown());
			}
			player.displayClientMessage(SIDE_CONNECTION.get(newConnect), true);
			return state.setValue(prop, newConnect);
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
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
			if (hitFace != Direction.DOWN && stack.getItem() instanceof BlockItem blockItem && RegistryHelper.contains(MantleTags.Blocks.ATTACHED_GAUGES, blockItem.getBlock())) {
				// for sides, need to toggle the property on
				if (hitFace != Direction.UP) {
					EnumProperty<ChannelConnection> prop = DIRECTION_MAP.get(hitFace);
					ChannelConnection connection = state.getValue(prop);
					if (connection == ChannelConnection.NONE) {
						BlockState newState = state.setValue(prop, ChannelConnection.IN);
						world.setBlockAndUpdate(pos, newState);
					}
				}
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
			boolean isPowered = worldIn.hasNeighborSignal(pos);
			if (isPowered != state.getValue(POWERED)) {
				state = state.setValue(POWERED, isPowered).setValue(DOWN, isPowered && canConnect(worldIn, pos, Direction.DOWN));
				worldIn.setBlock(pos, state, Block.UPDATE_CLIENTS);
			}
      BlockEntityHelper.get(ChannelBlockEntity.class, worldIn, pos)
                      .ifPresent(te -> te.removeCachedNeighbor(Util.directionFromOffset(pos, fromPos)));
		}
	}

	@Override
	@Deprecated
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return side.getAxis().isHorizontal() && adjacentBlockState.is(this) && state.getValue(DIRECTION_MAP.get(side)).canFlow() && adjacentBlockState.getValue(DIRECTION_MAP.get(side.getOpposite())).canFlow();
	}

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new ChannelBlockEntity(pPos, pState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> givenType) {
    return pLevel.isClientSide ? null : BlockEntityHelper.castTicker(givenType, TinkerSmeltery.channel.get(), ChannelBlockEntity.SERVER_TICKER);
  }

  public enum ChannelConnection implements StringRepresentable {
		/** No connection on this side */
		NONE,
		/** Channel is flowing inwards on this side */
		IN,
		/** Channel is flowing outwards on this side */
		OUT;

		@Override
		public String getSerializedName() {
			return this.toString().toLowerCase(Locale.US);
		}

		/**
		 * Checks if the channel can flow on this side
		 * @return  True if the channel can flow
		 */
		public boolean canFlow() {
			return this == IN || this == OUT;
		}

		/**
		 * Gets the opposite direction to this side
		 * @return  Opposite direction
		 */
		public ChannelConnection getOpposite() {
      return switch (this) {
        case IN -> OUT;
        case OUT -> IN;
        default -> NONE;
      };
    }

		/**
		 * Gets the next side in the cycle for interaction
		 * @param reverse  If true, reverse cycle order
		 * @return  Next side to cycle
		 */
		public ChannelConnection getNext(boolean reverse) {
			if (reverse) {
        return switch (this) {
          case NONE -> OUT;
          case OUT -> IN;
          case IN -> NONE;
        };
			} else {
        return switch (this) {
          case NONE -> IN;
          case IN -> OUT;
          case OUT -> NONE;
        };
			}
    }
	}
}
