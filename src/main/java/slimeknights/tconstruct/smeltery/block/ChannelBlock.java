package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.tileentity.ChannelTileEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.AbstractBlock.Properties;

public class ChannelBlock extends Block {
	private static final ITextComponent SIDE_IN = TConstruct.makeTranslation("block", "channel.side.in");
	private static final ITextComponent SIDE_OUT = TConstruct.makeTranslation("block", "channel.side.out");
	private static final ITextComponent SIDE_NONE = TConstruct.makeTranslation("block", "channel.side.none");
	private static final ITextComponent DOWN_OUT = TConstruct.makeTranslation("block", "channel.down.out");
	private static final ITextComponent DOWN_NONE = TConstruct.makeTranslation("block", "channel.down.none");
	private static final Map<ChannelConnection,ITextComponent> SIDE_CONNECTION = Util.make(new EnumMap<>(ChannelConnection.class), map -> {
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
		map.put(Direction.NORTH, VoxelShapes.join(box( 4, 4,  0, 12, 9,  4), box( 6, 6,  0, 10, 9,  4), IBooleanFunction.ONLY_FIRST));
		map.put(Direction.SOUTH, VoxelShapes.join(box( 4, 4, 12, 12, 9, 16), box( 6, 6, 12, 10, 9, 16), IBooleanFunction.ONLY_FIRST));
		map.put(Direction.WEST,  VoxelShapes.join(box( 0, 4,  4,  4, 9, 12), box( 0, 6,  6,  4, 9, 10), IBooleanFunction.ONLY_FIRST));
		map.put(Direction.EAST,  VoxelShapes.join(box(12, 4,  4, 16, 9, 12), box(12, 6,  6, 16, 9, 10), IBooleanFunction.ONLY_FIRST));
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
		VoxelShape centerUnconnected = VoxelShapes.joinUnoptimized(
				box(4, 4, 4, 12, 9, 12),
				VoxelShapes.or(box(6, 6, 4, 10, 9, 12), box(4, 6, 6, 12, 9, 10)),
				IBooleanFunction.ONLY_FIRST);
		// center with down connection
		VoxelShape centerConnected = VoxelShapes.joinUnoptimized(
				box(4, 2, 4, 12, 9, 12),
				VoxelShapes.or(box(6, 6, 4, 10, 9, 12), box(4, 6, 6, 12, 9, 10), box(6, 2, 6, 10, 9, 10)),
				IBooleanFunction.ONLY_FIRST);
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
							BOUNDS[makeKey(down, north, south, west, east)] = VoxelShapes.or(center, northBounds, southBounds, westBounds, eastBounds);
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
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOUNDS[makeKey(state.getValue(DOWN), state.getValue(NORTH).canFlow(), state.getValue(SOUTH).canFlow(), state.getValue(WEST).canFlow(), state.getValue(EAST).canFlow())];
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
		builder.add(DOWN, POWERED);
		DIRECTION_MAP.values().forEach(builder::add);
	}

  @Override
  public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
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
	private static boolean isFluidHandler(IWorld world, Direction side, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
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
	private boolean canConnect(IWorld world, Direction side, BlockState facingState, BlockPos facingPos) {
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
	private boolean canConnect(IWorld world, BlockPos pos, Direction side) {
		BlockPos facingPos = pos.relative(side);
		return canConnect(world, side, world.getBlockState(facingPos), facingPos);
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
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
      PlayerEntity player = context.getPlayer();
      connection = player != null && player.isShiftKeyDown() ? ChannelConnection.IN : ChannelConnection.OUT;
    } else if (isFluidHandler(world, side, placedOn)) {
      connection = ChannelConnection.OUT;
    }
    return state.setValue(DIRECTION_MAP.get(side.getOpposite()), connection);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		// down only cares about connected or not
		if (facing == Direction.DOWN) {
			if (state.getValue(DOWN) && facingState.isAir(world, facingPos)) {
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
				if (connection != ChannelConnection.NONE && facingState.isAir(world, facingPos)) {
					state = state.setValue(prop, ChannelConnection.NONE);
				}
			}
		}

		return state;
	}

	@Nullable
	private BlockState interactWithSide(BlockState state, World world, BlockPos pos, PlayerEntity player, Direction side) {
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
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {		// if the player is holding a channel, skip unless we clicked the top
		// they can shift click to place one on the top
		Direction hitFace = hit.getDirection();
		if (player.getItemInHand(hand).getItem() == this.asItem() && world.isEmptyBlock(pos.relative(hitFace))) {
			return ActionResultType.PASS;
		}

		// default to using the clicked side, though null (is that valid?) and up act as down
		Direction side = hitFace == Direction.UP ? Direction.DOWN : hitFace;
		if (player.isShiftKeyDown() && side != Direction.DOWN) {
			side = side.getOpposite();
		}

		// try each of the sides, if clicked use that
		Vector3d hitVec = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
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
				TileEntityHelper.getTile(ChannelTileEntity.class, world, pos).ifPresent(te -> te.refreshNeighbor(newState, finalSide));
			}
			world.setBlockAndUpdate(pos, newState);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		if (!worldIn.isClientSide) {
			boolean isPowered = worldIn.hasNeighborSignal(pos);
			if (isPowered != state.getValue(POWERED)) {
				state = state.setValue(POWERED, isPowered).setValue(DOWN, isPowered && canConnect(worldIn, pos, Direction.DOWN));
				worldIn.setBlock(pos, state, BlockFlags.BLOCK_UPDATE);
			}
      TileEntityHelper.getTile(ChannelTileEntity.class, worldIn, pos)
                      .ifPresent(te -> te.removeCachedNeighbor(Util.directionFromOffset(pos, fromPos)));
		}
	}

	@Override
	@Deprecated
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return side.getAxis().isHorizontal() && adjacentBlockState.is(this) && state.getValue(DIRECTION_MAP.get(side)).canFlow() && adjacentBlockState.getValue(DIRECTION_MAP.get(side.getOpposite())).canFlow();
	}

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new ChannelTileEntity();
  }

  public enum ChannelConnection implements IStringSerializable {
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
