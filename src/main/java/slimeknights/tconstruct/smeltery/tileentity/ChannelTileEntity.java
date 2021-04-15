package slimeknights.tconstruct.smeltery.tileentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.math.MathHelper;
import slimeknights.mantle.util.NotNullConsumer;
import slimeknights.tconstruct.fluids.IFluidHandler;
import slimeknights.tconstruct.library.fluid.FillOnlyFluidHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock.ChannelConnection;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket.IFluidPacketReceiver;
import slimeknights.tconstruct.smeltery.tileentity.tank.ChannelSideTank;
import slimeknights.tconstruct.smeltery.tileentity.tank.ChannelTank;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Logic for channel fluid transfer
 */
public class ChannelTileEntity extends BlockEntity implements Tickable, IFluidPacketReceiver {
	public static final int LIQUID_TRANSFER = 16;

	/** Channel internal tank */
	private final ChannelTank tank = new ChannelTank(36, this);
	/** Handler to return from channel top */
	private final Optional<IFluidHandler> topHandler = Optional.of(new FillOnlyFluidHandler((IFluidHandler) tank));
	/** Tanks for inserting on each side */
	private final Map<Direction,Optional<IFluidHandler>> sideTanks = Util.make(new EnumMap<>(Direction.class), map -> {
		for (Direction direction : Type.HORIZONTAL) {
			map.put(direction, Optional.of(new ChannelSideTank(this, tank, direction)));
		}
	});

	/** Cache of tanks on all neighboring sides */
	private final Map<Direction,Optional<IFluidHandler>> neighborTanks = new EnumMap<>(Direction.class);
	/** Consumers to attach to each of the neighbors */
	private final Map<Direction, NotNullConsumer<Optional<IFluidHandler>>> neighborConsumers = new EnumMap<>(Direction.class);

	/** Stores if the channel is currently flowing, set to 2 to allow a small buffer */
	private final byte[] isFlowing = new byte[5];

	public ChannelTileEntity() {
		this(TinkerSmeltery.channel);
	}

	protected ChannelTileEntity(BlockEntityType<?> type) {
		super(type);
	}

	/**
	 * Gets the central fluid tank of this channel
	 * @return  Central tank
	 */
	public FluidVolume getFluid() {
		return this.tank.getFluid();
	}

/*	@Override
	@Environment(EnvType.CLIENT)
	public Box getRenderBoundingBox() {
		return new Box(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	}


	*//* Fluid handlers *//*

	@Override
	public <T> Optional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		// top side gets the insert direct
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      if (side == null || side == Direction.UP) {
        return topHandler.cast();
      }
      // side tanks keep track of which side inserts
      if (side != Direction.DOWN && getCachedState().get(ChannelBlock.DIRECTION_MAP.get(side)) == ChannelConnection.IN) {
        return sideTanks.get(side).cast();
      }
    }

		return super.getCapability(capability, side);
	}*/

	/**
	 * Gets the fluid handler directly from a neighbor, skipping the cache
	 * @param side  Side of the neighbor to fetch
	 * @return  Fluid handler, or empty
	 */
	private Optional<IFluidHandler> getNeighborHandlerUncached(Direction side) {
/*		assert world != null;
		// must have a TE with a fluid handler
		BlockEntity te = world.getBlockEntity(pos.offset(side));
		if (te != null) {
			Optional<IFluidHandler> handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
			if (handler.isPresent()) {
				handler.addListener(neighborConsumers.computeIfAbsent(side, s -> new WeakConsumerWrapper<>(this, (self, lazy) -> self.neighborTanks.remove(s))));
				return handler;
			}
		}*/
		return Optional.empty();
	}

	/**
	 * Gets the fluid handler from a neighbor
	 * @param side  Side of the neighbor to fetch
	 * @return  Fluid handler, or empty
	 */
	protected Optional<IFluidHandler> getNeighborHandler(Direction side) {
		return neighborTanks.computeIfAbsent(side, this::getNeighborHandlerUncached);
	}

