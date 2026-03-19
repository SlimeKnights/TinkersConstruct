package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.fluid.FillOnlyFluidHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock.ChannelConnection;
import slimeknights.tconstruct.smeltery.block.entity.tank.ChannelSideTank;
import slimeknights.tconstruct.smeltery.block.entity.tank.ChannelTank;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket.IFluidPacketReceiver;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Logic for channel fluid transfer
 */
public class ChannelBlockEntity extends MantleBlockEntity implements IFluidPacketReceiver {
	/** Channel internal tank */
	private final ChannelTank tank = new ChannelTank(FaucetBlockEntity.MB_PER_TICK * 4, this);
	/** Handler to return from channel top */
	private final LazyOptional<IFluidHandler> topHandler = LazyOptional.of(() -> new FillOnlyFluidHandler(tank));
	/** Tanks for inserting on each side */
	private final Map<Direction,IFluidHandler> sideTanks = Util.make(new EnumMap<>(Direction.class), map -> {
		for (Direction direction : Plane.HORIZONTAL) {
			map.put(direction, new ChannelSideTank(this, tank, direction));
		}
	});
	/** Tanks for inserting on each side */
	private final Map<Direction,LazyOptional<IFluidHandler>> sideHandlers = new EnumMap<>(Direction.class);
	/** Tanks for alerting neighbors the given side is present */
	private final Map<Direction,LazyOptional<IFluidHandler>> emptySideHandler = new EnumMap<>(Direction.class);

	/** Cache of tanks on all neighboring sides */
	private final Map<Direction,LazyOptional<IFluidHandler>> neighborTanks = new EnumMap<>(Direction.class);
	/** Consumers to attach to each of the neighbors */
	private final Map<Direction,NonNullConsumer<LazyOptional<IFluidHandler>>> neighborConsumers = new EnumMap<>(Direction.class);

  /** Ticker instance for this TE, serverside only */
  public static final BlockEntityTicker<ChannelBlockEntity> SERVER_TICKER = (level, pos, state, self) -> self.tick(state);

	/** Stores if the channel is currently flowing, set to 2 to allow a small buffer */
	private final byte[] isFlowing = new byte[5];

	public ChannelBlockEntity(BlockPos pos, BlockState state) {
		this(TinkerSmeltery.channel.get(), pos, state);
	}

	protected ChannelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * Gets the central fluid tank of this channel
	 * @return  Central tank
	 */
	public FluidStack getFluid() {
		return this.tank.getFluid();
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.getX(), worldPosition.getY() - 1, worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
	}

	/** Called when a capability invalidates to clear the given side */
	private void invalidateSide(Direction side, LazyOptional<IFluidHandler> capability) {
		if (!this.isRemoved()) {
			if (neighborTanks.get(side) == capability) {
				neighborTanks.remove(side);
			}
		}
	}


	/* Fluid handlers */

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		// top side gets the insert direct
    if (capability == ForgeCapabilities.FLUID_HANDLER) {
      if (side == null || side == Direction.UP) {
        return topHandler.cast();
      }
      // side tanks keep track of which side inserts
      if (side != Direction.DOWN) {
        ChannelConnection connection = getBlockState().getValue(ChannelBlock.DIRECTION_MAP.get(side));
        if (connection == ChannelConnection.IN) {
          return sideHandlers.computeIfAbsent(side, s -> LazyOptional.of(() -> sideTanks.get(s))).cast();
        }
        // for out, return an empty fluid handler so the block we are pouring into knows we support fluids, even though we disallow any interaction
        // this will get invalidated when the connection goes back to in later
        if (connection == ChannelConnection.OUT) {
          return emptySideHandler.computeIfAbsent(side, s -> LazyOptional.of(() -> EmptyFluidHandler.INSTANCE)).cast();
        }
      }
    }

