package slimeknights.tconstruct.smeltery.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.tileentity.ChannelTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class ChannelBlock extends Block {
	private static final Text SIDE_IN = new TranslatableText(Util.makeTranslationKey("block", "channel.side.in"));
	private static final Text SIDE_OUT = new TranslatableText(Util.makeTranslationKey("block", "channel.side.out"));
	private static final Text SIDE_NONE = new TranslatableText(Util.makeTranslationKey("block", "channel.side.none"));
	private static final Text DOWN_OUT = new TranslatableText(Util.makeTranslationKey("block", "channel.down.out"));
	private static final Text DOWN_NONE = new TranslatableText(Util.makeTranslationKey("block", "channel.down.none"));
	private static final Map<ChannelConnection,Text> SIDE_CONNECTION = Util.make(new EnumMap<>(ChannelConnection.class), map -> {
		map.put(ChannelConnection.IN, SIDE_IN);
		map.put(ChannelConnection.OUT, SIDE_OUT);
		map.put(ChannelConnection.NONE, SIDE_NONE);
	});

	/** Properties for the channel */
	public static final BooleanProperty DOWN = Properties.DOWN;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final EnumProperty<ChannelConnection> NORTH = EnumProperty.of("north", ChannelConnection.class);
	public static final EnumProperty<ChannelConnection> SOUTH = EnumProperty.of("south", ChannelConnection.class);
	public static final EnumProperty<ChannelConnection> WEST = EnumProperty.of("west", ChannelConnection.class);
	public static final EnumProperty<ChannelConnection> EAST = EnumProperty.of("east", ChannelConnection.class);
	public static final Map<Direction,EnumProperty<ChannelConnection>> DIRECTION_MAP = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
		map.put(Direction.EAST, EAST);
	});

	/** Voxel bounds for each of the four cardinal directions */
	private static final Map<Direction,VoxelShape> SIDE_BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.NORTH, VoxelShapes.combineAndSimplify(createCuboidShape( 4, 4,  0, 12, 9,  4), createCuboidShape( 6, 6,  0, 10, 9,  4), BooleanBiFunction.ONLY_FIRST));
		map.put(Direction.SOUTH, VoxelShapes.combineAndSimplify(createCuboidShape( 4, 4, 12, 12, 9, 16), createCuboidShape( 6, 6, 12, 10, 9, 16), BooleanBiFunction.ONLY_FIRST));
		map.put(Direction.WEST,  VoxelShapes.combineAndSimplify(createCuboidShape( 0, 4,  4,  4, 9, 12), createCuboidShape( 0, 6,  6,  4, 9, 10), BooleanBiFunction.ONLY_FIRST));
		map.put(Direction.EAST,  VoxelShapes.combineAndSimplify(createCuboidShape(12, 4,  4, 16, 9, 12), createCuboidShape(12, 6,  6, 16, 9, 10), BooleanBiFunction.ONLY_FIRST));
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
		VoxelShape centerUnconnected = VoxelShapes.combine(
				createCuboidShape(4, 4, 4, 12, 9, 12),
				VoxelShapes.union(createCuboidShape(6, 6, 4, 10, 9, 12), createCuboidShape(4, 6, 6, 12, 9, 10)),
				BooleanBiFunction.ONLY_FIRST);
		// center with down connection
		VoxelShape centerConnected = VoxelShapes.combine(
				createCuboidShape(4, 2, 4, 12, 9, 12),
				VoxelShapes.union(createCuboidShape(6, 6, 4, 10, 9, 12), createCuboidShape(4, 6, 6, 12, 9, 10), createCuboidShape(6, 2, 6, 10, 9, 10)),
				BooleanBiFunction.ONLY_FIRST);
		// bounds for unconnected walls
		VoxelShape northWall = createCuboidShape( 6, 6,  4, 10, 9,  6);
		VoxelShape southWall = createCuboidShape( 6, 6, 10, 10, 9, 12);
		VoxelShape westWall  = createCuboidShape( 4, 6,  6,  6, 9, 10);
		VoxelShape eastWall  = createCuboidShape(10, 6,  6, 12, 9, 10);

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
							BOUNDS[makeKey(down, north, south, west, east)] = VoxelShapes.union(center, northBounds, southBounds, westBounds, eastBounds);
						}
					}
				}
			}
		}
	}

	public ChannelBlock(Settings props) {
		super(props);

		this.setDefaultState(this.getDefaultState()
														 .with(DOWN, false)
														 .with(NORTH, ChannelConnection.NONE)
														 .with(SOUTH, ChannelConnection.NONE)
														 .with(WEST, ChannelConnection.NONE)
														 .with(EAST, ChannelConnection.NONE));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return BOUNDS[makeKey(state.get(DOWN), state.get(NORTH).canFlow(), state.get(SOUTH).canFlow(), state.get(WEST).canFlow(), state.get(EAST).canFlow())];
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block,BlockState> builder) {
		builder.add(DOWN, POWERED);
		DIRECTION_MAP.values().forEach(builder::add);
	}

	/* Basic block logic */

	/**
	 * Checks if the block at the given position is a fluid handler
	 * @param world  World instance
	 * @param side   Side to check
	 * @param pos    Position to check
	 * @return  True if its a fluid handler
	 */
	private static boolean isFluidHandler(WorldAccess world, Direction side, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		return te != null && te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).isPresent();
	}

	/**
	 * Checks if the block can connect on the given side
	 * @param world        World instance
	 * @param facingState  State facing
	 * @param facingPos    Position facing
	 * @param side         Side facing
	 * @return  True if the channel can connect
	 */
	private boolean canConnect(WorldAccess world, Direction side, BlockState facingState, BlockPos facingPos) {
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
	private boolean canConnect(WorldAccess world, BlockPos pos, Direction side) {
		BlockPos facingPos = pos.offset(side);
		return canConnect(world, side, world.getBlockState(facingPos), facingPos);
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = this.getDefaultState().with(POWERED, world.isReceivingRedstonePower(pos));
		Direction side = context.getSide();

    // we cannot connect upwards, so done here
		if (side == Direction.DOWN) {
		  return state;
    }

		// if placed on the top face, try to connect down
		if (side == Direction.UP) {
			return state.with(DOWN, canConnect(world, pos, Direction.DOWN));
		}

		// if placed on a fluid handler, connect to that
		ChannelConnection connection = ChannelConnection.NONE;
    BlockPos placedOn = pos.offset(side.getOpposite());
    // on another channel means in or out
    if (world.getBlockState(placedOn).isOf(this)) {
      PlayerEntity player = context.getPlayer();
      connection = player != null && player.isSneaking() ? ChannelConnection.IN : ChannelConnection.OUT;
    } else if (isFluidHandler(world, side, placedOn)) {
      connection = ChannelConnection.OUT;
    }
    return state.with(DIRECTION_MAP.get(side.getOpposite()), connection);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos) {
		// down only cares about connected or not
		if (facing == Direction.DOWN) {
			if (state.get(DOWN) && !canConnect(world, facing, facingState, facingPos)) {
				state = state.with(DOWN, false);
			}
			return state;
		}

		// sides may connect to channels directly
		if (facing != Direction.UP) {
			// if the change was from another channel, copy, but invert its connection
			EnumProperty<ChannelConnection> prop = DIRECTION_MAP.get(facing);
			if (facingState.getBlock() == this) {
				state = state.with(prop, facingState.get(DIRECTION_MAP.get(facing.getOpposite())).getOpposite());
			} else {
				// in is invalid as it must point to a block, out is only valid if facing a fluid handler
				ChannelConnection connection = state.get(prop);
				if (connection == ChannelConnection.IN || (connection == ChannelConnection.OUT && !isFluidHandler(world, facing.getOpposite(), facingPos))) {
					state = state.with(prop, ChannelConnection.NONE);
				}
			}
		}

		return state;
	}

	@Nullable
	private BlockState interactWithSide(BlockState state, World world, BlockPos pos, PlayerEntity player, Direction side) {
		if (side == Direction.DOWN) {
			if (!state.get(DOWN) && canConnect(world, pos, side)) {
				player.sendMessage(DOWN_OUT, true);
				return state.with(DOWN, true);
			} else if (state.get(DOWN)) {
				player.sendMessage(DOWN_NONE, true);
				return state.with(DOWN, false);
			}
		} else {
			EnumProperty<ChannelConnection> prop = DIRECTION_MAP.get(side);
			ChannelConnection connection = state.get(prop);
			BlockPos facingPos = pos.offset(side);
			// if facing another channel, toggle to next connection prop
			BlockState facingState = world.getBlockState(facingPos);
			if (facingState.getBlock() == this) {
				ChannelConnection newConnect = connection.getNext(player.isSneaking());
				player.sendMessage(SIDE_CONNECTION.get(newConnect), true);
				return state.with(prop, newConnect);
				// if not connected and we can connect, do so
			} else if (connection != ChannelConnection.OUT && isFluidHandler(world, side.getOpposite(), facingPos)) {
				player.sendMessage(SIDE_OUT, true);
				return state.with(prop, ChannelConnection.OUT);
				// if connected, disconnect
			} else if (connection != ChannelConnection.NONE) {
				player.sendMessage(SIDE_NONE, true);
				return state.with(prop, ChannelConnection.NONE);
			}
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {		// if the player is holding a channel, skip unless we clicked the top
		// they can shift click to place one on the top
		Direction hitFace = hit.getSide();
		if (player.getStackInHand(hand).getItem() == this.asItem() && world.isAir(pos.offset(hitFace))) {
			return ActionResult.PASS;
		}

		// default to using the clicked side, though null (is that valid?) and up act as down
		Direction side = hitFace == Direction.UP ? Direction.DOWN : hitFace;

		// try each of the sides, if clicked use that
		Vec3d hitVec = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
		// map X and Z coords to a direction
		if (hitVec.getZ() < 0.25f) {
			side = Direction.NORTH;
		} else if (hitVec.getZ() > 0.75f) {
			side = Direction.SOUTH;
		} else if (hitVec.getX() < 0.25f) {
			side = Direction.WEST;
		} else if (hitVec.getX() > 0.75f) {
			side = Direction.EAST;
		}

		// toggle the side clicked
		BlockState newState = interactWithSide(state, world, pos, player, side);
		if (newState == null && side != Direction.DOWN) {
			// if the side did not change, toggle the bottom connection
			newState = interactWithSide(state, world, pos, player, Direction.DOWN);
		}

		// if we have changes, apply them and return success
		if (newState != null) {
			world.setBlockState(pos, newState);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborUpdate(state, worldIn, pos, blockIn, fromPos, isMoving);
		if (!worldIn.isClient) {
			boolean isPowered = worldIn.isReceivingRedstonePower(pos);
			if (isPowered != state.get(POWERED)) {
				state = state.with(POWERED, isPowered).with(DOWN, isPowered && canConnect(worldIn, pos, Direction.DOWN));
				worldIn.setBlockState(pos, state, BlockFlags.BLOCK_UPDATE);
			}
      TileEntityHelper.getTile(ChannelTileEntity.class, worldIn, pos)
                      .ifPresent(te -> te.removeCachedNeighbor(fromOffset(pos, fromPos)));
		}
	}

	@Override
	@Deprecated
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return side.getAxis().isHorizontal() && adjacentBlockState.isOf(this) && state.get(DIRECTION_MAP.get(side)).canFlow() && adjacentBlockState.get(DIRECTION_MAP.get(side.getOpposite())).canFlow();
	}

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public BlockEntity createTileEntity(BlockState state, BlockView world) {
    return new ChannelTileEntity();
  }

  private static Direction fromOffset(BlockPos pos, BlockPos neighbor) {
    BlockPos offset = neighbor.subtract(pos);
    for (Direction direction : Direction.values()) {
      if (direction.getVector().equals(offset)) {
        return direction;
      }
    }
    TConstruct.log.error("Channel found no offset for position pair {} and {} on neighbor changed", pos, neighbor);
    return Direction.DOWN;
  }

	public enum ChannelConnection implements StringIdentifiable {
		/** No connection on this side */
		NONE,
		/** Channel is flowing inwards on this side */
		IN,
		/** Channel is flowing outwards on this side */
		OUT;

		@Override
		public String asString() {
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
			switch(this) {
				case IN:  return OUT;
				case OUT: return IN;
			}
			return NONE;
		}

		/**
		 * Gets the next side in the cycle for interaction
		 * @param reverse  If true, reverse cycle order
		 * @return  Next side to cycle
		 */
		public ChannelConnection getNext(boolean reverse) {
			if (reverse) {
				switch(this) {
					case NONE: return OUT;
					case OUT:  return IN;
					case IN:   return NONE;
				}
			} else {
				switch(this) {
					case NONE: return IN;
					case IN:   return OUT;
					case OUT:  return NONE;
				}
			}
			// not possible
			throw new UnsupportedOperationException();
		}
	}
}