	/**
	 * Removes a cached handler from the given neighbor
	 * @param side  Side to remove
	 */
	public void removeCachedNeighbor(Direction side) {
		neighborTanks.remove(side);
	}


/*	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		topHandler.invalidate();
		for (Optional<IFluidHandler> handler : sideTanks.values()) {
			handler.invalidate();
		}
	}*/


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
		return side.getId() - 1;
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
		if(wasFlowing != flowing && world != null && !world.isClient) {
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
			return this.getCachedState().get(ChannelBlock.DOWN);
		}
		return this.getCachedState().get(ChannelBlock.DIRECTION_MAP.get(side)) == ChannelConnection.OUT;
	}

	/**
	 * Counts the number of side outputs on the given side
	 * @param state  State to check
	 * @return  Number of outputs
	 */
	private static int countOutputs(BlockState state) {
		int count = 0;
		for (Direction direction : Type.HORIZONTAL) {
			if (state.get(ChannelBlock.DIRECTION_MAP.get(direction)) == ChannelConnection.OUT) {
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
		TinkerNetwork.getInstance().sendToClientsAround(new ChannelFlowPacket(pos, side, flowing), world, pos);
	}


	/* Flow */

	/**
	 * Ticking logic
	 */
	@Override
	public void tick() {
		if(world == null || world.isClient) {
			return;
		}

		// must have fluid first
		FluidVolume fluid = tank.getFluid();
		if(!fluid.isEmpty()) {

			// if we have down and can flow, skip sides
			boolean hasFlown = false;
			BlockState state = getCachedState();
			if(state.get(ChannelBlock.DOWN)) {
				hasFlown = trySide(Direction.DOWN, FluidAmount.of(FaucetTileEntity.MB_PER_TICK, 1000));
			}
			// try sides if we have any sides
			int outputs = countOutputs(state);
			if(!hasFlown && outputs > 0) {
				// split the fluid evenly between sides
                FluidAmount flowRate = tank.getMaxUsable().div(FluidAmount.ofWhole(outputs)).max(FluidAmount.ONE).min(FluidAmount.of(FaucetTileEntity.MB_PER_TICK, 1000));
				// then transfer on each side
				for(Direction side : Type.HORIZONTAL) {
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
						direction = Direction.byId(i + 1);
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
	protected boolean trySide(Direction side, FluidAmount flowRate) {
		if(tank.isEmpty() || !this.isOutput(side)) {
			return false;
		}

		// get the handler on the side, try filling
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
	protected boolean fill(Direction side, IFluidHandler handler, FluidAmount amount) {
		// make sure we do not allow more than the fluid allows, should not happen but just in case
        FluidAmount usable = tank.getMaxUsable().min(amount);
		if (usable.isGreaterThan(FluidAmount.ZERO)) {
			// see how much works
			FluidVolume fluid = tank.drain(usable, Simulation.SIMULATE);
			FluidVolume filled = handler.fill(fluid, Simulation.SIMULATE);
			if (!filled.isEmpty()) {
				// drain the amount that worked
				fluid = tank.drain(filled, Simulation.ACTION);
				handler.fill(fluid, Simulation.ACTION);

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
		if (world != null && !world.isClient) {
			TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(pos, getFluid()), world, pos);
		}
	}

	@Override
  public void updateFluidTo(FluidVolume fluid) {
		tank.setFluid(fluid);
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return toTag(new CompoundTag());
	}

	@Override
	public CompoundTag toTag(CompoundTag nbt) {
		nbt = super.toTag(nbt);

		nbt.putByteArray(TAG_IS_FLOWING, isFlowing);
		nbt.put(TAG_TANK, tank.writeToNBT(new CompoundTag()));

		return nbt;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag nbt) {
		super.fromTag(state, nbt);

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