		return super.getCapability(capability, side);
	}

	/**
	 * Gets the fluid handler directly from a neighbor, skipping the cache
	 * @param side  Side of the neighbor to fetch
	 * @return  Fluid handler, or empty
	 */
	private LazyOptional<IFluidHandler> getNeighborHandlerUncached(Direction side) {
		assert level != null;
		// must have a TE with a fluid handler
		BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
		if (te != null) {
			LazyOptional<IFluidHandler> handler = te.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite());
			if (handler.isPresent()) {
				handler.addListener(neighborConsumers.computeIfAbsent(side, s -> new WeakConsumerWrapper<>(this, (self, lazy) -> self.invalidateSide(s, lazy))));
				return handler;
			}
		}
		return LazyOptional.empty();
	}

	/**
	 * Gets the fluid handler from a neighbor
	 * @param side  Side of the neighbor to fetch
	 * @return  Fluid handler, or empty
	 */
	protected LazyOptional<IFluidHandler> getNeighborHandler(Direction side) {
		return neighborTanks.computeIfAbsent(side, this::getNeighborHandlerUncached);
	}

	/**
	 * Removes a cached handler from the given neighbor as the block changed
	 * @param side  Side to remove
	 */
	public void removeCachedNeighbor(Direction side) {
		neighborTanks.remove(side);
	}

	/**
	 * Refreshes a neighbor based on the new connection
	 * @param state  The state that will later be put in the world, may not be the state currently in the world
	 * @param side   Side to update
	 */
	public void refreshNeighbor(BlockState state, Direction side) {
		// for below, only thing that needs to invalidate is if we are no longer connected down, remove the listener below
		if (side == Direction.DOWN) {
			if (!state.getValue(ChannelBlock.DOWN)) {
				neighborTanks.remove(Direction.DOWN);
			}
		} else if (side != Direction.UP) {
			ChannelConnection connection = state.getValue(ChannelBlock.DIRECTION_MAP.get(side));
			// if no longer flowing out, remove the neighbor tank
			if (connection != ChannelConnection.OUT) {
				neighborTanks.remove(Direction.DOWN);
				// remove the empty handler, mostly so the neighbor knows to update
				LazyOptional<IFluidHandler> handler = emptySideHandler.remove(side);
				if (handler != null) {
					handler.invalidate();
				}
			}
			// remove the side handler, if we changed from out or from in the handler is no longer correct
			if (connection != ChannelConnection.IN) {
				LazyOptional<IFluidHandler> handler = sideHandlers.remove(side);
				if (handler != null) {
					handler.invalidate();
				}
			}
		}
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		topHandler.invalidate();
		for (LazyOptional<IFluidHandler> handler : sideHandlers.values()) {
			if (handler != null) {
				handler.invalidate();
			}
		}
		for (LazyOptional<IFluidHandler> handler : emptySideHandler.values()) {
			if (handler != null) {
				handler.invalidate();
			}
		}
	}


	/* Flowing property */

	/**
	 * Gets the index for the given side for flowing. Same as regular index but without up
	 * @param side  Side to index
	 * @return Flow index
	 */
	private int getFlowIndex(Direction side) {
		if (side.getAxis().isVertical()) {
			return 0;
		}
		return side.get3DDataValue() - 1;
	}

	/**
	 * Marks the given side as flowing for the sake of rendering
	 * @param side     Side to set
	 * @param flowing  True to mark it as flowing
	 */
	public void setFlow(Direction side, boolean flowing) {
		if (side == Direction.UP) {
			return;
		}
		// update flowing state
		int index = getFlowIndex(side);
		boolean wasFlowing = isFlowing[index] > 0;
		isFlowing[index] = (byte)(flowing ? 2 : 0);

		// send packet to client if it changed
		if(wasFlowing != flowing && level != null && !level.isClientSide) {
			syncFlowToClient(side, flowing);
		}
	}

	/**
	 * Checks if the given side is flowing
	 * @param side  Side to check
	 * @return  True if flowing
	 */
	public boolean isFlowing(Direction side) {
		if (side == Direction.UP) {
			return false;
		}

		return isFlowing[getFlowIndex(side)] > 0;
	}


	/* Utilities */

	/**
	 * Gets the connection for a side
	 * @param side  Side to query
	 * @return  Connection on the specified side
	 */
	protected boolean isOutput(Direction side) {
		// just always return in for up, thats fine
		if(side == Direction.UP) {
			return false;
		}
		// down is boolean, sides is multistate
		if(side == Direction.DOWN) {
			return this.getBlockState().getValue(ChannelBlock.DOWN);
		}
		return this.getBlockState().getValue(ChannelBlock.DIRECTION_MAP.get(side)) == ChannelConnection.OUT;
	}

	/**
	 * Counts the number of side outputs on the given side
	 * @param state  State to check
	 * @return  Number of outputs
	 */
	private static int countOutputs(BlockState state) {
		int count = 0;
		for (Direction direction : Plane.HORIZONTAL) {
			if (state.getValue(ChannelBlock.DIRECTION_MAP.get(direction)) == ChannelConnection.OUT) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Syncs the given flowing state to the client side
	 * @param side     Side to sync
	 * @param flowing  Flowing state to sync
	 */
	private void syncFlowToClient(Direction side, boolean flowing) {
		TinkerNetwork.getInstance().sendToClientsAround(new ChannelFlowPacket(worldPosition, side, flowing), level, worldPosition);
	}


	/* Flow */

	/**
	 * Server ticking logic
	 */
	private void tick(BlockState state) {
		// must have fluid first
		FluidStack fluid = tank.getFluid();
		if (!fluid.isEmpty()) {
			// if we have down and can flow, skip sides
			boolean hasFlown = false;
			if(state.getValue(ChannelBlock.DOWN)) {
				hasFlown = trySide(Direction.DOWN, FaucetBlockEntity.MB_PER_TICK);
			}
			// try sides if we have any sides
			int outputs = countOutputs(state);
			if(!hasFlown && outputs > 0) {
				// split the fluid evenly between sides
				int flowRate = Mth.clamp(tank.getMaxUsable() / outputs, 1, FaucetBlockEntity.MB_PER_TICK);
				// then transfer on each side
				for(Direction side : Plane.HORIZONTAL) {
					trySide(side, flowRate);
				}
			}
		}

		// clear flowing if we should no longer flow on a side
		for (int i = 0; i < 5; i++) {
			if (isFlowing[i] > 0) {
				isFlowing[i]--;
				if (isFlowing[i] == 0) {
					Direction direction;
					if (i == 0) {
						direction = Direction.DOWN;
					} else {
						direction = Direction.from3DDataValue(i + 1);
					}
					syncFlowToClient(direction, false);
				}
			}
		}

		tank.freeFluid();
	}

	/**
	 * Tries transferring fluid on a single side of the channel
	 * @param side      Side to transfer from
	 * @param flowRate  Maximum amount to output
	 * @return  True if the side transferred fluid
	 */
	protected boolean trySide(Direction side, int flowRate) {
		if(tank.isEmpty() || !this.isOutput(side)) {
			return false;
		}

		// get the handler on the side, try filling
    // TODO: handle the case of no fluid handler on the side that may later become a handler
		return getNeighborHandler(side).filter(handler -> fill(side, handler, flowRate))
																	 .isPresent();
	}

	/**
	 * Fill the fluid handler on the given side
	 * @param side     Side to fill
	 * @param handler  Handler to fill
	 * @param amount   Amount to fill
	 * @return  True if the side successfully filled something
	 */
	protected boolean fill(Direction side, IFluidHandler handler, int amount) {
		// make sure we do not allow more than the fluid allows, should not happen but just in case
		int usable = Math.min(tank.getMaxUsable(), amount);
		if (usable > 0) {
			// see how much works
			FluidStack fluid = tank.drain(usable, FluidAction.SIMULATE);
			int filled = handler.fill(fluid, FluidAction.SIMULATE);
			if (filled > 0) {
				// drain the amount that worked
				fluid = tank.drain(filled, FluidAction.EXECUTE);
				handler.fill(fluid, FluidAction.EXECUTE);

				// mark that the side is flowing
				setFlow(side, true);
				return true;
			}
		}

		// failed to flow, mark side as not flowing
		setFlow(side, false);
		return false;
	}


	/* NBT and sync */
	private static final String TAG_IS_FLOWING = "is_flowing";
	private static final String TAG_TANK = "tank";

	/**
	 * Sends a fluid update to the client with the current fluid
	 */
	public void sendFluidUpdate() {
		if (level != null && !level.isClientSide) {
			TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(worldPosition, getFluid()), level, worldPosition);
		}
	}

	@Override
  public void updateFluidTo(FluidStack fluid) {
		tank.setFluid(fluid);
	}

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  protected void saveSynced(CompoundTag nbt) {
    super.saveSynced(nbt);
    nbt.putByteArray(TAG_IS_FLOWING, isFlowing);
    nbt.put(TAG_TANK, tank.writeToNBT(new CompoundTag()));
  }

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);

		// isFlowing
		if (nbt.contains(TAG_IS_FLOWING)) {
			byte[] nbtFlowing = nbt.getByteArray(TAG_IS_FLOWING);
			int max = Math.min(5, nbtFlowing.length);
			for (int i = 0; i < max; i++) {
				byte b = nbtFlowing[i];
				if (b > 2) {
					isFlowing[i] = 2;
				} else if (b < 0) {
					isFlowing[i] = 0;
				} else {
					isFlowing[i] = b;
				}
			}
		}

		// tank
		CompoundTag tankTag = nbt.getCompound(TAG_TANK);
		tank.readFromNBT(tankTag);
	}
}
